package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.dbControl;
import ch.model.EncapsulatedParameters;
import ch.model.OverviewParameters;
import ch.model.WeeklyParameters;
import ch.polarChart.PolarChartGenerator;
import ch.utility.dateUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class OverviewCtrl {

    // Reference to the main application.
    private MainApp mainAppRef;
    // Reference til Rootlayout
    private RootLayoutCtrl rootLayoutCtrlRef;


//Vi bruger vil ikke disse buttons?
    @FXML
    private Button weeklyOverviewBtn;
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

    //Obs disse er i MostFrequentTable
    @FXML
    private Label mostFreqDaySymptomLabel;
    @FXML
    private Label mostFreqNigthSymptomLabel;
    @FXML
    private Label triggerLabel;

    @FXML
    private VBox overViewCenter;

    private Integer patientCPR;
    private LocalDate startDate; //Start dato for FHIR-søgningen ´. Dette er den ældste dato
    private LocalDate endDate; //Slutdato for FHIR-søgningen ´. Dette er den nyeste dato

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public OverviewCtrl() {
    }

    /**
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
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
    private void handleWeeklyOverview(){
        rootLayoutCtrlRef.showWeeklyOverview();
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

        CalculatedParametersCtrl calcParam = new CalculatedParametersCtrl();
        EncapsulatedParameters beggeParam = calcParam.buildCalculatedParameters(patientCPR, startDate, endDate);
        OverviewParameters overviewParam = beggeParam.getOverviewParameters();

        PolarChartGenerator polarChartGenerator = new PolarChartGenerator();
        StackPane chart = polarChartGenerator.generateChart(overviewParam);

        overViewCenter.getChildren().setAll(chart);

        mostFreqDaySymptomLabel.setText( overviewParam.getMostFrequentDay());
        mostFreqNigthSymptomLabel.setText( overviewParam.getMostFrequentNight());
        triggerLabel.setText( overviewParam.getMostFrequentTrigger());

    }



    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param inputMain
     */
    public void setMainApp(MainApp inputMain) {
        this.mainAppRef = inputMain;
    }

    /**
     * Is called to give a reference back to itself.
     *
     * @param inputRootLayoutCtrl
     */
    public void setRootLayoutCtrlRef(RootLayoutCtrl inputRootLayoutCtrl) {
        this.rootLayoutCtrlRef = inputRootLayoutCtrl;
    }

}
