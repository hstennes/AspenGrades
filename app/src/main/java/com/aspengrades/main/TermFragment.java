package com.aspengrades.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;
import com.aspengrades.data.SchoolClass;

import java.util.HashMap;

public class TermFragment extends Fragment implements View.OnClickListener, ClassesListener {

    private ClassList classList;
    private LayoutInflater inflater;
    private HashMap<View, String> buttonMap;
    private ClassesActivity classesActivity;
    private View view;
    private int term;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_term, container, false);
        this.inflater = inflater;
        if(classList == null) {
            view.findViewById(R.id.scroll_view).setVisibility(View.GONE);
            return view;
        }
        else {
            view = inflater.inflate(R.layout.fragment_term, container, false);
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            setupClassList();
            return view;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { }

    @Override
    public void onClassesRead(ClassList classList) {
        if(view != null) {
            this.classList = classList;
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            setupClassList();
        }
    }

    @Override
    public void onClick(View v) {
        System.out.println("Class button pressed");
        Intent intent = new Intent(classesActivity, AssignmentsActivity.class);
        intent.putExtra(getString(R.string.extra_class_id), buttonMap.get(v));
        intent.putExtra(getString(R.string.extra_token), classList.getToken());
        intent.putExtra(getString(R.string.extra_cookie_keys), classesActivity.getCookies().getKeys());
        intent.putExtra(getString(R.string.extra_cookie_values), classesActivity.getCookies().getValues());
        intent.putExtra(getString(R.string.extra_term), term);
        startActivity(intent);
    }

    private void setupClassList(){
        LinearLayout classesLayout = view.findViewById(R.id.layout_classes);
        TextView textTerm = view.findViewById(R.id.text_term);
        textTerm.setText(getString(R.string.text_term, Integer.toString(classList.getTerm())));

        for(SchoolClass schoolClass : classList.getClasses()){
            View classButton = inflater.inflate(R.layout.class_button, classesLayout, false);
            buttonMap.put(classButton, schoolClass.getId());
            term = classList.getTerm();

            TextView description = classButton.findViewById(R.id.text_description);
            TextView grade = classButton.findViewById(R.id.text_grade);
            description.setText(schoolClass.getDescription());
            float gradeVal = schoolClass.getTermGrade();
            if(gradeVal != -1) {
                grade.setText(Float.toString(gradeVal));
                classButton.setOnClickListener(this);
            }
            classButton.getBackground().setColorFilter(getColor(gradeVal), PorterDuff.Mode.SRC);
            classesLayout.addView(classButton, classesLayout.getChildCount() - 1);
        }
    }

    private int getColor(float gradeVal){
        int gradeInt = Math.round(gradeVal);
        Context context = getContext();
        if(context == null) return 0;
        if(gradeInt > 89) return ContextCompat.getColor(context, R.color.colorA);
        else if(gradeInt > 79) return ContextCompat.getColor(context, R.color.colorB);
        else if(gradeInt > 69) return ContextCompat.getColor(context, R.color.colorC);
        else if(gradeInt > 59) return ContextCompat.getColor(context, R.color.colorD);
        else if(gradeInt == -1) return ContextCompat.getColor(context, R.color.colorN);
        else return ContextCompat.getColor(context, R.color.colorF);
    }

    public void giveParams(ClassList classList, ClassesActivity classesActivity){
        this.classList = classList;
        this.classesActivity = classesActivity;
        buttonMap = new HashMap<>();
    }
}
