package Test;

import tasks.VehicleLogAnalysis;

/**
 * This is test class of the vehicle log analysis.
 *
 * @author aladago
 */
public class VehicleLogAnalysisTest {

    /**
     * program entry
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //create an instance of VehicleLogAnalysis class
        //ensure files are located at project root. otherwise replace
        //parameters with paths to files
        VehicleLogAnalysis analysis = new VehicleLogAnalysis("2.vehicle_log.csv");

        //execute all three tasks
        analysis.task1();
        analysis.task2("1.interest_points.csv");
        analysis.task3();
       
       

    }

}
