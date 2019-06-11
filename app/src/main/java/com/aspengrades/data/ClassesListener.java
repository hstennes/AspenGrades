package com.aspengrades.data;

public interface ClassesListener {

    /**
     * Called when data for a ClassList has been read
     * @param classList The ClassList object that was created
     */
    void onClassesRead(ClassList classList);

}
