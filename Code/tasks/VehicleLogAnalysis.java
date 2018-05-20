package tasks;

import java.io.File;

/**
 * This class is the main class for starting all three tasks. However, the
 * processing of file for various task has been decentralized to their
 * respective classes
 *
 * Program ends if the file provided does not exist
 *
 * @author aladago
 */
public class VehicleLogAnalysis {

    //vehicle log analysis file
    private File inputFile;

    /**
     * initialize input file
     *
     * @param filePath the path to the file
     */
    public VehicleLogAnalysis(String filePath) {
        this.setSetFile(filePath);
    }

    /*
    create a file object given the path to the file
    exit when specified path is incorrect
    @param filePath path to file
     */
    private void setSetFile(String filepath) {
        this.inputFile = new File(filepath);
        if (!this.inputFile.exists()) {
            System.out.println("The input file does not exist");
            System.exit(1);
        }
    }

    /**
     * ****************TASK 1 *****************************
     */
    /**
     * generate a task1 instance and start task one analysis
     */
    public void task1() {
        Task1Analysis task1 = new Task1Analysis(this.inputFile);
        task1.startAnalysis();

    }

    /**
     * ***********END OF TASK 1 *********************
     */
    /* *********************Task 2 *******************/
    /**
     * Generate task 2 instance and start task 2 analysis
     *
     * @param ip_file the file containing the points of interest
     */
    public void task2(String ip_file) {
        Task2Analysis task2 = new Task2Analysis(this.inputFile);
        task2.startTask2Analysis(ip_file); //generates output file output-task2 at project root
    }

    /*
     * ** END OF TASK TWO **
     */
    /**
     * ***********************START OF TASK THREE ********************
     */
    /**
     * generate Task3 instance and start analysis
     */
    public void task3() {
        Task3 task3 = new Task3();
        task3.readFile(inputFile); //reads and process the file
        task3.startAnalysis(); //will write output file output-task3.txt to project rot

    }

    /**
     * ********TEST The METHODS ********************
     */
    /**
     * Creates an object of the class and call each of the various methods.
     * Idealy, this method should be located in a different file. However,
     * keeping it here makes the code navigation easier
     *
     * @param args
     *
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
