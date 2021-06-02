package com.approx.third;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для получения численных результатов наших методов
 */
public class Tester {

    /**
     * Стартовый метод класса
     *
     * @param args ignored
     */
    public static void main(final String[] args) {
        //second();
        //third();
        //fourth();
        fifth();
    }

    /**
     * Получение значений для 2 пункта лабораторной работы
     */
    private static void second() {
        for (int n = 10; n <= 100; n *= 10) {
            for (int k = 0; k <= 10; k++) {
                final String fileName = "second_try_" + n + "_" + k;
                final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
                matrixGenerator.generateProfileGilbertMatrix(k, n);
                final ProfileMatrix profileMatrix = new ProfileMatrix(fileName, k);
                /*for (int i = 0; i < profileMatrix.size(); i++) {
                    for (int j = 0; j < profileMatrix.size(); j++) {
                        System.out.print(profileMatrix.get(i, j) + " ");
                    }
                    System.out.println();
                }
                System.out.println();
                System.out.println();
                System.out.println();*/
                profileMatrix.splitMatrix();
                final List<Double> answer = profileMatrix.gauss();
                System.out.println(n + " " + k + " " + checkSecond(answer) + " " + checkSecondNorm(answer));
            }
        }
    }

    /**
     * Получение значений для 3 пункта лабораторной работы
     */
    /*private static void third() {
        for (int n = 5; n <= 100; n += 5) {
            final String fileName = "third_try_" + n;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            matrixGenerator.generateGilbertMatrix("gilbert", n);
            matrixGenerator.parseProfileMatrix(fileName + "\\gilbert");
            final ProfileMatrix profileMatrix = new ProfileMatrix(fileName);
            profileMatrix.splitMatrix();
            final List<Double> answer = profileMatrix.gauss();
            System.out.println(n + " " + checkSecond(answer) + " " + checkSecondNorm(answer));
        }
    }*/

    /**
     * Получение значений для 4 пункта лабораторной работы
     */
    /*private static void fourth() {
        for (int i = 10; i <= 1000; i += 50) {
            final String fileName = "fourth_try_" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            final BaseMatrix baseMatrix = matrixGenerator.generateRandomBaseMatrix("matrix", i, -10, 10);
            matrixGenerator.parseProfileMatrix(fileName + "\\matrix");
            final ProfileMatrix profileMatrix = new ProfileMatrix(fileName);
            profileMatrix.splitMatrix();


            final List<Double> baseAnswer = baseMatrix.gauss();
            final List<Double> profileAnswer = profileMatrix.gauss();
            compare(baseAnswer, profileAnswer, fileName, baseMatrix.getCount(), profileMatrix.getCount());
        }
    }*/
    private static void fifth() {
        //fifth_first();
        //fifth_second();
        fifth_third();
        //fifth_fourth();
    }

    private static void fifth_first() {
        for (int i = 9; i < 10; i++) {
            final String fileName = "fifth_try_first" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            final SparseMatrix sparseMatrix = matrixGenerator.generateSparseMatrix(6);
            System.out.println("GETTED MATRIX");
            for (int k = 0; k < 6; k++) {
                for (int q = 0; q < 6; q++) {
                    System.out.print(sparseMatrix.get(k, q) + " ");
                }
                System.out.println();
            }

            final List<Double> xes = sparseMatrix.conjugate();
            System.out.println("SOLVED");
            for (Double x : xes) {
                System.out.print(x + " ");
            }
        }
    }

    private static void fifth_second() {
        for (int i = 100; i <= 1000; i += 100) {
            final String fileName = "fifth_try_second_" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            matrixGenerator.generateSparseDiagonalMatrix(i, 100, true);
            final SparseMatrix sparseMatrix = new SparseMatrix(fileName);
            List<Double> solvation = sparseMatrix.conjugate();
            System.out.print(i + " " + sparseMatrix.getIterations() + " " + checkSecond(solvation) + " " + checkSecondNorm(solvation) + " ");
            cond(sparseMatrix, solvation);

        }
    }

