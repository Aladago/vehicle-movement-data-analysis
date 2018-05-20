/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class tackles task two of the given problem. It takes data from two
 * files, points of interest and a vehicle movement log file. It then writes the
 * number of times the vehicle came within a given distance of the points of and
 * the average time spent in that region. interest
 *
 * @author aladago
 */
public class Task2Analysis {

    //vehicle log file
    private File inputFile;

    /*
    set vehicle log file
     */
    public Task2Analysis(File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Read and prepare file silmultaneously for task 2 analysis. whilst reading
     * file, consider only the trip id, the time and the point of reference Pass
     * these parameters to the Task2Analysis for processing
     *
     * @param ip_file the points of interest file
     */
    public void startTask2Analysis(String ip_file) {
        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(new FileInputStream(ip_file)));
                PrintWriter pw
                = new PrintWriter(new FileOutputStream(new File("output-task2.txt")))) {
            // write output file header
            pw.println("IP_ID, Latitude, Longitude, Radius, NumberOFTimes"
                    + "Entered, AverageTime(Minutes)");

            String ipPoint = reader.readLine();

            //read input file and spwan threads of all the points of interest
            //extract parameters of interest
            while ((ipPoint = reader.readLine()) != null) {

                String[] attr = ipPoint.split(",");
                int id = Integer.parseInt(attr[0]);
                float lat = Float.parseFloat(attr[1]);
                float lon = Float.parseFloat(attr[2]);
                float rad = Float.parseFloat(attr[3]);
                //generate threads and spawn for faster processing
                Processor task2p
                        = new Processor(new PointOfInterest(id, lat, lon, rad, 0, 0l));
                Thread thrd = new Thread(task2p);
                thrd.start();
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * a nested class to start and spawn threads for processing
     */
    private class Processor implements Runnable {

        PointOfInterest pointOfInterest;

        /**
         *
         * @param p the point of interest to use
         * @param inputFile the input file under condieration
         */
        public Processor(PointOfInterest p) {
            this.pointOfInterest = p;
        }

        /**
         * Run method processes the file and then writes the output to a file.
         */
        @Override
        public void run() {
            try {
                this.processFileTask2();
                this.writeToFileTask2();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }

        /**
         * write to output file, the complete version of the point of interest
         * Synchronized since all threads will be appending to the same file
         *
         * @param point the point of interest with all the instance variables
         * set
         * @throws FileNotFoundException
         */
        private synchronized void writeToFileTask2() throws FileNotFoundException {
            try (PrintWriter writer
                    = new PrintWriter(new FileOutputStream(new File("output-task2.txt"), true))) {
                writer.println(this.pointOfInterest);
            }
        }

        /**
         * This method reads the input file containing the shuttle trips,
         * computes the average time the shuttle spend within geoferences of
         * points of interests and the average time spent in each point.
         *
         * @throws IOException
         */
        private void processFileTask2() throws IOException, Exception {
            int numEnterances = 0;
            long totalTimeinMils = 0; //expected to be large since in milis
            SimpleDateFormat dtime = new SimpleDateFormat("dd/mm/yyy hh:mm");

            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {

                String trip = reader.readLine();

                while ((trip = reader.readLine()) != null) {
                    String[] cols = trip.split(",");
                    Date startTime = null;
                    if (withinGeoference(cols)) {
                        //update tracking variables immediately upon entering geoference
                        numEnterances++;
                        startTime = dtime.parse(cols[1]);

                        //it's assummed that, the file is not disorganized. That's
                        //the various readings are chronological. 
                        //base on that assumption, keep reading trips until out of interested region
                        String ln = reader.readLine();
                        if (ln != null) {
                            cols = ln.split(",");
                        } else {
                            break;
                        }
                        //continue until outside geoference
                        while (withinGeoference(cols)) {
                            cols = reader.readLine().split(",");
                        }

                        //get the start time of the next trip outside range
                        Date exitTime = dtime.parse(cols[1]);
                        totalTimeinMils += (exitTime.getTime() - startTime.getTime());
                    }

                }  //end of OUTER while loop

                this.pointOfInterest.setNumTimes(numEnterances);
                //only attempt computation of average time if the car entered the region
                if (numEnterances > 0) {
                    this.pointOfInterest.setAverageTime(totalTimeinMils / numEnterances);
                } else {
                    this.pointOfInterest.setAverageTime(0);
                }
            }
        }

        /**
         * evaluate whether a given row is within the geoference of this point
         * The method used to detech is the simple cosine formular. Refer to
         * Point.java for detail view of the formular
         *
         * @param row a trip of the bush
         * @return true if this trip is within the geoference of interested
         * point. return False otherwise
         */
        private boolean withinGeoference(String[] cols) {
            float latitude = Float.parseFloat(cols[2]);
            float longitude = Float.parseFloat(cols[3]);
            //determine distnace between point of interest and other according to cosine rule
            double d = pointOfInterest.getDistanceFromOther(latitude, longitude);

            //point is withing geoference if distance is less than radius
            return d <= pointOfInterest.getRadius();
        }
    }
}
