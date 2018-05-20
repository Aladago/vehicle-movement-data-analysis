package tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class tackles task three. It reads data from a file containg vehicle log
 * information and then attempts a prediction of the unusual trips in the file.
 *
 * @author aladago
 */
public class Task3 {

    private static final int MIN_DURATION = 600; //minimum time for a valid trip is 10 minutes
    private static final float MIN_DISTANCE = 0.5F; //distances less than 500 meters will be discarded
    private Set<String> outliers; //unusual trips

    /**
     * initialize outliers to an empty set
     */
    public Task3() {
        outliers = new HashSet<>();
    }

    /**
     * start analysis of the the file for task three Also writes to file the
     * unusual trips detected according to my categorization
     */
    public void startAnalysis() {

        try (PrintWriter writer = new PrintWriter(new File("output-task3.txt"))) {
            writer.println(this.outliers.size());
            for (String tripId : this.outliers) {
                writer.println(tripId);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }

    }

    /**
     *
     * @param file the file object which contains the input data
     */
    public void readFile(File file) {

        Map<String, ArrayList<String[]>> trips = new HashMap<>();

        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String row = reader.readLine();//read and ignore file header
            String[] attributes;
            ArrayList<String[]> trip;
            String staticId;

            OUTER: //outer loop
            while ((row = reader.readLine()) != null) {
                trip = new ArrayList<>(); //keep track of new trips

                attributes = row.split(",");
                staticId = attributes[0]; //keep this id until a new id is detected
                String id = "";
                do {
                    trip.add(attributes); //update trip and row
                    row = reader.readLine();

                    //file can end at the middle of the loop
                    if (row != null) {
                        attributes = row.split(",");
                        id = attributes[0];
                    } else {
                        break OUTER; //break out if at the end of file
                    }

                } while (staticId.equals(id)); //END OF INNER

                //update map of trips taking notes of trips already added
                if (trips.containsKey(staticId)) {
                    trips.get(staticId).addAll(trip);
                } else {
                    trips.put(staticId, trip);
                }
                //deal with the remnants of the exited loop
                trip = new ArrayList<>();
                trip.add(attributes);
                trips.put(id, trip);
            } //END OF OUTTER LOOP

            //spawn all threads now. do not attempt this if exception occurs
            this.generateTrips(trips);

        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     *
     * @param trips all rows contain in the input file
     */
    private void generateTrips(Map<String, ArrayList<String[]>> rows) {
        //keep track of trips for further processing
        Map<String, Trip> trips = new HashMap<>();
        for (Map.Entry<String, ArrayList<String[]>> entry : rows.entrySet()) {
            Trip trip1 = new Trip(entry.getKey(), entry.getValue());
            trips.put(trip1.getId(), trip1);
        }

        //remove all trips whose average speed is less than 5km/hr 
        //and trips whose durations is less than 10 minutes
        //also eliminate trips whose distance is less than a 500 meters
        Map<String, Trip> cleanedTrips = new HashMap<>();
        boolean credibleTrip;
        for (Map.Entry<String, Trip> e : trips.entrySet()) {
            credibleTrip = e.getValue().getAvgSpeed() > 5
                    && e.getValue().getDuration() >= MIN_DURATION
                    && e.getValue().getDistance() >= MIN_DISTANCE;
            if (credibleTrip) {
                cleanedTrips.put(e.getKey(), e.getValue());                
            }
        }

        //now classfy the sorted out trips 
        //also reset instance variable, outliers
        FeaturesCategorizer feature = new FeaturesCategorizer(cleanedTrips);
        feature.startEngines();
        this.outliers = feature.getUnusualTrips();

    }

}
