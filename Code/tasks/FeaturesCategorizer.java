package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains all the details of valid trips to be considered in
 * unusual trip detection. The class categorizes the various attributes and then
 * calls an outlier generator to assemble all unusual trips
 *
 * @author aladago
 */
public class FeaturesCategorizer {

    //all trips to be considered in the analysis
    private Map<String, Trip> alltrips;
    //add only outliers which are unique
    private Set<String> unusualTrips;

    /**
     *
     * @param aldtrips all the valid trips under consideration
     */
    public FeaturesCategorizer(Map<String, Trip> aldtrips) {
        this.alltrips = aldtrips;
        unusualTrips = new HashSet<>();
    }

    /**
     *
     * @return the list of unusual trips
     */
    public Set<String> getUnusualTrips() {
        return unusualTrips;
    }

    /**
     * Group all trips according to the hour they started eg. 11.28 will be in
     * the same group as 11.48 but in a different group with 10.40 *
     *
     */
    private Map<String, ArrayList<String>> timeOutlierEngine() {
        //keep track of different groups and the trip ids as values
        Map<String, ArrayList<String>> times = new HashMap<>();

        // in categorizing, consider only the hour part of the start journeys      
        String first2Chars = "";
        for (Map.Entry<String, Trip> entry : alltrips.entrySet()) {
            first2Chars = entry.getValue().getStartTime().substring(0, 2);
            if (times.containsKey(first2Chars)) {
                times.get(first2Chars).add(entry.getKey());
            } else {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(entry.getKey());
                times.put(first2Chars, ids);
            }
        }
        return times;
    }

    /**
     * group maximimum directions according to their (n-1)th significant digit.
     * This is considered to accomodate for measurement errors in direction
     * readings and lapses in record times and vehicle positions at recording *
     * Note: Can lead to overfitting or underfitting: Idea is personal and NOT
     * scientific
     *
     * @ retrurns a map of the dinstic groupings of trips according to
     * maximumDirections
     */
    private Map<Integer, ArrayList<String>> maximumDirectionOutlierEngine() {
        //kee track of groups of maximum directions 
        Map<Integer, ArrayList<String>> maxDirections = new HashMap<>();
        int maxDirectionAppro;
        for (Map.Entry<String, Trip> entry : alltrips.entrySet()) {
            maxDirectionAppro = (entry.getValue().getMaxHeading()) / 10; //ignore last digit
            if (maxDirections.containsKey(maxDirectionAppro)) {
                maxDirections.get(maxDirectionAppro).add(entry.getKey());
            } else {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(entry.getKey());
                maxDirections.put(maxDirectionAppro, ids);
            }
        }

        return maxDirections;
    }

    /*
    *group trips according to their mimimumDirections.
    * Rule of grouping is all trips whose minimum directions differe by only the 
    * last significant digits are put into the same category.    
    *Note: formulated is not proven. Can lead to overfitting or underfitting
    * @return a map of all the trips categorized according to their minimum directions
     */
    private Map<Integer, ArrayList<String>> mimumDirectionsOutlierEngine() {
        //first group the minimum values
        Map<Integer, ArrayList<String>> minDirections = new HashMap<>();
        int minDirectionAppro;
        for (Map.Entry<String, Trip> entry : alltrips.entrySet()) {
            minDirectionAppro = (entry.getValue().getMinHeading()) / 10; //ignore lass significant digit
            if (minDirections.containsKey(minDirectionAppro)) {
                minDirections.get(minDirectionAppro).add(entry.getKey());
            } else {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(entry.getKey());
                minDirections.put(minDirectionAppro, ids);
            }
        }
        return minDirections;
    }

