package com.example.cover_a01.ui.infection_status;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.cover_a01.R;
import com.example.cover_a01.data.localdatabase.CoVerDatabase;
import com.example.cover_a01.data.model.Contact;
import com.example.cover_a01.data.model.Exposee;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfectionRiskTesting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfectionRiskTesting extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button infecTestBtn;
    private  Button notInfecTestBtn;
    private Button btn_compareInfectionRisk;

    private CoVerDatabase database;


    public InfectionRiskTesting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfectionRiskTesting.
     */
    // TODO: Rename and change types and number of parameters
    public static InfectionRiskTesting newInstance(String param1, String param2) {
        InfectionRiskTesting fragment = new InfectionRiskTesting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        database = CoVerDatabase.getAppDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_infection_risk_testing, container, false);
        infecTestBtn = v.findViewById(R.id.infectedTestBtn);
        infecTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changeInfectionStatus(true);
            }
        });
        notInfecTestBtn = v.findViewById(R.id.notInfectedTestBtn);
        notInfecTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_compareInfectionRisk = v.findViewById(R.id.btn_compareInfectionRisk);
        btn_compareInfectionRisk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //changeInfectionStatus(checkInfectionStatus());
            }
        });

        return v;
    }

    public boolean checkInfectionStatus(){
        if(database==null){
            database = CoVerDatabase.getAppDatabase();
        }
        List<Contact> contacts = database.contactDao().getAll();
        List<Exposee> exposees = database.exposeesDao().getAll();

        // It would be nice if we could use a stream filter instead of this loop,
        // but our minimal supported Android Version is too old for that.
        boolean matchFound = false;
        for (Exposee exposee:exposees) {
            for (Contact contact:contacts) {
                if(contact.getKey().equals(exposee.getKey())){
                    matchFound=true;
                    //todo : Something happens
                    return matchFound;
                }
            }
        }
        return matchFound;
    }
}