package com.patetlex.displayphoenix.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author TBroski
 */
public class ListHelper {
    public static <T> List<T> removeDuplicates(List<T> list) {
        List<T> newList = new ArrayList<>();
        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    public static <T> List<T> remove(List<T> list, Function<T, Boolean> function) {
        List<T> newList = new ArrayList<>();
        for (T element : list) {
            if (function.apply(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    public static void moveElement(List list, int posA, int posB) {
        int d = posA > posB ? 1 : -1;
        int mi = posA < posB ? posA : posB;
        int ma = posB > posA ? posB : posA;
        Collections.rotate(list.subList(mi, ma + 1), d);
    }

    public static float[] toFloatArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}
