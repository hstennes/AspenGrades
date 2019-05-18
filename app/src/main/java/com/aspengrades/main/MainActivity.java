package com.aspengrades.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aspengrades.data.Assignment;
import com.aspengrades.data.AssignmentList;
import com.aspengrades.data.Category;
import com.aspengrades.data.CategoryList;
import com.aspengrades.data.ClassInfo;
import com.aspengrades.data.ClassInfoListener;
import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;
import com.aspengrades.data.Cookies;
import com.aspengrades.data.LoginListener;
import com.aspengrades.data.LoginManager;
import com.aspengrades.data.SchoolClass;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoginListener, ClassesListener, ClassInfoListener {

    private Cookies cookies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.text_error).setVisibility(View.INVISIBLE);
                String username = ((EditText) findViewById(R.id.edit_username)).getText().toString();
                String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();
                LoginManager.attemptLogin(MainActivity.this, username, password);
            }
        });
    }

    @Override
    public void onLoginSuccessful(Cookies cookies){
        System.out.println("Login successful");
        this.cookies = cookies;
        Intent intent = new Intent(MainActivity.this, ClassesActivity.class);
        Map<String, String> cookieMap = cookies.getCookieMap();
        intent.putExtra(getString(R.string.extra_cookie_keys), cookieMap.keySet().toArray(new String[1]));
        intent.putExtra(getString(R.string.extra_cookie_values), cookieMap.values().toArray(new String[1]));
        startActivity(intent);
    }

    @Override
    public void onInvalidCredentials(){
        System.out.println("Incorrect username/password");
        TextView textViewError = findViewById(R.id.text_error);
        textViewError.setText(getString(R.string.text_invalid_credentials));
        textViewError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoginFailed(){
        System.out.println("Login failed");
        TextView textViewError = findViewById(R.id.text_error);
        textViewError.setText(getString(R.string.text_aspen_error));
        textViewError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClassesRead(ClassList classList){
        ArrayList<SchoolClass> classes = classList.getClasses();
        for(SchoolClass schoolClass : classes){
            System.out.println(schoolClass);
        }
        //ClassInfo.readClassInfo(this, classes.get(7).getId(), classList.getToken(), cookies);
        ClassList.readClasses(this, cookies);
    }

    @Override
    public void onClassInfoRead(ClassInfo classInfo){
        CategoryList cList = classInfo.getCategoryList();
        AssignmentList aList = classInfo.getAssignmentList();
        for(Category category : cList) System.out.println(category);
        for(Assignment assignment : aList) System.out.println(assignment);
    }
}
