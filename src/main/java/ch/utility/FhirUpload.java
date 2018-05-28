package ch.utility;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.mysql.cj.result.LocalDateValueFactory;
import org.hl7.fhir.dstu3.model.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Klasse til upload af FHIR patienter og observationer
 */
public class FhirUpload {

    public static void main(String[] args) throws IOException {

        // We're connecting to a DSTU3 compliant server
        FhirContext ctx = FhirContext.forDstu3();

        // TestServer adresse http://vonk.fire.ly/
        String serverBase = "http://hapi.fhir.org/baseDstu3";

        // Oprettelse af klient til tilgang af serveren (Klient tilgår server)
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        // CPR Marianne: 120773-1450
        // CPR Jens: 130380-3813
        String CPRtest = "2002802067";
        String MarianneCPR = "1207731470";
        String JensCPR = "1303803833";

        String startString = "21.05.2018";
        String slutString = "25.05.2018";

        LocalDate startDate = dateUtil.parse(startString);
        LocalDate endDate = dateUtil.parse(slutString);

        //Put Marianne
        //putMarianne(client, CPRtest);
        //putJens(client, JensCPR);

        // Se "generateObservations" metoden for at ændre antallet af observationer, som uploades
        generateObservations(CPRtest, client, startDate, endDate);
        //generateObservations(JensCPR, client, startDate, endDate);


    }
        /**
         * Metoden bruges kun til at skubbe data op til serveren, og dette skal kun gøres én gang for hver server reset
         */
        private static void putMarianne(IGenericClient client, String patientIdentifier){
            // Skal kun kaldes såfremt der ikke er en FhirContext aktiv i det pågældende stykke kode

            //Mariannes detaljer
            Patient Marianne = new Patient();

            Marianne.addIdentifier().setSystem("urn:https://www.cpr.dk/cpr-systemet/opbygning-af-cpr-nummeret/").setValue(patientIdentifier);
            Marianne.addName().setFamily("Fisker").addGiven("Lazarus");
            Marianne.setGender(Enumerations.AdministrativeGender.FEMALE);
            LocalDate dato = dateUtil.parse("24.02.1980");
            Marianne.setBirthDate(java.sql.Date.valueOf(dato));

            Marianne.getName().get(0).getGiven();
            //Index 0, Følgende kode får fat i en extension Marianne.getExtension().getId(Index)
            Extension ext = new Extension();
            ext.setUrl("Is Registered?");
            ext.setValue(new BooleanType(true));
            Marianne.addExtension(ext);
            LocalDate dato1 = dateUtil.parse("10.04.2018");


            //Index 1
            Extension ext1 = new Extension();
            ext1.setUrl("Creation Date");
            ext1.setValue(new DateType(java.sql.Date.valueOf(dato1)));
            Marianne.addExtension(ext1);

            //Index 2
            Extension ext2 = new Extension();
            ext2.setUrl("Chosen App");
            ext2.setValue(new StringType("Astma App"));
            Marianne.addExtension(ext2);



            MethodOutcome outcome1 = client.create()
                    .resource(Marianne)
                    .prettyPrint()
                    .encodedJson()
                    .execute();
        }

    private static void putJens(IGenericClient client, String patientIdentifier){
        // Skal kun kaldes såfremt der ikke er en FhirContext aktiv i det pågældende stykke kode

        //Jens detaljer
        Patient Jens = new Patient();
        Jens.addIdentifier().setSystem("urn:https://www.cpr.dk/cpr-systemet/opbygning-af-cpr-nummeret/").setValue(patientIdentifier);
        Jens.addName().setFamily("Hansen").addGiven("Jens");
        Jens.setGender(Enumerations.AdministrativeGender.MALE);
        LocalDate dato = dateUtil.parse("13.03.1980");
        Jens.setBirthDate(java.sql.Date.valueOf(dato));

        Jens.getName().get(0).getGiven();
        //Index 0, Følgende kode får fat i en extension Marianne.getExtension().getId(Index)
        Extension ext = new Extension();
        ext.setUrl("Is Registered?");
        ext.setValue(new BooleanType(false));
        Jens.addExtension(ext);

        LocalDate dato1 = dateUtil.parse("04.04.2018");

        //Index 1
        /**
         * Not Applicable to Jens
        Extension ext1 = new Extension();
        ext1.setUrl("Creation Date");
        ext1.setValue(new DateType(java.sql.Date.valueOf(dato1)));
        Jens.addExtension(ext1);
        */
        //Index 2
        Extension ext2 = new Extension();
        ext2.setUrl("Chosen App");
        ext2.setValue(new StringType("Astma App"));
        Jens.addExtension(ext2);

        MethodOutcome outcome1 = client.create()
                .resource(Jens)
                .prettyPrint()
                .encodedJson()
                .execute();
    }

