package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.dbControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.dstu3.model.Condition;

import java.io.IOException;
import java.util.List;

public class ConditionCtrl {
    @FXML
    private ListView<String> conditionListView;

    // Reference to the main application.
    private MainApp mainAppRef;
    private List<Condition> conditionList;
    private ObservableList comorbidityNameList = FXCollections.observableArrayList();

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ConditionCtrl() { }

    /**
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */

    private void initialize() {

    }

    public void setCondition(){

        dbControl dbControlOb = dbControl.getInstance();

        conditionList = dbControlOb.buildConditionData(mainAppRef.getPatientCPR());

        for (int i = 0; i<conditionList.size(); i++){
            comorbidityNameList.add(String.valueOf(conditionList.get(i).getCode().getCoding().get(0).getCode()));
        }
        conditionListView.setItems(comorbidityNameList);
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
