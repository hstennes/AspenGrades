package com.aspengrades.data;

public interface ClassInfoListener {

    /**
     * Called when data for ClassInfo has been read
     * @param classInfo The ClassInfo object that was created
     */
    void onClassInfoRead(ClassInfo classInfo);

}