        /**
         * Bruges til at generere observationer
         */
        private static void generateObservations(String patientIdentifier, IGenericClient client, LocalDate startDate, LocalDate endDate){

            Bundle results = client
                    .search()
                    .forResource(Patient.class)
                    .where(Patient.IDENTIFIER.exactly().identifier(patientIdentifier))
                    .returnBundle(Bundle.class)
                    .execute();

//            System.out.println(results.getEntry().get(0).getResource().getId());
            // Læs enkelt patient ind i et patient objekt (Kan kun gøres med ID, altså IKKE Identifier):
            Patient searchedPatient = client
                    .read()
                    .resource(Patient.class)
                    .withId(results.getEntry().get(0).getResource().getId())
                    .execute();
            //System.out.println(searchedPatient.getExtension().get(0).getValue());


            //Kalder metoderne for observations


            List<Observation> aktivitetsList =  generateAktivitetsbegraensning(searchedPatient, startDate, endDate, 1);
            List<Observation> anfaldOgTrigger =  generateAnfaldOgTrigger(searchedPatient, startDate, endDate, 1); //Antal Obs * 2
            List<Observation> dagSymptom =  generateDagSymptom(searchedPatient, startDate, endDate, 3); //Antal Obs * 2 (i gennemsnit)
            List<Observation> natSymptom =  generateNatSymptom(searchedPatient, startDate, endDate,2);  //Antal Obs * 2 (i gennemsnit)
            //List<Observation> PEF =  generatePEF(searchedPatient, startDate, endDate, 7); //Antal Obs * 2

            client.transaction().withResources(aktivitetsList).execute();
            //client.transaction().withResources(anfaldOgTrigger).execute();
            client.transaction().withResources(dagSymptom).execute();
            client.transaction().withResources(natSymptom).execute();
            //client.transaction().withResources(PEF).execute();


        }

        /**
         * Genererer dagsymptomer tilfældigt for en patient. Op til 3 symptomer på én dag, og spreder dem ud på tilfældige datoer i et spænd
         * @param searchedPatient ID på patientet, som ønskes at oprette en anfald Trigger observation
         * @param startDate Start dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param endDate slut dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param antalObs antal observationer man vil have indenfor tidsintervallet, kan ikke være højere end datospændet
         * @return
         */
        private static List<Observation> generateDagSymptom(Patient searchedPatient, LocalDate startDate, LocalDate endDate, int antalObs){
            List<Observation> dagSymptom = new ArrayList<Observation>();
            List<LocalDate> dateList = getDatesBetween(startDate, endDate);
            List<LocalDate> randomElements = new ArrayList<LocalDate>();
            Type hvaesen = new StringType("Hvaesen");
            Type hosten = new StringType("Hosten");
            Type slimHoste = new StringType("Hoste m. slim");
            Type brystTrykken = new StringType("Trykken for brystet");
            Type aandeNoed = new StringType("Åndenød");

            List<Type> symptomListe = new ArrayList<Type>(
                    Arrays.asList(hvaesen, hosten, slimHoste, brystTrykken, aandeNoed));
            List<Observation> tempObs = new ArrayList<>();

            for(int i = 0; i< antalObs; i++){

                // Opretter 3 observationer
                for(int j = 0; j <3; j++) {
                    tempObs.add(new Observation());
                    tempObs.get(j).getCode().addCoding()
                            .setSystem("Dag/Nat Symptom")
                            .setCode("Dag Symptom")
                            .setDisplay("Dag Symptom");
                    tempObs.get(j).addIdentifier().setValue(searchedPatient.getIdentifier().get(0).getValue());
                    tempObs.get(j).setSubject(new Reference(searchedPatient.getId()));
                }        //System.out.println(searchedPatient.getIdentifier().get(0).getValue());
                // nextInt er ikke-inklusiv sit bound, derfor + 1
                int randomnum = ThreadLocalRandom.current().nextInt(1,3+1);
                Random rand = new Random();

                int randomIndex = rand.nextInt(dateList.size());
                randomElements.add(dateList.get(randomIndex));
                dateList.remove(randomIndex);

                if (randomnum == 1){
                    tempObs.get(0).setValue(randomReturnType(symptomListe,randomnum).get(0));
                    tempObs.get(0).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                    randomElements.clear();
                    dagSymptom.add(tempObs.get(0));
                    tempObs.clear();
                }else if(randomnum == 2){
                    for (int k = 0; k<randomnum; k++){
                        tempObs.get(k).setValue(randomReturnType(symptomListe,randomnum).get(k));
                        tempObs.get(k).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                        dagSymptom.add(tempObs.get(k));

                    }
                    randomElements.clear();
                    tempObs.clear();
                }else if(randomnum == 3){
                    for (int k = 0; k<randomnum; k++){
                        tempObs.get(k).setValue(randomReturnType(symptomListe,randomnum).get(k));
                        tempObs.get(k).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                        dagSymptom.add(tempObs.get(k));
                    }
                    randomElements.clear();
                    tempObs.clear();
                }

            }

            return dagSymptom;
        }

