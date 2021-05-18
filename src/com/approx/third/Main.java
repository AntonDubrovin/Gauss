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
        final MatrixGenerator matrixGenerator = new MatrixGenerator("first_try");
        matrixGenerator.generateGilbertMatrix("gilbert", 10, -100, 100);
        matrixGenerator.generateRandomBaseMatrix("base_random", 1000, -100, 100);

        //final BaseMatrix baseMatrix = matrixGenerator.parseBaseMatrix("first_try\\gilbert");
        final BaseMatrix baseRandom = matrixGenerator.parseBaseMatrix("first_try\\base_random");
        matrixGenerator.parseProfileMatrix("first_try\\gilbert");
        //matrixGenerator.parseProfileMatrix("first_try\\base_random");
        //final ProfileMatrix profileMatrix = new ProfileMatrix("first_try");
        //profileMatrix.splitMatrix();
        final List<Double> ans = (baseRandom.gauss());
        show(ans);
        System.out.println(check(ans));
    }

    public static void show(final @NotNull List<Double> array) {
        for (Double current : array) {
            System.out.print(current + " ");
        }
        System.out.println();
    }

    private static boolean check(final @NotNull List<Double> array) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of("first_try\\base_random_answer"))) {
            List<Double> currentLine = Arrays.stream(bufferedReader.readLine().split(" ")).map(Double::parseDouble).collect(Collectors.toList());
            for (int i = 0; i < currentLine.size(); i++) {
                if (Math.abs(array.get(i) - currentLine.get(i)) > 1e-5) {
                    System.out.println(i + " " + array.get(i) + " " + currentLine.get(i));
                    return false;
                }
            }
            return true;
        } catch (final IOException exception) {
            return false;
        }
    }
}
