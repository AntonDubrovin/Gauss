package com.approx.third;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс содержащий профильную матрицу
 */
public final class ProfileMatrix {


    /**
     * Переменная хранящая число итераций метода LU разложения
     */
    private int cnt = 0;

    final Function<String, Double> toDouble = Double::parseDouble;
    final Function<String, Integer> toInt = Integer::parseInt;

    /**
     * Имя дериктории, в которой содержатся файлы для текущего тестирования
     */
    private final String directoryName;
    /**
     * Вектор значений диоганальных элементов
     */
    private final List<Double> di;
    /**
     * Значения ненулевых элементов профильной матрицы по строкам
     */
    private final List<Double> al;
    /**
     * Значения ненулевых элементов профильной матрицы по столбцам
     */
    private final List<Double> au;
    /**
     * Массив профилей матрицы
     */
    private final List<Integer> ia;
    /**
     * Вектор правой части
     */
    private final List<Double> b;

    public ProfileMatrix(final String directoryName, final int k) {
        this.directoryName = directoryName;
        di = parseToList("di", toDouble);
        al = parseToList("al", toDouble);
        au = parseToList("au", toDouble);
        ia = parseToList("ia", toInt);
        b = parseToList("b", toDouble);
        evaluateDi(k);
        final List<Double> I = new ArrayList<>();
        for (int i = 0; i < di.size(); i++) {
            I.add(i + 1.0);
        }
        evaluateB(I);
    }

    private void evaluateB(final List<Double> ans) {
        for (int i = 0; i + 1 < ia.size(); i++) {
            for (int j = ia.get(i); j < ia.get(i + 1); j++) {
                final int prof = ia.get(i + 1) - ia.get(i);
                final int zeros = i - prof;
                final int k = (j + zeros - ia.get(i));
                b.set(i, b.get(i) + ans.get(k) * al.get(j));
                b.set(k, b.get(k) + ans.get(i) * au.get(j));
            }
            b.set(i, b.get(i) + ans.get(i) * di.get(i));
        }
    }

    private void evaluateDi(final int k) {
        for (int i = 0; i < di.size(); i++) {
            long sum = 0;
            for (int j = 0; j < di.size(); j++) {
                if (i == j) {
                    continue;
                } else {
                    sum += get(i, j);
                }
            }
            di.set(i, (double) -sum);
        }
        di.set(0, di.get(0) + Math.pow(10, -k));
    }

    /**
     * @return Размерность профильной матрицы
     */
    public int size() {
        return di.size();
    }

