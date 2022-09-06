package com.aspengrades.main;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;
import com.aspengrades.data.SchoolClass;
import com.aspengrades.util.ColorUtil;

import java.util.HashMap;
import java.util.Locale;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.NO_DATA;
import static com.aspengrades.data.AspenTaskStatus.PARSING_ERROR;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

public class TermFragment extends Fragment implements View.OnClickListener, ClassesListener {

    private ClassList classList;
    private ClassesActivity classesActivity;
    private HashMap<View, SchoolClass> btnToClass;
    private int term;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_term, container, false);
        if(classList == null) view.findViewById(R.id.scroll_view).setVisibility(View.GONE);
        else {
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            if(classList.getStatus() == SUCCESSFUL) setupClassList(view);
            else showStatusMessage(view);
        }
        return view;
    }

    @Override
    public void onClassesRead(ClassList classList) {
        View view = getView();
        this.classList = classList;
        if(view != null) {
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            if(classList.getStatus() == SUCCESSFUL) {
                view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
                setupClassList(view);
            }
            else showStatusMessage(view);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), AssignmentsActivity.class);
        SchoolClass schoolClass = btnToClass.get(v);
        intent.putExtra(getString(R.string.extra_class_id), schoolClass.getId());
        intent.putExtra(getString(R.string.extra_class_description), schoolClass.getDescription());
        intent.putExtra(getString(R.string.extra_teacher), schoolClass.getTeacher());
        intent.putExtra(getString(R.string.extra_schedule), schoolClass.getSchedule());
        intent.putExtra(getString(R.string.extra_clssrm), schoolClass.getClssrm());
        intent.putExtra(getString(R.string.extra_token), classList.getToken());
        intent.putExtra(getString(R.string.extra_cookie_keys), classesActivity.getCookies().getKeys());
        intent.putExtra(getString(R.string.extra_cookie_values), classesActivity.getCookies().getValues());
        intent.putExtra(getString(R.string.extra_term), term);
        classesActivity.getTermLoader().pause();
        startActivity(intent);
    }

    private void setupClassList(View view){
        LinearLayout classesLayout = view.findViewById(R.id.layout_classes);
        TextView textTerm = view.findViewById(R.id.text_term);
        textTerm.setText(getString(R.string.text_term, Integer.toString(classList.getTerm())));

        for(SchoolClass schoolClass : classList) {
            View classButton = getLayoutInflater().inflate(R.layout.class_button, classesLayout, false);
            btnToClass.put(classButton, schoolClass);
            term = classList.getTerm();

            TextView description = classButton.findViewById(R.id.text_description);
            TextView grade = classButton.findViewById(R.id.text_grade);
            description.setText(schoolClass.getDescription());
            float gradeVal = schoolClass.getTermGrade();
            if (gradeVal != SchoolClass.BLANK_GRADE) {
                grade.setText(String.format(Locale.getDefault(), "%.2f", gradeVal));
            }
            classButton.setOnClickListener(this);
            classButton.getBackground().setColorFilter(ColorUtil.colorFromGrade(getContext(), gradeVal), PorterDuff.Mode.SRC);
            classesLayout.addView(classButton);
        }
    }

    private void showStatusMessage(View v){
        TextView textStatus = v.findViewById(R.id.text_status);
        if(classList.getStatus() == ASPEN_UNAVAILABLE){
            textStatus.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorError));
            textStatus.setText(getString(R.string.text_network_error));
        }
        else if(classList.getStatus() == PARSING_ERROR){
            textStatus.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorError));
            textStatus.setText(R.string.text_parsing_error);
        }
        else if(classList.getStatus() == NO_DATA){
            textStatus.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorStatus));
            textStatus.setText(R.string.text_no_classes);
        }
        textStatus.setVisibility(View.VISIBLE);
    }

    public void setParams(ClassList classList, ClassesActivity classesActivity){
        this.classList = classList;
        this.classesActivity = classesActivity;
        btnToClass = new HashMap<>();
    }

    public void reset(){
        View view = getView();
        classList = null;
        btnToClass.clear();
        if(view != null) {
            view.findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);
            view.findViewById(R.id.text_status).setVisibility(View.INVISIBLE);
            LinearLayout classesLayout = view.findViewById(R.id.layout_classes);
            classesLayout.removeViews(1, classesLayout.getChildCount() - 1);
            view.findViewById(R.id.scroll_view).setVisibility(View.GONE);
        }
    }
}