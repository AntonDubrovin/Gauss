package com.approx.third;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Random random = new Random();
        final MatrixGenerator matrixGenerator = new MatrixGenerator("second_try");
        matrixGenerator.generateProfileMatrix(1, 10);
        final ProfileMatrix profileMatrix = new ProfileMatrix("second_try");
        profileMatrix.splitMatrix();
        System.out.println(profileMatrix.gauss());
        //matrixGenerator.generateGilbertMatrix("gilbert", 10, -100, 100);
        //matrixGenerator.generateRandomBaseMatrix("base_random", 5, -100, 100);
        //final BaseMatrix baseMatrix = matrixGenerator.parseBaseMatrix("first_try\\gilbert");
        //final BaseMatrix baseRandom = matrixGenerator.parseBaseMatrix("first_try\\base_random");
        //matrixGenerator.parseProfileMatrix("first_try\\gilbert");
        //matrixGenerator.parseProfileMatrix("first_try\\base_random");
        //final ProfileMatrix profileMatrix = new ProfileMatrix("first_try");
        //profileMatrix.splitMatrix();
        //show(baseRandom.gauss());
    }

    public static void show(final @NotNull List<Double> array) {
        for (Double current : array) {
            System.out.print(current + " ");
        }
        System.out.println();
    }
}
