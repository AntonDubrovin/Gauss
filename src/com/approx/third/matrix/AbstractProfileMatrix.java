package com.approx.third.matrix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AbstractProfileMatrix {

    /**
     * Переменная хранящая число итераций метода LU разложения
     */
    protected int cnt = 0;

    protected final Function<String, Double> toDouble = Double::parseDouble;
    protected final Function<String, Integer> toInt = Integer::parseInt;

    /**
     * Имя дериктории, в которой содержатся файлы для текущего тестирования
     */
    protected String directoryName;
    /**
     * Вектор значений диоганальных элементов
     */
    protected List<Double> di;
    /**
     * Значения ненулевых элементов профильной матрицы по строкам
     */
    protected List<Double> al;
    /**
     * Значения ненулевых элементов профильной матрицы по столбцам
     */
    protected List<Double> au;
    /**
     * Массив профилей матрицы
     */
    protected List<Integer> ia;
    /**
     * Вектор правой части
     */
    protected List<Double> b;


    public List<Double> getB() {
        return b;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractProfileMatrix that = (AbstractProfileMatrix) o;
        return cnt == that.cnt && toDouble.equals(that.toDouble) && toInt.equals(that.toInt) && directoryName.equals(that.directoryName) && di.equals(that.di) && al.equals(that.al) && au.equals(that.au) && ia.equals(that.ia) && getB().equals(that.getB());
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnt, toDouble, toInt, directoryName, di, al, au, ia, getB());
    }

    protected <T> List<T> parseToList(final @NotNull String fileName, final Function<String, T> function) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(directoryName).resolve(fileName))) {
            final List<String> currentLine = Arrays.asList(bufferedReader.readLine().split(" "));
            return currentLine.stream().map(function).collect(Collectors.toList());
        } catch (final @NotNull IOException | NullPointerException exception) {
            return new ArrayList<>();
        }
    }
}
