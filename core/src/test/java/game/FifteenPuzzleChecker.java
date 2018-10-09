package game;

import java.util.Arrays;
import java.util.List;

/**
 * Copied from internet puzzle solutions for finding a sum of unordered elements.
 * These methods were used to check own algorithm.
 */
public class FifteenPuzzleChecker {

    /**
     * Taken from this source http://www.javenue.info/files/Pyatnashki.java
     */
    public Integer checkSum(List<Integer> seq) {
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            if (seq.get(i) == 0) {
                sum += i / 4;
                continue;
            }

            for (int j = i + 1; j < 16; j++) {
                if (seq.get(j) < seq.get(i)) {
                    sum++;
                }
            }
        }
        return sum;
    }

    /**
     * Taken from https://www.dokwork.ru/2012/03/blog-post.html
     */
    public Integer checkSum2(List<Integer> seq) {
        int N = 0;
        int e = 0;
        for (int i = 0; i < seq.size(); i++) {
            /* Определяется номер ряда пустой клетки (считая с 1). */
            if (seq.get(i) == 0) {
                e = i / 4 + 1;
            }
            if (i == 0)
                continue;
            /* Производится подсчет количества клеток меньших текущей */
            for (int j = i + 1; j < seq.size(); j++) {
                if (seq.get(j) < seq.get(i)) {
                    N++;
                }
            }
        }
        N = N + e;
        return N;
    }

    public static List<Integer> getNums() {
        return Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,15,14,0);
    }
}


