package ch;

import ch.controller.*;
import ch.db_And_FHIR.*;
import ch.model.EncapsulatedParameters;
import ch.model.OverviewParameters;
import ch.model.WeeklyParameters;
import ch.utility.dateUtil;
import ch.controller.CreateAsthmaAppUserCtrl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sun.plugin.javascript.navig.Anchor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MainApp extends Application {

    private Stage primaryStage;
  
    // mia OVergaard = 1405936524
    // Emma Dahl = 1906982274
    // Lazarus Fisker = 2002802067
    //
    private Integer patientCPR = 1207731450; //Marianne. Daniel vil gerne = 1207731470
    //private Integer patientCPR = 1303803813;  //Jens. Daniel vil gerne = 1303803823
    //private Integer patientCPR = 2002802067;
    private Integer practitionerID = 56789; // Ole Bosen


    /**
     * Constructor
     */
    public MainApp() {

    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Sundhedsappmodul");


        FhirControl FhirClass = FhirControl.getInstance();
        FhirClass.startCtx();

        dbControl myDBClass = dbControl.getInstance();
        myDBClass.startConnection();

        RootLayoutCtrl rootLayoutCtrlRef = new RootLayoutCtrl(this);
        rootLayoutCtrlRef.initRootLayout(this.primaryStage);


        /*
        CalculatedParametersCtrl calcParam = new CalculatedParametersCtrl();
        // HER HENTER JEG DE UDREGNEDE PARAMETRE
        EncapsulatedParameters beggeParam = calcParam.buildCalculatedParameters(1207731470, startDate, endDate); // Marianne CPR på FHIR Server = 1207731470

        OverviewParameters OverviewParam = beggeParam.getOverviewParameters();

        WeeklyParameters WeeklyOverviewParam = beggeParam.getWeeklyParameters();
        WeeklyOverviewParam.getUgeListeDagSymptomer().get(0);


        System.out.println(OverviewParam.getAvgFEV1()); */
        boolean isAppData;
        Integer FHIR = 0;
        boolean isRegistered = myDBClass.requestIsRegistered(patientCPR);
        if (patientCPR.equals(1207731450)){
            FHIR = patientCPR + 20;
        }
        else if (patientCPR.equals(1303803813)) {
            FHIR = patientCPR + 10;
        }
        if (FHIR != 0)
            isAppData = FhirClass.requestIsAppData(FHIR.toString());
        else
            isAppData = FhirClass.requestIsAppData(patientCPR.toString());

        if(isRegistered && isAppData) {
            rootLayoutCtrlRef.initBasicLayout();
            rootLayoutCtrlRef.showOverview();


        }
        else if (!isRegistered && isAppData){
            /**
             * Denne advarsel skal også forekomme i Create AsthmaAppUserCtrl, hvor programmet skal termineres.
             */
            if (!isAppData){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(primaryStage);
                alert.setTitle("Ingen Appdata");
                alert.setHeaderText("Brugeren skal have mindst én uges astmaappdata");
                alert.setContentText("Denne patient har ikke astmaappdata!");
                alert.showAndWait();
            }

            //denne sætter basicLayout og overviewView efter patienten er oprettet
            rootLayoutCtrlRef.showCreateAsthmaAppUserView();

        }
        else if (!isAppData && isRegistered){

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(primaryStage);
                alert.setTitle("Ingen Appdata");
                alert.setHeaderText("Brugeren skal have mindst én uges astmaappdata");
                alert.setContentText("Denne patient har ikke astmaappdata!");
                alert.showAndWait();
                rootLayoutCtrlRef.initBasicLayout();
        }

    }


    public Integer getPatientCPR() {return patientCPR; }

    public Integer getPractitionerID() { return practitionerID; }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}





