package com.aspengrades.data;

import java.util.HashMap;
import java.util.Map;

public class Cookies {

    private Map<String, String> cookies;

    public Cookies(Map<String, String> cookies){
        this.cookies = cookies;
    }

    public Map<String, String> getCookieMap(){
        return cookies;
    }

    public String[] getKeys(){
        return cookies.keySet().toArray(new String[1]);
    }

    public String[] getValues(){
        return cookies.values().toArray(new String[1]);
    }

    public static Cookies from(String[] keys, String[] values){
        HashMap<String, String> cookieMap = new HashMap<>();
        for(int i = 0; i < keys.length; i++){
            cookieMap.put(keys[i], values[i]);
        }
        return new Cookies(cookieMap);
    }
}
