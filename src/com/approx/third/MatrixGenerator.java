package com.approx.third;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
                System.err.println(exception.getMessage());
            }
        }
    }


    /**
     * Генерирует список заданного размера заполненный случайными значениями в пределах переданных значений
     *
     * @param n   Размерность списка
     * @param min Минимальное значение элементов
     * @param max Максимальное значение элементов
     * @return Сгенерированный список
     */
    public @NotNull List<Double> generateList(final int n, final double min, final double max) {
        final List<Double> ans = new ArrayList<>(n);
        final Random random = new Random();
        for (int i = 0; i < n; i++) {
            ans.add(min + (max - min) * random.nextDouble());
        }
        return ans;
    }

    /**
     * Вычисляет значение вектора правой части по переданной матрице и вектору решений
     *
     * @param baseMatrix Матрица
     * @param ans        Вектор решений
     * @return Вектор правой части
     */
    public @NotNull List<Double> computeB(final @NotNull BaseMatrix baseMatrix, final @NotNull List<Double> ans) {
        final List<Double> b = new ArrayList<>();
        for (int k = 0; k < baseMatrix.size(); k++) {
            double sum = 0;
            for (int i = 0; i < baseMatrix.size(); i++) {
                sum += ans.get(i) * baseMatrix.get(k, i);
            }
            b.add(sum);
        }
        return b;
    }

    /**
     * Устанавливвает значение вектора правой части по переданному вектору решения
     *
     * @param ans Вектор решений
     */
    public void computeB(final @NotNull List<Double> ans) {
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

    /**
     * Генерирует Гильбертову матрицу в профильном формате и записывает её в файл
     *
     * @param k параметр Гильбертовой матрицы
     * @param n Размерность матрицы
     * @see #out()
     */
    public void generateProfileGilbertMatrix(final int k, final int n) {
        final int MOD = 5;
        final int MAX_DISTANCE_FROM_DIAGONAL = 5;
        ia.add(0);
        final Random random = new Random();
        long sum = 0;
        for (int i = 0; i < n; i++) {
            int index = Integer.max((random.nextInt() % (i + 1) + (i + 1)) % (i + 1), i - MAX_DISTANCE_FROM_DIAGONAL);
            ia.add(ia.get(i) + i - index);
            for (int j = index; j < i; j++) {
                int ran = (random.nextInt() % MOD + MOD) % MOD;
                al.add((double) -ran);
                sum += ran;
            }
            for (int j = index; j < i; j++) {
                int ran = (random.nextInt() % MOD + MOD) % MOD;
                au.add((double) -ran);
                sum += ran;
            }
        }
        List<Double> ans = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            ans.add((double) i);
            b.add(0.0);
        }
        di.add((double) sum + Math.pow(10.0, -k));
        for (int i = 1; i < n; i++) {
            di.add((double) sum);
        }
        computeB(ans);
        out();
    }

    /**
     * Генерирует матрицу размерноти {@code n} и векрот решений в пределах от {@code min} до {@code max}
     *
     * @param fileName Имя файла
     * @param n        Размерность матрицы
     * @param min      Минимальное значение
     * @param max      Максимальное значение
     * @return Матрицу в плотном формате
     */
    public @NotNull BaseMatrix generateRandomBaseMatrix(final @NotNull String fileName, final int n, final double min, final double max) {
        final List<List<Double>> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(new ArrayList<>(Collections.nCopies(n, 0.0)));
        }
        final List<Double> ans = generateList(n, min, max);
        final BaseMatrix baseMatrix = new BaseMatrix(list, new ArrayList<>());
        final Random random = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                baseMatrix.set(i, j, (min + (max - min) * random.nextDouble()));
            }
        }
        baseMatrix.setB(computeB(baseMatrix, ans));
        printMatrix(baseMatrix, ans, fileName);
        return baseMatrix;
    }

    /**
     * Генерирует Гильбертову матрицу размерноти {@code n} и векрот решений в пределах от {@code min} до {@code max}
     *
     * @param fileName Имя файла
     * @param n        Размерность матрицы
     */
    public void generateGilbertMatrix(final @NotNull String fileName, final int n) {
        final List<List<Double>> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(new ArrayList<>(Collections.nCopies(n, 0.0)));
        }
        final List<Double> ans = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ans.add(i + 1.0);
        }
        final BaseMatrix baseMatrix = new BaseMatrix(list, new ArrayList<>());
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                baseMatrix.set(i, j, 1.0 / (i + j + 1));
            }
        }
        baseMatrix.setB(computeB(baseMatrix, ans));
        printMatrix(baseMatrix, ans, fileName);
    }

    /**
     * Вывод сгенерированной матрицы в файл
     *
     * @param baseMatrix Исходная матрица
     * @param ans        Вектор решений
     * @param fileName   Имя файла
     */
    public void printMatrix(final @NotNull BaseMatrix baseMatrix,
                            final @NotNull List<Double> ans,
                            final @NotNull String fileName) {
        try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(directory).resolve(fileName))) {
            final BufferedWriter bufferedWriterAns = Files.newBufferedWriter(Path.of(directory).resolve(fileName + "_answer"));
            bufferedWriter.write(baseMatrix.toString());
            bufferedWriterAns.write(ans.stream().map(String::valueOf).collect(Collectors.joining(" ")));
            bufferedWriterAns.close();
        } catch (final @NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Читает вектор правой части из файла
     *
     * @param bufferedReader Класс для чтения из файла
     * @return Массив правой части
     * @throws IOException При невозможности прочитать из файла
     */
    public List<Double> parseB(final @NotNull BufferedReader bufferedReader) throws IOException {
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
            System.err.println(exception.getMessage());
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
    private <T> void writeToFile(final @NotNull String fileName,
                                 final @NotNull List<T> values) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter((Path.of(directory).resolve(fileName)))) {
            bufferedWriter.write(listToString(values));
        } catch (final @NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}