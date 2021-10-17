import java.io.FileWriter;
import java.io.IOException;

public class Main{
    static double minT = 0;
    static double maxT = 1;
    static double minX = 0;
    static double maxX = 1;

//    static double alpha = 0.1;
//    static double beta = 0.1;

//    static double alpha = 0.1;
//    static double beta = 1;

//    static double alpha = 0.01;
//    static double beta = 0.1;
//
    static double alpha = 0.01;
    static double beta = 1;



    static FileWriter writer;

    static {
        try {
            writer = new FileWriter("values_a=" + alpha + "_b=" + beta, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var xStep = 0.01;
        var tStep = 0.01;

        var coef = 0.5 / xStep - 4 * alpha / Math.pow(xStep, 3);

        double deltaX = maxX - minX;
        var xKnotsAmount = (deltaX % xStep == 0) ? (int) (deltaX / xStep) : (int) (deltaX / xStep) + 1;

        var nextLayer = new double[xKnotsAmount];
        var prevLayer = new double[xKnotsAmount];
        printLayer(prevLayer, minT);


        var t = minT;


        while (t <= maxT) {
            boolean done = true;

            t += tStep;

            nextLayer[0] = t / (t + beta);
            nextLayer[xKnotsAmount - 1] = 0;

            for (int i = 1; i < xKnotsAmount - 1; i++) {
                nextLayer[i] = function(prevLayer, i, tStep, xStep);
            }

            //check
            for (int i = 1; i < nextLayer.length - 1; i++) {
                var valueToCheck = tStep * coef * (nextLayer[i-1] - nextLayer[i+1]);
                if (valueToCheck > 0 || valueToCheck < -2) {
                    System.out.println("Error happened!");
                    System.out.println("\tOld step: " + tStep);
                    t -= tStep;
                    tStep = 0.5 * tStep;
                    System.out.println("\tNew step: " + tStep);
                    done = false;
                    break;
                }
            }

            if (done) {
                swap(nextLayer, prevLayer);
                printLayer(nextLayer, t);
            }
        }
    }

    private static void swap(double[] next, double[] prev) {
        System.arraycopy(next, 0, prev, 0, next.length);
    }

    private static void printLayer(double[] layer, double t) {
        try {
            writer.write(t + "");
            for (double value : layer) {
                //System.out.print(value + " ");
                writer.write(";" + value);
            }
            //System.out.println();
            writer.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double function(double[] layer, int pos, double tStep, double xStep) {
        var left = layer[pos - 1];
        var current = layer[pos];
        var right = layer[pos + 1];

        return current
            - 0.5 * (tStep / xStep) * current * (right - left)
            - alpha * (tStep / Math.pow(xStep, 3)) * (right - left) * (right - 2 * current + left);
    }

    private static void check(double current, double left, double right, double up, double tStep, double xStep) {
        var val = (up - current) / tStep
            + 0.5 * current * (right - left) / xStep
            + alpha * (right - left) * (right - 2 *  current + left) / Math.pow(xStep, 3);
        System.out.println(val);
    }
}
