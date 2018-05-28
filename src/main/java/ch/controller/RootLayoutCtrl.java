package ch.controller;

import ch.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import java.io.IOException;

public class RootLayoutCtrl {

    @FXML
    private BorderPane rootLayout;
    @FXML
    private VBox sidePaneLeft;
    @FXML
    private VBox sidePaneRight;
    @FXML
    private VBox centerView;

    private MainApp mainAppRef;
    private VBox mostFrequentTable;
    private OverviewCtrl overviewCtrlRef;

    /**
     * Constructor
     */
    public RootLayoutCtrl(MainApp inputMainAppRef)
    {this.mainAppRef = inputMainAppRef; }


    public void initRootLayout(Stage inputPrimaryStage) {
        FXMLLoader loader = new FXMLLoader();

        try {
            // Load root layout from fxml file.
            loader.setLocation(RootLayoutCtrl.class.getResource("/ch/view/RootLayout.fxml"));
            loader.setController(this);
            rootLayout = loader.<BorderPane>load();


            sidePaneLeft.setSpacing(20); //laver mellemrum mellem objekterne i VBox'en.
            sidePaneLeft.setPadding(new Insets(5, 5, 10, 10)); //Sætter objekternes afstand fra kanterne

            sidePaneRight.setSpacing(20); //laver mellemrum mellem objekterne i VBox'en.
            sidePaneRight.setPadding(new Insets(5, 10, 10, 5)); //Sætter objekternes afstand fra kanterne

            centerView.setPadding(new Insets(0, 5, 0, 5)); //Sætter objekternes afstand fra kanterne

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(rootLayout);
        inputPrimaryStage.setScene(scene);
        inputPrimaryStage.show();
        inputPrimaryStage.setFullScreen(false);
    }

    public void initBasicLayout(){

        try {   //Practitioner
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(PractitionerCtrl.class.getResource("/ch/view/PractitionerView.fxml"));
                HBox practitionerView = (HBox) loader.load();

                sidePaneLeft.getChildren().add(practitionerView);

                PractitionerCtrl controller = loader.getController();
                controller.setMainApp(mainAppRef);
                controller.showPractitioner();


            } catch (IOException e) {
                e.printStackTrace();
            }


        try {  //Patient
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PatientCtrl.class.getResource("/ch/view/PatientView.fxml"));
            AnchorPane patientView = (AnchorPane) loader.load();

            sidePaneLeft.getChildren().add(patientView);

            PatientCtrl controller = loader.getController();
            controller.setMainApp(mainAppRef);
            controller.setPatient();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {  //Medication
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MedicationCtrl.class.getResource("/ch/view/MedicationView.fxml"));
            VBox MedicationView = (VBox) loader.load();

            sidePaneLeft.getChildren().add(MedicationView);

            MedicationCtrl controller = loader.getController();
            controller.setMainApp(mainAppRef);
            controller.setMedication();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {  //Condition
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ConditionCtrl.class.getResource("/ch/view/ConditionView.fxml"));
            VBox conditionView = (VBox) loader.load();

            sidePaneRight.getChildren().add(conditionView);

            ConditionCtrl controller = loader.getController();
            controller.setMainApp(mainAppRef);
            controller.setCondition();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {  //AllergyIntolerance
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AllergyIntoleranceCtrl.class.getResource("/ch/view/AllergyIntoleranceView.fxml"));
            VBox allergyIntoleranceView = (VBox) loader.load();

            sidePaneRight.getChildren().add(allergyIntoleranceView);

            AllergyIntoleranceCtrl controller = loader.getController();
            controller.setMainApp(mainAppRef);
            controller.setAllergyIntolerance();

        } catch (IOException e) {
            e.printStackTrace();
        }


      //  AllergyIntoleranceCtrl allergyIntoleranceCtrl = new AllergyIntoleranceCtrl();
      //  this.sidePaneRight = allergyIntoleranceCtrl.showAllergyIntolerance(sidePaneRight);
    }

    public void showOverview() {
        OverviewCtrl overviewCtrl = new OverviewCtrl();
        try {
            FXMLLoader loader = new FXMLLoader();
            //Tabellen med hyppigst, der skal sættes i den højre sidebar.
            loader.setLocation(getClass().getResource("/ch/view/OverviewView.fxml"));
            loader.setController(overviewCtrl);
            AnchorPane overview = loader.load();
            centerView.getChildren().setAll(overview);
            //centerView.setFillWidth(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader();
        if (!sidePaneRight.getChildren().contains(mostFrequentTable)) {
            try {
                //Tabellen med hyppigst, der skal sættes i den højre sidebar.
                loader.setLocation(getClass().getResource("/ch/view/MostFrequentTable.fxml"));
                //OBS: Her sætter vi selv ctrl da vi ikke kan sætte det i FMLX-filen, da denne controller styrer 2 FMXML-filer
                loader.setController(overviewCtrl);
                mostFrequentTable = (VBox) loader.load();

                //Da man skifte frem og tilbage ml. weekly og overview sørger vi her for at mostFrequent-tabellen ikke tilføjes
                // igen hver gang så der kommer flere end én.

                sidePaneRight.getChildren().add(mostFrequentTable);

                overviewCtrlRef = loader.getController();
                overviewCtrlRef.setRootLayoutCtrlRef(this);
                overviewCtrlRef.setMainApp(this.mainAppRef);
                overviewCtrlRef.showData();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void showWeeklyOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ch/view/WeeklyOverviewView.fxml"));
            //loader.setController(WeeklyOverviewCtrl);
            AnchorPane weeklyOverview = loader.load();

            centerView.getChildren().setAll(weeklyOverview);

            WeeklyOverviewCtrl controller = loader.getController();
            controller.setRootLayoutCtrlRef(this);
            controller.setMainApp(this.mainAppRef);
           controller.showData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCreateAsthmaAppUserView(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CreateAsthmaAppUserCtrl.class.getResource("/ch/view/CreateAsthmaAppUserView.fxml"));
            AnchorPane createAstmaAppUserView = (AnchorPane) loader.load();

            centerView.getChildren().setAll(createAstmaAppUserView);

            CreateAsthmaAppUserCtrl controller = loader.getController();
            controller.setMainApp(mainAppRef);
            controller.setRootLayoutCtrlRef(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSummaryView(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SummaryCtrl.class.getResource("/ch/view/Summary.fxml"));
            AnchorPane summaryView = (AnchorPane) loader.load();

            //skal laves om da der istedet skal laves en bottompane.
            rootLayout.setBottom(summaryView);
            BorderPane.setAlignment(summaryView, Pos.BOTTOM_CENTER);

            SummaryCtrl controller = loader.getController();
            controller.setMainApp(mainAppRef);
            controller.setRootLayoutCtrlRef(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public VBox getSidepaneLeft(){
        return this.sidePaneLeft;
    }
    public VBox getSidepaneRight(){
        return this.sidePaneRight;
    }
   // public HBox getCenterView(){ return this.centerView; }
    public BorderPane getRootLayout(){return rootLayout;}
    public MainApp getMainAppRef(){return this.mainAppRef;}

}
