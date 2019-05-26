package com.aspengrades.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aspengrades.data.Assignment;
import com.aspengrades.data.ClassInfo;
import com.aspengrades.data.ClassInfoListener;
import com.aspengrades.data.Cookies;
import com.aspengrades.data.TermSelector;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.xml.transform.Result;

public class AssignmentsActivity extends AppCompatActivity implements ClassInfoListener {

    private String id;
    private String token;
    private Cookies cookies;

    @Override
    public void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.activity_assignments);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra(getString(R.string.extra_class_description)));
        id = intent.getStringExtra(getString(R.string.extra_class_id));
        token = intent.getStringExtra(getString(R.string.extra_token));
        String[] keys = intent.getStringArrayExtra(getString(R.string.extra_cookie_keys));
        String[] values = intent.getStringArrayExtra(getString(R.string.extra_cookie_values));
        int term = intent.getIntExtra(getString(R.string.extra_term), 0);
        cookies = Cookies.from(keys, values);
        new TermSelectorTask(this, term).execute(cookies);
    }

    public void onTermSelected() {
        ClassInfo.readClassInfo(this, id, token, cookies);
    }

    @Override
    public void onClassInfoRead(ClassInfo classInfo) {
        ProgressBar progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);
        int status  = classInfo.getStatus();

        if(status == ClassInfo.SESSION_EXPIRED)
            AlertUtil.showSessionExpiredAlert(this);
        else if(status == ClassInfo.ASPEN_UNAVAILABLE)
            findViewById(R.id.text_error).setVisibility(View.VISIBLE);
        else setupRecyclerView(classInfo);
    }

    private void setupRecyclerView(ClassInfo classInfo){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CategoryAdapter adapter = new CategoryAdapter(classInfo, this);
        recyclerView.setAdapter(adapter);
    }

    private static class TermSelectorTask extends AsyncTask<Cookies, Void, Void>{
        private WeakReference<AssignmentsActivity> callback;
        private int term;

        public TermSelectorTask(AssignmentsActivity activity, int term){
            callback = new WeakReference<>(activity);
            this.term = term;
        }

        @Override
        protected Void doInBackground(Cookies... cookies) {
            try {
                new TermSelector().selectTerm(cookies[0], term);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing){
            callback.get().onTermSelected();
        }
    }
}