package com.aspengrades.data;

import java.util.Map;
import java.util.Set;

public class Cookies {

    private Map<String, String> cookies;

    public Cookies(Map<String, String> cookies){
        this.cookies = cookies;
    }

    public Map<String, String> getCookieMap(){
        return cookies;
    }

    public String[] getKeys(){
        return setToArray(cookies.keySet());
    }

    private String[] setToArray(Set<String> set){
        String[] strs = new String[set.size()];
        int i = 0;
        for(String s : set){
            strs[i] = s;
            i++;
        }
        return strs;
    }
}
