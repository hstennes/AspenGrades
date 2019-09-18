package com.aspengrades.data;

import java.util.ArrayList;

/**
 * Manages the loading of all terms in a specific order
 */
public class TermLoader implements ClassesListener {

    /**
     * The listener to notify when the ClassList for a certain term has been loaded
     */
    private ArrayList<ClassesListener> listeners;

    /**
     * The cookies from LoginManager
     */
    private Cookies cookies;

    /**
     * The student OID (can be null)
     */
    private String studentOid;

    /**
     * The order to load the terms in
     */
    private int[] loadingOrder;

    /**
     * The index of the term being loaded in the loadingOrder array
     */
    private int loadingIndex;

    /**
     * Tells whether the class should continue loading terms
     */
    private boolean continueLoading;

    /**
     * Tells whether all terms have been loaded
     */
    private boolean done;

    /**
     * Creates a new TermLoader
     * @param cookies The cookies from LoginManager
     */
    public TermLoader(Cookies cookies){
        this.cookies = cookies;
        listeners = new ArrayList<>();
        continueLoading = true;
        done = false;
        loadingIndex = 0;
    }

    /**
     * Adds a listener to be notified when a class list is loaded.
     * @param listener The listener to notify
     */
    public void addClassesListener(ClassesListener listener){
        listeners.add(listener);
    }

    /**
     * Reads all terms, starting with the given term and following with the closest terms to the given term. For example, if term 3 is
     * given, the terms will be loaded in the order 3, 2, 4, 1. If term 4 is given, the terms will be loaded in the order 4, 3, 2, 1.
     * @param priorityTerm The term that will be loaded first
     */
    public void readAllTerms(int priorityTerm){
        readAllTerms(priorityTerm, null);
    }

    /**
     * Reads all terms for the specified student, starting with the given term and following with the closest terms to the given term.
     * For example, if term 3 is given, the terms will be loaded in the order 3, 2, 4, 1. If term 4 is given, the terms will be loaded
     * in the order 4, 3, 2, 1. The student OID can be null unless selecting a specific student on a parent account.
     * @param priorityTerm The term that will be loaded first
     * @param studentOid The student OID
     */
    public void readAllTerms(int priorityTerm, String studentOid){
        this.studentOid = studentOid;
        continueLoading = true;
        done = false;
        loadingIndex = 0;
        loadingOrder = getLoadingOrder(priorityTerm);
        ClassList.readClasses(this, loadingOrder[loadingIndex], studentOid, cookies);
    }

    /**
     * Notifies the listener that a term has been loaded and begins the loading of the next term in the loading order if appropriate
     * @param classList The ClassList object that was created
     */
    @Override
    public void onClassesRead(ClassList classList) {
        if(classList.getStudentOid() != null && !classList.getStudentOid().equals(studentOid)) return;
        for(ClassesListener cl : listeners) cl.onClassesRead(classList);
        loadingIndex++;
        if(loadingIndex >= loadingOrder.length) done = true;
        else if(continueLoading) ClassList.readClasses(this, loadingOrder[loadingIndex], studentOid, cookies);
    }

    /**
     * Prevents this TermLoader from beginning the loading of another term after the term currently being loaded is complete
     */
    public void pause(){
        continueLoading = false;
    }

    /**
     * Continues with loading if the TermLoader has been paused and the loading is not complete
     */
    public void resumeIfNecessary(){
        if(!done && !continueLoading) {
            continueLoading = true;
            ClassList.readClasses(this, loadingOrder[loadingIndex], studentOid, cookies);
        }
    }

    /**
     * Returns the order to load terms in based off of a priority term. See readAllTerms for details.
     * @param priorityTerm The term to prioritize in the order
     * @return The optimal order for loading terms
     */
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