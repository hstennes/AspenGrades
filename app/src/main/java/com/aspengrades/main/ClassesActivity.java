package com.aspengrades.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.aspengrades.data.Cookies;
import com.aspengrades.data.TermLoader;

public class ClassesActivity extends AppCompatActivity {

    private Cookies cookies;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        Intent intent = getIntent();
        String[] keys = intent.getStringArrayExtra(getString(R.string.extra_cookie_keys));
        String[] values = intent.getStringArrayExtra(getString(R.string.extra_cookie_values));
        cookies = Cookies.from(keys, values);

        TermPagerAdapter adapter = new TermPagerAdapter(getSupportFragmentManager(), this);
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        new TermLoader(adapter, cookies).readAllTerms(1);
    }

    public Cookies getCookies(){
        return cookies;
    }
}
