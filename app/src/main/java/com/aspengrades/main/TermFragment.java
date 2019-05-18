package com.aspengrades.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspengrades.data.ClassList;

public class TermFragment extends Fragment {

    private ClassList classList;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(classList == null) return inflater.inflate(R.layout.fragment_loading, container, false);
        else {
            View view = inflater.inflate(R.layout.fragment_term, container, false);
            TextView textTest = view.findViewById(R.id.text_test);
            textTest.setText(Float.toString(classList.getClasses().get(0).getTermGrade()));
            return view;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void setClassList(ClassList classList){
        this.classList = classList;
    }
}
