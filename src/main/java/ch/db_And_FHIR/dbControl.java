package ch.db_And_FHIR;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.ObservableList;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;


public class dbControl {


    //Opretter forbindelse til vores database

    //?autoReconnect=true
    static String dbAdress = "jdbc:mysql://db.course.hst.aau.dk:3306/hst_2018_18gr6404?&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    static String dbUsername = "hst_2018_18gr6404";         // UNI-NET DETALJER
    static String dbPassword = "eenuathaiheugohxahmo";
    private Connection con;
    /**
     * db Control er lavet som en "SingleTon" Hvilket betyder at der kun laves én instans af klassen
     * dvs. at hvis vi kører startCon i main på det nyligt oprettede objekt, så behøver vi ikke køre den igen
     * i calculated Parameters. (Det gør ikke noget at kalde den, men den gør ikke noget)
     */
    private static dbControl instance;
    public static dbControl getInstance(){
        if (instance ==null)
            instance = new dbControl();
        return instance;
    }


    public void startConnection() {


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("The class was not found");
            //return null;
        }

        try {
            con = DriverManager.getConnection(dbAdress, dbUsername, dbPassword);
            System.out.println("Connection established");
        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.getMessage());
        }
    }

    public void setAsthmaAppUser(Integer patientCPR, String choosenAppInput, Integer isRegisteredInput, Integer pastDataWantedInput) {

        LocalDateTime dateTimeInput = LocalDateTime.now();
          try{
            String SQL = "UPDATE App SET choosenApp = '" + choosenAppInput + "'," +
                    " dateTime = '" + dateTimeInput + "'," +
                    " isRegistered = '" + isRegisteredInput + "'," +
                    " pastDataWanted = '" + pastDataWantedInput+ "'" +
                    "WHERE cpr = '" + patientCPR + "'";
            int rows = con.createStatement().executeUpdate(SQL, 1);
            if (rows > 0)
                System.out.println("Updated!");
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error on updating Data");
        }


    }

    public boolean requestIsRegistered(Integer patienCPR){


        int requestedIsRegistered= 0;

        Statement stmnt = null;
        try{
            String SQL = "SELECT isRegistered FROM App WHERE cpr = '" + patienCPR + "'";
            //ResultSet rs = con.createStatement().executeQuery(SQL);
            stmnt = con.createStatement();
            ResultSet rs = stmnt.executeQuery(SQL);

            while (rs.next()) {
                int isRegistered = rs.getInt("isRegistered");

                System.out.print("Fra DB får vi isRegistered =" + isRegistered);
                requestedIsRegistered = isRegistered;
            }

        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error fetching isRegistered");
        }


       if(requestedIsRegistered == 0){
            return false;
        }
        else return true;

    }

    public Practitioner buildPractitionerData(Integer practitionerID) {
        Statement stmnt = null;

        Practitioner p = null;
        String query = "SELECT firstName, lastName, practitionerID FROM Practitioner WHERE practitionerID=" + practitionerID;
     //   String practitionerID = practitionerIDET.toString();
        try {
            stmnt = con.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            while (rs.next()) {
                Practitioner practitioner = new Practitioner();
                practitioner.addIdentifier().setValue(String.valueOf(rs.getInt("practitionerID")));
                practitioner.addName().setFamily(rs.getString("lastName")).addGiven(rs.getString("firstName"));
                p = practitioner;
            //    System.out.println(practitioner.getName().get(0).getFamily() + practitioner.getName().get(0).getGivenAsSingleString() + practitioner.getIdentifier().get(0).getValue() );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return p;
    }

    public List<List<AllergyIntolerance>> buildAllergyIntoleranceData(int patientCPR) {

        Statement stmntA = null;
        Statement stmntI = null;
        List<AllergyIntolerance> allergyList = new ArrayList<>();
        List<AllergyIntolerance> intoleranceList = new ArrayList<>();
        List<List<AllergyIntolerance>> allergyintoleranceList = new ArrayList<>();

        String queryA = "SELECT name FROM Allergy WHERE cpr=" + patientCPR;
        String queryI = "SELECT name FROM Intolerance WHERE cpr=" + patientCPR;
        try {
            stmntA = con.createStatement();
            stmntI = con.createStatement();
            ResultSet rsA = stmntA.executeQuery(queryA);
            ResultSet rsI = stmntI.executeQuery(queryI);
            while (rsA.next()) {

                AllergyIntolerance allergy = new AllergyIntolerance();
                allergy.setType(AllergyIntolerance.AllergyIntoleranceType.ALLERGY);
                allergy.getCode().addCoding()
                        .setCode(rsA.getString("name")) // Normalt er det en talkode
                        .setDisplay(rsA.getString("name")); // Her vises talkoden som den tekst man ønsker at vise i stedet
                allergyList.add(allergy);

//                String name = rs.getString("name");
//                int cpr = rs.getInt("cpr");
                //System.out.println(name + ", "+ cpr);

              //  System.out.println(allergy.getCode().getCoding().get(0).getCode());
            }

            while (rsI.next()) {
                AllergyIntolerance intolerance = new AllergyIntolerance();
                intolerance.setType(AllergyIntolerance.AllergyIntoleranceType.INTOLERANCE);
                intolerance.getCode().addCoding()
                        .setCode(rsI.getString("name"))
                        .setDisplay(rsI.getString("name"));
                intoleranceList.add(intolerance);

               // System.out.println(intolerance.getCode().getCoding().get(0).getCode());
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (!allergyList.isEmpty())
            allergyintoleranceList.add(allergyList);
        if (!intoleranceList.isEmpty())
            allergyintoleranceList.add(intoleranceList);
        return allergyintoleranceList;
    }

        public void insertSummary(ClinicalImpression summary, Integer patientCPR, Integer practitionerID) {

        String query = "INSERT INTO Summary (cpr, dateTime, text, practitionerID) values ("
                + "'" + patientCPR + "',"
                + "'" + Instant.ofEpochMilli(summary.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() + "',"
                + "'" + summary.getSummary() + "',"
                + "'" + practitionerID + "')"
                ;

        try { //Noget Daniel har brugt til at teste det.
            int rows = con.createStatement().executeUpdate(query, 1);
          /*  if (rows>0)
            //System.out.println("Succes!");
            else
            throw new RuntimeException();*/

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Condition> buildConditionData(int patientCPR) {

        Statement stmnt = null;
        List<Condition> conditionList = new ArrayList<>();
        String query = "SELECT name FROM Diagnosis WHERE cpr=" + patientCPR;

        try {
            stmnt = con.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            while (rs.next()) {
                Condition condition = new Condition();
                condition.getCode().addCoding()
                        .setCode(rs.getString("name"))
                        .setDisplay(rs.getString("name"));
                conditionList.add(condition);

              //  System.out.println(condition.getCode().getCoding().get(0).getCode());

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conditionList;
    }

    public Patient buildPatientData(int patientCPR) {

        Statement stmnt = null;
        Patient p = null;
        String query = "SELECT cpr, firstName, lastName, age, gender FROM Patient WHERE cpr=" + patientCPR;

        try {
            stmnt = con.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            while (rs.next()) {
                Patient patient = new Patient();


                //Ny extension til alder
                //Extension ext3 = new Extension();
                //ext3.setUrl("age");
                //ext3.setValue(new Quantity(rs.getInt("age")));
                //patient.addExtension(ext3);

                patient.addIdentifier().setValue(String.valueOf(rs.getInt("cpr"))); //Vi beregner alder i patientCtrl ud fra cpr.
                patient.addName().setFamily(rs.getString("lastName")).addGiven(rs.getString("firstName"));
               if (rs.getString("gender").equals("K")) {
                   patient.setGender(Enumerations.AdministrativeGender.FEMALE);
               }
               else {
                   patient.setGender(Enumerations.AdministrativeGender.MALE);
               }
               p = patient;
             //  System.out.println(patient.getExtension().get(0).getExtensionFirstRep().

              //  System.out.println(patient.getGender());
                //System.out.println(patient.getIdentifier().get(0).getValue() + "," + patient.getName().get(0).getGivenAsSingleString() + " " + patient.getName().get(0).getFamily());

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return p;
    }

    public List<MedicationRequest> buildMedicineData(int patientCPR) {

        Statement stmnt = null;
        List<MedicationRequest> medicationRequestList = new ArrayList<>();
        String query = "SELECT type, dosage, dateTime FROM Medication WHERE cpr=" + patientCPR;

        try {
            stmnt = con.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            while (rs.next()) {
                MedicationRequest medicationRequest = new MedicationRequest();
                medicationRequest.setMedication(new StringType(rs.getString("type")));
                medicationRequest.addDosageInstruction().setDose(new StringType(rs.getString("dosage")));
                medicationRequest.setAuthoredOn(rs.getDate("dateTime"));
                medicationRequestList.add(medicationRequest);

               //System.out.println(medicationRequest.getMedication() +","+ medicationRequest.getDosageInstruction().get(0).getDose()+","+ medicationRequest.getAuthoredOn());
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return medicationRequestList;
    }

    public List<Observation> buildFEV(int patientCPR) {

        Statement stmnt = null;
        String query = "SELECT value, dateTime FROM Fev1 WHERE cpr=" + patientCPR;
        List<Observation> fev1List = new ArrayList<>();
        try {
            stmnt = con.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            while (rs.next()) {
                Observation Fev1 = new Observation();
                Fev1.setValue((new Quantity(rs.getFloat("value"))));
                Fev1.setIssued(rs.getDate("dateTime"));

                fev1List.add(Fev1);
                try{
                    System.out.println(Fev1.getValueQuantity().getValue() + "," +  "," + Fev1.getIssued());
                }catch(FHIRException e){
                    System.out.println(e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return fev1List;
    }

    public void insertfev1(Observation fev1, Integer patientCPR, Integer practitionerID){

        String query = "INSERT INTO Fev1(cpr, dateTime, value, practitionerID) values ("
                + "'" + patientCPR + "',"
                + "'" + Instant.ofEpochMilli(fev1.getIssued().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() + "',"
                + "'" + fev1.getValue() + "',"
                + "'" + practitionerID + "')"
                ;



        try {
            int rows = con.createStatement().executeUpdate(query, 1);
            /*if (rows>0)
                //System.out.println("Succes!");
            else
                throw new RuntimeException();*/

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LocalDate getLatestConsultationDate(Integer patientCPR){

        Statement stmnt = null;
        List<ClinicalImpression> clinicalImpressionDateList = new ArrayList<>();
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate localDate = null;
        String query = "SELECT dateTime FROM Summary WHERE cpr=" + patientCPR;

        try {
            stmnt = con.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            while (rs.next()) {
                ClinicalImpression clinicalImpression = new ClinicalImpression();
                clinicalImpression.setDate(rs.getDate("dateTime"));
                clinicalImpressionDateList.add(clinicalImpression);
                }
            for (int i = 0; i<clinicalImpressionDateList.size(); i++) {
                dateList.add(Instant.ofEpochMilli(clinicalImpressionDateList.get(i).getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            }
            for (int i = 0; i<dateList.size(); i++) {
                LocalDate testDate = dateList.get(0);
                if (dateList.get(i).isAfter(testDate) || dateList.get(i).equals(testDate)) {
                    localDate = dateList.get(i);
                    i++;
                }

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(localDate);
        return localDate;
    }

    }










