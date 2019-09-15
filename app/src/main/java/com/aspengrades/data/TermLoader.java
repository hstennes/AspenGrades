package com.aspengrades.data;

/**
 * Manages the loading of all terms in a specific order
 */
public class TermLoader implements ClassesListener {

    /**
     * The listener to notify when the ClassList for a certain term has been loaded
     */
    private ClassesListener classesListener;

    /**
     * The cookies from LoginManager
     */
    private Cookies cookies;

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
     * @param classesListener The listener to notify when a term has been loaded
     * @param cookies The cookies from LoginManager
     */
    public TermLoader(ClassesListener classesListener, Cookies cookies){
        this.classesListener = classesListener;
        this.cookies = cookies;
        continueLoading = true;
        done = false;
        loadingIndex = 0;
    }

    /**
     * Reads all terms, starting with the given term and following with the closest terms to the given term. For example, if term 3 is
     * given, the terms will be loaded in the order 3, 2, 4, 1. If term 4 is given, the terms will be loaded in the order 4, 3, 2, 1.
     * @param priorityTerm The term that will be loaded first
     */
    public void readAllTerms(int priorityTerm){
        loadingOrder = getLoadingOrder(priorityTerm);
        ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
    }

    /**
     * Notifies the listener that a term has been loaded and begins the loading of the next term in the loading order if appropriate
     * @param classList The ClassList object that was created
     */
    @Override
    public void onClassesRead(ClassList classList) {

        if(classList.isParentAccount()) System.out.println("Students: " + classList.getStudents());
        else System.out.println("Not parent account");

        if(classesListener != null) classesListener.onClassesRead(classList);
        loadingIndex++;
        if(loadingIndex >= loadingOrder.length) done = true;
        else if(continueLoading)
            ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
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
            ClassList.readClasses(this, loadingOrder[loadingIndex], cookies);
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