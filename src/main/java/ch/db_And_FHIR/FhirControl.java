package ch.db_And_FHIR;

import java.io.IOException;
import java.text.DateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.base.composite.BaseResourceReferenceDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ch.utility.dateUtil;
import com.google.common.collect.Lists;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;

import static java.lang.Math.floor;


public class FhirControl {
    /**
     * Fhir Control er lavet som en "SingleTon" Hvilket betyder at der kun laves én instans af klassen
     * dvs. at hvis vi kører startCtx i main på det nyligt oprettede objekt, så behøver vi ikke køre den igen
     * i calculated Parameters. (Det gør ikke noget at kalde den, men den gør ikke noget)
     */
    private static FhirControl instance;

    public static FhirControl getInstance(){
        if (instance ==null)
                instance = new FhirControl();
        return instance;
    }

    private IGenericClient instansClient;
    boolean ctxEstablished = false;

    public void startCtx(){
        if(!ctxEstablished){

        FhirContext ctx = FhirContext.forDstu3();

        // TestServer adresse http://vonk.fire.ly/
        String serverBase = "http://hapi.fhir.org/baseDstu3";

        // Oprettelse af klient til tilgang af serveren (Klient tilgår server)
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        instansClient = client;
        ctxEstablished = true;
        }
    }

    public List<Observation> getFHIRObservations(String patientIdentifer, LocalDate startDate, LocalDate endDate){
        List<Observation> observationList = new ArrayList<Observation>();

        double numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate) ;
        // % er resten efter division med tallet efter %
        long extraDays = (long) (numOfDaysBetween % 7);
        startDate = startDate.plusDays(extraDays-1);
        //endDate = endDate.plusDays(1);

        System.out.println(ChronoUnit.DAYS.between(startDate, endDate));

        Date FHIRstartDate = java.sql.Date.valueOf(startDate);
        Date FHIRendDate = java.sql.Date.valueOf(endDate);

        Bundle results = instansClient
                .search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().identifier(patientIdentifer))
                .returnBundle(Bundle.class)
                .execute();

        Bundle observationsBundle = instansClient
                .search()
                .forResource(Observation.class)
                .where(Observation.SUBJECT.hasId(results.getEntry().get(0).getResource().getId()))
                //.where(Observation.DATE.after().day(FHIRstartDate))
                .returnBundle(Bundle.class)
                .execute();

        // Tilføjer observationer til en liste
        for (int i = 0; i<observationsBundle.getEntry().size(); i++){
            observationList.add((Observation) observationsBundle.getEntry().get(i).getResource());
        }


            /**
     * Loader næster side af bundle og lægger det ind i en array liste
     *
     */
        for (int j = 0; j < (floor(observationsBundle.getTotal()/20))-1; j++){

            observationsBundle = instansClient.loadPage().next(observationsBundle).execute();

            for (int i = 0; i<observationsBundle.getEntry().size(); i++){
                if(observationList.size() < observationsBundle.getTotal()) {
                    observationList.add((Observation) observationsBundle.getEntry().get(i).getResource());
                }
            }
        }
        for (int k = 0; k<observationList.size(); k++){
            // Hvis Observations datoen IKKE er efter Startdatoen ELLER IKKE er før Slutdatoen så fjern fra listen
            // NOTE! Ser ud til ikke at inkludere start og slutdato, hvorfor, man måske skal trække/lægge en dag til
            if (!observationList.get(k).getIssued().after(FHIRstartDate) || !observationList.get(k).getIssued().before(FHIRendDate)){
                observationList.remove(k);
            }
        }
        /*for (int m = 0; m<observationList.size(); m++){
            System.out.println(observationList.get(m).getCode().getCoding().get(0).getCode());
        }*/

        return observationList;

    }
public boolean requestIsAppData(String patientIdentifer){
       boolean isAppData = false;
    Bundle results = instansClient
            .search()
            .forResource(Patient.class)
            .where(Patient.IDENTIFIER.exactly().identifier(patientIdentifer))
            .returnBundle(Bundle.class)
            .execute();

    /**
     * Hvis der ikke findes en patient, så returnér false
     */
    if (results.getEntry().isEmpty())
    return isAppData;

    Bundle observationsBundle = instansClient
            .search()
            .forResource(Observation.class)
            .where(Observation.SUBJECT.hasId(results.getEntry().get(0).getResource().getId()))
            //.where(Observation.DATE.after().day(FHIRstartDate))
            .returnBundle(Bundle.class)
            .execute();

    /**
     * Hvis observationsbundle ikke er tomt, så lav appData = true og returnér
     */
        if (!observationsBundle.getEntry().isEmpty())
            isAppData = true;
        return isAppData;
}

}


/**
 * Ting i Main:
        String startString = "10.04.2018";
        String slutString = "10.05.2018";
        LocalDate FHIRstartDate = dateUtil.parse(startString);
        LocalDate FHIRendDate = (dateUtil.parse(slutString));

        FhirControl FHIR = new FhirControl();
        FHIR.startCtx();
        List<Observation> obsList = FHIR.getFHIRObservations(patientCPR.toString(), FHIRstartDate, FHIRendDate);
        obsList.stream().map(Observation::getValue).forEach(System.out::println);
        System.out.println(obsList.size());
 */
