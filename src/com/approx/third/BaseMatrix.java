package com.approx.third;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaseMatrix implements Matrix{

    private final List<List<Double>> elements;

    public BaseMatrix(final List<List<Double>> matrix) {
        elements = matrix;
    }

    public BaseMatrix() {
        elements = new ArrayList<>();
    }

    public void add(List<String> elements) {
        this.elements.add(elements.stream().map(Double::parseDouble).collect(Collectors.toList()));
    }

    public Double get(int i, int j) {
        return elements.get(i).get(j);
    }

    public void set(int i, int j, Double element) {
        elements.get(i).set(j, element);
    }

}