    private static void fifth_third() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~");
        for (int i = 10; i <= 10; i += 100) {
            final String fileName = "fifth_try_third_" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            matrixGenerator.generateSparseDiagonalMatrix(i, 100, false);
            final SparseMatrix sparseMatrix = new SparseMatrix(fileName);
            for (int j = 0; j < i; j++) {
                for (int q = 0; q < i; q++) {
                    System.out.print(sparseMatrix.get(j,q) + " ");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
            System.out.println();
            List<Double> solvation = sparseMatrix.conjugate();
            for(int p=0;p<solvation.size();p++) {
                System.out.print(solvation.get(p) + " ");
            }
            System.out.print(i + " " + sparseMatrix.getIterations() + " " + checkSecond(solvation) + " " + checkSecondNorm(solvation) + " ");
            cond(sparseMatrix, solvation);
        }
    }

    private static void fifth_fourth() {
        for (int i = 10; i <= 100; i += 10) {
            final String fileName = "fifth_try_fourth_" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            final BaseMatrix baseMatrix = matrixGenerator.generateGilbertMatrix(fileName, i);
            final SparseMatrix sparseMatrix = new SparseMatrix(baseMatrix.getElements(), baseMatrix.getB());
            final List<Double> solved = sparseMatrix.conjugate();
            // System.out.println(distance(solved));
            System.out.println(i + " " + sparseMatrix.getIterations());
            cond(sparseMatrix, solved);
        }
    }

    private static Double distance(final List<Double> ans) {
        double max = -1.0;
        for (int i = 0; i < ans.size(); i++) {
            max = Math.max(max, Math.abs(ans.get(i) - ((double) i + 1.0)));
        }
        return max;
    }


    private static void compare(final @NotNull List<Double> base,
                                final @NotNull List<Double> profile,
                                final String filename,
                                final int baseCnt,
                                final int profileCnt) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(filename + "\\matrix_answer"))) {
            List<Double> currentLine = Arrays.stream(bufferedReader.readLine().split(" ")).map(Double::parseDouble).collect(Collectors.toList());
            double ans = norm(base, currentLine);
            double ans1 = norm(profile, currentLine);
            double norm = 0.0;
            for (Double aDouble : currentLine) {
                norm += aDouble * aDouble;
            }
            norm = Math.sqrt(norm);
            System.out.println(currentLine.size() + " ||| " + ans + " " + ans / norm + " " + baseCnt +
                    " ||| " + ans1 + " " + ans1 / norm + " " + profileCnt);

        } catch (final @NotNull IOException ignored) {
        }
    }

    private static double norm(final @NotNull List<Double> current, final @NotNull List<Double> answer) {
        double ans = 0.0;
        for (int i = 0; i < answer.size(); i++) {
            ans += (current.get(i) - answer.get(i)) * (current.get(i) - answer.get(i));
        }
        return Math.sqrt(ans);
    }

    private static double norm(final List<Double> current) {
        double ans = 0.0;
        for (Double aDouble : current) {
            ans += aDouble * aDouble;
        }
        return Math.sqrt(ans);
    }

    private static double checkSecond(final @NotNull List<Double> array) {
        double ans = 0.0;
        for (int i = 0; i < array.size(); i++) {
            ans += (array.get(i) - i - 1) * (array.get(i) - i - 1);
        }
        return Math.sqrt(ans);
    }

    private static double checkSecondNorm(final @NotNull List<Double> array) {
        double ans = checkSecond(array);
        /*for (Double aDouble : array) {
            System.out.print(aDouble + " ");
        }
        System.out.println();
        System.out.println();*/
        double norm = 0.0;
        for (int i = 0; i < array.size(); i++) {
            norm += (i + 1) * (i + 1);
        }
        return ans / Math.sqrt(norm);
    }


    private static void cond(final SparseMatrix sparseMatrix, final List<Double> solved) {
        final double normX = checkSecondNorm(solved);
        final double normRight = norm(sparseMatrix.b, sparseMatrix.multiply(solved));
        final double normSolo = norm(sparseMatrix.b);
        System.out.println("NORM SOLO IS " + normSolo);
        System.out.println("NORMX IS " + normX);
        System.out.println("NORM Right IS " + normRight);
        System.out.println(normX  / (normRight/normSolo));

    }

    private static List<Double> subtractVectors(final List<Double> first,
                                                final List<Double> second) {
        final List<Double> diff = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            diff.add(first.get(i) - second.get(i));
        }
        return diff;
    }
}
