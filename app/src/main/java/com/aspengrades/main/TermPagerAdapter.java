package com.aspengrades.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;

public class TermPagerAdapter extends FragmentPagerAdapter implements ClassesListener {

    public static String NO_RESTRICTION = "";

    private Fragment[] fragments;
    private ClassList[] classLists;
    private ClassesActivity classesActivity;
    private String studentOid = NO_RESTRICTION;

    public TermPagerAdapter(FragmentManager fm, ClassesActivity classesActivity) {
        super(fm);
        this.classesActivity = classesActivity;
        classLists = new ClassList[ClassList.NUM_TERMS];
        fragments = new Fragment[ClassList.NUM_TERMS];
    }

    @Override
    public Fragment getItem(int i) {
        TermFragment fragment = new TermFragment();
        fragment.setParams(classLists[i], classesActivity);
        fragments[i] = fragment;

        return fragment;
    }

    @Override
    public int getCount() {
        return ClassList.NUM_TERMS;
    }

    @Override
    public String getPageTitle(int position){
        return "Term " + (position + 1);
    }

    @Override
    public void onClassesRead(ClassList classList) {
        if(studentOid != null &&
                !studentOid.equals(NO_RESTRICTION) &&
                !studentOid.equals(classList.getStudentOid())) return;
        int index = classList.getTerm() - 1;
        classLists[index] = classList;
        if(fragments[index] != null) ((TermFragment) fragments[index]).onClassesRead(classList);
    }

    public void reset(){
        classLists = new ClassList[ClassList.NUM_TERMS];
        for(Fragment fragment : fragments){
            if(fragment != null) ((TermFragment) fragment).reset();
        }
    }

    public void restrictToStudent(String studentOid){
        this.studentOid = studentOid;
    }
}
