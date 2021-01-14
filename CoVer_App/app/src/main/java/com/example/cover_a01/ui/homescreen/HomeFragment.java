package com.example.cover_a01.ui.homescreen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.cover_a01.Analytics;
import com.example.cover_a01.Notifier;
import com.example.cover_a01.data.model.InfectionStatus;
import com.example.cover_a01.InfectionStatusChangedListener;
import com.example.cover_a01.R;

import java.text.DateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements InfectionStatusChangedListener {
    private SharedPreferences preferences;
    private int width;
    private int height;

    private String currentDateTime;

    private Button btn_HomeRisk;
    private TextView text_HomeDate, text_HomeRisk;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        preferences = this.getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        switch (preferences.getString("Theme", "MODE_NIGHT_NO")){
            case "MODE_NIGHT_YES":
                getActivity().getTheme().applyStyle(R.style.Theme_CoVer_Dark, true);
                break;
            case "MODE_NIGHT_NO":
                getActivity().getTheme().applyStyle(R.style.Theme_CoVer_Light, true);
                break;
        }
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        preferences = this.getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        btn_HomeRisk = root.findViewById(R.id.btn_HomeRisk);
        text_HomeDate = root.findViewById(R.id.text_HomeDate);
        text_HomeRisk = root.findViewById(R.id.text_HomeRisk);

        Analytics.getAnalytics().getInfectionObserver().addListener(this);
        Pair<InfectionStatus, Integer> analysis = Analytics.getAnalytics().getLastInfectionStatus();
        onInfectionStatusChanged(analysis.first, analysis.second);

        updateHome();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Analytics.getAnalytics().getInfectionObserver().removeListener(this);
    }

    @Override
    public void onInfectionStatusChanged(InfectionStatus infectionStatus, int riskEncounters) {
        switch (infectionStatus) {
            case highRiskOfInfection:
                btn_HomeRisk.setBackgroundColor(getResources().getColor(R.color.red_NICE));
                if(riskEncounters>1){
                    btn_HomeRisk.setText("Infektionsrisiko\nHoch\n\nEs bestanden "+riskEncounters+" Risikokontakte");
                }else
                if(riskEncounters==1){
                    btn_HomeRisk.setText("Infektionsrisiko\nHoch\n\nEs bestand "+riskEncounters+" Risikokontakt");
                }else{
                    btn_HomeRisk.setText("Infektionsrisiko\nHoch\n\nEs bestanden ein oder mehrere Risikokontakte");
                }
                text_HomeRisk.setText("Schutz: Aktiviert");
                new Notifier(getActivity()); //Sends notification todo: Notifier abändern, dass low risk andere notifacion sendet
                break;
            case deactivated:
                btn_HomeRisk.setBackgroundColor(getResources().getColor(R.color.cover_dark_grey));
                btn_HomeRisk.setText("Infektionsrisiko\nUnbekannt");
                text_HomeRisk.setText("Schutz: Deaktiviert");
                break;
            case notInfected:
                btn_HomeRisk.setBackgroundColor(getResources().getColor(R.color.green_NICE));
                btn_HomeRisk.setText("Infektionsrisiko\nNiedrig");
                text_HomeRisk.setText("Schutz: Aktiviert");
                break;
            case calculating:
                btn_HomeRisk.setBackgroundColor(getResources().getColor(R.color.light_grey));
                btn_HomeRisk.setText("Infektionsrisiko wird \nberechnet");
                text_HomeRisk.setText("Schutz: Aktiviert");
                break;
            case infectionReported:
                btn_HomeRisk.setBackgroundColor(getResources().getColor(R.color.red_lowRisk_NICE));
                btn_HomeRisk.setText("Ihre Erkrankung wurde gemeldet \nVielen Dank für ihre Mithilfe");
                text_HomeRisk.setText("Schutz: Aktiviert");
                break;
            case lowRiskOfInfection:
                btn_HomeRisk.setBackgroundColor(getResources().getColor(R.color.red_lowRisk_NICE));
                btn_HomeRisk.setText("Infektionsrisiko\nNiedrig\n\nEs bestand "+ riskEncounters+" Risikokontakt");
                text_HomeRisk.setText("Schutz: Aktiviert");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + infectionStatus);
        }

        //update the Time displayed
        Calendar calendar = Calendar.getInstance();
        currentDateTime = DateFormat.getDateTimeInstance().format(calendar.getTime());
        text_HomeDate.setText(currentDateTime);
    }

    //updates the UI + Risk check. perform this on screen resume / on a timer / sensible intervals
    private void updateHome() {
        Analytics.getAnalytics().executeAnalysisAsync();
    }
}