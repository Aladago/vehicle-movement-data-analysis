import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is a generic template for detecting outlier groups.
 *
 * @author aladago
 * @param <K> the data type of the map ids
 * @param <V> the data type of the arrayList in the map
 *
 */
public class Task3OutlierGen<K, V> {

    //the size of all the valid trips under consideration
    private int populationSize;

    /**
     * initialize parent size of the generator
     *
     * @param parentSize
     */
    public Task3OutlierGen(int parentSize) {
        this.populationSize = parentSize;
    }

    /**
     * A category is considered an outlier if it's frequency is less than 1 less
     * the square root of the population size/the um of categories.
     *
     * Note: This is an ad-hoc way of generating outliers [concocted by me]. It
     * can lead to mis-classification of outliers: False positives are
     * particulary highly likely. It however returns great results
     *
     * @param attributCategories the map of the groupings of the trips according
     * to attibute
     * @return the outliers according to this category
     */
    public Set<V> getOutliers(Map<K, ArrayList<V>> attributCategories) {

        Set<V> outliers = new HashSet<>(); //keep track of outliers
        int numCategories = attributCategories.size();

        //search and find possible outliers in this attributes of all the trips
        //classy groups whose size is 1 less the squared root of 
        //the population size/number of categories
        for (Map.Entry<K, ArrayList<V>> entry : attributCategories.entrySet()) {
            int cateSize = entry.getValue().size();
            if (cateSize < Math.sqrt(populationSize / numCategories) - 1) {
                outliers.addAll(entry.getValue());
            }

        }
        return outliers;
    }
}
