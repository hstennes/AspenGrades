package com.aspengrades.data;

import android.util.Log;

public class TermLoader implements ClassesListener {

    private ClassesListener classesListener;
    private Cookies cookies;
    private int[] loadingOrder;
    private int loadingIndex;
    private boolean continueLoading;
    private boolean done;

    public TermLoader(ClassesListener classesListener, Cookies cookies){
        this.classesListener = classesListener;
        this.cookies = cookies;
        continueLoading = true;
        done = false;
        loadingIndex = 0;
    }

    public void readAllTerms(int priorityTerm){
        loadingOrder = getLoadingOrder(priorityTerm);
        ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
    }

    @Override
    public void onClassesRead(ClassList classList) {
        if(classList != null) Log.d("TermLoader", "Loaded term " + classList.getTerm());
        if(classesListener != null) classesListener.onClassesRead(classList);
        loadingIndex++;
        if(loadingIndex >= loadingOrder.length) done = true;
        else if(continueLoading)
            ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
    }

    public void pause(){
        continueLoading = false;
    }

    public void resumeIfNecessary(){
        if(!done && !continueLoading) {
            continueLoading = true;
            ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
        }
    }

    private int[] getLoadingOrder(int priorityTerm) {
        int[] result = new int[ClassList.NUM_TERMS];
        result[0] = priorityTerm;

        int iterations = Math.max(priorityTerm - 1, ClassList.NUM_TERMS - priorityTerm);
        int arrayIndex = 1;
        for(int i = 0; i < iterations; i++) {
            int beforeTerm = priorityTerm - i - 1;
            int afterTerm = priorityTerm + i + 1;
            if(beforeTerm > 0) {
                result[arrayIndex] = beforeTerm;
                arrayIndex++;
            }
            if(afterTerm <= ClassList.NUM_TERMS) {
                result[arrayIndex] = afterTerm;
                arrayIndex++;
            }
        }
        return result;
    }
}