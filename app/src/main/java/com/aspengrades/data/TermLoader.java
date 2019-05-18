package com.aspengrades.data;

public class TermLoader implements ClassesListener {

    private ClassesListener listener;
    private Cookies cookies;
    private int loadingTerm;
    private int priorityTerm;

    public TermLoader(ClassesListener listener, Cookies cookies){
        this.listener = listener;
        this.cookies = cookies;
        loadingTerm = 0;
    }

    public void readAllTerms(int priorityTerm){
        this.priorityTerm = priorityTerm;
        ClassList.readClasses(this, priorityTerm, cookies);
    }

    @Override
    public void onClassesRead(ClassList classList) {
        System.out.println("TERM LOADED " + classList.getTerm());
        listener.onClassesRead(classList);
        loadingTerm++;
        if(loadingTerm == priorityTerm) loadingTerm++;
        if(loadingTerm <= ClassList.NUM_TERMS) ClassList.readClasses(this, loadingTerm, cookies);
    }
}
