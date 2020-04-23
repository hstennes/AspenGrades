package com.aspengrades.util;

public class StringUtil {

    /**
     * Checks to see if a String contains one or more other Strings
     * @param str The String to check
     * @param items The Strings to look for
     * @return True if one or more strings appear, false otherwise
     */
    public static boolean containsAny(String str, String[] items){
        for(String item : items) if(str.contains(item)) return true;
        return false;
    }

}
