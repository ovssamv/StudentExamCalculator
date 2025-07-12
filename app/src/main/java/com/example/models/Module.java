package com.example.models;

public class Module {
    private String name;
    private double coefficient;
    private double tdScore;
    private double tpScore;
    private double examScore;
    private double average;

    public Module(String name, double coefficient) {
        this.name = name;
        this.coefficient = coefficient;
        this.tdScore = 0.0;
        this.tpScore = 0.0;
        this.examScore = 0.0;
        this.average = 0.0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCoefficient() {
        return coefficient;
    }



    public double getTdScore() {
        return tdScore;
    }

    public void setTdScore(double tdScore) {
        this.tdScore = tdScore;
    }

    public double getTpScore() {
        return tpScore;
    }

    public void setTpScore(double tpScore) {
        this.tpScore = tpScore;
    }

    public double getExamScore() {
        return examScore;
    }

    public void setExamScore(double examScore) {
        this.examScore = examScore;
    }

    public double getAverage() {
        return average;
    }

    public void calculateAverage() {

        this.average = (0.6 * examScore) + (0.2 * tdScore) + (0.2 * tpScore);
    }
}