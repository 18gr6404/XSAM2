package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.dbControl;
import ch.utility.dateUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.hl7.fhir.dstu3.model.Patient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;


public class PatientCtrl {
    // Reference to the main application.
    private MainApp mainAppRef;
    private Patient patientObject;

    @FXML
    private Label nameLabel;
    @FXML
    private Label cprLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label genderLabel;


    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PatientCtrl() {}


    /**
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    public void setPatient(){

        dbControl dbControlOb = dbControl.getInstance();

        patientObject = dbControlOb.buildPatientData(mainAppRef.getPatientCPR());

        String day, month, year, birthdateString;


        day = (patientObject.getIdentifier().get(0).getValue()).substring(0,2);
        month = (patientObject.getIdentifier().get(0).getValue()).substring(2,4);
        if(Integer.parseInt(patientObject.getIdentifier().get(0).getValue().substring(4,6)) > 67 )
            year ="19" + patientObject.getIdentifier().get(0).getValue().substring(4,6);
        else
            year = "20" + patientObject.getIdentifier().get(0).getValue().substring(4,6);
        birthdateString = day + "." + month + "." + year;
        LocalDate birthdate = dateUtil.parse(birthdateString);
        Period age = Period.between(birthdate, LocalDate.now());
        birthdateString = String.valueOf(age.getYears());
     //   LocalDate birthDate = LocalDate.parse("04.04.2018");
     //   LocalDate today = LocalDate.now();
     //   long age1 = ChronoUnit.YEARS.between(today, birthDate);
        nameLabel.setText(patientObject.getName().get(0).getGivenAsSingleString() +" "+ patientObject.getName().get(0).getFamily());
        cprLabel.setText(patientObject.getIdentifier().get(0).getValue());
        ageLabel.setText(birthdateString);



       // LocalDate birthdaten = LocalDate.parse(birthdate);
       // LocalDate today = LocalDate.now();
       // long age1 = ChronoUnit.YEARS.between(today, birthdaten);
      //  String cpr = patientObject.getIdentifier().get(0).getValue();

        if (String.valueOf(patientObject.getGender()).equals("FEMALE")) {
            genderLabel.setText("K");
        }
        else if (String.valueOf(patientObject.getGender()).equals("MALE")) {
            genderLabel.setText("M");
        }
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
