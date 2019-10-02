package com.aspengrades.data;

/**
 * Manages the loading of all terms in a specific order
 */
public class TermLoader implements ClassesListener {

    /**
     * The listener to notify when the ClassList for a certain term has been loaded
     */
    private ClassesListener listener;

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
     * The current taskId, which is incremented on each call of readAllTerms to prevent the loading of multiple terms at once
     */
    private int taskId = 0;

    /**
     * Creates a new TermLoader
     * @param cookies The cookies from LoginManager
     */
    public TermLoader(ClassesListener listener, Cookies cookies){
        this.listener = listener;
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
        taskId++;
        this.studentOid = studentOid;
        continueLoading = true;
        done = false;
        loadingIndex = 0;
        loadingOrder = getLoadingOrder(priorityTerm);
        ClassList.readClasses(this, loadingOrder[loadingIndex], studentOid, cookies, taskId);
    }

    /**
     * Notifies the listener that a term has been loaded and begins the loading of the next term in the loading order if appropriate.
     * This method uses taskId to allow only ClassLists resulting from the most recent call of readAllTerms to have an effect. The
     * listener being notified can call pause() or finish() to prevent another term from being loaded even though this term is
     * complete.
     * @param classList The ClassList object that was created
     */
    @Override
    public void onClassesRead(ClassList classList) {
        if(classList.getTaskId() != taskId) return;
        listener.onClassesRead(classList);
        if(done) return;

        loadingIndex++;
        if(loadingIndex >= loadingOrder.length) done = true;
        else if(continueLoading) ClassList.readClasses(this, loadingOrder[loadingIndex], studentOid, cookies, taskId);
    }

    /**
     * Prevents this TermLoader from beginning the loading of another term after the term currently being loaded is complete.  The
     * loading can then be resumed with resumeIfNecessary()
     */
    public void pause(){
        continueLoading = false;
    }

    /**
     * Prevents this TermLoader from beginning the loading of another term after the term being loaded is complete.  Unlike with
     * pause(), the loading cannot be resumed with resumeIfNecessary()
     */
    public void finish(){
        done = true;
    }

    /**
     * Continues with loading if the TermLoader has been paused and the loading is not complete
     */
    public void resumeIfNecessary(){
        if(!done && !continueLoading) {
            continueLoading = true;
            ClassList.readClasses(this, loadingOrder[loadingIndex], studentOid, cookies, taskId);
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