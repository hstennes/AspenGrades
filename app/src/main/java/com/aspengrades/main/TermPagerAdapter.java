package com.aspengrades.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;

public class TermPagerAdapter extends FragmentStatePagerAdapter implements ClassesListener {

    private ClassList[] classLists;

    public TermPagerAdapter(FragmentManager fm) {
        super(fm);
        classLists = new ClassList[ClassList.NUM_TERMS];
    }

    @Override
    public Fragment getItem(int i) {
        System.out.println("Get item " + i);
        TermFragment fragment = new TermFragment();
        fragment.setClassList(classLists[i]);
        return fragment;
    }

    @Override
    public int getCount() {
        return ClassList.NUM_TERMS;
    }

    @Override
    public int getItemPosition(Object item){
        return POSITION_NONE;
    }

    @Override
    public void onClassesRead(ClassList classList) {
        classLists[classList.getTerm() - 1] = classList;
        notifyDataSetChanged();
    }
}
