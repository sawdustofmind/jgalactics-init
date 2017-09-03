package ru.jgalactics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class JGalacticsInit {
    public static void main(String[] args) {
        int nStars = 1000000;
        a = 0.25;
        M = 1;
        double maxR = 1;
        int nBeans = 8000;
        double[] nPerBean = new double[nBeans];

        double fullArea = intPdf(0, maxR);
        System.out.println("nStars: " + nStars);

        double summ = 0;
        double step = maxR / nBeans;
        for(int i = 0; i < nBeans; i++){
            nPerBean[i] = intPdf(i*step, (i + 1) * step) * nStars / fullArea;
            summ += nPerBean[i];
        }

        int[] nPerBeanMax = normalize(nPerBean, nStars);
        int[] nPerBeanCurrent = new int[nBeans];
        int score = 0;
        for (int i = 0; i < nBeans; i++) {
            score += nPerBeanMax[i];
            if(nPerBeanMax[i] == 0){
                nBeans = i - 1;
                System.out.println("Beans used: " + nBeans + "score: " + score);
                break;
            }
        }
        //catching
        System.out.println("Starting rendering");

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

            int index = (int)(beansImportant*r); //0.95
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
                    //System.out.println(generated + "\t" + beansImportant + "\t" + nPerBeanCurrent[0] + "\t" + nPerBeanMax[0]);

            }
        }
        System.out.println("Rendered. Starts checking");

        nPerBeanCurrent = new int[nBeans];
        for (int i = 0; i < nStars; i++) {
            r = Math.sqrt(data[i][0] * data[i][0] + data[i][1] * data[i][1] + data[i][2] * data[i][2]);
            nPerBeanCurrent[(int)(nBeans*r)]++;
        }

        for (int i = 0; i < nBeans; i++) {
            if(nPerBeanCurrent[i] != nPerBeanCurrent[i]){
                System.out.println("ERROR!!!");
            }
        }


        System.out.println("Finished checking. Staring dumping");
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
        return undefInfPdf(finish) - undefInfPdf(start);
    }

    static double undefInfPdf(double r){
        double coef = 3 * M / ( 4 * Math.PI * Math.pow(a, 3));
        return coef * 2 * a * r * (r * r + 1.5 * a * a) / (3 * Math.pow(r * r + a * a, 1.5));
    }
}

