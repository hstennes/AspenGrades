package com.aspengrades.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;
import com.aspengrades.data.SchoolClass;

public class TermFragment extends Fragment implements ClassesListener {

    private ClassList classList;
    private LayoutInflater inflater;
    private View view;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void setClassList(ClassList classList){
        this.classList = classList;
    }

    @Override
    public void onClassesRead(ClassList classList) {
        if(view != null) {
            this.classList = classList;
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            setupClassList();
        }
    }

    private void setupClassList(){
        LinearLayout classesLayout = view.findViewById(R.id.layout_classes);
        TextView term = view.findViewById(R.id.text_term);
        term.setText(getString(R.string.text_term, Integer.toString(classList.getTerm())));

        for(SchoolClass schoolClass : classList.getClasses()){
            View classButton = inflater.inflate(R.layout.class_button, classesLayout, false);
            TextView description = classButton.findViewById(R.id.text_description);
            TextView grade = classButton.findViewById(R.id.text_grade);
            description.setText(schoolClass.getDescription());
            float gradeVal = schoolClass.getTermGrade();
            if(gradeVal != -1) {
                grade.setText(Float.toString(gradeVal));
            }
            classesLayout.addView(classButton, classesLayout.getChildCount() - 1);
        }
    }
}
