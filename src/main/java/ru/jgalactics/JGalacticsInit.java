package ru.jgalactics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class JGalacticsInit {
    public static void main(String[] args) {
        int nStars = 10000;
        a = 0.25;
        M = 1;
        double maxR = 1;
        int nBeans = 30;
        double[] nPerBean = new double[nBeans];

        double fullArea = intPdf(0, maxR);
        System.out.println("nStars: " + nStars);

        double summ = 0;
        double step = maxR / nBeans;
        for(int i = 0; i < nBeans; i++){
            nPerBean[i] = intPdf(i*step, (i + 1) * step) * nStars / fullArea;
            summ += nPerBean[i];
        }


        //catching
        System.out.println("Starting rendering");
        int[] nPerBeanMax = normalize(nPerBean, nStars);
        int[] nPerBeanCurrent = new int[nBeans];
        double[][] data = new double[nStars][3];
        Random rnd = new Random(System.currentTimeMillis());
        double x, y, z, r;
        int generated = 0;
        while(generated < nStars){
            x = 2 * rnd.nextDouble() - 1;
            y = 2 * rnd.nextDouble() - 1;
            z = 2 * rnd.nextDouble() - 1;
            r = Math.sqrt(x*x + y*y + z*z);
            if(r > 1.0){
                continue;
            }

            int index = (int)(nBeans*r); //0.95
            if(nPerBeanCurrent[index] < nPerBeanMax[index]){
                data[generated][0] = x;
                data[generated][1] = y;
                data[generated][2] = z;
                generated++;
                nPerBeanCurrent[index]++;
                if(generated % 100 == 0){
                    System.out.println(generated);
                }
            }
        }
        System.out.println("Rendered");

        System.out.println("Staring dumping");
        //dump
        try(RandomAccessFile rafile = new RandomAccessFile("/home/david/dump.bin", "rw")){
            //write number of columns to first bytes
            rafile.writeInt(data.length);

            //write number of stars to first bytes
            rafile.writeInt(data[0].length);

            //write data column by column. like "xxxyyyzzzVxVxVxVyVyVyVzVzVz"
            for(int i = 0; i < data.length; i++){
                for(int k = 0; k < data[0].length; k++){
                    rafile.writeDouble(data[i][k]);
                }
            }

        } catch (FileNotFoundException ex) {
            System.out.print("File cant be opened: "+ ex);
        } catch (IOException ex){
            ex.printStackTrace();
        }
        System.out.println("Dumped");
    }

    static int[] normalize(double[] arr, int nElems){
        double[] data = Arrays.copyOf(arr, arr.length);
        double summ = 0;
        for(int i = 0; i < data.length; i++){
            summ += data[i];
        }
        for(int i = 0; i < data.length; i++){
            data[i] = data[i] * nElems / summ;
        }

        int intPart = 0;
        for(int i = 0; i < data.length; i++){
            intPart += (int)data[i];
        }

        int relevantPos = 0;
        double biggestValue = 0;
        while(intPart < nElems){
            //findind most relevant element
            for(int i = 0; i < data.length; i++) {
                if((data[i] - Math.floor(data[i])) > biggestValue){
                    biggestValue = data[i] - Math.floor(data[i]);
                    relevantPos = i;
                }
            }
            data[relevantPos] = Math.ceil(data[relevantPos]);
            intPart++;
            relevantPos = 0;
            biggestValue = 0;
        }

        //creating resultArr;
        int[] resultArr = new int[data.length];
        for(int i = 0; i < data.length; i++){
            resultArr[i] = (int)data[i];
        }
        return resultArr;
    }

    static double a;
    static double M;

    static double pdf(double r){
        double coef = 3 * M / ( 4 * Math.PI * Math.pow(a, 3));
        return coef * Math.pow(1 + r * r / (a * a), -2.5);
    }

    static double intPdf(double start, double finish){
        int nSteps = 1000000;
        double rBean = (finish - start) / nSteps;
        double result = 0;
        double step = (finish - start) / (nSteps + 1);
        double current = start + step;
        for(int i = 0; i < nSteps; i++, current += step){
            result += pdf(current) * rBean;
        }
        return result;
    }

}

