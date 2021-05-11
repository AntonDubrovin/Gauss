package com.approx.third;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProfileMatrix implements Matrix {

    private final String directoryName;
    private final List<Double> di;
    private final List<Double> al;
    private final List<Double> au;
    private final List<Integer> ia;

    private final int size;


    Function<String, Double> toDouble = Double::parseDouble;
    Function<String, Integer> toInt = Integer::parseInt;

    public ProfileMatrix(String directoryName) {
        this.directoryName = directoryName;
        di = parseToList("di", toDouble);
        al = parseToList("al", toDouble);
        au = parseToList("au", toDouble);
        ia = parseToList("ia", toInt);

        if (di != null) {
            size = di.size();
        } else {
            size = 0;
        }
    }


    private <T> List<T> parseToList(String fileName, Function<String, T> function) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(directoryName).resolve(fileName))) {
            List<String> currentLine = Arrays.asList(bufferedReader.readLine().split(" "));
            return currentLine.stream().map(function).collect(Collectors.toList());
        } catch (IOException exception) {
            return null;
        }
    }

    public Double get(int i, int j) {
        if (i == j) {
            return di.get(i);
        }else if (i > j) {
            return getElement(i, j, al);
        } else {
            return getElement(j, i, au);
        }
    }

    private Double getElement(int i, int j, List<Double> arr) {
        int prof = ia.get(i + 1) - ia.get(i);
        int zeros = i - prof;
        if (j < zeros) {
            return 0.0;
        } else {
            return arr.get(ia.get(i) + (j - zeros) - 1);
        }
    }

    private boolean setElement(int i, int j, List<Double> arr, Double newValue) {
        if(i == j){
            di.set(i,newValue);
            return true;
        }
        int prof = ia.get(i + 1) - ia.get(i);
        int zeros = i - prof;
        if (j >= zeros) {
            arr.set(ia.get(i) + (j - zeros) - 1, newValue);
            return true;
        }
        return false;
    }

    public boolean setL(int i, int j, Double newValue) {
        return setElement(i, j, al, newValue);
    }

    public boolean setU(int i, int j, Double newValue) {
        return setElement(j, i, au, newValue);
    }

    public double getL(int i, int j) {
        if (j > i) return 0.0;
        return get(i, j);
    }

    public double getU(int i, int j) {
        if (i == j) {
            return 1.0;
        }else if (i > j) {
            return 0.0;
        } else {
            return get(i, j);
        }
    }


    public void splitMatrix() {
        setL(0, 0, get(0, 0));
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < i; j++) {
                setL(i, j, getL(i, j) - sum(i, j, j));
                setU(j, i, (get(j, i) - sum(j, i, j)) / getL(j, j));
            }
            setL(i, i, get(i, i) - sum(i, i, i));
        }
    }

    private Double sum(int i, int j, int border) {
        double result = 0.0;
        for (int k = 0; k < border; k++) {
            result += getL(i, k) * getU(k, j);
        }
        return result;
    }


}
