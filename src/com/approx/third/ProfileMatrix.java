package com.approx.third;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ProfileMatrix {

    private final String directoryName;
    private final List<Double> di;
    private final List<Double> al;
    private final List<Double> au;
    private final List<Integer> ia;
    private final List<Double> b;


    final Function<String, Double> toDouble = Double::parseDouble;
    final Function<String, Integer> toInt = Integer::parseInt;

    public ProfileMatrix(final String directoryName) {
        this.directoryName = directoryName;
        di = parseToList("di", toDouble);
        al = parseToList("al", toDouble);
        au = parseToList("au", toDouble);
        ia = parseToList("ia", toInt);
        b = parseToList("b", toDouble);
    }

    public int size() {
        return di.size();
    }

    private <T> List<T> parseToList(final @NotNull String fileName, final Function<String, T> function) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(directoryName).resolve(fileName))) {
            final List<String> currentLine = Arrays.asList(bufferedReader.readLine().split(" "));
            return currentLine.stream().map(function).collect(Collectors.toList());
        } catch (final @NotNull IOException exception) {
            return new ArrayList<>();
        }
    }

    public double get(final int i, final int j) {
        if (i == j) {
            return di.get(i);
        } else if (i > j) {
            return getElement(i, j, al);
        } else {
            return getElement(j, i, au);
        }
    }

    private double getElement(final int i, final int j, final @NotNull List<Double> arr) {
        final int prof = ia.get(i + 1) - ia.get(i);
        final int zeros = i - prof;
        if (j < zeros) {
            return 0.0;
        } else {
            return arr.get(ia.get(i) + (j - zeros) - 1);
        }
    }

    private void setElement(final int i, final int j, final @NotNull List<Double> arr, final Double newValue) {
        if (i == j) {
            di.set(i, newValue);
            return;
        }
        final int prof = ia.get(i + 1) - ia.get(i);
        final int zeros = i - prof;
        if (j >= zeros) {
            arr.set(ia.get(i) + (j - zeros) - 1, newValue);
        }
    }

    public void setL(final int i, final int j, final Double newValue) {
        setElement(i, j, al, newValue);
    }

    public void setU(final int i, final int j, final Double newValue) {
        setElement(j, i, au, newValue);
    }

    public double getL(final int i, final int j) {
        if (j > i) return 0.0;
        return get(i, j);
    }

    public double getU(final int i, final int j) {
        if (i == j) {
            return 1.0;
        } else if (i > j) {
            return 0.0;
        } else {
            return get(i, j);
        }
    }


    public void splitMatrix() {
        setL(0, 0, get(0, 0));
        for (int i = 1; i < size(); i++) {
            for (int j = 0; j < i; j++) {
                setL(i, j, getL(i, j) - sum(i, j, j));
                setU(j, i, (get(j, i) - sum(j, i, j)) / getL(j, j));
            }
            setL(i, i, get(i, i) - sum(i, i, i));
        }
    }

    private double sum(final int i, final int j, final int border) {
        double result = 0.0;
        for (int k = 0; k < border; k++) {
            result += getL(i, k) * getU(k, j);
        }
        return result;
    }

    public @NotNull List<Double> gaussL() {
        final List<Double> ans = new ArrayList<>();

        for (int i = 0; i < size(); i++) {
            double sum = 0.0;
            for (int j = 0; j < i; j++) {
                sum += getL(i, j) * ans.get(j);
            }
            ans.add((b.get(i) - sum) / getL(i, i));
        }
        return ans;
    }

    public @NotNull List<Double> gaussU(final @NotNull List<Double> y) {
        final List<Double> ans = new ArrayList<>();

        for (int i = size() - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = size() - 1; j > i; j--) {
                sum += getU(i, j) * ans.get(size() - 1 - j);
            }
            ans.add((y.get(i) - sum) / getU(i, i));
        }

        Collections.reverse(ans);
        return ans;
    }

    public @NotNull List<Double> gauss() {
        return gaussU(gaussL());
    }

}
