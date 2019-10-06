package com.aspengrades.data;

import java.util.ArrayList;

/**
 * A class that holds student names and IDs for parent accounts. NEVER forget that HashMaps are unordered :(
 */
public class StudentList {

    /**
     * The names of the students
     */
    private ArrayList<String> names;

    /**
     * The ID numbers of the students
     */
    private ArrayList<String> ids;

    /**
     * Creates a new StudentList
     */
    public StudentList(){
        names = new ArrayList<>();
        ids = new ArrayList<>();
    }

    /**
     * Adds a new student to the list
     * @param name The name of the student
     * @param id The ID number
     */
    public void addStudent(String name, String id){
        names.add(name);
        ids.add(id);
    }

    /**
     * Gets the names of the students in the same order as the students were added to the list
     * @return The names of the students
     */
    public ArrayList<String> getNames(){
        return names;
    }

    /**
     * Gets the IDs of the students in the same order as the names
     * @return The IDs of the students
     */
    public ArrayList<String> getIds(){
        return ids;
    }

    /**
     * Tells whether any students have been added to this list.
     * @return false if empty, true otherwise
     */
    public boolean isEmpty(){
        return names.size() == 0;
    }
}