    /**
     * Читает массив из файла, и преобразует его в {@code List<T>}
     *
     * @param fileName Имя файла, в котором содежится массив
     * @param function Функция преобразующая строку в {@code List<T>}
     * @param <T>      ЖЕНЕРИГ
     * @return {@code List<T>}
     */
    private <T> List<T> parseToList(final @NotNull String fileName, final Function<String, T> function) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(directoryName).resolve(fileName))) {
            final List<String> currentLine = Arrays.asList(bufferedReader.readLine().split(" "));
            return currentLine.stream().map(function).collect(Collectors.toList());
        } catch (final @NotNull IOException | NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    /**
     * Получает значение матрицы по переданным координатам
     *
     * @param i Строка матрицы
     * @param j Столбец матрицы
     * @return Значение матрицы в строке {@code i}, столбце {@code j}
     * @see #getElement(int, int, List)
     */
    public double get(final int i, final int j) {
        if (i == j) {
            return di.get(i);
        } else if (i > j) {
            return getElement(i, j, al);
        } else {
            return getElement(j, i, au);
        }
    }

    /**
     * Получает значение элемента профильной матрицы по координатам исходной
     *
     * @param i   Строка матрицы
     * @param j   Столбец матрицы
     * @param arr Массив строковых(столбцовых) значений профильной матрицы
     * @return Значение по переданным координатам
     */
    private double getElement(final int i, final int j, final @NotNull List<Double> arr) {
        final int prof = ia.get(i + 1) - ia.get(i);
        final int zeros = i - prof;
        if (j < zeros) {
            return 0.0;
        } else {
            return arr.get(ia.get(i) + (j - zeros));
        }
    }

    /**
     * Устанавливает значение элемента профильной матрицы по координатам исходной
     *
     * @param i        Строка матрицы
     * @param j        Столбец матрицы
     * @param arr      Массив строковых(столбцовых) значений профильной матрицы
     * @param newValue Новое значение
     */
    private void setElement(final int i, final int j, final @NotNull List<Double> arr, final Double newValue) {
        if (i == j) {
            di.set(i, newValue);
            return;
        }
        final int prof = ia.get(i + 1) - ia.get(i);
        final int zeros = i - prof;
        if (j >= zeros) {
            arr.set(ia.get(i) + (j - zeros), newValue);
        }
    }

    /**
     * Устанавливает значение элемента матрицы L по переданным координатам
     *
     * @param i        Строка матрицы L
     * @param j        Столбец матрицы L
     * @param newValue Новое значение
     */
    public void setL(final int i, final int j, final Double newValue) {
        setElement(i, j, al, newValue);
    }

    /**
     * Устанавливает значение элемента матрицы U по переданным координатам
     *
     * @param i        Строка матрицы U
     * @param j        Столбец матрицы U
     * @param newValue Новое значение
     */
    public void setU(final int i, final int j, final Double newValue) {
        setElement(j, i, au, newValue);
    }

    /**
     * Получает значение элемента матрицы L по переданным координатам
     *
     * @param i Строка матрицы L
     * @param j Столбец матрицы L
     * @return Значение элемента матрицы L по переданным координатам
     */
    public double getL(final int i, final int j) {
        if (j > i) return 0.0;
        return get(i, j);
    }

    /**
     * Получает значение элемента матрицы U по переданным координатам
     *
     * @param i Строка матрицы U
     * @param j Столбец матрицы U
     * @return Значение элемента матрицы U по переданным координатам
     */
    public double getU(final int i, final int j) {
        if (i == j) {
            return 1.0;
        } else if (i > j) {
            return 0.0;
        } else {
            return get(i, j);
        }
    }

    /**
     * Деление профильной матрицы на две треугольные матрицы L и U
     */
    public void splitMatrix() {
        setL(0, 0, get(0, 0));
        for (int i = 1; i < size(); i++) {
            for (int j = 0; j < i; j++) {
                setL(i, j, getL(i, j) - sum(i, j, j));
                setU(j, i, (get(j, i) - sum(j, i, j)) / getL(j, j));
                cnt++;
            }
            setL(i, i, get(i, i) - sum(i, i, i));
        }
    }

    /**
     * Получение суммы элементов матрицы вида {@code L(i,k) * U(k,j)} для всех {@code k} в заданных границах
     *
     * @param i      номер строки
     * @param j      номер столбца
     * @param border верхняя граница суммирования
     * @return сумму элементов
     */
    private double sum(final int i, final int j, final int border) {
        double result = 0.0;
        for (int k = 0; k < border; k++) {
            cnt++;
            result += getL(i, k) * getU(k, j);
        }
        return result;
    }

    /**
     * Прямой ход метода Гаусса для матрицы L
     *
     * @return Вектор решений
     */
    public @NotNull List<Double> gaussL() {
        final List<Double> ans = new ArrayList<>();

        for (int i = 0; i < size(); i++) {
            double sum = 0.0;
            for (int j = 0; j < i; j++) {
                cnt++;
                sum += getL(i, j) * ans.get(j);
            }
            cnt++;
            ans.add((b.get(i) - sum) / getL(i, i));
        }
        return ans;
    }

    /**
     * Обратный ход метода Гаусса для матрицы U
     *
     * @param lResult Вектор результата полученного для матрицы L
     * @return Вектор решений
     */
    public @NotNull List<Double> gaussU(final @NotNull List<Double> lResult) {
        final List<Double> ans = new ArrayList<>();

        for (int i = size() - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = size() - 1; j > i; j--) {
                cnt++;
                sum += getU(i, j) * ans.get(size() - 1 - j);
            }
            cnt++;
            ans.add((lResult.get(i) - sum) / getU(i, i));
        }

        Collections.reverse(ans);
        return ans;
    }

    /**
     * Метод Гаусса для разделённой матрицы
     *
     * @return Вектор решений
     */
    public @NotNull List<Double> gauss() {
        return gaussU(gaussL());
    }

    /**
     * Получение числа умножений/делений производимых алгоритмом LU-разложения
     *
     * @return число действий
     */
    public int getCount() {
        return cnt;
    }

    /////////////////////////////////////////////////

    record poshelNahuy(int fuck, double you) {

    }

}