        /**
         * Genererer natsymptomer tilfældigt for en patient. Op til 3 symptomer på én nat, og spreder dem ud på tilfældige datoer i et spænd
         * @param searchedPatient ID på patientet, som ønskes at oprette en anfald Trigger observation
         * @param startDate Start dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param endDate slut dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param antalObs antal observationer man vil have indenfor tidsintervallet, kan ikke være højere end datospændet
         * @return
         */
        private static List<Observation> generateNatSymptom(Patient searchedPatient, LocalDate startDate, LocalDate endDate, int antalObs){
            List<Observation> natSymptom = new ArrayList<Observation>();
            List<LocalDate> dateList = getDatesBetween(startDate, endDate);
            List<LocalDate> randomElements = new ArrayList<LocalDate>();
            Type natHoste = new StringType("Hoste");
            Type opvaagning = new StringType("Opvågning");
            Type traethed = new StringType("Træthed");

            List<Type> symptomListe = new ArrayList<Type>(
                    Arrays.asList(natHoste, opvaagning, traethed));
            List<Observation> tempObs = new ArrayList<>();

            for(int i = 0; i< antalObs; i++){

                // Opretter 3 observationer
                for(int j = 0; j <3; j++) {
                    tempObs.add(new Observation());
                    tempObs.get(j).getCode().addCoding()
                            .setSystem("Dag/Nat Symptom")
                            .setCode("Nat Symptom")
                            .setDisplay("Nat Symptom");
                    tempObs.get(j).addIdentifier().setValue(searchedPatient.getIdentifier().get(0).getValue());
                    tempObs.get(j).setSubject(new Reference(searchedPatient.getId()));
                }        //System.out.println(searchedPatient.getIdentifier().get(0).getValue());
                // nextInt er ikke-inklusiv sit bound, derfor + 1
                int randomnum = ThreadLocalRandom.current().nextInt(1,3+1);
                Random rand = new Random();

                    int randomIndex = rand.nextInt(dateList.size());
                    randomElements.add(dateList.get(randomIndex));
                    dateList.remove(randomIndex);

                if (randomnum == 1){
                    tempObs.get(0).setValue(randomReturnType(symptomListe,randomnum).get(0));
                    tempObs.get(0).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                    randomElements.clear();
                    natSymptom.add(tempObs.get(0));
                    tempObs.clear();
                }else if(randomnum == 2){
                    for (int k = 0; k<randomnum; k++){
                        tempObs.get(k).setValue(randomReturnType(symptomListe,randomnum).get(k));
                        tempObs.get(k).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                        natSymptom.add(tempObs.get(k));
                    }
                    randomElements.clear();
                    tempObs.clear();
                }else if(randomnum == 3){
                    for (int k = 0; k<randomnum; k++){
                        tempObs.get(k).setValue(randomReturnType(symptomListe,randomnum).get(k));
                        tempObs.get(k).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                        natSymptom.add(tempObs.get(k));
                    }
                    randomElements.clear();
                    tempObs.clear();
                }

            }

            return natSymptom;
        }

