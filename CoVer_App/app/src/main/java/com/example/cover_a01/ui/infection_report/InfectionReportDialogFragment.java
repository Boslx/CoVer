package com.example.cover_a01.ui.infection_report;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cover_a01.Analytics;
import com.example.cover_a01.R;
import com.example.cover_a01.data.api.CoVerApi;
import com.example.cover_a01.data.api.CoVerApiBuilder;
import com.example.cover_a01.data.localdatabase.CoVerDatabase;
import com.example.cover_a01.data.model.SecretKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class InfectionReportDialogFragment extends DialogFragment {

    private Button weiterButton;
    private Button abortButton;

    private EditText codeInputView;

    private AlertDialog successDialog;

    private CoVerDatabase database;
    private CoVerApi coVerApi;
    private Analytics.InfectionObserver infectionObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        coVerApi = CoVerApiBuilder.getCoVerApi();
        database = CoVerDatabase.getAppDatabase();


        View v = inflater.inflate(R.layout.fragment_infection_report_dialog, container, false);

        codeInputView = v.findViewById(R.id.input_authenticationcode);
        weiterButton = v.findViewById(R.id.input_dialog_positive_button);
        weiterButton.setOnClickListener(v1 -> {
            if(codeInputView.getText().toString().equals("")){
                codeInputView.setError("Bitte geben Sie ihren Code hier ein!");
                return;
            }

            List<String> ephIDs = new ArrayList();

            List<SecretKey> keys = database.secretKeyDao().getAll();
            for (SecretKey key:keys) {
                ephIDs.add(key.getKey());
            }

            Call<Void> call = coVerApi.reportInfectionPost(codeInputView.getText().toString(), ephIDs);

            try {
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    show("Erfolgreicher Upload!", "Vielen Dank für ihre Mithilfe!", this);
//                    infectionObserver.notifyInfectionStatus(InfectionStatus.infectionReported, 0); //Crashes
                    database.secretKeyDao().nukeTable();

                } else{
                    codeInputView.setError("Ihr Code ist fehlerhaft!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        abortButton = v.findViewById(R.id.input_dialog_negative_button);
        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;

    }

    void show(String title, String message, DialogFragment dialogFragment)
    {
        successDialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogFragment.dismiss();
                        successDialog.cancel();
                    }
                })
                .show();
    }
}