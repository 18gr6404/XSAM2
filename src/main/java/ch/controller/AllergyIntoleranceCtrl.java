package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.dbControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.Type;

import java.io.IOException;
import java.util.List;

public class AllergyIntoleranceCtrl{

    @FXML
    private ListView<String> allergyListView;
    @FXML
    private ListView<String> intoleranceListView;

    // Reference to the main application.
    private MainApp mainAppRef;
    private List<List<AllergyIntolerance>> allergyIntoleranceList;
    private ObservableList allergyList = FXCollections.observableArrayList();
    private ObservableList intoleranceList = FXCollections.observableArrayList();

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public AllergyIntoleranceCtrl() {
    }

    /**
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */

    private void initialize() {
    }

    public void setAllergyIntolerance() {

        dbControl dbControlOb = dbControl.getInstance();

        allergyIntoleranceList = dbControlOb.buildAllergyIntoleranceData(mainAppRef.getPatientCPR());

        AllergyIntolerance.AllergyIntoleranceType a = AllergyIntolerance.AllergyIntoleranceType.ALLERGY;
        AllergyIntolerance.AllergyIntoleranceType b = AllergyIntolerance.AllergyIntoleranceType.INTOLERANCE;
        int largestSize = 1;
        if (allergyIntoleranceList.size() > 0){
            for (int j = 0; j<allergyIntoleranceList.size(); j++)
                if (largestSize < allergyIntoleranceList.get(j).size())
                largestSize = allergyIntoleranceList.get(j).size();
    }
        for (int i = 0; i<allergyIntoleranceList.size(); i++) {
            for (int j = 0; j < largestSize; j++) {
                if (allergyIntoleranceList.get(i).get(j).getType().equals(a)) {
                    allergyList.add(allergyIntoleranceList.get(i).get(j).getCode().getCoding().get(0).getCode());
                } else if (allergyIntoleranceList.get(i).get(j).getType().equals(b)) {
                    intoleranceList.add(allergyIntoleranceList.get(i).get(j).getCode().getCoding().get(0).getCode());
                }
            }
        }
            allergyListView.setItems(allergyList);
            intoleranceListView.setItems(intoleranceList);
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
