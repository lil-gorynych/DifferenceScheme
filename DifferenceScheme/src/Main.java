import java.io.FileWriter;
import java.io.IOException;

public class Main{
    static double minT = 0;
    static double maxT = 1;
    static double minX = 0;
    static double maxX = 1;

//    static double alpha = 0.1;
//    static double beta = 0.1;
//
//    static double alpha = 0.1;
//    static double beta = 1;

//    static double alpha = 0.01;
//    static double beta = 0.1;
//
//    static double alpha = 0.01;
//    static double beta = 1;

    private static double[] alphaArr = new double[]{0.1, 0.01};
    private static double[] betaArr = new double[]{0.1, 1};


    public static void main(String[] args) {
        for (double alpha : alphaArr) {
            for (double beta : betaArr) {
                System.out.println(alpha+" "+beta);
                FileWriter writer;
                try {
                    writer = new FileWriter("values_a=" + alpha + "_b=" + beta, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                var xStep = 0.01;
                var tStep = 0.01;

                var coef = 0.5 / xStep - 4 * alpha / Math.pow(xStep, 3);

                double deltaX = maxX - minX;
                var xKnotsAmount = (deltaX % xStep == 0) ? (int) (deltaX / xStep) : (int) (deltaX / xStep) + 1;

                var nextLayer = new double[xKnotsAmount];
                var prevLayer = new double[xKnotsAmount];
                printLayer(writer, prevLayer, minT);


                var t = minT;


                while (t <= maxT) {
                    boolean done = true;

                    t += tStep;
//                    System.out.println(t);
                    nextLayer[0] = t / (t + beta);
                    nextLayer[xKnotsAmount - 1] = 0;

                    for (int i = 1; i < xKnotsAmount - 1; i++) {
                        nextLayer[i] = function(prevLayer, i, tStep, xStep, alpha);
                    }

                    //check
                    for (int i = 1; i < nextLayer.length - 1; i++) {
                        var valueToCheck = check(nextLayer[i], nextLayer[i-1], nextLayer[i+1], xStep, tStep, alpha);
                        if (valueToCheck > 1 + Math.pow(10, -2)) {
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
                        printLayer(writer, nextLayer, t);
                    }
                }
            }
        }

    }

    private static void swap(double[] next, double[] prev) {
        System.arraycopy(next, 0, prev, 0, next.length);
    }

    private static void printLayer(FileWriter writer, double[] layer, double t) {
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

    private static double function(double[] layer, int pos, double tStep, double xStep, double alpha) {
        var left = layer[pos - 1];
        var current = layer[pos];
        var right = layer[pos + 1];

        return current
            - 0.5 * (tStep / xStep) * current * (right - left)
            - alpha * (tStep / Math.pow(xStep, 3)) * (Math.pow(right - current, 2) - Math.pow(current - left, 2));
    }

    private static double check(double current, double left, double right, double xStep, double tStep, double alpha) {
        var firstRe = 0.5 * (right - left) / xStep;
        var secondRe = 2 * alpha * (-2 * (right - left) + (right - 2 * current + left)) / Math.pow(xStep, 3);
//        var re = Math.max(
//            Math.abs(1 - tStep * firstRe),
//            Math.abs(1 - tStep * (firstRe + 2 * secondRe))
//        );

        var re = 1 - tStep * (firstRe + 0 * secondRe);

        //var im = 0;
        var im = tStep * (current / xStep + 2 * alpha * (right - 2 * current + left) / Math.pow(xStep, 3));
        return Math.sqrt(Math.pow(re, 2) + Math.pow(im, 2));
    }
}
