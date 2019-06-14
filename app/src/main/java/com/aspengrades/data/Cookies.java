package com.aspengrades.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of cookies
 */
public class Cookies {

    /**
     * The list of cookies
     */
    private Map<String, String> cookies;

    /**
     * Creates a new Cookies object from a HashMap
     * @param cookies The map containing the cookies as key-value pairs
     */
    public Cookies(Map<String, String> cookies){
        this.cookies = cookies;
    }

    /**
     * Creates a new Cookies object from a list of keys and values
     * @param keys the list of keys
     * @param values the list of values
     */
    public Cookies(String[] keys, String[] values){
        cookies = new HashMap<>();
        for(int i = 0; i < keys.length; i++){
            cookies.put(keys[i], values[i]);
        }
    }

    /**
     * Returns the list of cookies as a HashMap
     * @return the list of cookies
     */
    public Map<String, String> getCookieMap(){
        return cookies;
    }

    /**
     * Returns the list of all keys used in the cookies. The order will match that of the array from getValues()
     * @return the list of all keys
     */
    public String[] getKeys(){
        return cookies.keySet().toArray(new String[1]);
    }

    /**
     * Returns the list of all values used in the cookies. The order will match that of the array from getKeys()
     * @return the list of all values.
     */
    public String[] getValues(){
        return cookies.values().toArray(new String[1]);
    }
}
