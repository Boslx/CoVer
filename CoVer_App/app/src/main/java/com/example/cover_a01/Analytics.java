package com.example.cover_a01;

import android.os.AsyncTask;

import androidx.core.util.Pair;

import com.example.cover_a01.data.api.CoVerApi;
import com.example.cover_a01.data.localdatabase.CoVerDatabase;
import com.example.cover_a01.data.model.Contact;
import com.example.cover_a01.data.model.Exposee;
import com.example.cover_a01.data.model.InfectionStatus;
import com.example.cover_a01.data.model.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Response;

public class Analytics {
    public static class InfectionObserver{
        private List<InfectionStatusChangedListener> listeners = new ArrayList<InfectionStatusChangedListener>();

        public void addListener(InfectionStatusChangedListener toAdd){
            listeners.add(toAdd);
        }

        public void removeListener(InfectionStatusChangedListener toRem){
            listeners.remove(toRem);
        }

        public void notifyInfectionStatus(InfectionStatus infectionStatus, int riskEncounters){
            for(InfectionStatusChangedListener listener:listeners){
                listener.onInfectionStatusChanged(infectionStatus, riskEncounters);
            }
        }
    }

    static Analytics analytics;

    private final CoVerDatabase database;
    private final CoVerApi api;

    public Pair<InfectionStatus, Integer> getLastInfectionStatus() {
        return new Pair<InfectionStatus, Integer>(InfectionStatus.fromInteger(database.settingsDao().getAll().get(0).getLastInfectionStatus()),
                database.settingsDao().getAll().get(0).getRiskEncounters());
    }

    public void setLastInfectionStatus(InfectionStatus infectionStatus, int riskEncounters){
        database.settingsDao().nukeTable();
        Settings settings = new Settings();
        settings.setLastInfectionStatus(infectionStatus.getValue());
        settings.setRiskEncounters(riskEncounters);
        database.settingsDao().insert(settings);
    }

    public InfectionObserver getInfectionObserver() {
        return infectionObserver;
    }

    private InfectionObserver infectionObserver;
    private Analytics(CoVerDatabase database, CoVerApi api) {
        this.database = database;
        this.api = api;
    }

    public static Analytics getAnalytics() {
        return analytics;
    }

    public static void setupAnalytics(CoVerDatabase database, CoVerApi api) {
        if (analytics == null) {
            analytics = new Analytics(database, api);
            analytics.infectionObserver = new InfectionObserver();
        }
    }

    public boolean updateLocalDatabase() {
        Response<List<Exposee>> exposees;
        try {
            exposees = api.exposeesGet().execute();
            if (exposees.isSuccessful()) {
                database.exposeesDao().nukeTable();
                for (Exposee exposee : exposees.body()) {
                    database.exposeesDao().insert(exposee);
                }

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Pair<InfectionStatus, Integer> checkIfExposed(Collection<Contact> contacts) {
        int countShortContacts = 0;
        int countLongContacts = 0;

        // Check whether a certain time was spent with the infected time
        int minutesSpendInfected = 0;
        long dateLastExposeeContact = 0;

        for (Contact contact : contacts) {
            if (contact.isKnownExposee()) {
                long timespan = contact.getContactDate() - dateLastExposeeContact;
                if (timespan < App.getApp().getResources().getInteger(R.integer.maxTimeSpanSinceLastContact)) {
                    // Another exposee contact within 1.2 minutes since last contact
                    minutesSpendInfected++;
                } else {
                    // Another exposee contact, but more than 1.2 minutes ago
                    minutesSpendInfected = 1;
                }
                dateLastExposeeContact = contact.getContactDate();

                if(minutesSpendInfected==2) countShortContacts++;
                else if(minutesSpendInfected==16) countLongContacts++;
            }
        }

        if(countLongContacts>0) return new Pair<InfectionStatus, Integer>(InfectionStatus.highRiskOfInfection, countLongContacts);
        else if(countShortContacts>0) return new Pair<InfectionStatus, Integer>(InfectionStatus.lowRiskOfInfection, countShortContacts);
        else return new Pair<InfectionStatus, Integer>(InfectionStatus.notInfected, 0);
    }

    public void markExposeeContacts(Collection<Contact> contacts, Collection<Exposee> exposees) {
        // Mark all contacts that are infected
        for (Exposee exposee : exposees) {
            for (Contact contact : contacts) {
                if (exposee.getKey().equals(contact.getKey())) {
                    contact.setKnownExposee(true);
                }
            }
        }
    }

    ExecuteAnalysis executeAnalysis;
    public void executeAnalysisAsync(){
        abortAnalysisAsync();

        executeAnalysis = new ExecuteAnalysis();
        executeAnalysis.execute();
    }

    public void abortAnalysisAsync(){
        if(executeAnalysis!=null){
            executeAnalysis.cancel(true);
        }
    }

    private final class ExecuteAnalysis extends AsyncTask<Void, Void, Pair<InfectionStatus, Integer>> {

        @Override
        protected Pair<InfectionStatus, Integer> doInBackground(Void... params) {
            if(getLastInfectionStatus().first==InfectionStatus.deactivated){
                return new Pair<InfectionStatus, Integer>(InfectionStatus.deactivated, 0);
            }

            Analytics analytics = Analytics.getAnalytics();
            analytics.updateLocalDatabase();

            List<Contact> contacts = database.contactDao().getAll();
            List<Exposee> exposees = database.exposeesDao().getAll();
            markExposeeContacts(contacts, exposees);

            Pair<InfectionStatus, Integer> analysis = analytics.checkIfExposed(contacts);
            setLastInfectionStatus(analysis.first, analysis.second);
            return analysis;
        }

        @Override
        protected void onPostExecute(Pair<InfectionStatus, Integer> result) {
            infectionObserver.notifyInfectionStatus(result.first, result.second);
        }
    }
}
