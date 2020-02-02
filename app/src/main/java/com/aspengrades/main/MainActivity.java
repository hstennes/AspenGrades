package com.aspengrades.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspengrades.data.Cookies;
import com.aspengrades.data.LoginListener;
import com.aspengrades.data.LoginManager;

public class MainActivity extends AppCompatActivity implements LoginListener {

    private Button buttonLogin;
    private ProgressBar progressBar;
    private String username;
    private String password;
    private String name;
    private boolean isParentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.toolbar_login);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.credentials_file_key), Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(getString(R.string.saved_name_key)) ||
                getIntent().hasExtra(getString(R.string.extra_main_activity_relaunch))) {
            setupButtonListener();
        }
        else {
            Intent intent = new Intent(this, ClassesActivity.class);
            username = sharedPreferences.getString(getString(R.string.saved_username_key), "");
            password = sharedPreferences.getString(getString(R.string.saved_password_key), "");
            name = sharedPreferences.getString(getString(R.string.saved_name_key), LoginManager.DEFAULT_NAME);
            isParentAccount = sharedPreferences.getBoolean(getString(R.string.saved_is_parent_key), false);
            intent.putExtra(getString(R.string.saved_username_key), username);
            intent.putExtra(getString(R.string.saved_password_key), password);
            intent.putExtra(getString(R.string.saved_name_key), name);
            intent.putExtra(getString(R.string.saved_is_parent_key), isParentAccount);
            startActivity(intent);
        }
    }

    @Override
    public void onLoginSuccessful(Cookies cookies, String name, boolean isParentAccount){
        this.name = name;
        this.isParentAccount = isParentAccount;
        saveUsernamePassword();
        Intent intent = new Intent(MainActivity.this, ClassesActivity.class);
        intent.putExtra(getString(R.string.extra_cookie_keys), cookies.getKeys());
        intent.putExtra(getString(R.string.extra_cookie_values), cookies.getValues());
        intent.putExtra(getString(R.string.saved_name_key), name);
        intent.putExtra(getString(R.string.saved_is_parent_key), isParentAccount);
        startActivity(intent);
    }

    @Override
    public void onInvalidCredentials(){
        loginUnsuccessful(getString(R.string.text_invalid_credentials));
    }

    @Override
    public void onLoginFailed(){
        loginUnsuccessful(getString(R.string.text_network_error));
    }

    private void loginUnsuccessful(String text){
        TextView textViewError = findViewById(R.id.text_error);
        textViewError.setText(text);
        textViewError.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void saveUsernamePassword(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.credentials_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.saved_username_key), username);
        editor.putString(getString(R.string.saved_password_key), password);
        editor.putString(getString(R.string.saved_name_key), name);
        editor.putBoolean(getString(R.string.saved_is_parent_key), isParentAccount);
        editor.apply();
    }

    private void setupButtonListener(){
        buttonLogin = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progress_circular);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.text_error).setVisibility(View.INVISIBLE);
                String username = ((EditText) findViewById(R.id.edit_username)).getText().toString().trim();
                String password = ((EditText) findViewById(R.id.edit_password)).getText().toString().trim();
                buttonLogin.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                LoginManager.attemptLogin(MainActivity.this, username, password);
                MainActivity.this.username = username;
                MainActivity.this.password = password;
            }
        });
    }
}
