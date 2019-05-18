package com.aspengrades.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aspengrades.data.Cookies;
import com.aspengrades.data.TermLoader;

import java.util.HashMap;

public class ClassesActivity extends AppCompatActivity {

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
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        new TermLoader(adapter, cookies).readAllTerms(1);
    }
}