    /*
    * Group all trips according to the mean of their latitudes. 
    * Groups are organized such that latitudes with with equal values to 2.dps are 
    * put in a group. over and under approximation exist. eg. 1.7447 and 1.7392 
     will be in the same group    
     
    @return a map of all the trips grouped according to the mean of their lattidues
     */
    private Map<Integer, ArrayList<String>> latMeanOutlierEngine() {
        //keep track of groups according to latitude means
        Map<Integer, ArrayList<String>> latMeans = new HashMap<>();
        int latMean2Twodc;
        for (Map.Entry<String, Trip> entry : alltrips.entrySet()) {
            latMean2Twodc = Math.round(entry.getValue().getLatMean() * 100); //consider mean to 2dc
            if (latMeans.containsKey(latMean2Twodc)) {
                latMeans.get(latMean2Twodc).add(entry.getKey());
            } else {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(entry.getKey());
                latMeans.put(latMean2Twodc, ids);
            }
        }

        return latMeans;
    }

    /*
    * Group all trips according to the mean of their longitudes. 
    * Groups are organized such that longitudes with with equal values to 2.dps are 
    * put in a group. over and under approximation exist. eg. 1.7447 and 1.7392 
     will be in the same group    
     
    @return a map of all the trips grouped according to the mean of their longitudes
     */
    private Map<Integer, ArrayList<String>> lonMeanOutlierEngine() {
        //keep track of longitudes
        Map<Integer, ArrayList<String>> lonMeans = new HashMap<>();
        int lonMean2Twodc;
        for (Map.Entry<String, Trip> entry : alltrips.entrySet()) {
            lonMean2Twodc = Math.round(entry.getValue().getLatMean() * 100); //consider mean to 2dc
            if (lonMeans.containsKey(lonMean2Twodc)) {
                lonMeans.get(lonMean2Twodc).add(entry.getKey());
            } else {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(entry.getKey());
                lonMeans.put(lonMean2Twodc, ids);
            }
        }

        return lonMeans;
    }
    /**
     *Group all trips according to their distance to the nearest 100 meters 
    * Groups are organized such that distances which vary less than 100 meters are 
    * put in a group. over and under approximation exist. eg. 1.8547 and 1.8392 
     *will be in the same group    
     
    @return a map of all the trips grouped according to distances traveled
    */
    private Map<Integer, ArrayList<String>> distanceOutlierEngine() {
        //keep track of distances
        Map<Integer, ArrayList<String>> distance = new HashMap<>();
        int distance2100thMeters;
        //consider distance to nearest 100th meters
        for (Map.Entry<String, Trip> entry : alltrips.entrySet()) {
            distance2100thMeters =(int) Math.round(entry.getValue().getDistance() * 10); 
            if (distance.containsKey(distance2100thMeters)) {
                distance.get(distance2100thMeters).add(entry.getKey());
            } else {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(entry.getKey());
                distance.put(distance2100thMeters, ids);
            }
        }

        return distance;
    }

    /**
     * start all the various engines a call of this method startups the
     * detection of all outliers according to **trip start time ** the
     * maximumdirection of the trip ** the minimumDirection of the trip ** the
     * mean of the latitude of the trip ** the mean of the longitude of the trip
     */
    public void startEngines() {
        //create outlier generators for processing all attributes
        Task3OutlierGen<String, String> outlierGen
                = new Task3OutlierGen<>(this.alltrips.size());
        this.unusualTrips = outlierGen.getOutliers(this.timeOutlierEngine());

        //create second generator for processing integer ids
        Task3OutlierGen<Integer, String> outlierGen2
                = new Task3OutlierGen<>(this.alltrips.size());
        this.unusualTrips.addAll(outlierGen2.getOutliers(this.maximumDirectionOutlierEngine()));
        this.unusualTrips.addAll(outlierGen2.getOutliers(this.latMeanOutlierEngine()));
        this.unusualTrips.addAll(outlierGen2.getOutliers(this.lonMeanOutlierEngine()));
        this.unusualTrips.addAll(outlierGen2.getOutliers(this.mimumDirectionsOutlierEngine()));
        this.unusualTrips.addAll(outlierGen2.getOutliers(this.distanceOutlierEngine()));

    }

}
