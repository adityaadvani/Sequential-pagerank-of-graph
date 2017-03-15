//package graphpagerank;

//******************************************************************************
//
// File:    Graphpagerank.java
//
//******************************************************************************
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import edu.rit.pj2.Task;

/**
 * This class is used to calculate the Page rank in sequential version
 * 
* @author Arjun Nair (an3395)
 * @author Aditya Advani (aa5394)
 * @version 10-Dec-2015
 */
public class Graphpagerank extends Task {

    int n;
// Main program.

    /**
     * Main program
     *
     * @param args
     * @throws java.lang.Exception
     */
    public void main(String[] args) throws Exception {

        // Parse command line arguments.
        if (args.length != 2) {
            usage();
        }

        // number of nodes in the graph
        n = Integer.parseInt(args[1]);

        // variables for checking the convergence criteria
        int z;
        boolean flag = true;

        //arrays to hold intermediate
        double[][] current = new double[n][1];
        double[][] prev = new double[n][1];

        double a = 0.85;
        double vcalc = (double) (1 / (double) n);

        //initializing page rank starting values
        for (int i = 0; i < n; i++) {
            current[i][0] = vcalc;
        }

        vcalc *= (1 - a);

        //data structure to save sparse matrix
        //outer arrayList represents the node of the graph 
        //inner arrayList represents the outlinks from the respective node
        ArrayList<ArrayList<sparseMatrix>> list = readFile(args[0]);

        // current^(k+1) = [ a.P.current^(k) + (1 -a) v] where current^(0) = v or current^(0) = 0
        while (flag) {

            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.get(i).size(); j++) {
                    int xTemp = list.get(i).get(j).x;
                    int yTemp = list.get(i).get(j).y;
                    double valueTemp = a * list.get(i).get(j).value;
                    current[xTemp][0] += valueTemp * prev[yTemp][0];
                }
            }

            for (int i = 0; i < n; i++) {
                current[i][0] += vcalc;
            }

            z = 0;

            // checking convergence
            z = converge(prev, current, z);

            if (z == n) {
                flag = false;
            }

            for (int i = 0; i < n; i++) {
                prev[i][0] = current[i][0];
                current[i][0] = 0.0;
            }

        }

        System.out.println("PageRank Scores:");
        for (int i = 0; i < n; i++) {
            System.out.print("PageRank score for node " + (i + 1) + ": \t");
            System.out.printf("%.10g", prev[i][0]);
            System.out.println();
        }
    }

    /**
     * Check for convergence
     *
     * @param current [ double[][] ]
     * @param prev [double[][] ]
     * @param z [ int ]
     * @return [ int ]
     */
    public int converge(double[][] prev, double[][] current, int z) {
        for (int i = 0; i < n; i++) {
            if (Math.abs(prev[i][0] - current[i][0]) < 0.000001) {
                z++;
            }
        }
        return z;
    }

    /**
     * Reads the input file and stores it in a list
     *
     * @param file [Name of the file containing the sparse matrix]
     * @exception Exception
     * @return [ArrayList of ArrayList containing the sparse matrix]
     */
    public ArrayList<ArrayList<sparseMatrix>> readFile(String file) {
        String line;
        ArrayList<ArrayList<sparseMatrix>> list = new ArrayList<ArrayList<sparseMatrix>>();
        list.add(new ArrayList<sparseMatrix>());
        try {
            try (Scanner sc = new Scanner(new File(file))) {
                while (sc.hasNext()) {
                    line = sc.nextLine();
                    String split[] = line.split("\\s+");
                    int current = Integer.parseInt(split[0]);
                    int y = Integer.parseInt(split[1]);
                    double z = Double.parseDouble(split[2]);
                    if (list.size() - current > 0) {
                        //same size
                        list.get(list.size() - 1).add(new sparseMatrix(current, y, z));
                    } else {
                        //increment size
                        list.add(new ArrayList<sparseMatrix>());
                        list.get(list.size() - 1).add(new sparseMatrix(current, y, z));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    /**
     * Print a usage message and exit.
     */
    private static void usage() {
        System.err.println("Invalid Arguments");
        throw new IllegalArgumentException();
    }

    /**
     * Specify that this task requires one core.
     *
     * @return
     */
    protected static int coresRequired() {
        return 1;
    }
}