package com.approx.third;


import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        /*Random random = new Random();
        final MatrixGenerator matrixGenerator = new MatrixGenerator("second_try");
        matrixGenerator.generateProfileMatrix(1, 10);
        final ProfileMatrix profileMatrix = new ProfileMatrix("second_try");
        profileMatrix.splitMatrix();
        System.out.println(profileMatrix.gauss());*/
        //matrixGenerator.generateGilbertMatrix("gilbert", 10, -100, 100);
        //matrixGenerator.generateRandomBaseMatrix("base_random", 5, -100, 100);

        final MatrixGenerator matrixGenerator = new MatrixGenerator("first_try");
        matrixGenerator.generateGilbertMatrix("gilbert", 115, -1000, 1000);
        //matrixGenerator.generateRandomBaseMatrix("base_random", 1000, -100, 100);


        final BaseMatrix baseMatrix = matrixGenerator.parseBaseMatrix("first_try\\gilbert");
        //final BaseMatrix baseRandom = matrixGenerator.parseBaseMatrix("first_try\\base_random");
        matrixGenerator.parseProfileMatrix("first_try\\gilbert");
        //matrixGenerator.parseProfileMatrix("first_try\\base_random");
        final ProfileMatrix profileMatrix = new ProfileMatrix("first_try");
        profileMatrix.splitMatrix();

        //show(baseRandom.gauss());

        final List<Double> ans = (baseMatrix.gauss());
        show(ans);
        System.out.println(check(ans));
        System.out.println(checkNorm(ans));
    }

    public static void show(final @NotNull List<Double> array) {
        for (Double current : array) {
            System.out.print(current + " ");
        }
        System.out.println();
    }

    private static double check(final @NotNull List<Double> array) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of("first_try\\gilbert_answer"))) {
            List<Double> currentLine = Arrays.stream(bufferedReader.readLine().split(" ")).map(Double::parseDouble).collect(Collectors.toList());
            double ans = 0.0;
            for (int i = 0; i < currentLine.size(); i++) {
                ans += (array.get(i) - currentLine.get(i)) * (array.get(i) - currentLine.get(i));
            }
            return Math.sqrt(ans);
        } catch (final IOException ignored) {
            return 0.0;
        }
    }

    private static double checkNorm(final @NotNull List<Double> array) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of("first_try\\gilbert_answer"))) {
            List<Double> currentLine = Arrays.stream(bufferedReader.readLine().split(" ")).map(Double::parseDouble).collect(Collectors.toList());
            double ans = 0.0;
            for (int i = 0; i < currentLine.size(); i++) {
                ans += (array.get(i) - currentLine.get(i)) * (array.get(i) - currentLine.get(i));
            }
            double norm = 0.0;
            for (Double aDouble : currentLine) {
                norm += aDouble * aDouble;
            }
            return Math.sqrt(ans) / Math.sqrt(norm);
        } catch (final IOException ignored) {
            return 0.0;
        }
    }
}
