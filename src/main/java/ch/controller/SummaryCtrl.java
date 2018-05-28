package ch.controller;

import ch.MainApp;
import ch.db_And_FHIR.dbControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.hl7.fhir.dstu3.model.ClinicalImpression;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public class SummaryCtrl {

    // Reference to the main application.
    private MainApp mainAppRef;
    private RootLayoutCtrl rootLayoutCtrlRef;

    @FXML
    private TextArea summaryField;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public SummaryCtrl() {
    }

    /**
     * Initializes the ch.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */

    private void initialize() {

    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            int cpr = mainAppRef.getPatientCPR();
            int practitionerID = mainAppRef.getPractitionerID();

            ClinicalImpression summary = new ClinicalImpression();
            summary.setSummary(summaryField.getText());

            Date dateIn = new Date();
            LocalDateTime ldt = LocalDateTime.ofInstant(dateIn.toInstant(), ZoneId.systemDefault());
            Date dateOut = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

            summary.setDate(dateOut);


            dbControl dbControlOb = dbControl.getInstance();

            dbControlOb.insertSummary(summary, cpr, practitionerID);

            rootLayoutCtrlRef.getRootLayout().setBottom(null);

        }
    }

        @FXML
        private void handleCancel () {
            rootLayoutCtrlRef.getRootLayout().setBottom(null);
        }

        @FXML
        private void handlePrintSelectedData () {

        }

        private boolean isInputValid () {
            String errorMessage = "";

            // || !StringUtils.isStrictlyNumeric(ConsultationMeasurementTextField.getText())
            if (summaryField.getText() == null || summaryField.getText().length() == 0) {
                errorMessage += "Ugyldig indtastning\n";
            }

            if (errorMessage.length() == 0) {
                return true;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(mainAppRef.getPrimaryStage());
                alert.setTitle("Ugyldig indtastning");
                alert.setHeaderText("Udfyld konsultationsnotat for at bekræfte");
                alert.setContentText(errorMessage);
                alert.showAndWait();

                return false;
            }
        }

        /**
         * Is called to give a reference back to itself.
         *
         * @param inputRootLayoutCtrl
         */
        public void setRootLayoutCtrlRef (RootLayoutCtrl inputRootLayoutCtrl){
            this.rootLayoutCtrlRef = inputRootLayoutCtrl;
        }

        /**
         * Is called by the main application to give a reference back to itself.
         *
         * @param inputMain
         */
        public void setMainApp (MainApp inputMain){
            this.mainAppRef = inputMain;
        }

    }

