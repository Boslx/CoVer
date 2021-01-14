package com.example.cover_a01;

import com.example.cover_a01.data.model.InfectionStatus;

public interface InfectionStatusChangedListener {
    void onInfectionStatusChanged(InfectionStatus infectionStatus, int countRiskEncounters);
}
