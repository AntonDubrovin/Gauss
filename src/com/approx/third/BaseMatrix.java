package com.approx.third;

import java.util.*;
import java.util.stream.Collectors;

public final class BaseMatrix implements Matrix {

    private final List<List<Double>> elements;
    private List<Double> b;

    public BaseMatrix(final List<List<Double>> matrix, final List<Double> b) {
        elements = matrix;
        this.b = b;
    }

    public BaseMatrix() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public int size() {
        return elements.size();
    }

    public void add(final List<String> elements) {
        this.elements.add(elements.stream()
                .map(Double::parseDouble)
                .collect(Collectors.toList()));
    }

    public void setB(final List<Double> solves) {
        b = solves;
    }

    public double get(final int i, final int j) {
        return elements.get(i).get(j);
    }

    public void set(final int i, final int j, final Double element) {
        elements.get(i).set(j, element);
    }

    private void fillMap(final Map<Integer, Integer> mp) {
        for (int i = 0; i < elements.size(); i++) {
            mp.put(i, i);
        }
    }

    private void remap(final Map<Integer, Integer> mp, final int from, final int to) {
        int fromValue = mp.get(from);
        int toValue = mp.get(to);
        mp.put(from, toValue);
        mp.put(to, fromValue);
    }

    private void sub(final int first, final int second, final int diag) {
        if (get(second, diag) == 0) {
            return;
        }
        final double mul = get(second, diag) / get(first, diag);
        b.set(second, b.get(second) - b.get(first) * mul);
        for (int i = 0; i < size(); i++) {
            double newValue = get(second, i) - get(first, i) * mul;
            set(second, i, newValue);
        }
    }

    public List<Double> gauss() {
        final Map<Integer, Integer> mp = new HashMap<>();
        final List<Double> ans = new ArrayList<>();
        fillMap(mp);
        for (int j = 0; j < elements.size() - 1; j++) {
            final int jj = mp.get(j);
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
