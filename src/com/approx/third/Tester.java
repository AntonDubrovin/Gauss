package com.approx.third;

import com.approx.third.matrix.BaseMatrix;
import com.approx.third.matrix.ProfileMatrix;
import com.approx.third.matrix.SparseMatrix;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для получения численных результатов наших методов
 */
public class Tester {

    /**
     * Стартовый метод класса
     */
    public static void main(final String[] args) {
        second();
        third();
        fourth();
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
                matrixGenerator.generateProfileDiagonalMatrix(n);
                final ProfileMatrix profileMatrix = new ProfileMatrix(fileName, k);
                profileMatrix.splitMatrix();
                final List<Double> answer = profileMatrix.gauss();
                System.out.println(n + " " + k + " " + checkSecond(answer) + " " + checkSecondNorm(answer));
            }
        }
    }

    /**
     * Получение значений для 3 пункта лабораторной работы
     */
    private static void third() {
        for (int n = 5; n <= 100; n += 5) {
            final String fileName = "third_try_" + n;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            matrixGenerator.generateGilbertMatrix(n);
            matrixGenerator.parseProfileMatrix(fileName + "\\gilbert");
            final ProfileMatrix profileMatrix = new ProfileMatrix(fileName, n);
            profileMatrix.splitMatrix();
            final List<Double> answer = profileMatrix.gauss();
            System.out.println(n + " " + checkSecond(answer) + " " + checkSecondNorm(answer));
        }
    }

    /**
     * Получение значений для 4 пункта лабораторной работы
     */
    private static void fourth() {
        for (int i = 10; i <= 1000; i += 50) {
            final String fileName = "fourth_try_" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            final BaseMatrix baseMatrix = matrixGenerator.generateRandomBaseMatrix("matrix", i, -10, 10);
            matrixGenerator.parseProfileMatrix(fileName + "\\matrix");
            final ProfileMatrix profileMatrix = new ProfileMatrix(fileName, i);
            profileMatrix.splitMatrix();

            final List<Double> baseAnswer = baseMatrix.gauss();
            final List<Double> profileAnswer = profileMatrix.gauss();
            compare(baseAnswer, profileAnswer, fileName, baseMatrix.getCount(), profileMatrix.getCount());
        }
    }

    private static void fifth() {
        fifth_first();
        fifth_second();
        fifth_third();
        fifth_fourth();
    }

    private static void fifth_first() {
        for (int i = 9; i < 10; i++) {
            final String fileName = "fifth_try_first" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            final SparseMatrix sparseMatrix = matrixGenerator.generateSparseMatrix(6);
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
            matrixGenerator.generateSparseDiagonalMatrix(i, true);
            output(i, fileName);

        }
    }

    private static void fifth_third() {
        for (int i = 10; i <= 10; i += 100) {
            final String fileName = "fifth_try_third_" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            matrixGenerator.generateSparseDiagonalMatrix(i, false);
            output(i, fileName);
        }
    }

    private static void output(final int i, final String fileName) {
        final SparseMatrix sparseMatrix = new SparseMatrix(fileName);
        List<Double> solvation = sparseMatrix.conjugate();
        System.out.print(i + " " + sparseMatrix.getIterations() + " " + checkSecond(solvation) + " " + checkSecondNorm(solvation) + " ");
        cond(sparseMatrix, solvation);
    }


    private static void fifth_fourth() {
        for (int i = 10; i <= 100; i += 10) {
            final String fileName = "fifth_try_fourth_" + i;
            final MatrixGenerator matrixGenerator = new MatrixGenerator(fileName);
            final BaseMatrix baseMatrix = matrixGenerator.generateGilbertMatrix(i);
            final SparseMatrix sparseMatrix = new SparseMatrix(baseMatrix.getElements(), baseMatrix.getB());
            final List<Double> solved = sparseMatrix.conjugate();
            System.out.println(i + " " + sparseMatrix.getIterations());
            cond(sparseMatrix, solved);
        }
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

    private static double norm(final @NotNull List<Double> current) {
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
        double norm = 0.0;
        for (int i = 0; i < array.size(); i++) {
            norm += (i + 1) * (i + 1);
        }
        return ans / Math.sqrt(norm);
    }


    private static void cond(final @NotNull SparseMatrix sparseMatrix, final @NotNull List<Double> solved) {
        final double normX = checkSecondNorm(solved);
        final double normRight = norm(sparseMatrix.getB(), sparseMatrix.multiply(solved));
        final double normSolo = norm(sparseMatrix.getB());
        System.out.println(normX / (normRight / normSolo));

    }

}
