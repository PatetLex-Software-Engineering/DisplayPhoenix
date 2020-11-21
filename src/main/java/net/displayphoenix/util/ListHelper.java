package net.displayphoenix.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public static void moveElement(List list, int posA, int posB) {
        int d = posA > posB ? 1 : -1;
        int mi = posA < posB ? posA : posB;
        int ma = posB > posA ? posB : posA;
        Collections.rotate(list.subList(mi, ma + 1), d);
    }
}
