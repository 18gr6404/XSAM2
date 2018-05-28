package ch.model;

import org.hl7.fhir.dstu3.model.Observation;

import java.util.ArrayList;
import java.util.List;

public class WeeklyParameters {
    private List<Integer> ugeListeDagSymptomer;
    private List<Integer> ugeListeNatSymptomer;
    private List<Integer> ugeListeAktivitet;
    private List<Integer> ugeListeAnfaldsMed;
    private List<Observation> morgenPEF;
    private List<Observation> aftenPEF;
    private List<Observation> fev1;
    private List<List<Double>> pctListeDagSymptomer;
    private List<List<Double>> pctListeNatSymptomer;
    private List<Double> pctPeriodeDagSymptom;
    private List<Double> pctPeriodeNatSymptom;
    private List<Double> pctPeriodeTriggers;




    public List<Integer> getUgeListeDagSymptomer() {
        return ugeListeDagSymptomer;
    }

    public void setUgeListeDagSymptomer(List<Integer> ugeListeDagSymptomer) {
        this.ugeListeDagSymptomer = ugeListeDagSymptomer;
    }

    public List<Integer> getUgeListeNatSymptomer() {
        return ugeListeNatSymptomer;
    }

    public void setUgeListeNatSymptomer(List<Integer> ugeListeNatSymptomer) {
        this.ugeListeNatSymptomer = ugeListeNatSymptomer;
    }

    public List<Integer> getUgeListeAktivitet(){
        return ugeListeAktivitet;
    }

    public void setUgeListeAktivitet(List<Integer> ugeListeAktivitet){
        this.ugeListeAktivitet = ugeListeAktivitet;
    }

    public List<Integer> getUgeListeAnfaldsMed(){
        return ugeListeAnfaldsMed;
    }

    public void setUgeListeAnfaldsMed(List<Integer> ugeListeAnfaldsMed){
        this.ugeListeAnfaldsMed = ugeListeAnfaldsMed;
    }

    public List<List<Double>> getPctListeDagSymptomer(){
        return pctListeDagSymptomer;
    }

    public void setPctListeDagSymptomer(List<List<Double>> pctListeDagSymptomer){
        this.pctListeDagSymptomer = pctListeDagSymptomer;
    }

    public List<List<Double>> getPctListeNatSymptomer() {
        return pctListeNatSymptomer;
    }

    public void setPctListeNatSymptomer(List<List<Double>> pctListeNatSymptomer) {
        this.pctListeNatSymptomer = pctListeNatSymptomer;
    }

    public List<Double> getPctPeriodeDagSymptom() {
        return pctPeriodeDagSymptom;
    }

    public void setPctPeriodeDagSymptom(List<Double> pctPeriodeDagSympmtom) {
        this.pctPeriodeDagSymptom = pctPeriodeDagSympmtom;
    }

    public List<Double> getPctPeriodeNatSymptom() {
        return pctPeriodeNatSymptom;
    }

    public void setPctPeriodeNatSymptom(List<Double> pctPeriodeNatSympmtom) {
        this.pctPeriodeNatSymptom = pctPeriodeNatSympmtom;
    }

    public List<Observation> getMorgenPEF() {
        return morgenPEF;
    }

    public void setMorgenPEF(List<Observation> morgenPEF) {
        this.morgenPEF = morgenPEF;
    }

    public List<Observation> getAftenPEF() {
        return aftenPEF;
    }

    public void setAftenPEF(List<Observation> aftenPEF) {
        this.aftenPEF = aftenPEF;
    }

    public List<Observation> getFev1() {
        return fev1;
    }

    public void setFev1(List<Observation> fev1) {
        this.fev1 = fev1;
    }

    public List<Double> getPctPeriodeTriggers() {
        return pctPeriodeTriggers;
    }

    public void setPctPeriodeTriggers(List<Double> pctPeriodeTriggers) {
        this.pctPeriodeTriggers = pctPeriodeTriggers;
    }
}
