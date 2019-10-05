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
import android.view.MenuItem;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;
import com.aspengrades.data.Cookies;
import com.aspengrades.data.LoginListener;
import com.aspengrades.data.LoginManager;
import com.aspengrades.data.TermLoader;
import com.aspengrades.util.AlertUtil;

import java.util.HashMap;

import static com.aspengrades.data.AspenTaskStatus.SESSION_EXPIRED;

public class ClassesActivity extends AppCompatActivity implements LoginListener, ClassesListener, AlertUtil.StudentSelectorCallback {

    private Cookies cookies;
    private ViewPager pager;
    private MenuItem selectStudentItem, refreshItem;
    private TermPagerAdapter adapter;
    private TermLoader termLoader;

    private int favTerm;
    private String studentOid;
    private HashMap<String, String> students;
    private boolean isParentAccount;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        favTerm = readFavTerm();
        adapter = new TermPagerAdapter(getSupportFragmentManager(), this);
        pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        pager.setCurrentItem(favTerm - 1);

        Intent intent = getIntent();
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(intent.getStringExtra(getString(R.string.saved_name_key)));
        isParentAccount = intent.getBooleanExtra(getString(R.string.saved_is_parent_key), false);
        boolean loggedIn = intent.hasExtra(getString(R.string.extra_cookie_keys));
        if(loggedIn) {
            String[] keys = intent.getStringArrayExtra(getString(R.string.extra_cookie_keys));
            String[] values = intent.getStringArrayExtra(getString(R.string.extra_cookie_values));
            cookies = new Cookies(keys, values);
            termLoader = new TermLoader(this, cookies);
            termLoader.readAllTerms(favTerm);
        }
        else{
            String username = intent.getStringExtra(getString(R.string.saved_username_key));
            String password = intent.getStringExtra(getString(R.string.saved_password_key));
            LoginManager.attemptLogin(this, username, password);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classes, menu);
        if(isParentAccount) {
            selectStudentItem = menu.findItem(R.id.action_switch_student);
            selectStudentItem.setVisible(true);
            setEnabled(selectStudentItem, false);
        }
        refreshItem = menu.findItem(R.id.action_refresh);
        setEnabled(refreshItem, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.credentials_file_key), Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            relaunchMainActivity();
            return true;
        }
        else if(item.getItemId() == R.id.action_switch_student){
            if(students == null) return false;
            AlertUtil.showStudentSelector(this, this, students, studentOid);
            return true;
        }
        else if(item.getItemId() == R.id.action_refresh){
            if(termLoader == null) return false;
            adapter.reset();
            termLoader.readAllTerms(pager.getCurrentItem() + 1, studentOid);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStudentSelected(String studentOid) {
        if(!studentOid.equals(this.studentOid)) {
            this.studentOid = studentOid;
            adapter.reset();
            termLoader.readAllTerms(pager.getCurrentItem() + 1, studentOid);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        writeFavTerm();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(termLoader != null) termLoader.resumeIfNecessary();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onLoginSuccessful(Cookies cookies, String name, boolean isParentAccount) {
        this.cookies = cookies;
        termLoader = new TermLoader(this, cookies);
        termLoader.readAllTerms(favTerm, studentOid);
    }

    @Override
    public void onInvalidCredentials() {
        relaunchMainActivity();
    }

    @Override
    public void onLoginFailed() {
        relaunchMainActivity();
    }

    @Override
    public void onClassesRead(ClassList classList) {
        if(classList.getStatus() == SESSION_EXPIRED) {
            termLoader.finish();
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.credentials_file_key), Context.MODE_PRIVATE);
            String username = sharedPreferences.getString(getString(R.string.saved_username_key), "");
            String password = sharedPreferences.getString(getString(R.string.saved_password_key), "");
            LoginManager.attemptLogin(this, username, password);
        }
        else adapter.onClassesRead(classList);
        this.studentOid = classList.getStudentOid();
        this.students = classList.getStudents();
        if(studentOid == null && students != null)
            studentOid = students.values().toArray(new String[1])[0];
        if(selectStudentItem != null && !selectStudentItem.isEnabled() && students != null)
            setEnabled(selectStudentItem, true);
        if(refreshItem != null && !refreshItem.isEnabled())
            setEnabled(refreshItem, true);
    }

    private int readFavTerm(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.fav_term_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getString(R.string.saved_fav_term_key), 1);
    }

    private void writeFavTerm(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.fav_term_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.saved_fav_term_key), pager.getCurrentItem() + 1);
        editor.apply();
    }

    private void relaunchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.extra_main_activity_relaunch), true);
        startActivity(intent);
    }

    private void setEnabled(MenuItem item, boolean enabled){
        item.setEnabled(enabled);
        item.getIcon().setAlpha(enabled ? 255 : 130);
    }

    public TermLoader getTermLoader(){
        return termLoader;
    }

    public Cookies getCookies(){
        return cookies;
    }
}