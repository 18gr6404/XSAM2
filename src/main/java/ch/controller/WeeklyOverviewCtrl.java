package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.dbControl;
import ch.model.EncapsulatedParameters;
import ch.model.WeeklyParameters;
import ch.utility.FhirUpload;
import ch.utility.dateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class WeeklyOverviewCtrl implements Initializable {

    private WeeklyParameters weeklyOverviewParam;

    @FXML
    private Button overViewBtn;
    @FXML
    private Button consultationMeasurementBtn;
    @FXML
    private Button summaryBtn;
    @FXML
    private Button sinceLastConBtn;
    @FXML
    private Button twoWeeksBtn;
    @FXML
    private Button fourWeeksBtn;
    @FXML
    private Button dateOkBtn;

    @FXML
    private DatePicker startPicker;
    @FXML
    private DatePicker endPicker;

    @FXML
    private Label HvaesenLabel;
    @FXML
    private Label HosteLabel;
    @FXML
    private Label HosteSlimLabel;
    @FXML
    private Label TrykkenBrystLabel;
    @FXML
    private Label AandenoedLabel;
    @FXML
    private Label NathosteLabel;
    @FXML
    private Label OpvaagningLabel;
    @FXML
    private Label TraethedLabel;
    @FXML
    private Label AktivitetLabel;
    @FXML
    private Label AllergiLabel;
    @FXML
    private Label StoevLabel;
    @FXML
    private Label UkendtLabel;
    @FXML
    private BarChart AstmaAppBarChart;
    @FXML
    private CategoryAxis UgeNr;
    @FXML
    private NumberAxis Antal;
    @FXML
    private LineChart LineChart;

    private Integer patientCPR;
    private LocalDate startDate; //Start dato for FHIR-søgningen ´. Dette er den ældste dato
    private LocalDate endDate; //Slutdato for FHIR-søgningen ´. Dette er den nyeste dato
    private XYChart.Series pefaften;
    private XYChart.Series dagSymptomer;
    private XYChart.Series natSymptomer;
    private XYChart.Series aktivitetsBegraensning;
    private XYChart.Series anfald;
    private XYChart.Series FEV1;
    private XYChart.Series pefmorgen;
    private MainApp mainAppRef;
    dbControl dbControlOB = dbControl.getInstance();
    // REference til Rootlayout
    private RootLayoutCtrl rootLayoutCtrlRef;

    public WeeklyOverviewCtrl() {
    }

    /**
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */

    public void initialize(URL url, ResourceBundle resourceBundle){

        //Sætter instansvariablerne for start og slut dato til defaultværdier for at vise de seneste 4 uger.
        //startDate = LocalDate.now().minusDays(28);
        //endDate = LocalDate.now();
        String startDateString = "16.04.2018";
        startDate = dateUtil.parse(startDateString);
        endDate = startDate.plusDays(28);


    }


    @FXML
    private void handleConsultationMeasurement(){
        ConsultationMeasurementCtrl.showConsultationMeasurementView(mainAppRef.getPatientCPR(), mainAppRef.getPractitionerID());
    }

    @FXML
    private void handleOverview(){
        rootLayoutCtrlRef.showOverview();
    }

    @FXML
    private void handleSummary(){
        if(rootLayoutCtrlRef.getRootLayout().getBottom() == null){
            rootLayoutCtrlRef.showSummaryView(); }
        else{
            rootLayoutCtrlRef.getRootLayout().setBottom(null);
        }
    }

    @FXML
    private void handleTwoWeeks(){
        endDate = LocalDate.now();
        startDate = LocalDate.now().minusDays(14);

        showData();
    }

    @FXML
    private void handleFourWeeks(){
        endDate = LocalDate.now();
        startDate = LocalDate.now().minusDays(28);

       showData();
    }

    @FXML
    private void handleCustomDate(){
        /* I start pickereren vælger lægen hvilken dato han vil se fra, dette vil typisk være den nyeste dato derfor sættes denne som slutningen
        på FHIR-søgningen. Ligeledes omvendt.
        */
        LocalDate tempEndDate = startPicker.getValue();
        LocalDate tempStartDate = endPicker.getValue();
        //Sørger for at den nyeste dato sættes som endDate:
        if (endDate.isAfter(startDate)){
            endDate = tempEndDate;
            startDate = tempStartDate;
        }
        else{
            endDate = tempStartDate;
            startDate = tempEndDate;
        }

        showData();
    }

    @FXML
    private void handleSinceLastConsultation(){
        int cpr = mainAppRef.getPatientCPR();

        dbControl dbControlOb = dbControl.getInstance();

        startDate = dbControlOb.getLatestConsultationDate(cpr);
        endDate = LocalDate.now();
        showData();
    }


    public void showData(){
        if(mainAppRef == null) {
            mainAppRef = rootLayoutCtrlRef.getMainAppRef();
        }
        patientCPR = (Integer) mainAppRef.getPatientCPR();
        if (patientCPR == 1207731450){
            patientCPR = 1207731470;
        }
        else if (patientCPR == 1303803813){
            patientCPR = 1303803823;
        }

        //Dette hører til BarChart
        CalculatedParametersCtrl calcParam = new CalculatedParametersCtrl();
        EncapsulatedParameters beggeParam = calcParam.buildCalculatedParameters(patientCPR, startDate, endDate);
        weeklyOverviewParam = beggeParam.getWeeklyParameters();

        //Dette hører til LineChart:FEV1
        List<Observation> FEVListe = weeklyOverviewParam.getFev1();

        //Dette hører til LineChart:PEF
        List<Observation> PEFMorgen = weeklyOverviewParam.getMorgenPEF();
        List<Observation> PEFAften = weeklyOverviewParam.getAftenPEF();

        if (!AstmaAppBarChart.getData().isEmpty())
        AstmaAppBarChart.getData().removeAll(dagSymptomer, natSymptomer, aktivitetsBegraensning, anfald);
        if (!LineChart.getData().isEmpty()) {
            LineChart.getData().removeAll(FEV1, pefaften, pefmorgen);
        }
        //BarChart

        dagSymptomer = new XYChart.Series<>();
        dagSymptomer.setName("Dagsymptomer");


        natSymptomer = new XYChart.Series<>();
        natSymptomer.setName("Natsymptomer");


        aktivitetsBegraensning = new XYChart.Series<>();
        aktivitetsBegraensning.setName("Aktivitetsbegrænsning");


        anfald = new XYChart.Series<>();
        anfald.setName("Anfaldsmedicin");

        //LineChart
        FEV1 = new XYChart.Series<>();
        FEV1.setName("FEV1");



        pefmorgen = new XYChart.Series<>();
        pefmorgen.setName("Morgen PEF");



        pefaften = new XYChart.Series<>();
        pefaften.setName("Aften PEF");

        /**
         * Dato sortering
         */

        Collections.sort(FEVListe, new Comparator<Observation>() {
            public int compare(Observation o1, Observation o2) {
                return o1.getIssued().compareTo(o2.getIssued());
            }
        });
        //Add data to LineChart:FEV1
        for (int i = 0; i<FEVListe.size(); i++){
            try{
                FEV1.getData().add(new XYChart.Data("" + FEVListe.get(i).getIssued(), FEVListe.get(i).getValueQuantity().getValue()));
            }catch(FHIRException e){
                System.out.println(e.getMessage());
            }
        }


        /**
         * Sortér lister, så de er i dato rækkefølge
         */
        Collections.sort(PEFMorgen, new Comparator<Observation>() {
            public int compare(Observation o1, Observation o2) {
                return o1.getIssued().compareTo(o2.getIssued());
            }
        });
        Collections.sort(PEFAften, new Comparator<Observation>() {
            public int compare(Observation o1, Observation o2) {
                return o1.getIssued().compareTo(o2.getIssued());
            }
        });
        //Add data to LineChart:PEF
       for (int i = 0; i<PEFMorgen.size(); i++){
           try{
               pefmorgen.getData().add(new XYChart.Data(""+ Instant.ofEpochMilli(PEFMorgen.get(i).getIssued().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                       PEFMorgen.get(i).getValueQuantity().getValue()));
               pefaften.getData().add(new XYChart.Data(""+ Instant.ofEpochMilli(PEFAften.get(i).getIssued().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                       PEFAften.get(i).getValueQuantity().getValue()));
           }catch(FHIRException e){
               System.out.println(e.getMessage());
           }

       }
        //Add data to BarChart
        for (int i = 0; i<weeklyOverviewParam.getUgeListeDagSymptomer().size(); i++) {
            int weeknumber = (1+i);

            //Dagsymptomer
            dagSymptomer.getData().add(new XYChart.Data("Uge " + weeknumber, weeklyOverviewParam.getUgeListeDagSymptomer().get(i)));

            //Natsymptomer
            natSymptomer.getData().add(new XYChart.Data("Uge " + weeknumber, weeklyOverviewParam.getUgeListeNatSymptomer().get(i)));

            //Aktivitetsbegrænsning
            aktivitetsBegraensning.getData().add(new XYChart.Data("Uge " + weeknumber, weeklyOverviewParam.getUgeListeAktivitet().get(i)));

            //Anfaldsmedicin
            anfald.getData().add(new XYChart.Data("Uge " + weeknumber, weeklyOverviewParam.getUgeListeAnfaldsMed().get(i)));

        }
        //Add data and legend to BarChart
        AstmaAppBarChart.setLegendSide(Side.RIGHT);
        //AstmaAppBarChart.getData().removeAll(dagSymptomer, natSymptomer, aktivitetsBegraensning, anfald);
        AstmaAppBarChart.getData().addAll(dagSymptomer, natSymptomer, aktivitetsBegraensning, anfald);
        AstmaAppBarChart.setAnimated(false);
        //Add data and legend to LineChart
        LineChart.setLegendSide(Side.RIGHT);
        LineChart.getData().addAll(FEV1, pefaften, pefmorgen);
        LineChart.setAnimated(false);
        setSymptomFordelingTabel();
    }


    public void setSymptomFordelingTabel () {
        //Laver listen over den procentvise dagsymptomfordeling
        List<Double> pctPeriodeDagSymptom = weeklyOverviewParam.getPctPeriodeDagSymptom();
        //Her bestemmer vi hvor mange decimaler vi vil vise
        DecimalFormat df = new DecimalFormat("###.##");
        //Her sætter vi værdierne fra listen ind på deres tilhørende Labels i WeeklyOverviwViewet (Tabellen i højre side)
        AandenoedLabel.setText(String.valueOf(df.format(pctPeriodeDagSymptom.get(0))));
        HosteLabel.setText(String.valueOf(df.format(pctPeriodeDagSymptom.get(1))));
        HvaesenLabel.setText(String.valueOf(df.format(pctPeriodeDagSymptom.get(2))));
        HosteSlimLabel.setText(String.valueOf(df.format(pctPeriodeDagSymptom.get(3))));
        TrykkenBrystLabel.setText(String.valueOf(df.format(pctPeriodeDagSymptom.get(4))));
        //Her benytter vi tilsvarende metode for natsymptomfordelingen
        List<Double> pctPeriodeNatSymptom = weeklyOverviewParam.getPctPeriodeNatSymptom();
        NathosteLabel.setText(String.valueOf(df.format(pctPeriodeNatSymptom.get(0))));
        TraethedLabel.setText(String.valueOf(df.format(pctPeriodeNatSymptom.get(1))));
        OpvaagningLabel.setText(String.valueOf(df.format(pctPeriodeNatSymptom.get(2))));
        //Her benytter vi tilsvarende metode for triggerfordelingen
        List<Double> pctPeriodeTriggers = weeklyOverviewParam.getPctPeriodeTriggers();
        AktivitetLabel.setText(String.valueOf(df.format(pctPeriodeTriggers.get(0))));
        AllergiLabel.setText(String.valueOf(df.format(pctPeriodeTriggers.get(1))));
        StoevLabel.setText(String.valueOf(df.format(pctPeriodeTriggers.get(2))));
        UkendtLabel.setText(String.valueOf(df.format(pctPeriodeTriggers.get(3))));
    }


    /**
     * Is called to give a reference back to itself.
     *
     * @param inputRootLayoutCtrl
     */
    public void setRootLayoutCtrlRef(RootLayoutCtrl inputRootLayoutCtrl) {
        this.rootLayoutCtrlRef = inputRootLayoutCtrl;
    }
    public void setMainApp(MainApp inputMain) {
        this.mainAppRef = inputMain;
    }


}




