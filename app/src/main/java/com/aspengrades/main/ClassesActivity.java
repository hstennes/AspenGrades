package com.aspengrades.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aspengrades.data.Cookies;
import com.aspengrades.data.LoginListener;
import com.aspengrades.data.LoginManager;
import com.aspengrades.data.TermLoader;

public class ClassesActivity extends AppCompatActivity implements LoginListener {

    private Cookies cookies;
    private TermPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        boolean loggedIn = intent.hasExtra(getString(R.string.extra_cookie_keys));
        if(loggedIn) {
            String[] keys = intent.getStringArrayExtra(getString(R.string.extra_cookie_keys));
            String[] values = intent.getStringArrayExtra(getString(R.string.extra_cookie_values));
            cookies = Cookies.from(keys, values);
        }
        else{
            String username = intent.getStringExtra(getString(R.string.saved_username_key));
            String password = intent.getStringExtra(getString(R.string.saved_password_key));
            LoginManager.attemptLogin(this, username, password);
        }

        adapter = new TermPagerAdapter(getSupportFragmentManager(), this);
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);

        if(loggedIn) new TermLoader(adapter, cookies).readAllTerms(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.credentials_file_key), Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            relaunchMainActivity();
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginSuccessful(Cookies cookies) {
        this.cookies = cookies;
        new TermLoader(adapter, cookies).readAllTerms(1);
    }

    @Override
    public void onInvalidCredentials() {
        relaunchMainActivity();
    }

    @Override
    public void onLoginFailed() {
        relaunchMainActivity();
    }

    private void relaunchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.extra_main_activity_relaunch), true);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_classes, menu);
        return true;
    }

    public Cookies getCookies(){
        return cookies;
    }

    @Override
    public void onBackPressed() {}
}
