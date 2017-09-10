package ru.jgalactics.utils;

import java.util.Arrays;

public class ArrayUtils {
    private ArrayUtils(){}

    public static int[] normalize(double[] arr, int nElems){
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
}
