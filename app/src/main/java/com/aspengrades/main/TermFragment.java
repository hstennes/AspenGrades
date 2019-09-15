package com.aspengrades.main;

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
import com.aspengrades.util.ColorUtil;

import java.util.HashMap;
import java.util.Locale;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.NO_DATA;
import static com.aspengrades.data.AspenTaskStatus.PARSING_ERROR;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

public class TermFragment extends Fragment implements View.OnClickListener, ClassesListener {

    private ClassList classList;
    private LayoutInflater inflater;
    private HashMap<View, String> idMap;
    private HashMap<View, String> nameMap;
    private ClassesActivity classesActivity;
    private View view;
    private int term;
    private boolean created = false;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_term, container, false);
        this.inflater = inflater;
        created = true;
        if(classList == null) {
            view.findViewById(R.id.scroll_view).setVisibility(View.GONE);
            return view;
        }
        else {
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            if(classList.getStatus() == SUCCESSFUL) setupClassList();
            else if(classList.getStatus() == ASPEN_UNAVAILABLE)
                showStatusMessage(view, true, getString(R.string.text_network_error));
            else if(classList.getStatus() == PARSING_ERROR)
                showStatusMessage(view, true, getString(R.string.text_parsing_error));
            else if(classList.getStatus() == NO_DATA)
                showStatusMessage(view, false, getString(R.string.text_no_classes));
            return view;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { }

    @Override
    public void onClassesRead(ClassList classList) {
        if(view != null && created) {
            this.classList = classList;
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            if(classList.getStatus() == SUCCESSFUL) {
                view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
                setupClassList();
            }
            else if(classList.getStatus() == ASPEN_UNAVAILABLE)
                showStatusMessage(view, true, getString(R.string.text_network_error));
            else if(classList.getStatus() == PARSING_ERROR)
                showStatusMessage(view, true, getString(R.string.text_parsing_error));
            else if(classList.getStatus() == NO_DATA)
                showStatusMessage(view, false, getString(R.string.text_no_classes));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(classesActivity, AssignmentsActivity.class);
        intent.putExtra(getString(R.string.extra_class_id), idMap.get(v));
        intent.putExtra(getString(R.string.extra_class_description), nameMap.get(v));
        intent.putExtra(getString(R.string.extra_token), classList.getToken());
        intent.putExtra(getString(R.string.extra_cookie_keys), classesActivity.getCookies().getKeys());
        intent.putExtra(getString(R.string.extra_cookie_values), classesActivity.getCookies().getValues());
        intent.putExtra(getString(R.string.extra_term), term);
        classesActivity.getTermLoader().pause();
        startActivity(intent);
    }

    private void setupClassList(){
        LinearLayout classesLayout = view.findViewById(R.id.layout_classes);
        TextView textTerm = view.findViewById(R.id.text_term);
        textTerm.setText(getString(R.string.text_term, Integer.toString(classList.getTerm())));

        for(SchoolClass schoolClass : classList){
            View classButton = inflater.inflate(R.layout.class_button, classesLayout, false);
            idMap.put(classButton, schoolClass.getId());
            nameMap.put(classButton, schoolClass.getDescription());
            term = classList.getTerm();

            TextView description = classButton.findViewById(R.id.text_description);
            TextView grade = classButton.findViewById(R.id.text_grade);
            description.setText(schoolClass.getDescription());
            float gradeVal = schoolClass.getTermGrade();
            if(gradeVal != -1) {
                grade.setText(String.format(Locale.getDefault(), "%.2f", gradeVal));
                classButton.setOnClickListener(this);
            }
            classButton.getBackground().setColorFilter(ColorUtil.colorFromGrade(getContext(), gradeVal), PorterDuff.Mode.SRC);
            classesLayout.addView(classButton);
        }
    }

    private void showStatusMessage(View v, boolean isError, String text){
        TextView textStatus = v.findViewById(R.id.text_status);
        textStatus.setTextColor(ContextCompat.getColor(v.getContext(),
                isError ? R.color.colorError: R.color.colorStatus));
        textStatus.setText(text);
        textStatus.setVisibility(View.VISIBLE);
    }

    public void giveParams(ClassList classList, ClassesActivity classesActivity){
        this.classList = classList;
        this.classesActivity = classesActivity;
        idMap = new HashMap<>();
        nameMap = new HashMap<>();
    }
}