        /**
         * Metode til generering af Anfald + Trigger observation
         * @param searchedPatient ID på patientet, som ønskes at oprette en anfald Trigger observation
         * @param startDate Start dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param endDate slut dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param antalObs antal observationer, som ønskes oprettet i det givne dato interval
         * @return returnerer en liste med observationer af typen Anfald, Trigger, Anfald, Trigger osv...
         */
        private static List<Observation> generateAnfaldOgTrigger(Patient searchedPatient, LocalDate startDate, LocalDate endDate, int antalObs) {
            List<Observation> anfaldOgTrigger = new ArrayList<Observation>();
            List<LocalDate> dateList = getDatesBetween(startDate, endDate);
            List<LocalDate> randomElements = new ArrayList<LocalDate>();

            //Behov for anfaldsmedicin
            Type anfaldsMedicin = new StringType("Behov for anfaldsmedicin");
            //Trigger, skal typisk hænge sammen med "Behov for anfaldsMedicin
            Type aktivitet = new StringType("Aktivitet");
            Type allergi = new StringType("Allergi");
            Type stoev = new StringType("Støv");
            Type ukendt = new StringType("Ukendt");

            List<Type> triggerListe = new ArrayList<Type>(
                    Arrays.asList(aktivitet, allergi, stoev, ukendt));
            List<Observation> tempObs = new ArrayList<>();


            // Opretter 2 observationer, én til anfalds medicin og én til trigger
            for (int i = 0; i < antalObs; i++) {
                tempObs.add(new Observation());
                tempObs.get(0).getCode().addCoding()
                        .setSystem("")
                        .setCode("Behov for anfalds medicin")
                        .setDisplay("Behov for anfalds medicin");
                tempObs.get(0).addIdentifier().setValue(searchedPatient.getIdentifier().get(0).getValue());
                tempObs.get(0).setSubject(new Reference(searchedPatient.getId()));
                tempObs.add(new Observation());
                tempObs.get(1).getCode().addCoding()
                        .setSystem("")
                        .setCode("Triggers")
                        .setDisplay("Triggers");
                tempObs.get(1).addIdentifier().setValue(searchedPatient.getIdentifier().get(0).getValue());
                tempObs.get(1).setSubject(new Reference(searchedPatient.getId()));

                Random rand = new Random();
                //Tilføjer tilfældig dato til en liste.
                int randomIndex = rand.nextInt(dateList.size());
                randomElements.add(dateList.get(randomIndex));
                dateList.remove(randomIndex);

                tempObs.get(0).setValue(anfaldsMedicin);
                tempObs.get(0).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                anfaldOgTrigger.add(tempObs.get(0));
                tempObs.get(1).setValue(randomReturnType(triggerListe,1).get(0));
                tempObs.get(1).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                anfaldOgTrigger.add(tempObs.get(1));

                randomElements.clear();
                tempObs.clear();

            }
            return anfaldOgTrigger;
        }

        /**
         *
         * @param searchedPatient ID på patientet, som ønskes at oprette en anfald Trigger observation
         * @param startDate Start dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param endDate slut dato for intervallet, hvori observationer skal tilfældigt spredes ud
         * @param antalObs antal observationer, som ønskes oprettet i det givne dato interval
         * @return returnerer en liste med observationer af typen Aktivitetsvegrænsning...
         */
        private static List<Observation> generateAktivitetsbegraensning(Patient searchedPatient, LocalDate startDate, LocalDate endDate, int antalObs) {
            List<Observation> aktivitetBegraensningList = new ArrayList<Observation>();
            List<LocalDate> dateList = getDatesBetween(startDate, endDate);
            List<LocalDate> randomElements = new ArrayList<LocalDate>();
            //Begrænsning i aktivitet
            Type aktivitetsBegraensning = new StringType("Aktivitetsbegrænsning");

            List<Observation> tempObs = new ArrayList<>();
            // Opretter 2 observationer, én til anfalds medicin og én til trigger
            for (int i = 0; i < antalObs; i++) {
                tempObs.add(new Observation());
                tempObs.get(0).getCode().addCoding()
                        .setSystem("")
                        .setCode("Aktivitetsbegraensning")
                        .setDisplay("Aktivitetsbegraensning");
                tempObs.get(0).addIdentifier().setValue(searchedPatient.getIdentifier().get(0).getValue());
                tempObs.get(0).setSubject(new Reference(searchedPatient.getId()));

                Random rand = new Random();
                //Tilføjer tilfældig dato til en liste.
                int randomIndex = rand.nextInt(dateList.size());
                randomElements.add(dateList.get(randomIndex));
                dateList.remove(randomIndex);

                tempObs.get(0).setValue(aktivitetsBegraensning);
                tempObs.get(0).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
                aktivitetBegraensningList.add(tempObs.get(0));


                randomElements.clear();
                tempObs.clear();

            }
            return aktivitetBegraensningList;
        }

