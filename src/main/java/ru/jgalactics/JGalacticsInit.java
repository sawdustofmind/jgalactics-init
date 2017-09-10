package ru.jgalactics;

public class JGalacticsInit {
    public static void main(String[] args) {
        int nStars = 1000000;
        double maxR = 1;
        int nBeans = 8000;
        double[][] data = new Bulge(1, 0.25).getThrowedArray(nStars, nBeans, maxR);
        new DataProvider("/home/david/").dump(data, "data.bin");
    }

}

