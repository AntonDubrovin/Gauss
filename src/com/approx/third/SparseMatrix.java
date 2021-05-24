package com.approx.third;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SparseMatrix {


    private final List<Double> al;
    private final List<Double> au;
    private final List<Double> di;
    private final List<Integer> ia;
    private final List<Integer> ja;
    private final List<Double> b;


    public SparseMatrix(List<List<Double>> matrix, List<Double> b) {
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


    private void evaluateIa(List<List<Double>> matrix,
                            List<Double> all,
                            List<Double> auu,
                            List<Integer> jaa) {
        ia.add(0);
        ia.add(0);
        for (int i = 1; i < matrix.size(); i++) {
            ia.add(i + 1, ia.get(i) + profileLen(matrix, i, all, auu, jaa));
        }

    }

    private int profileLen(List<List<Double>> matrix,
                           int row,
                           List<Double> all,
                           List<Double> auu,
                           List<Integer> jaa) {
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

    private List<Double> evaluateDiagonal(List<List<Double>> matrix) {
        final List<Double> b = new ArrayList<>();
        for (int i = 0; i < matrix.size(); i++) {
            b.add(matrix.get(i).get(i));
        }
        return b;
    }

    private List<Double> multiply(final List<Double> other) {
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
                res.set(column, res.get(column) +  au.get(border + j) * other.get(i));
            }
            border += cnt;
        }
        return res;
    }

    private List<Double> subtract(final List<Double> first, final List<Double> second) {
        final List<Double> result = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            result.add(first.get(i) - second.get(i));
        }
        return result;
    }

    private List<Double> sum(final List<Double> first, final List<Double> second) {
        final List<Double> ans = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            ans.add(first.get(i) + second.get(i));
        }
        return ans;
    }

    private Double scalar(final List<Double> first, final List<Double> second) {
        double ans = 0.0;
        for (int i = 0; i < first.size(); i++) {
            ans += first.get(i) * second.get(i);
        }
        return ans;
    }

    private List<Double> mulOnCnt(final List<Double> list, final Double cnt) {
        final List<Double> ans = new ArrayList<>();
        for (Double aDouble : list) {
            ans.add(aDouble * cnt);
        }
        return ans;
    }

    public List<Double> conjugate() {
        List<Double> x = new ArrayList<>();
        x.add(1.0);
        for (int i = 1; i < b.size(); i++) {
            x.add(0.0);
        }
        List<Double> r = subtract(b, multiply(x));
        List<Double> z = new ArrayList<>(r);
        for (int i = 1; i <= 1000; i++) {
            final List<Double> zz = multiply(z);
            final Double alpha = scalar(r, r) / scalar(zz, z);
            final List<Double> xk = sum(x, mulOnCnt(z, alpha));
            final List<Double> rk = subtract(r, mulOnCnt(zz, alpha));
            double beta = scalar(rk, rk) / scalar(r, r);
            final List<Double> zk = sum(rk, mulOnCnt(z, beta));
            if ((scalar(rk, rk) / scalar(b, b)) <= 1e-7) {
                return xk;
            }
            x = xk;
            r = rk;
            z = zk;

        }
        return x;
    }


    public double get(int i, int j) {
        if (i == j) {
            return di.get(i);
        }
        boolean f = true;
        if (j > i) {
            int tmp = j;
            j = i;
            i = tmp;
            f = false;
        }
        int countInRow = ia.get(i + 1) - ia.get(i);
        List<Integer> getAllColInRow = new ArrayList<>();
        for (int z = ia.get(i); z < ia.get(i) + countInRow; z++) {
            getAllColInRow.add(ja.get(z));
        }
        if (getAllColInRow.contains(j)) {
            if (f) {
                return al.get(ia.get(i) + getAllColInRow.indexOf(j));
            } else {
                return au.get(ia.get(i) + getAllColInRow.indexOf(j));
            }
        } else {
            return 0;
        }
    }
}
