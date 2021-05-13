package com.approx.third;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        final MatrixGenerator matrixGenerator = new MatrixGenerator("first_try");
        matrixGenerator.parseProfileMatrix("testMatrix");

        final ProfileMatrix profileMatrix = new ProfileMatrix("first_try");
        profileMatrix.splitMatrix();

        final BaseMatrix baseMatrix = matrixGenerator.parseBaseMatrix("testMatrix");
        show(profileMatrix.gauss());
        show(baseMatrix.gauss());

    }

    public static void show(final List<Double> array) {
        for (Double current : array) {
            System.out.print(current + " ");
        }
        System.out.println();
    }
}
