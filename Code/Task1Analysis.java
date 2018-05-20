
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class analysis reads in an input file containing a vehicle log data. It
 * then determines the parking location of the car at night based on the data
 *
 * @author aladago
 */
public class Task1Analysis {
    //vehicle log file

    private File inputFile;
    public static final int MILISECONDS_PER_DAY = 86400000;

    /**
     *
     * @param inputFile vehicle log file
     */
    public Task1Analysis(File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * a method for interfacing with the puplic When called, it generates an
     * output file call output-task1.txt located at project root
     */
    public void startAnalysis() {
        this.writeOuputFile(this.processData());
    }

    /*
    This method computes and writes the location of a car's parking point at night
    * first reads the file and determines vehicle stop times which are in the night
    * and the nex trips started a day after
    * it considers the location with the maximum stops assumming that the vehicle stops there everyday
     */
    private Map<String, Integer> processData() {
        //keep track of parkings at night
        Map<String, Integer> parkings = new HashMap<>();
        float longitude, latitude = 0; //parking locations
        String p; //point of parking locations
        //open 
        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(new FileInputStream(this.inputFile)))) {

            String row = reader.readLine(); //read and keep header. [Unused] 

            //at parking, car's ignition must be off
            //if that's the case, and time is at night, then it's a possible parking location if 
            //the next trip started on a different day
            while ((row = reader.readLine()) != null) {
                if (row.contains("No")) {
                    String[] features = row.split(",");
                    String time = features[1].split("\\s")[1];
                    
                    //short circuit for whether journey is in night
                    boolean night = time.compareTo("6:00") <= 0 && time.compareTo("18:00") >= 0;
                    if (night) {
                        //get this date and the one after
                        String date1 = features[1].split("\\s")[0];
                        String row2 = reader.readLine();
                        String[] features2;
                        String date2;
                        if (row2 != null) {
                            features2 = row2.split(",");
                            date2 = features2[1].split("\\s")[0];
                        } else {
                            //reached end of file
                            break;
                        }

                        SimpleDateFormat f = new SimpleDateFormat("dd/mm/yyyy");
                        Date y = f.parse(date1);
                        Date z = f.parse(date2);

                        //Date
                        //if next trip is the next day?
                        boolean nextDay = z.getTime() - y.getTime() >= MILISECONDS_PER_DAY;
                        if (nextDay) {
                            latitude = (float)(Math.round(Float.parseFloat(features[2])*100)/100.0);
                            longitude = (float)(Math.round(Float.parseFloat(features[3])*100)/100.0);
                            p = Float.toString(latitude) + " "
                                    + Float.toString(longitude);

                            //if this location has already been visited, increase its count
                            //otherwise, start counting
                            if (parkings.containsKey(p)) {
                                parkings.put(p, parkings.get(p) + 1);
                            } else {
                                parkings.put(p, 1);
                            }
                        }
                    }
                }

            }

        } catch (FileNotFoundException fNExp) {
            System.out.println(fNExp.toString());

        } catch (IOException ex) {
            System.out.println(ex.toString());
        } catch (ParseException ex) {
            System.out.println(ex.toString());
        }
        return parkings;
    }

    /*
    *The parking location at night is the key with the maximum number of stops. 
    *It's expected that the bus stops here almost everyday
    @return the location of the car at night
     */
    private String getLocation(Map<String, Integer> parkings) {
        String location = null;
        if (parkings != null) {
            int max = 0;
            for (Map.Entry<String, Integer> entry : parkings.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    location = entry.getKey();
                }
            }
        }
        return location;
    }

    /**
     * A method to output the vehicle approximate parking place at night to a
     * file
     *
     *
     * @param parkings the parking locations of the car and the number of times
     */
    private void writeOuputFile(Map<String, Integer> parkings) {
        //get the location the car parks at night        
        String location = this.getLocation(parkings);
        //output that location to a file
        try (PrintWriter writer = new PrintWriter(new File("output-task1.txt"))) {
            writer.println("Latitude, Longitude");
            String[] point = location.split("\\s"); //latitude and longitude separated by white space

            //consider values to 2 decimal places
            DecimalFormat f = new DecimalFormat("##0.00");
            float latitude = Float.parseFloat(point[0]);
            float longitude = Float.parseFloat(point[1]);
            writer.println(f.format(latitude) + ", " + f.format(longitude));//write to file as csv
        } catch (IOException ex) {
            System.out.println(ex.toString());

        }
    }

}
