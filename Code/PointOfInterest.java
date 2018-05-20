
/*
 *Ponts of Interest a specialized extension of point which allows for the seting 
 *geoference around it given a radius. 
 * @author aladago
 */
public class PointOfInterest extends Point {

    private float radius;
    private long averageTime;
    private long numTimes, id;

    /**
     *
     * @param id id of point of interest
     * @param lat latitude of point of interest
     * @param lon longitude of point of interest
     * @param rad the radius of point of interest
     * @param nTimes the number of times vehicle enters point of interest
     * @param avgTime the average time vehicle spend inside point of interest in
     * each entrance
     */
    public PointOfInterest(int id, float lat, float lon, float rad, int nTimes, long avgTime) {
        super(lat, lon);
        this.radius = rad;
        this.averageTime = avgTime;
        this.numTimes = nTimes;
        this.id = id;
    }

    /**
     *
     * @return the radius of the geoference
     */
    public float getRadius() {
        return radius;
    }

    /**
     *
     * @return the average time vehicle spent in the geoference of this point
     */
    public float getAverageTime() {
        return averageTime;
    }

    /**
     *
     * @return the number of times vehicle entered the geoference of this point
     */
    public long getNumTimes() {
        return numTimes;
    }

    /**
     *
     * @param averageTime the average time vehichle spent inside this point
     */
    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    /**
     * Set the number of time the car enters this point of interest
     *
     * @param numTimes number of times vehicle entered this point
     */
    public void setNumTimes(int numTimes) {
        this.numTimes = numTimes;
    }
    

    /**
     *
     * @return a tring of this object for well formated for io operations
     */
    @Override
    public String toString() {

        return id + ", " + this.getLatitude() + "," + this.getLongitude() + ", "
                + radius + ", " + numTimes + ", " + Math.round(this.averageTime/600.0)/100.0;
    }

}
