package com.aspengrades.data;

import android.support.annotation.NonNull;

import org.jsoup.nodes.Element;

/**
 * A class that represents a single class/course or whatever you want to call it
 */
public class SchoolClass {

    /**
     * Indicates that the assignment has not yet been graded
     */
    public static final int BLANK_GRADE = -1;

    /**
     * The id of the class, used for retrieving assignment data
     */
    private String id;

    /**
     * The description (name) of the class
     */
    private String description;

    /**
     * The current grade in the class
     */
    private float termGrade;

    /**
     * Creates a new SchoolClass
     * @param row The row in the classes table containing info about the class
     * @param descriptionIndex the index in the row that contains that class description
     * @param gradeIndex the index in the row that contains the grade
     */
    public SchoolClass(Element row, int descriptionIndex, int gradeIndex){
        id = row.child(descriptionIndex).attr("id");
        description = row.child(descriptionIndex).text();
        String gradeString = row.child(gradeIndex).text();
        try {
            termGrade = Float.parseFloat(gradeString);
        } catch (NumberFormatException e){
            termGrade = BLANK_GRADE;
        }
    }

    public String getId(){
        return id;
    }

    public String getDescription() {
        return description;
    }

    public float getTermGrade() {
        return termGrade;
    }

    @Override
    @NonNull
    public String toString(){
        return id + ", " + description + ", " + termGrade;
    }
}
