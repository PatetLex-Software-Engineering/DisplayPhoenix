package net.displayphoenix.util;

import java.util.ArrayList;
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
}
