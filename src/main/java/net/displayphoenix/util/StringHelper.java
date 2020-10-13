package net.displayphoenix.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TBroski
 */
public class StringHelper {

    public static String abbreviateString(String input, int maxLength) {
        return abbreviateString(input, maxLength, true);
    }

    public static String abbreviateString(String input, int maxLength, boolean sumUp) {
        if (input.length() <= maxLength)
            return input;
        else if (sumUp)
            return input.substring(0, maxLength - 3) + "...";
        else
            return input.substring(0, maxLength);
    }

    public static String abbreviateStringInverse(String input, int maxLength) {
        if (input.length() <= maxLength)
            return input;

        return "..." + input.substring(input.length() - maxLength);
    }

    public static String id(String string) {
        String id = string;
        id = id.toLowerCase();
        id = id.replace(' ', '_');
        id = id.replace("\t", "");
        return id;
    }

    public static String condense(String string) {
        String condensedName = string.replace(" ", "");
        condensedName = condensedName.replace("\t", "");
        return condensedName;
    }

    public static String getPrettyPath(String path) {
        return path.replace("\\", "/");
    }

    public static boolean massContains(String input, CharSequence... sequences) {
        boolean flag = false;
        for (CharSequence sequence : sequences) {
            if (input.contains(sequence)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static String[] substringsBetween(final String str, final String open, final String close) {
        if (str == null || open.isEmpty() || close.isEmpty()) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == 0) {
            return new String[] {};
        }
        final int closeLen = close.length();
        final int openLen = open.length();
        final List<String> list = new ArrayList<>();
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            final int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        }
        String[] array = new String[list.size()];
        array = list.toArray(array);
        return array;
    }
}
