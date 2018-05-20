package tasks;

/**
 * This class models a single point on the earth surface referenciable by its
 * gps coordinates. The class also has a property for calculating the distance
 * between it and any other point
 *
 * @author aladago
 */
public class Point {

    //vehicle's position
    private float latitude;
    private float longitude;

    /**
     *
     * @param latitude the latitude of this point
     * @param longitude the logitude of this point
     */
    public Point(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     *
     * @return the latitude of this point
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     *
     * @return the longitude of this point
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * computes the distance between this point and another point using using
     * The Harvesine formular. 
     * The Harvesine formular is preferred over the Law of Cosines for Spherical Trigonometry 
     * because of the small distances involved. However, 
     * in this task, both the Law of Cosines and the Harvesine formular achieve 
     * the same results. The formular is retrieved from
     * https://engineering.purdue.edu/ece477/Archive/2008/Spring/S08-Grp03/nb/datasheets/haversine.pdf
     *
     * @param latitude the latitude of the other point
     * @param longitude the longitude of the other point
     *
     * @return the distance between this point and the other point according to
     * cosines.
     */
    public double getDistanceFromOther(double latitude, double longitude) {
        //compute approximate radius of the earth at latitude 6[most of the points
        //of interest are are above 5 but below 6]. 
        //6378 is the Equatorial radius of the earth in km
        int EARTH_RADIUS = 6378 - (int) (21 * Math.sin(6));
        //convert gps points in degrees to radians
        double lon1R = degree2Rad(this.longitude);
        double lat1R = degree2Rad(this.latitude);
        double lon2R = degree2Rad(longitude);
        double lat2R = degree2Rad(latitude);
        //get stainght distances
        double dLon = lon2R - lon1R;
        double dLat = lat2R - lat1R;
        double r = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1R)
                * Math.cos(lat2R) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(r), Math.sqrt(1 - r)); //c is the great circle distance
        
        return EARTH_RADIUS * c;

    }

    /**
     * Convert gps points in degrees to radians for use Consine was computation
     *
     * @param coordinate
     * @return the radians equavalent of this coordiante
     */
    private double degree2Rad(double axisLength) {
        return axisLength * Math.PI / 180;
    }

}
