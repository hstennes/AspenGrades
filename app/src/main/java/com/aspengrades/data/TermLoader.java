package com.aspengrades.data;

import android.util.Log;

public class TermLoader implements ClassesListener {

    private ClassesListener listener;
    private Cookies cookies;
    private int[] loadingOrder;
    private int loadingIndex;

    public TermLoader(ClassesListener listener, Cookies cookies){
        this.listener = listener;
        this.cookies = cookies;
        loadingIndex = 0;
    }

    public void readAllTerms(int priorityTerm){
        loadingOrder = getLoadingOrder(priorityTerm);
        ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
    }

    @Override
    public void onClassesRead(ClassList classList) {
        Log.d("TermLoader", "Loaded term " + classList.getTerm());
        listener.onClassesRead(classList);
        loadingIndex++;
        if(loadingIndex < loadingOrder.length)
            ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
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
