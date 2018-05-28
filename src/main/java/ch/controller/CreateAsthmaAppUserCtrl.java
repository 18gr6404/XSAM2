package ch.controller;

import ch.MainApp;
import ch.controller.RootLayoutCtrl;
import ch.db_And_FHIR.dbControl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;


public class CreateAsthmaAppUserCtrl {

    @FXML
    private ChoiceBox chosenAppDropdown;

    @FXML
    private CheckBox pastDataWantedCheckbox;

    // Reference to the main application.
    private MainApp mainAppRef;
    private RootLayoutCtrl rootLayoutCtrlRef;


    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public CreateAsthmaAppUserCtrl() {
    }

    /*
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        chosenAppDropdown.setValue("AstmaApp");
        //Nedenfor tilføjes muligheder i dropdown-listen. - Vi har kun AstmaApp.
        chosenAppDropdown.setItems(FXCollections.observableArrayList("AstmaApp"));
    }


    public int getPastDataWantedCheckBox (){
        if (pastDataWantedCheckbox.isSelected()){
            return 1;
        }
        else return 0;
    }

    /**
     * Kaldes når brugeren klikker på "Bekræft" i CreateAsthmaAppUserView.
     */

    @FXML
    private void handleOk (ActionEvent event) throws IOException {
        dbControl dbControlOb = dbControl.getInstance();

        String choosenAppInput = (String) chosenAppDropdown.getValue();
        int isRegisteredInput = 1;
        int pastDataWantedInput = getPastDataWantedCheckBox();


        Integer patientCPR = mainAppRef.getPatientCPR();

        //skal indkommenteres hvis vi vil sætte isRegistered i DB. Det er dog træls når man tester
        dbControlOb.setAsthmaAppUser(patientCPR, choosenAppInput, isRegisteredInput, pastDataWantedInput);

        //Henter den stage som actionevent'et (altså knap-trykket) kommer fra.

        //Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        //RootLayoutCtrl rootLayoutCtrlRef = new RootLayoutCtrl();
        //rootLayoutCtrlRef.initRootLayout(window);

        rootLayoutCtrlRef.initBasicLayout();

        //Vis showOverview er i rootlayout.
        rootLayoutCtrlRef.showOverview();
    }


    /**
     * Is called to give a reference back to itself.
     *
     * @param inputRootLayoutCtrl
     */
    public void setRootLayoutCtrlRef(RootLayoutCtrl inputRootLayoutCtrl) {
        this.rootLayoutCtrlRef = inputRootLayoutCtrl;
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param inputMain
     */
    public void setMainApp(MainApp inputMain) {
        this.mainAppRef = inputMain;
    }


}
