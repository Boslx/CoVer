package com.example.cover_a01.ui.ephid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cover_a01.R;

public class ephidFragment extends Fragment {

    private ephidViewModel ephidViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ephidViewModel =
                new ViewModelProvider(this).get(ephidViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ephemeral, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        ephidViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}