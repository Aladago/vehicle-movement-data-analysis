import java.util.ArrayList;
import java.util.Date;

/**
 * This class models a trip. the class is selective on the attributes of a trip
 * given in a file. Only the ids, time, latitude and longitude, speed and
 * direction are considered
 *
 * Implemented as thread to allow for concurrent processing of trips. Can speed
 * up processing itme
 *
 * @author aladago
 */
public class Trip {

    //set up properties of a trip
    private String id;
    private ArrayList<String[]> recordings;
    private float latMean, lonMean, avgSpeed;
    private int size, maxHeading, minHeading;
    private Point startPoint, endPoint;

    /**
     *
     * @param id trip id
     * @param recordings all recordings in the trip
     */
    public Trip(String id, ArrayList<String[]> recordings) {
        this.id = id;
        latMean = lonMean = avgSpeed = 0;
        size = maxHeading = minHeading = 0;
        startPoint = new Point(0, 0);
        endPoint = new Point(0, 0);
        this.recordings = recordings;        
        this.initializeAllInstances();
    }

    /**
     * @return time range at which this trip was performed.
     */
    public String getStartTime() {
        String[] firstRow = this.recordings.get(0);
        return firstRow[1].split(" ")[1];
    }

    /**
     *
     * @return the duration of this trip in seconds
     */
    public long getDuration() {
        String[] firstRow = this.recordings.get(0);
        String[] lastRow = this.recordings.get(size - 1);
        Date start = new Date(firstRow[1]);
        Date end = new Date(lastRow[1]);
        return (end.getTime() - start.getTime()) / 1000; //return time in seconds
    }

    /**
     * @return the ID of this trip
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the latitudinal mean of this trip
     */
    public float getLatMean() {
        return latMean;
    }

    /**
     *
     * @return the longitudinal mean of this trip
     */
    public float getLonMean() {
        return lonMean;
    }

    /**
     *
     * @return the average speed of this trip. Good for excluding statitionary
     * pts
     */
    public float getAvgSpeed() {
        return avgSpeed;
    }

    /**
     *
     * @return the maximum direction of this trip
     */
    public int getMaxHeading() {
        return maxHeading;
    }

    /**
     *
     * @return the minimum direction of this trip
     */
    public int getMinHeading() {
        return minHeading;
    }

    /**
     *
     * @return the total distance convered by this journey. Change of directions
     * are not considered in the computations. the strainght line difference
     * between the start of the journey and it's are the main parameters
     */
    public double getDistance() {
        float startLatitude = this.startPoint.getLatitude();
        float startLongitude = this.startPoint.getLongitude();
        return this.endPoint.getDistanceFromOther(startLatitude, startLongitude);
    }

    /**
     * Initialized all instance variables of this trip Because method is quite
     * large, it will be called in a thread
     */
    private void initializeAllInstances() {
        if (recordings != null) {
            this.size = recordings.size(); //assign size
            //get parameters for points
            float startLatide = Float.parseFloat(recordings.get(0)[2]);
            float startLongitude = Float.parseFloat(recordings.get(0)[3]);
            float endLatitude = Float.parseFloat(recordings.get(size -1)[2]);
            float endLongitude = Float.parseFloat(recordings.get(size-1)[3]);
            this.startPoint = new Point(startLatide, startLongitude);
            this.endPoint = new Point(endLatitude, endLongitude);
            
            String[] row;
            double latSum = 0;
            double lonSum = 0;
            double totalSpeed = 0;
            float lat, lon;
            int direction;
            //initialize instance variable
            maxHeading = 0;
            minHeading = Integer.MAX_VALUE; //will be overriden soon
            
            //process each trip
            for (int i = 0; i < recordings.size(); i++) {
                row = recordings.get(i);
                lat = Float.parseFloat(row[2]);
                lon = Float.parseFloat(row[3]);
                direction = Integer.parseInt(row[4]);

                //update minimum and maximum direction     
                if (minHeading > direction) {
                    minHeading = direction;
                } else if (maxHeading < direction) {
                    maxHeading = direction;
                }
                //update total sums values
                latSum += lat;
                lonSum += lon;
                totalSpeed += Float.parseFloat(row[5]);
            } //END OF LOOP

            //set up the instance variables 
            this.latMean = (float) latSum / this.size;
            this.lonMean = (float) lonSum / this.size;
            this.avgSpeed = (float) totalSpeed / this.size;
        } else {
           throw new IllegalArgumentException("Trip cannot be null");

        }
    }
}
