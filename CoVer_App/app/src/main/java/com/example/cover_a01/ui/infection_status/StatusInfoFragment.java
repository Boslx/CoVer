package com.example.cover_a01.ui.infection_status;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.cover_a01.Analytics;
import com.example.cover_a01.data.model.InfectionStatus;
import com.example.cover_a01.InfectionStatusChangedListener;
import com.example.cover_a01.MainActivity;
import com.example.cover_a01.R;

import static android.content.Context.MODE_PRIVATE;

public class StatusInfoFragment extends Fragment implements InfectionStatusChangedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private TextView tv;

    private TextView textView;
    private TableRow tr;

    private SharedPreferences preferences;
    private MainActivity main;

    private static StatusInfoFragment statusInfoFragment;

    private View view;

    public StatusInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        main = MainActivity.getMainActivity();
        statusInfoFragment = this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        preferences = this.getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        switch (preferences.getString("Theme", "MODE_NIGHT_NO")){
            case "MODE_NIGHT_YES":
                getContext().getTheme().applyStyle(R.style.Theme_CoVer_Dark, true);
                break;
            case "MODE_NIGHT_NO":
                getContext().getTheme().applyStyle(R.style.Theme_CoVer_Light, true);
                break;
        }
        View root = inflater.inflate(R.layout.fragment_status_info, container, false);
        textView = root.findViewById(R.id.textView10);
        tr = root.findViewById(R.id.tableRow1);
        preferences = this.getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        Analytics.getAnalytics().getInfectionObserver().addListener(this);

        Pair<InfectionStatus, Integer> analytics = Analytics.getAnalytics().getLastInfectionStatus();

        onInfectionStatusChanged(analytics.first, analytics.second);
        return view = root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Analytics.getAnalytics().getInfectionObserver().removeListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        Pair<InfectionStatus, Integer> analysis = Analytics.getAnalytics().getLastInfectionStatus();
        onInfectionStatusChanged(analysis.first, analysis.second);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onInfectionStatusChanged(InfectionStatus infectionStatus, int riskEncounters) {
        switch (infectionStatus){
            case highRiskOfInfection:
                textView.setText("Ihr Risikostatus: Es bestand Kontakt mit Erkrankten.");
                tr.setBackground(getResources().getDrawable(R.color.red_NICE));
                break;
            case calculating:
                textView.setText("Ihr Riskostatus wird gerade berechnet.");
                tr.setBackground(getResources().getDrawable(R.color.light_grey));
                break;
            case notInfected:
                textView.setText("Ihr Risikostatus: Es bestand kein risikobehafteter Kontakt.");
                tr.setBackground(getResources().getDrawable(R.color.green_NICE));
                break;
            case deactivated:
                textView.setText("Ihr Risikostatus: Unbekannt. Bitte aktivieren Sie den Schutz über die Einstellungen.");
                tr.setBackground(getResources().getDrawable(R.color.cover_dark_grey));
                break;
            case lowRiskOfInfection:
                textView.setText("Ihr Risikostatus: Es bestand gelegentlicher Kontakt mit Erkrankten, achten Sie bitte auf Symptome.");
                tr.setBackground(getResources().getDrawable(R.color.red_lowRisk_NICE));
        }
    }

    public static StatusInfoFragment getStatusInfoFragment(){
        return statusInfoFragment;
    }
}