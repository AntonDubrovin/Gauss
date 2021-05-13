package com.approx.third;

import java.util.*;
import java.util.stream.Collectors;

public class BaseMatrix implements Matrix {

    private final List<List<Double>> elements;
    private List<Double> b;

    public BaseMatrix(final List<List<Double>> matrix, List<Double> b) {
        elements = matrix;
        this.b = b;
    }

    public BaseMatrix(final List<Double> b) {
        this(new ArrayList<>(), b);
    }

    public BaseMatrix() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public int size() {
        return elements.size();
    }

    public void add(List<String> elements) {
        this.elements.add(elements.stream().map(Double::parseDouble).collect(Collectors.toList()));
    }

    public void setB(List<Double> solves) {
        b = solves;
    }

    public Double get(int i, int j) {
        return elements.get(i).get(j);
    }

    public void set(int i, int j, Double element) {
        elements.get(i).set(j, element);
    }

    private void fillMap(Map<Integer, Integer> mp) {
        for (int i = 0; i < elements.size(); i++) {
            mp.put(i, i);
        }
    }

    private void remap(Map<Integer, Integer> mp, int from, int to) {
        //System.out.println("mapping " + from + " " + to);
        int fromValue = mp.get(from);
        int toValue = mp.get(to);
        //System.out.println("remapped " + fromValue + " " + toValue + " " + from + " " + to);
        mp.put(from, toValue);
        mp.put(to, fromValue);
    }

    private void sub(int first, int second, int diag) {
        if (get(second, diag) == 0) {
            return;
        }
        double mul = get(second, diag) / get(first, diag);
        b.set(second, b.get(second) - b.get(first) * mul);
        for (int i = 0; i < size(); i++) {
            double newValue = get(second, i) - get(first, i) * mul;
            set(second, i, newValue);
        }
    }

    public List<Double> gauss() {
        Map<Integer, Integer> mp = new HashMap<>();
        List<Double> ans = new ArrayList<>();
        fillMap(mp);
        for (int j = 0; j < elements.size() - 1; j++) {
            int jj = mp.get(j);
            double max = get(jj, j);
            int index = j;
            for (int i = j; i < elements.size(); i++) {
                double value = get(mp.get(i), j);
                if (value > max) {
                    max = value;
                    index = i;
                }
            }
            remap(mp, j, index);
            for (int i = j; i < size(); i++) {
                if (mp.get(i) != index) {
                    sub(index, mp.get(i), j);
                }
            }
        }

        for (int i = size() - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = size() - 1; j > i; j--) {
                sum += get(mp.get(i), j) * ans.get(size() - 1 - j);
            }
            ans.add((b.get(mp.get(i)) - sum) / get(mp.get(i), i));
        }

        Collections.reverse(ans);
        return ans;
    }

}
