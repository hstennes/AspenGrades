package com.aspengrades.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;
import com.aspengrades.data.Cookies;
import com.aspengrades.data.TermLoader;

import java.util.HashMap;

public class ClassesActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        Intent intent = getIntent();
        String[] keys = intent.getStringArrayExtra(getString(R.string.extra_cookie_keys));
        String[] values = intent.getStringArrayExtra(getString(R.string.extra_cookie_values));
        HashMap<String, String> cookieMap = new HashMap<>();
        for(int i = 0; i < keys.length; i++){
            cookieMap.put(keys[i], values[i]);
        }
        Cookies cookies = new Cookies(cookieMap);

        TermPagerAdapter adapter = new TermPagerAdapter(getSupportFragmentManager());
        ((ViewPager) findViewById(R.id.pager)).setAdapter(adapter);
        new TermLoader(adapter, cookies).readAllTerms(1);
    }
}
