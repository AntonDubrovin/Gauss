package com.approx.third;

public class Main {

    public static void main(String[] args) {
        MatrixGenerator matrixGenerator = new MatrixGenerator("first_try");
        matrixGenerator.parseMatrix("testMatrix");

        ProfileMatrix profileMatrix = new ProfileMatrix("first_try");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(profileMatrix.get(i,j) + " ");
            }
            System.out.println();
        }
        System.out.println();
        profileMatrix.splitMatrix();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(profileMatrix.get(i,j) + " ");
            }
            System.out.println();
        }
    }
}
