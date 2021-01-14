package com.example.cover_a01.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private int lastInfectionStatus;

    @ColumnInfo
    private int riskEncounters;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLastInfectionStatus() {
        return lastInfectionStatus;
    }

    public void setLastInfectionStatus(int lastInfectionStatus) {
        this.lastInfectionStatus = lastInfectionStatus;
    }

    public int getRiskEncounters() {
        return riskEncounters;
    }

    public void setRiskEncounters(int riskEncounters) {
        this.riskEncounters = riskEncounters;
    }
}
