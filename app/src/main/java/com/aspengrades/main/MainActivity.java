package com.aspengrades.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progress_circular);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.text_error).setVisibility(View.INVISIBLE);
                String username = ((EditText) findViewById(R.id.edit_username)).getText().toString();
                String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();
                buttonLogin.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                LoginManager.attemptLogin(MainActivity.this, username, password);
            }
        });
    }

    @Override
    public void onLoginSuccessful(Cookies cookies){
        System.out.println("Login successful");
        Intent intent = new Intent(MainActivity.this, ClassesActivity.class);
        intent.putExtra(getString(R.string.extra_cookie_keys), cookies.getKeys());
        intent.putExtra(getString(R.string.extra_cookie_values), cookies.getValues());
        startActivity(intent);
    }

    @Override
    public void onInvalidCredentials(){
        System.out.println("Incorrect username/password");
        loginUnsuccessful(getString(R.string.text_invalid_credentials));
    }

    @Override
    public void onLoginFailed(){
        System.out.println("Login failed");
        loginUnsuccessful(getString(R.string.text_aspen_error));
    }

    private void loginUnsuccessful(String text){
        TextView textViewError = findViewById(R.id.text_error);
        textViewError.setText(text);
        textViewError.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
