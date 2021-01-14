package com.example.cover_a01.data.model;

public enum InfectionStatus {
    calculating(0),
    deactivated(1),
    highRiskOfInfection(2),
    notInfected(3),
    lowRiskOfInfection(4),
    infectionReported(5);




    private final int value;

    private InfectionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static InfectionStatus fromInteger(int x) {
        switch(x) {
            case 0:
                return calculating;
            case 1:
                return deactivated;
            case 2:
                return highRiskOfInfection;
            case 3:
                return notInfected;
            case 4:
                return lowRiskOfInfection;
            case 5:
                return infectionReported;
        }
        return null;
    }
}
