package com.approx.third.matrix;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SparseMatrix extends AbstractProfileMatrix {

    private final List<Integer> ja;

    public SparseMatrix(final @NotNull List<List<Double>> matrix, final List<Double> b) {
        this.b = b;
        this.di = evaluateDiagonal(matrix);
        this.ia = new ArrayList<>();
        final List<Double> all = new ArrayList<>();
        final List<Double> auu = new ArrayList<>();
        final List<Integer> jaa = new ArrayList<>();
        evaluateIa(matrix, all, auu, jaa);
        this.al = all;
        this.au = auu;
        this.ja = jaa;
    }


    public SparseMatrix(final String directory) {
        this.directoryName = directory;
        di = parseToList("di", toDouble);
        al = parseToList("al", toDouble);
        au = parseToList("au", toDouble);
        ia = parseToList("ia", toInt);
        ja = parseToList("ja", toInt);
        b = parseToList("b", toDouble);
        evaluateDi();
        final List<Double> I = new ArrayList<>();
        for (int i = 0; i < di.size(); i++) {
            I.add(i + 1.0);
        }
        computeBB(I);
    }

    private void computeBB(final @NotNull List<Double> ans) {
        b = multiply(ans);
    }

    private void evaluateDi() {
        final List<Double> I = new ArrayList<>();
        for (int i = 0; i < di.size(); i++) {
            I.add(1.0);
        }
        List<Double> diag = multiply(I);
        for (int i = 0; i < diag.size(); i++) {
            di.set(i, -diag.get(i));
        }
        di.set(0, di.get(0) + 1.0);
    }

    private void evaluateIa(@NotNull List<List<Double>> matrix,
                            @NotNull List<Double> all,
                            @NotNull List<Double> auu,
                            @NotNull List<Integer> jaa) {
        ia.add(0);
        ia.add(0);
        for (int i = 1; i < matrix.size(); i++) {
            ia.add(i + 1, ia.get(i) + profileLen(matrix, i, all, auu, jaa));
        }

    }

    private int profileLen(@NotNull List<List<Double>> matrix,
                           int row,
                           @NotNull List<Double> all,
                           @NotNull List<Double> auu,
                           @NotNull List<Integer> jaa) {
        int ans = 0;
        for (int i = 0; i < row; i++) {
            if (Math.abs(matrix.get(row).get(i)) >= 1e-14) {
                all.add(matrix.get(row).get(i));
                auu.add(matrix.get(i).get(row));
                jaa.add(i);
                ans++;
            }
        }
        return ans;
    }

    private @NotNull List<Double> evaluateDiagonal(@NotNull List<List<Double>> matrix) {
        final List<Double> b = new ArrayList<>();
        for (int i = 0; i < matrix.size(); i++) {
            b.add(matrix.get(i).get(i));
        }
        return b;
    }

    public @NotNull List<Double> multiply(final @NotNull List<Double> other) {
        int border = 0;
        final List<Double> res = new ArrayList<>();
        for (int i = 0; i < other.size(); i++) {
            res.add(0.0);
        }
        for (int i = 0; i < other.size(); i++) {
            int cnt = ia.get(i + 1) - ia.get(i);
            res.set(i, res.get(i) + di.get(i) * other.get(i));
            for (int j = 0; j < cnt; j++) {
                final int column = ja.get(border + j);
                res.set(i, res.get(i) + al.get(border + j) * other.get(column));
                res.set(column, res.get(column) + au.get(border + j) * other.get(i));
            }
            border += cnt;
        }
        return res;
    }

    private @NotNull List<Double> subtract(final @NotNull List<Double> first, final @NotNull List<Double> second) {
        final List<Double> result = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            result.add(first.get(i) - second.get(i));
        }
        return result;
    }

    private @NotNull List<Double> sum(final @NotNull List<Double> first, final @NotNull List<Double> second) {
        final List<Double> ans = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            ans.add(first.get(i) + second.get(i));
        }
        return ans;
    }

    private Double scalar(final @NotNull List<Double> first, final @NotNull List<Double> second) {
        double ans = 0.0;
        for (int i = 0; i < first.size(); i++) {
            ans += first.get(i) * second.get(i);
        }
        return ans;
    }

    private @NotNull List<Double> mulOnCnt(final @NotNull List<Double> list, final Double cnt) {
        final List<Double> ans = new ArrayList<>();
        for (Double aDouble : list) {
            ans.add(aDouble * cnt);
        }
        return ans;
    }

    private int countIterations = 0;

    public int getIterations() {
        return countIterations;
    }

    public @NotNull List<Double> conjugate() {
        countIterations = 0;
        List<Double> x = new ArrayList<>();
        x.add(1.0);
        for (int i = 1; i < b.size(); i++) {
            x.add(0.0);
        }
        List<Double> r = subtract(b, multiply(x));
        List<Double> z = new ArrayList<>(r);
        for (int i = 1; i <= 1000; i++) {
            countIterations++;
            final List<Double> zz = multiply(z);
            final Double alpha = scalar(r, r) / scalar(zz, z);
            final List<Double> xk = sum(x, mulOnCnt(z, alpha));
            final List<Double> rk = subtract(r, mulOnCnt(zz, alpha));
            double beta = scalar(rk, rk) / scalar(r, r);
            final List<Double> zk = sum(rk, mulOnCnt(z, beta));
            if (Math.sqrt(scalar(rk, rk) / scalar(b, b)) <= 1e-12) {
                return xk;
            }
            x = xk;
            r = rk;
            z = zk;

        }
        return x;
    }


}
