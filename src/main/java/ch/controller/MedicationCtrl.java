package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.dbControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.MedicationRequest;

import java.io.IOException;
import java.util.List;

public class MedicationCtrl {

    // Reference to the main application.
    private MainApp mainAppRef;
    private List<MedicationRequest> medicationReguestList;
    private ObservableList medicationList = FXCollections.observableArrayList();

    @FXML
    private ListView<String> medicationListView;



    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public MedicationCtrl() { }

    /**
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    public void setMedication(){

        dbControl dbControlOb = dbControl.getInstance();

        medicationReguestList = dbControlOb.buildMedicineData(mainAppRef.getPatientCPR());

        for (int i = 0; i<medicationReguestList.size(); i++){
            medicationList.add(medicationReguestList.get(i).getMedication());
            medicationList.add(medicationReguestList.get(i).getDosageInstruction().get(0).getDose());
            medicationList.add(medicationReguestList.get(i).getAuthoredOn());
        }

        medicationListView.setItems(medicationList);

      //  typeLabel.setText(String.valueOf(medicationObject.getMedication()));
      //  dosisLabel.setText(String.valueOf(medicationObject.getDosageInstruction().get(0).getDose()));
      //  dateLabel.setText(String.valueOf(medicationObject.getAuthoredOn()));

        //System.out.println(medicationRequest.getMedication() +","+ medicationRequest.getDosageInstruction().get(0).getDose()+","+ medicationRequest.getAuthoredOn());
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
