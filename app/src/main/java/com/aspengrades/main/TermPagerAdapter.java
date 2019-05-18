package com.aspengrades.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;

public class TermPagerAdapter extends FragmentPagerAdapter implements ClassesListener {

    private ClassList[] classLists;
    private Fragment[] fragments;

    public TermPagerAdapter(FragmentManager fm) {
        super(fm);
        classLists = new ClassList[ClassList.NUM_TERMS];
        fragments = new Fragment[ClassList.NUM_TERMS];
    }

    @Override
    public Fragment getItem(int i) {
        TermFragment fragment = new TermFragment();
        fragment.setClassList(classLists[i]);
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
        int index = classList.getTerm() - 1;
        classLists[index] = classList;
        if(fragments[index] != null) ((TermFragment) fragments[index]).onClassesRead(classList);
    }
}
