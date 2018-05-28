package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.FhirControl;
import ch.db_And_FHIR.dbControl;
import ch.model.EncapsulatedParameters;
import ch.model.OverviewParameters;
import ch.model.WeeklyParameters;
import javafx.scene.control.Alert;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.exceptions.FHIRException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CalculatedParametersCtrl {

    public CalculatedParametersCtrl() {
    }



    //FhirControl FhirClass = FhirControl.getInstance();

    public EncapsulatedParameters buildCalculatedParameters(Integer patientIdentifier, LocalDate startDate, LocalDate endDate) {
        List<Observation> FhirObservations;// = new ArrayList<>();
        List<Observation> fev1Liste = new ArrayList<>();
        List<Observation> dagSymptomListe = new ArrayList<>();
        List<Observation> natSymptomListe = new ArrayList<>();
        List<Observation> anfaldsMedListe = new ArrayList<>();
        List<Observation> triggerListe = new ArrayList<>();
        List<Observation> aktivitetsListe = new ArrayList<>();
        List<Observation> pefMorgenListe = new ArrayList<>();
        List<Observation> pefAftenListe = new ArrayList<>();
        List<Observation> dagSHoste = new ArrayList<>();
        List<Observation> dagSHvaesen = new ArrayList<>();
        List<Observation> dagSSlim = new ArrayList<>();
        List<Observation> dagSTryk = new ArrayList<>();
        List<Observation> dagSAande = new ArrayList<>();
        List<Observation> triggerAktiv = new ArrayList<>();
        List<Observation> triggerAllergi = new ArrayList<>();
        List<Observation> triggerStoev = new ArrayList<>();
        List<Observation> triggerUkendt = new ArrayList<>();
        List<Observation> natSHoste = new ArrayList<>();
        List<Observation> natSOpvaagning = new ArrayList<>();
        List<Observation> natSTraethed = new ArrayList<>();

        // Sætter de to instans variable til null, da vi aldrig skal bibeholde noget data hvis denne metode kaldes igen.


        OverviewParameters OVParam;
        WeeklyParameters WeekParam = new WeeklyParameters();
        EncapsulatedParameters encapsulatedParameters = new EncapsulatedParameters();
        //OVParam = null;
        //WeekParam = null;

        // Antallet af uger inkluderet i observationerne
        Long avgWeekRounder = (ChronoUnit.DAYS.between(startDate, endDate)) / 7;
        Integer avgWeekRounderInt = Math.round(avgWeekRounder.intValue());


        // Henter instansen af FhirControl, dette er IKKE at lave et nyt objekt.
        FhirControl FhirClass = FhirControl.getInstance();

        // Henter observationer
        FhirObservations = FhirClass.getFHIRObservations(patientIdentifier.toString(), startDate, endDate);

        /**
         * Henter DB instans
         * Henter FEV fra DB
         * Udregner % ift. forventet af Fev Målingerne (Bruges i WeeklyParam)
         */
        dbControl dbClass = dbControl.getInstance();
        fev1Liste = dbClass.buildFEV(patientIdentifier);
        List<Observation> pctFev = new ArrayList<>();
        pctFev = pctAfPEV1(fev1Liste);

        //Max findes i oprindelige Fev liste, hvorefter der udregnes hvad dene gennemsnitlige FEV måling svarer til i % af forventet
        Double maxFev = findMax(fev1Liste);
        Double avgFev = ((gnmsnit(fev1Liste))/maxFev)*100;

        /*for (int j = 0; j <FhirObservations.size(); j++){
    System.out.println(FhirObservations.get(j).getCode().getCoding().get(0).getCode());
}*/
        /**
         * Inddeler FhirObservationerne i liste efter deres koder
         */
        while (!FhirObservations.isEmpty()) {
            if (FhirObservations.get(0).getCode().getCoding().get(0).getCode().equals("Behov for anfalds medicin")) {
                anfaldsMedListe.add(FhirObservations.get(0));
                FhirObservations.remove(0);
            } else if (FhirObservations.get(0).getCode().getCoding().get(0).getCode().equals("Nat Symptom")) {
                natSymptomListe.add(FhirObservations.get(0));
                FhirObservations.remove(0);
            } else if (FhirObservations.get(0).getCode().getCoding().get(0).getCode().equals("Dag Symptom")) {
                dagSymptomListe.add(FhirObservations.get(0));
                FhirObservations.remove(0);
            } else if (FhirObservations.get(0).getCode().getCoding().get(0).getCode().equals("Triggers")) {
                triggerListe.add(FhirObservations.get(0));
                FhirObservations.remove(0);
            } else if (FhirObservations.get(0).getCode().getCoding().get(0).getCode().equals("Aktivitetsbegraensning")) {
                aktivitetsListe.add(FhirObservations.get(0));
                FhirObservations.remove(0);
            } else if (FhirObservations.get(0).getCode().getCoding().get(0).getCode().equals("Morgen måling")) {
                pefMorgenListe.add(FhirObservations.get(0));
                FhirObservations.remove(0);
            } else if (FhirObservations.get(0).getCode().getCoding().get(0).getCode().equals("Aften måling")) {
                pefAftenListe.add(FhirObservations.get(0));
                FhirObservations.remove(0);
            }
        }
        //System.out.println("DagsSymptom size = " + dagSymptomListe.size());


        /**
         * Inddeler yderligere listerne "NatSymptomer", "DagSymptomer" og "Triggers", i deres respektive underkomponenter vha. deres values.
         * Dette gøres ved nye lister. Ye I know, lister, lister, lister...
         */
        for (int j = 0; j < natSymptomListe.size(); j++) {
            if (natSymptomListe.get(j).getValue().toString().equals("Hoste")) {
                natSHoste.add(natSymptomListe.get(j));
            } else if (natSymptomListe.get(j).getValue().toString().equals("Opvågning")) {
                natSOpvaagning.add(natSymptomListe.get(j));
            } else if (natSymptomListe.get(j).getValue().toString().equals("Træthed")) {
                natSTraethed.add(natSymptomListe.get(j));
            }
        }
        for (int j = 0; j < triggerListe.size(); j++) {
            if (triggerListe.get(j).getValue().toString().equals("Aktivitet")) {
                triggerAktiv.add(triggerListe.get(j));
            } else if (triggerListe.get(j).getValue().toString().equals("Allergi")) {
                triggerAllergi.add(triggerListe.get(j));
            } else if (triggerListe.get(j).getValue().toString().equals("Støv")) {
                triggerStoev.add(triggerListe.get(j));
            } else if (triggerListe.get(j).getValue().toString().equals("Ukendt")) {
                triggerUkendt.add(triggerListe.get(j));
            }
        }
        for (int j = 0; j < dagSymptomListe.size(); j++) {
            if (dagSymptomListe.get(j).getValue().toString().equals("Hvaesen")) {
                dagSHvaesen.add(dagSymptomListe.get(j));
            } else if (dagSymptomListe.get(j).getValue().toString().equals("Hosten")) {
                dagSHoste.add(dagSymptomListe.get(j));
            } else if (dagSymptomListe.get(j).getValue().toString().equals("Hoste m. slim")) {
                dagSSlim.add(dagSymptomListe.get(j));
            } else if (dagSymptomListe.get(j).getValue().toString().equals("Trykken for brystet")) {
                dagSTryk.add(dagSymptomListe.get(j));
            } else if (dagSymptomListe.get(j).getValue().toString().equals("Åndenød")) {
                dagSAande.add(dagSymptomListe.get(j));
            }
        }
        /*
        // Test print
        System.out.println("Hvaesen Size = " + dagSHvaesen.size());
        System.out.println("Hoste Size = " + dagSHoste.size());
        System.out.println("Aandenød Size = " + dagSAande.size());
        System.out.println("Slim Size = " + dagSSlim.size());
        System.out.println("Tryk Size = " + dagSTryk.size());*/

        /**
         * Afgør hvilket Dagsymptom er forekommet mest.
         * Simpelt størrelsescheck på de 5 lister.
         * Kan laves til metode. (Burde laves til metode)
         */
        String mostFrequentDay = "Ingen";
        if(dagSymptomListe.size() != 0){
            int size = 0;
            if(size<dagSHvaesen.size())
        mostFrequentDay = dagSHvaesen.get(0).getValue().toString();
            size = dagSHvaesen.size();
        if (size < dagSHoste.size()) {
            size = dagSHoste.size();

            mostFrequentDay = dagSHoste.get(0).getValue().toString();
        }        if(size<dagSSlim.size()){
            size = dagSSlim.size();
            mostFrequentDay = dagSSlim.get(0).getValue().toString();
        }if(size<dagSTryk.size()){
            size = dagSTryk.size();
            mostFrequentDay = dagSTryk.get(0).getValue().toString();
        }if(size<dagSAande.size()){

            size = dagSAande.size();
            mostFrequentDay = dagSAande.get(0).getValue().toString();
        }}else{ mostFrequentDay = "Ingen";}

        /**
         * Afgør hvilket Natsymptom er forekommet mest.
         * Simpelt størrelsescheck på de 5 lister.
         */
        String mostFrequentNight = "Ingen";
        if(natSymptomListe.size() != 0){
            int size = 0;
        if(size<natSHoste.size())
        mostFrequentNight = natSHoste.get(0).getValue().toString();
        size = natSHoste.size();
        if(size<natSHoste.size()){
            size = natSHoste.size();
            mostFrequentNight = natSHoste.get(0).getValue().toString();
        }        if(size<natSOpvaagning.size()){
            size = natSOpvaagning.size();
            mostFrequentNight = natSOpvaagning.get(0).getValue().toString();
        }if(size<natSTraethed.size()){
            size = natSTraethed.size();
            mostFrequentNight = natSTraethed.get(0).getValue().toString();
        }} else{
            mostFrequentNight = "Ingen"; }

        /**
         * Afgør hvilken Trigger symptom er forekommet mest.
         * Simpelt størrelsescheck på de 5 lister.
         */
        String mostFrequentTrigger = "Ingen";
        if (triggerListe.size() != 0){
            int size = 0;
        if(size<triggerAktiv.size())
        mostFrequentTrigger = triggerAktiv.get(0).getValue().toString();
        size = triggerAktiv.size();
        if(size<triggerAllergi.size()){
            size = triggerAllergi.size();
            mostFrequentTrigger = triggerAllergi.get(0).getValue().toString();
        }        if(size<triggerStoev.size()){
            size = triggerStoev.size();
            mostFrequentTrigger = triggerStoev.get(0).getValue().toString();
        }if(size<triggerUkendt.size()){
            size = triggerUkendt.size();
            mostFrequentTrigger = triggerUkendt.get(0).getValue().toString();
        }
        } else {mostFrequentTrigger = "Ingen";}

        Double maxPefMorgen = findMax(pefMorgenListe);
        Double maxPefAften = findMax(pefAftenListe);

        Double avgPefMorgen = ((gnmsnit(pefMorgenListe))/maxPefMorgen)*100;
        Double avgPefAften = ((gnmsnit(pefAftenListe))/maxPefAften)*100;


        /**
         * Sætter alt i OverviewParameter modellen, her med constructor, har en idé om hvordan vi undgår constructor, hvis vi ikke gider det,
         * se weeklyparam og observér at der ikke er erklæret nogen constructor, så vi defaulter til en noArg constructor. Det kunne også
         * gøres her.
         */
        OVParam = new OverviewParameters(Math.round(dagSymptomListe.size()/avgWeekRounderInt), Math.round(natSymptomListe.size()/avgWeekRounderInt), Math.round(aktivitetsListe.size()/avgWeekRounderInt), Math.round(anfaldsMedListe.size()/avgWeekRounderInt), avgPefMorgen, avgPefAften, avgFev, mostFrequentDay, mostFrequentNight, mostFrequentTrigger);


        //////////////////// SLUT PÅ OVERVIEWPARAMETER BEREGNING ////////////////////////////////////////////////


        /**
         * Skaffer ugenumre for start og slut dato
         * Ikke længere aktuelt.
         */
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNumber = startDate.get(woy);
        int weekNumber2 = endDate.get(woy);
        int calendarWeeks = weekNumber2 - weekNumber;
        DayOfWeek doW = startDate.getDayOfWeek();

        /**
         * DagSymptom pr uge liste, hver index i den yderste liste er et symptom
         * /////////////////////////////////////////////////////////////////////
         * Hver index i den inderste liste referer til symptomantallet for én uge
         * Inderste liste har den første uge = index[0]
         * Det samme er gældende for de efterfølgende lister
         */

        List<List<Integer>> ugeListeDagSymptomer = new ArrayList<>();
        ugeListeDagSymptomer.add(symptomListe(startDate, endDate, dagSAande));
        ugeListeDagSymptomer.add(symptomListe(startDate, endDate, dagSHoste));
        ugeListeDagSymptomer.add(symptomListe(startDate, endDate, dagSHvaesen));
        ugeListeDagSymptomer.add(symptomListe(startDate, endDate, dagSSlim));
        ugeListeDagSymptomer.add(symptomListe(startDate, endDate, dagSTryk));


        /**
         * Tager listen med lister og summerer med fastholdt inderste index, mens der itereres over den yderste liste
         * På denne måde tilføjer vi det totale antal symptomer (summen) pr. uge til en ny liste
         * Nyt index er så i uger (I ugeListeTotalDagSymptomer
         * Kan laves til en metode.
         */
        List<Integer> ugeListeTotalDagSymptom = new ArrayList<>();
        Integer sum = 0;
        for (int j = 0; j < ugeListeDagSymptomer.get(0).size(); j++) {
            for (int i = 0; i < ugeListeDagSymptomer.size(); i++) {
                sum += ugeListeDagSymptomer.get(i).get(j);
            }
            ugeListeTotalDagSymptom.add(sum);
            sum = 0;
        }



        // Nat Symptomer
        List<List<Integer>> ugeListeNatSymptomer = new ArrayList<>();
        ugeListeNatSymptomer.add(symptomListe(startDate, endDate, natSHoste));
        ugeListeNatSymptomer.add(symptomListe(startDate, endDate, natSTraethed));
        ugeListeNatSymptomer.add(symptomListe(startDate, endDate, natSOpvaagning));


        /**
         * Laver uge Listen med én værdi for hvert symptom pr. uge
         */
        List<Integer> ugeListeTotalNatSymptom = new ArrayList<>();
        for (int j = 0; j < ugeListeNatSymptomer.get(0).size(); j++) {
            for (int i = 0; i < ugeListeNatSymptomer.size(); i++) {
                sum += ugeListeNatSymptomer.get(i).get(j);
            }
            ugeListeTotalNatSymptom.add(sum);
            sum = 0;
        }


        List<Double> pctPeriodeTrigger = new ArrayList<>();

            List<List<Integer>> ugeListeTriggers = new ArrayList<>();
            ugeListeTriggers.add(symptomListe(startDate, endDate, triggerAktiv));
            ugeListeTriggers.add(symptomListe(startDate, endDate, triggerAllergi));
            ugeListeTriggers.add(symptomListe(startDate, endDate, triggerStoev));
            ugeListeTriggers.add(symptomListe(startDate, endDate, triggerUkendt));
            if(triggerListe.size() != 0) {
            double triggerSize = triggerListe.size();
            pctPeriodeTrigger.add(triggerAktiv.size() / triggerSize);
            pctPeriodeTrigger.add(triggerAllergi.size() / triggerSize);
            pctPeriodeTrigger.add(triggerStoev.size() / triggerSize);
            pctPeriodeTrigger.add(triggerUkendt.size() / triggerSize);
        }
        else{
            Double zero = new Double(0);
            // Her skal i < antallet af mulige symptomer
            for (int i=0; i<ugeListeTriggers.size();i++){
                pctPeriodeTrigger.add(zero);
            }
        }
        // Aktivitet, kun én liste
        List<Integer> ugeListeAktivitet = symptomListe(startDate, endDate, aktivitetsListe);

        //Anfaldsmedicin, kun én liste
        List<Integer> ugeListeAnfaldsMed = symptomListe(startDate, endDate, anfaldsMedListe);

        /**
         * pctLister for alle uger
         */
        List<List<Double>> pctListeDagSymptomer = udregnPCT(ugeListeDagSymptomer);
        List<List<Double>> pctListeNatSymptomer = udregnPCT(ugeListeNatSymptomer);
        List<Observation> pctMorgenPEF = pctAfPEV1(pefMorgenListe);
        List<Observation> pctAftenPEF = pctAfPEV1(pefAftenListe);

        /**
         * Laver pctPerioden for dagSymptom
         */
        List<Double> pctPeriodeDagSymptom = new ArrayList<>();
        if (dagSymptomListe.size() != 0){

            double dagSymptomSize = dagSymptomListe.size();
            pctPeriodeDagSymptom.add(dagSAande.size() / dagSymptomSize);
            pctPeriodeDagSymptom.add(dagSHoste.size() / dagSymptomSize);
            pctPeriodeDagSymptom.add(dagSHvaesen.size() / dagSymptomSize);
            pctPeriodeDagSymptom.add(dagSSlim.size() / dagSymptomSize);
            pctPeriodeDagSymptom.add(dagSTryk.size() / dagSymptomSize);
        } else{
            Double zero = new Double(0);
            for (int i=0; i<ugeListeDagSymptomer.size();i++){
                pctPeriodeDagSymptom.add(zero);
            }
        }


        /**
         * Laver pctPerioden for natSymptom
         */
        List<Double> pctPeriodeNatSymptom = new ArrayList<>();
        if (natSymptomListe.size() != 0){
        double natSymptomSize = natSymptomListe.size();
        pctPeriodeNatSymptom.add(natSHoste.size() / natSymptomSize);
        pctPeriodeNatSymptom.add(natSTraethed.size() / natSymptomSize);
        pctPeriodeNatSymptom.add(natSOpvaagning.size() / natSymptomSize);
    } else{
        Double zero = new Double(0);
        for (int i=0; i<ugeListeNatSymptomer.size();i++){
            pctPeriodeNatSymptom.add(zero);
            }
        }

        /**
         * Sætter Weekly Parametre
         */
        boolean emptyWeek = false;
        for (int i = 0; i < ugeListeTotalDagSymptom.size(); i++){
            if (ugeListeTotalDagSymptom.get(i) == 0 && ugeListeTotalDagSymptom.get(i) == 0 &&
                    ugeListeAktivitet.get(i) ==0 && ugeListeAnfaldsMed.get(i) ==0){
                emptyWeek = true;
                break;
            }
        }
       if (emptyWeek){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.initOwner(getPrimaryStage);
            alert.setTitle("Tomme uger");
            alert.setHeaderText("Der findes uger uden astmadata");
            alert.setContentText("Visse uger indeholder ikke astmaappdata! Tjek Ugeoversigt!");
            alert.showAndWait();
        }

        WeekParam.setUgeListeDagSymptomer(ugeListeTotalDagSymptom);
        WeekParam.setUgeListeNatSymptomer(ugeListeTotalNatSymptom);
        WeekParam.setUgeListeAktivitet(ugeListeAktivitet);
        WeekParam.setUgeListeAnfaldsMed(ugeListeAnfaldsMed);
        WeekParam.setPctListeDagSymptomer(pctListeDagSymptomer);
        WeekParam.setPctListeNatSymptomer(pctListeNatSymptomer);
        WeekParam.setPctPeriodeDagSymptom(pctPeriodeDagSymptom);
        WeekParam.setPctPeriodeNatSymptom(pctPeriodeNatSymptom);
        WeekParam.setPctPeriodeTriggers(pctPeriodeTrigger);
        WeekParam.setMorgenPEF(pctMorgenPEF);
        WeekParam.setAftenPEF(pctAftenPEF);
        WeekParam.setFev1(pctFev);

        encapsulatedParameters.setOverviewParameters(OVParam);
        encapsulatedParameters.setWeeklyParameters(WeekParam);

        System.out.println("CalculatedParameters ok");
        return encapsulatedParameters;
    }

    /*private List<Integer> symptomListe(Integer antalKalenderUger, Integer foersteUge, List<Observation> liste){
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        List<Integer> ugeListe = new ArrayList<>();
        int value = 0;
        for(int k = 0; k<antalKalenderUger+1; k++){
            ugeListe.add(0);
        }

        //for (int i = 0; i < antalKalenderUger; i++){
            for (int j = 0; j <liste.size(); j++){
                Integer weekNr = liste.get(j).getIssued().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().get(woy);
                for (int i = 0; i <antalKalenderUger + 1; i++) {
                    if (weekNr.equals(foersteUge + i) && weekNr < foersteUge + antalKalenderUger + 1) {
                        ugeListe.set(i, ugeListe.get(i) + 1);// += ugeListe
                        break;
                    }
                }
            }
            return ugeListe;
    }*/


    /**
     * Tager en liste med observationer og tæller op på antallet af observationer i hver uge (7 dage) baseret på start og slut dato
     * @param startDate startdato, dvs den dato der er længst fra dagsdato
     * @param endDate slutdato, dvs den dato der er tættest på dags dato
     * @param liste Liste med observationer, som skal tælles op på
     * @return  Returnerer en liste med Integers, som indeholder det totale antal forekomster for [0] = første uge, [1] = anden uge osv.
     */
    private List<Integer> symptomListe(LocalDate startDate, LocalDate endDate, List<Observation> liste) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        double numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        // % er resten efter division med tallet efter %
        long extraDays = (long) (numOfDaysBetween % 7);
        double numberOfWeeks = Math.floor(numOfDaysBetween / 7);
        // Disse to skal bruges som ikke-inklusive senere, derfor +-
        startDate = startDate.plusDays(extraDays - 1);
        //endDate = endDate.plusDays(1);

        List<Integer> ugeListe = new ArrayList<>();
        for (int k = 0; k < numberOfWeeks; k++) {
            ugeListe.add(0);
        }

        for (int j = 0; j < liste.size(); j++) {
            for (int i = 1; i < numberOfWeeks + 1; i++) {
                /**
                 * Hvis observationsdatoen er indenfor startdatoen plus i*7 (Husk i = 1, som det første)
                 * så tilføj på plads i
                 * Ydermere skal det gælde at observationen ikke har en dato, som ligger FØR startDato eller EFTER slutdato
                 * Disse to er ikke inklusive, hvorfor der er +- en dag ved start og slutdato længere oppe
                 */
                if (liste.get(j).getIssued().toInstant().atZone(
                        ZoneId.systemDefault()).toLocalDate().isBefore(startDate.plusDays(i * 7 + 1)) &&
                        !liste.get(j).getIssued().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(startDate) &&
                        !liste.get(j).getIssued().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(endDate) ) {
                    ugeListe.set(i - 1, ugeListe.get(i - 1) + 1);
                    break;
                }

            }
        }

        return ugeListe;
    }

    /**
     * Kører over en liste inden i en liste og udregner pctfordeling af de inderste listes elementer
     * Bruges til at finde pctfordelingerne i "Symptomfordeling"
     * @param sumPrUgeListe Liste med antal af forekomster for hvert symptom pr. uge.
     *                      Yderste Index = symptomer
     *                      Inderste Index = uger
     * @return Returnerer en liste på samme form (bare som Doubles for decimalernes skyld),
     * hvor det nu er et symptoms bidrag til en given uges symptom antal
     *
     */
    private List<List<Double>> udregnPCT(List<List<Integer>> sumPrUgeListe) {
        List<List<Double>> pctListe = new ArrayList<>();
        double sum = 0;
        double zero = 0;
        for (int i = 0; i < sumPrUgeListe.size(); i++) {
            pctListe.add(new ArrayList<>());
        }
        for (int j = 0; j < sumPrUgeListe.get(0).size(); j++) {
            for (int i = 0; i < sumPrUgeListe.size(); i++) {
                sum += sumPrUgeListe.get(i).get(j);
            }
            for (int k = 0; k < sumPrUgeListe.size(); k++) {
                if (sum != 0 && j < sumPrUgeListe.get(0).size())
                    pctListe.get(k).add(sumPrUgeListe.get(k).get(j) / sum);
                else if (j < sumPrUgeListe.get(0).size())
                    pctListe.get(k).add(zero);
            }
            sum = 0;
        }
        return pctListe;
    }

    /**
     * Finder et gennemsnit af en observationslistes værdier, tiltænkt PEF
     * Dette kam ikke gøres med Java's average metoder, da der skal ekstraheres en værdi fra en FHIR ressource
     * @param liste Observations liste
     * @return gennemsnittet som Double
     */
    private Double gnmsnit(List<Observation> liste) {
        Double sum = new Double(0);
        for (int i = 0; i < liste.size(); i++) {
            try {
                sum += (liste.get(i).getValueQuantity().getValue().doubleValue());
            } catch (FHIRException e) {
                System.out.println(e.getMessage());
            }
        }
        Double avg = sum / liste.size();

        return avg;

    }

    /**
     * Udregner % af maks PEF, igen kan dette ikke gøres med Java's metoder grundet FHIR
     * @param liste observationsliste
     * @return returnerer inputliste, men nu med pct værdier fremfor PEF værdier
     */
    public List<Observation> pctAfPEV1(List<Observation> liste) {
        Double max = new Double(0);
        Double test = new Double(0);
        for (int i = 0; i < liste.size(); i++) {
            try {
                test = liste.get(i).getValueQuantity().getValue().doubleValue();
            } catch (FHIRException e) {
                System.out.println(e.getMessage());
            }
            if (max < test) {
                max = test;
            }
        }
        for (int j = 0; j < liste.size(); j++) {
            try {
                double temp = ((liste.get(j).getValueQuantity().getValue().doubleValue()) / max) * 100;
                liste.get(j).setValue(new Quantity(temp));

            } catch (FHIRException e) {
                System.out.println(e.getMessage());
            }
        }
        /*try {
            System.out.println(liste.get(0).getValueQuantity().getValue());
        } catch (FHIRException e) {
            e.getMessage();
        }*/
        return liste;
    }

    public Double findMax(List<Observation> liste) {
        Double max = new Double(0);
        Double test = new Double(0);
        for (int i = 0; i < liste.size(); i++) {
            try {
                test = liste.get(i).getValueQuantity().getValue().doubleValue();
            } catch (FHIRException e) {
                System.out.println(e.getMessage());
            }
            if (max < test) {
                max = test;
            }
        }
        return max;
    }
}