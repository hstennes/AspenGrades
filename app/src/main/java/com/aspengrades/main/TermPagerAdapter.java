package com.aspengrades.main;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.aspengrades.data.ClassList;
import com.aspengrades.data.ClassesListener;

public class TermPagerAdapter extends FragmentPagerAdapter implements ClassesListener {

    private SparseArray<String> fragmentTags;
    private ClassList[] classLists;
    private ClassesActivity classesActivity;
    private FragmentManager fm;

    public TermPagerAdapter(FragmentManager fm, ClassesActivity classesActivity) {
        super(fm);
        this.fm = fm;
        this.classesActivity = classesActivity;
        fragmentTags = new SparseArray<>();
        classLists = new ClassList[ClassList.NUM_TERMS];
    }

    @Override
    public Fragment getItem(int i) {
        return new TermFragment();
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof TermFragment) {
            TermFragment fragment = (TermFragment) obj;
            fragment.setParams(classLists[position], classesActivity);
            String tag = fragment.getTag();
            fragmentTags.put(position, tag);
        }
        return obj;
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
        Fragment fragment = getFragment(index);
        if(fragment != null) ((TermFragment) fragment).onClassesRead(classList);
    }

    public void reset(){
        classLists = new ClassList[ClassList.NUM_TERMS];
        for(int i = 0; i < ClassList.NUM_TERMS; i++){
            Fragment fragment = getFragment(i);
            if(fragment != null) ((TermFragment) fragment).reset();
        }
    }

    private Fragment getFragment(int position) {
        String tag = fragmentTags.get(position);
        if (tag == null) return null;
        return fm.findFragmentByTag(tag);
    }
}
