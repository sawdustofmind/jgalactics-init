package ru.jgalactics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

import static ru.jgalactics.utils.ArrayUtils.normalize;

public class Bulge {
    private double M;
    private double a;
    private double coef;

    private static Logger LOG = LoggerFactory.getLogger(Bulge.class);

    public Bulge(double M, double a){
        this.M = M;
        this.a = a;
        coef =  3 * M / ( 4 * Math.PI * Math.pow(a, 3));
    }

    public double pdf(double r){
        return coef * Math.pow(1 + r * r / (a * a), -2.5);
    }

    public double intPdf(double start, double finish){
        return undefInfPdf(finish) - undefInfPdf(start);
    }

    public double undefInfPdf(double r){
        return coef * 2 * a * r * (r * r + 1.5 * a * a) / (3 * Math.pow(r * r + a * a, 1.5));
    }

    public double[][] getThrowedArray(int nStars, int nBeans, double maxR){
        LOG.info("Throwing of Bulge, a = {}, M = {}, maxR = {} nStars = {}, nBeans = {}",
                a, M, maxR, nStars, nBeans);
        LOG.info("Rendering beans");
        double[] nPerBean = new double[nBeans];
        double fullArea = intPdf(0, maxR);

        double step = maxR / nBeans;
        for(int i = 0; i < nBeans; i++){
            nPerBean[i] = intPdf(i*step, (i + 1) * step) * nStars / fullArea;
        }

        int[] nPerBeanMax = normalize(nPerBean, nStars);
        int[] nPerBeanCurrent = new int[nBeans];

        double[][] data = new double[nStars][3];
        Random rnd = new Random(System.currentTimeMillis());
        double x, y, z, r;
        int generated = 0, beansImportant = nBeans;

        while(generated < nStars){
            x = 2 * rnd.nextDouble() - 1;
            y = 2 * rnd.nextDouble() - 1;
            z = 2 * rnd.nextDouble() - 1;
            r = Math.sqrt(x*x + y*y + z*z);
            if(r > 1.0){
                continue;
            }

            int index = (int)(beansImportant*r);
            if(nPerBeanCurrent[index] < nPerBeanMax[index]){
                double coef = (double)beansImportant / nBeans;
                data[generated][0] = x * coef;
                data[generated][1] = y * coef;
                data[generated][2] = z * coef;
                generated++;
                nPerBeanCurrent[index]++;

                if(nPerBeanCurrent[beansImportant - 1] == nPerBeanMax[beansImportant - 1]){
                    beansImportant--;
                }
                if(generated % nStars / 20 == 0) {
                    LOG.info("generated: {} stars", generated);
                }
            }
        }
        LOG.info("Bulge threw, a = {}, M = {}, maxR = {} nStars = {}, nBeans = {}",
                a, M, maxR, nStars, nBeans);
        return data;
    }
}
