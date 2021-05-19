package com.approx.third;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс предназначенный для хранения плотной матрицы
 */
public final class BaseMatrix {


    /**
     * Переменная хранящая число итераций алгоритм Гаусса
     */
    private int cnt = 0;

    /**
     * Двумерный List элементов матрицы
     */
    private final List<List<Double>> elements;
    /**
     * Лист хранящий в себе вектор правой части
     */
    private List<Double> b;

    public BaseMatrix(final List<List<Double>> matrix, final List<Double> b) {
        elements = matrix;
        this.b = b;
    }

    public BaseMatrix() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * @return Размер матрицы
     */
    public int size() {
        return elements.size();
    }

    /**
     * Добавляет вектор элементов в матрицу
     *
     * @param elements Вектор элементов в строковом представлении
     */
    public void add(final @NotNull List<String> elements) {
        this.elements.add(elements.stream()
                .map(Double::parseDouble)
                .collect(Collectors.toList()));
    }

    /**
     * Устанавливает вектор правой части для данной матрицы
     *
     * @param solves Вектор правой части
     */
    public void setB(final List<Double> solves) {
        b = solves;
    }

    /**
     * Метод возвращающий элемент матрицы по её координатам
     *
     * @param i Строка матрицы
     * @param j Столбец матрицы
     * @return Возвращает элемент матрицы по данный координатам
     */
    public double get(final int i, final int j) {
        return elements.get(i).get(j);
    }

    /**
     * Устанавливает новое значение элемента по переданным координатам
     *
     * @param i       Строка матрицы
     * @param j       Столбец матрицы
     * @param element Новое значение
     */
    public void set(final int i, final int j, final Double element) {
        elements.get(i).set(j, element);
    }

    /**
     * Инициализирует словарь значениями по умолчанию
     *
     * @param mp Словарь содержащий отображение текущего расположения строк к изначальному
     */
    private void fillMap(final @NotNull Map<Integer, Integer> mp) {
        for (int i = 0; i < elements.size(); i++) {
            mp.put(i, i);
        }
    }

    /**
     * Меняет две строки местами в словаре
     *
     * @param mp   Словарь содержащий отображение текущего расположения строк к изначальному
     * @param from Индекс первой строки
     * @param to   Индекс второй строки
     */
    private void remap(final @NotNull Map<Integer, Integer> mp, final int from, final int to) {
        int fromValue = mp.get(from);
        int toValue = mp.get(to);
        mp.put(from, toValue);
        mp.put(to, fromValue);
    }

    /**
     * Вычитает из строки матрицы {@code first} строку {@code second} домноженную на константу, приводя элемент второй строки с индексом {@code diag} к нулю
     *
     * @param first  Номер первой строки (которую вычитаем)
     * @param second Номер второй строки (из которой вычитаем)
     * @param diag   Номер столбца, элемент которого приводится к 0
     */
    private void sub(final int first, final int second, final int diag) {
        if (Math.abs(get(second, diag)) <= 1e-20) {
            return;
        }
        cnt++;
        final double mul = get(second, diag) / get(first, diag);
        cnt++;
        b.set(second, b.get(second) - b.get(first) * mul);
        for (int i = 0; i < size(); i++) {
            if (i == diag) {
                set(second, i, 0.0);
            } else {
                cnt++;
                double newValue = get(second, i) - get(first, i) * mul;
                set(second, i, newValue);
            }
        }
    }

    /**
     * Находит решение СЛАУ методом Гаусса с выбором ведущего элемента
     *
     * @return Вектор значений {@code x1..xn}
     */
    public @NotNull List<Double> gauss() {
        final Map<Integer, Integer> mp = new HashMap<>();
        final List<Double> ans = new ArrayList<>();
        fillMap(mp);
        for (int j = 0; j < elements.size(); j++) {
            final int jj = mp.get(j);
            double max = Math.abs(get(jj, j));
            int index = j;
            for (int i = j + 1; i < elements.size(); i++) {
                double value = Math.abs(get(mp.get(i), j));
                if (value >= max) {
                    max = value;
                    index = i;
                }
            }
            remap(mp, j, index);
            for (int i = j + 1; i < size(); i++) {
                sub(mp.get(j), mp.get(i), j);
            }
        }

        for (int i = size() - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = size() - 1; j > i; j--) {
                cnt++;
                sum += get(mp.get(i), j) * ans.get(size() - 1 - j);
            }
            cnt++;
            ans.add((b.get(mp.get(i)) - sum) / get(mp.get(i), i));
        }

        Collections.reverse(ans);
        return ans;
    }

    /**
     * Строковое представление элементов матрицы и вектора правой части
     *
     * @return Строку содержащую строковое представление матрицы
     */
    @Override
    public @NotNull String toString() {
        return elements.size() + System.lineSeparator() + elements
                .stream()
                .map(list -> list.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(System.lineSeparator()))
                + System.lineSeparator() + b.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
    }

    /**
     * Получение числа итераций, проделанных алгоритмом Гаусса
     *
     * @return число итераций
     */
    public int getCount() {
        return cnt;
    }

}