    /**
     * Metode til generering af PEF observation
     * @param searchedPatient ID på patientet, som ønskes at oprette en anfald Trigger observation
     * @param startDate Start dato for intervallet, hvori observationer skal tilfældigt spredes ud
     * @param endDate slut dato for intervallet, hvori observationer skal tilfældigt spredes ud
     * @param antalObs antal observationer, som ønskes oprettet i det givne dato interval
     * @return returnerer en liste med observationer af typen PEF
     */
    private static List<Observation> generatePEF(Patient searchedPatient, LocalDate startDate, LocalDate endDate, int antalObs) {
        List<Observation> PEF = new ArrayList<Observation>();
        List<LocalDate> dateList = getDatesBetween(startDate, endDate);
        List<LocalDate> randomElements = new ArrayList<LocalDate>();
        List<Quantity> randomPEF = new ArrayList<>();

        List<Long> arrayOfRange  = new ArrayList<Long>();
        long[] range = LongStream.iterate(300, n -> {arrayOfRange.add(n);return n + 1;}).limit(700).toArray();
        List<Long> PEFrange = LongStream.of(range).boxed().collect(Collectors.toList());
        //System.out.println(list);
        //Behov for anfaldsmedicin
        Type PEFType = new Quantity();


        List<Observation> tempObs = new ArrayList<>();

        // Opretter 2 observationer, én til anfalds medicin og én til trigger
        for (int i = 0; i < antalObs; i++) {
            tempObs.add(new Observation());
            tempObs.get(0).getCode().addCoding()
                    .setSystem("Morgen/Aften måling")
                    .setCode("Morgen måling")
                    .setDisplay("Morgen måling");
            tempObs.get(0).addIdentifier().setValue(searchedPatient.getIdentifier().get(0).getValue());
            tempObs.get(0).setSubject(new Reference(searchedPatient.getId()));

            tempObs.add(new Observation());
            tempObs.get(1).getCode().addCoding()
                    .setSystem("Morgen/Aften måling")
                    .setCode("Aften måling")
                    .setDisplay("Aften måling");
            tempObs.get(1).addIdentifier().setValue(searchedPatient.getIdentifier().get(0).getValue());
            tempObs.get(1).setSubject(new Reference(searchedPatient.getId()));

            Random rand = new Random();
            //Tilføjer tilfældig dato til en liste.
            int randomIndex = rand.nextInt(dateList.size());
            randomElements.add(dateList.get(randomIndex));
            dateList.remove(randomIndex);

            //Vælger tilfældig PEF måling fra listen
            for (int k = 0; k<2; k++){
            int randomIndexPEF = rand.nextInt(PEFrange.size());
            randomPEF.add(new Quantity(PEFrange.get(randomIndexPEF)));
            }

            tempObs.get(0).setValue(randomPEF.get(0));
            tempObs.get(0).setIssued((java.sql.Date.valueOf(randomElements.get(0))));
            PEF.add(tempObs.get(0));
            tempObs.get(1).setValue(randomPEF.get(1));
            tempObs.get(1).setIssued(java.sql.Date.valueOf(randomElements.get(0)));
            PEF.add(tempObs.get(1));

            randomElements.clear();
            randomPEF.clear();
            tempObs.clear();

        }
        return PEF;
    }

        private static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
            endDate = endDate.plusDays(1);
            long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            return IntStream.iterate(0, i -> i + 1)
                    .limit(numOfDaysBetween)
                    .mapToObj(i -> startDate.plusDays(i))
                    .collect(Collectors.toList());
        }


        private static List<Type> randomReturnType(List<Type> typeList, int numberOfElements) {
            Random rand = new Random();
            List<Type> randomElements = new ArrayList<Type>();
            for (int i = 0; i < numberOfElements; i++) {
                int randomIndex = rand.nextInt(typeList.size());
                randomElements.add(typeList.get(randomIndex));
                //typeList.remove(randomIndex);
            }
            return randomElements;
        }

}

