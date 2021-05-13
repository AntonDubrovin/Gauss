package com.approx.third;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        MatrixGenerator matrixGenerator = new MatrixGenerator("first_try");
        matrixGenerator.parseProfileMatrix("testMatrix");

        ProfileMatrix profileMatrix = new ProfileMatrix("first_try");
        profileMatrix.splitMatrix();

        List<Double> result = profileMatrix.gauss();
        for (Double an : result) {
            System.out.print(an + " ");
        }
        System.out.println();

        BaseMatrix baseMatrix = matrixGenerator.parseBaseMatrix("testMatrix");
        List<Double> ans = baseMatrix.gauss();
        for (Double an : ans) {
            System.out.print(an + " ");
        }

    }
}
