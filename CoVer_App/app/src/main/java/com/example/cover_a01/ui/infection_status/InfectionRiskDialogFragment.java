package com.example.cover_a01.ui.infection_status;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cover_a01.R;

public class InfectionRiskDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Button weiterButton;

    public InfectionRiskDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_infektion_risiko_dialog, container, false);
        weiterButton = v.findViewById(R.id.weitere_informationen_button);
        weiterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                StatusInfoFragment statusInfoFrag= new StatusInfoFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(((ViewGroup)getView().getParent()).getId(),statusInfoFrag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return v;
    }
}