package com.example.cover_a01.ui.homescreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cover_a01.Analytics;
import com.example.cover_a01.MainActivity;
import com.example.cover_a01.data.model.InfectionStatus;
import com.example.cover_a01.R;
import com.example.cover_a01.ui.infection_report.InfectionReportDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class TrackingFragment extends Fragment {
    private ImageButton btn_protection;
    private Button infectionReportBtn;
    private Button contactUsBtn;
    private TextView text_Protection;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ViewPager pager;
    private Switch darkModeSwitch;

    public TrackingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackingFragment newInstance(String param1, String param2) {
        TrackingFragment fragment = new TrackingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = this.getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        switch (preferences.getString("Theme", "MODE_NIGHT_NO")){
            case "MODE_NIGHT_YES":
                getContext().getTheme().applyStyle(R.style.Theme_CoVer_Dark, true);
                break;
            case "MODE_NIGHT_NO":
                getContext().getTheme().applyStyle(R.style.Theme_CoVer_Light, true);
                break;
        }
        View v = inflater.inflate(R.layout.fragment_tracking, container, false);
        pager = v.findViewById(R.id.pager);


        editor = preferences.edit();

        text_Protection = v.findViewById(R.id.text_Protection);

        darkModeSwitch = v.findViewById(R.id.dark_mode_switch);
        if(preferences.getString("Theme","MODE_NIGHT_NO").equals("MODE_NIGHT_YES")){
            darkModeSwitch.setChecked(true);
//            getContext().getTheme().applyStyle(R.style.Theme_CoVer_Light, true);
        }
        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (darkModeSwitch.isChecked()) {
                        Toast.makeText(getActivity(), "Theme auf dunkel gewechselt", Toast.LENGTH_SHORT).show();
                        editor.putString("Theme", "MODE_NIGHT_YES");
                        editor.apply();

                        BottomNavigationView bottomView = getActivity().findViewById(R.id.bottomNavigationView);
                        bottomView.setBackgroundColor(getResources().getColor(R.color.cover_dark_grey));

                        androidx.appcompat.widget.Toolbar topBar = getActivity().findViewById(R.id.toolbar3);
                        topBar.setBackgroundColor(getResources().getColor(R.color.cover_dark_grey));

                        ((MainActivity) getActivity()).populateList();
                        darkModeSwitch.toggle();
                    } else {
                        Toast.makeText(getActivity(), "Theme auf hell gewechselt", Toast.LENGTH_SHORT).show();
                        editor.putString("Theme", "MODE_NIGHT_NO");
                        editor.apply();
                        BottomNavigationView bottomView = getActivity().findViewById(R.id.bottomNavigationView);
                        bottomView.setBackgroundColor(getResources().getColor(R.color.blue_NICE));

                        Toolbar topBar = getActivity().findViewById(R.id.toolbar3);
                        topBar.setBackgroundColor(getResources().getColor(R.color.blue_NICE));
                        ((MainActivity) getActivity()).populateList();
                    }
                }
            });

        btn_protection = v.findViewById(R.id.btn_protection);
        btn_protection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSlider();
            }
        });
        infectionReportBtn = v.findViewById(R.id.report_infection_button);
        infectionReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfectionReportDialogFragment infectionReportDialog = new InfectionReportDialogFragment();
                infectionReportDialog.show(getFragmentManager(),"dialog");
            }
        });

        contactUsBtn = v.findViewById(R.id.button_contactUs);
        contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"cover@<domain>"});
                i.putExtra(Intent.EXTRA_SUBJECT, "CoVer Supportanfrage");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    System.out.println("Sie sollten einen Email Client installieren.");
                }
            }
        });

        setSilderOn(Analytics.getAnalytics().getLastInfectionStatus().first!=InfectionStatus.deactivated);

        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                ((int)preferences.
                        getFloat("Width75%",810),(int)preferences.getFloat("Height10%",192));
        params.gravity = Gravity.CENTER;

        infectionReportBtn.setLayoutParams(params);
        contactUsBtn.setLayoutParams(params);
        btn_protection.setLayoutParams(params);
    }

    private void updateSlider(){
        Analytics analytics = Analytics.getAnalytics();
        //sets the text in center of the on / off button half when checked or unchecked
        if(analytics.getLastInfectionStatus().first != InfectionStatus.deactivated){
            analytics.abortAnalysisAsync();
            analytics.setLastInfectionStatus(InfectionStatus.deactivated, 0);
            analytics.getInfectionObserver().notifyInfectionStatus(InfectionStatus.deactivated, 0);

            //swap image
            setSilderOn(false);
        }else{
            analytics.setLastInfectionStatus(InfectionStatus.calculating, 0);
            analytics.executeAnalysisAsync();
            //swap image
            setSilderOn(true);
        }
    }

    private void setSilderOn(boolean on){
        if(on){
            //swap image
            btn_protection.setBackgroundResource(R.drawable.toggle_on_alt);
            text_Protection.setText("Schutz aktiv.\nVielen Dank für Ihre Unterstützung.");
        } else {
            btn_protection.setBackgroundResource(R.drawable.toggle_off_alt);
            text_Protection.setText("Aktivieren Sie den Schutz,\num sich selbst und andere zu schützen.");
        }
    }
}