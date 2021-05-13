package com.approx.third;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Генератор матриц
 */
public final class MatrixGenerator {

    /**
     * Вектор значений диоганальных элементов
     */
    private final @NotNull List<Double> di;
    /**
     * Значения ненулевых элементов профильной матрицы по строкам
     */
    private final @NotNull List<Double> al;
    /**
     * Значения ненулевых элементов профильной матрицы по столбцам
     */
    private final @NotNull List<Double> au;
    /**
     * Массив профилей матрицы
     */
    private final @NotNull List<Integer> ia;
    /**
     * Имя дериктории в которую необходимо генерировать значения
     */
    private final @NotNull String directory;
    /**
     * Вектор правой части
     */
    private List<Double> b;

    public MatrixGenerator(final @NotNull String directory) {
        di = new ArrayList<>();
        au = new ArrayList<>();
        al = new ArrayList<>();
        ia = new ArrayList<>();
        b = new ArrayList<>();
        this.directory = directory;

        createDirectory(directory);
    }

    /**
     * Создаёт директории с переданным названием
     *
     * @param directory Имя дериктории
     */
    private void createDirectory(final @NotNull String directory) {
        if (!Files.exists(Path.of(directory))) {
            try {
                Files.createDirectory(Paths.get(directory));
            } catch (final @NotNull IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Парсит матрицу в плотном формате из переданного файла
     *
     * @param fileName Имя файла из которого получаются значения
     * @return Плотную матрицу
     */
    public @NotNull BaseMatrix parseBaseMatrix(final String fileName) {
        BaseMatrix baseMatrix = new BaseMatrix();
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(fileName))) {
            final int matrixSize = Integer.parseInt(bufferedReader.readLine());
            for (int i = 0; i < matrixSize; i++) {
                List<String> currentLine = Arrays.asList(bufferedReader.readLine().split(" "));
                baseMatrix.add(currentLine);
            }
            baseMatrix.setB(parseB(bufferedReader));
            return baseMatrix;
        } catch (final @NotNull IOException exception) {
            return new BaseMatrix();
        }
    }

    /**
     * Читает вектор правой части из файла
     *
     * @param bufferedReader Класс для чтения из файла
     * @return Массив правой части
     * @throws IOException При невозможности прочитать из файла
     */
    public List<Double> parseB(final BufferedReader bufferedReader) throws IOException {
        return Arrays.stream(bufferedReader.readLine().split(" ")).map(Double::parseDouble).collect(Collectors.toList());
    }

    /**
     * Генерирует профильную матрицу по переданному файлу
     *
     * @param fileName имя файла
     */
    public void parseProfileMatrix(final String fileName) {
        final BaseMatrix baseMatrix = new BaseMatrix();
        ia.add(1);
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(fileName))) {
            final int matrixSize = Integer.parseInt(bufferedReader.readLine());
            for (int i = 0; i < matrixSize; i++) {
                final List<String> currentLine = Arrays.asList(bufferedReader.readLine().split(" "));
                baseMatrix.add(currentLine);
                di.add(Double.parseDouble(currentLine.get(i)));
                int index = -1;
                for (int j = 0; j <= i; j++) {
                    if (Double.parseDouble(currentLine.get(j)) != 0.0) {
                        index = j;
                        break;
                    }
                }
                ia.add(ia.get(i) + i - index);
                for (int k = index; k < i; k++) {
                    al.add(Double.parseDouble(currentLine.get(k)));
                }
            }

            for (int j = 0; j < matrixSize; j++) {
                int index = -1;
                for (int i = 0; i <= j; i++) {
                    if (baseMatrix.get(i, j) != 0.0) {
                        index = i;
                        break;
                    }
                }
                for (int k = index; k < j; k++) {
                    au.add(baseMatrix.get(k, j));
                }
            }

            b = parseB(bufferedReader);

            out();
        } catch (final @NotNull IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Записывает полученные массивы в файлы
     */
    private void out() {
        writeToFile("di", di);
        writeToFile("al", al);
        writeToFile("au", au);
        writeToFile("ia", ia);
        writeToFile("b", b);
    }

    /**
     * Генерирует строковое представление переданного массива элементов
     *
     * @param list Массив значений типа {@code T}
     * @param <T>  ЖЕНЕРИГ
     * @return Строковое представление переданного масссива
     */
    private <T> String listToString(final @NotNull List<T> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    /**
     * Запиывает значения элементов массива {@code values} в файл с переданным именем
     *
     * @param fileName Имя файла для записи
     * @param values   Массив элементов
     * @param <T>      ЖЕНЕРИГ
     */
    private <T> void writeToFile(final @NotNull String fileName, final @NotNull List<T> values) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter((Path.of(directory).resolve(fileName)))) {
            bufferedWriter.write(listToString(values));
        } catch (final @NotNull IOException exception) {
            exception.printStackTrace();
        }
    }
}
