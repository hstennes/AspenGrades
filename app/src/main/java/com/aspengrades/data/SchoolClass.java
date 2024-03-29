package com.aspengrades.data;

import androidx.annotation.NonNull;

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
     * The teacher's name
     */
    private String teacher;

    /**
     * The schedule (period) of the class
     */
    private String schedule;

    /**
     * The classroom
     */
    private String clssrm;

    /**
     * The current grade in the class
     */
    private float termGrade;

    /**
     * Creates a new SchoolClass
     * @param row The row in the classes table containing info about the class
     * @param indexes The column indexes in the table containing each piece of information {class name, teacher, schedule, classroom, grade}
     */
    public SchoolClass(Element row, int[] indexes){
        id = row.child(indexes[0]).attr("id");

        //If this results in index out of bounds, allow exception to be caught as this scenario is not recoverable (class name is essential info)
        description = row.child(indexes[0]).text();

        //If any of these properties were not found, set them to null, which will allow them to be excluded from the displayed info.
        teacher = indexes[1] == -1 ? null : row.child(indexes[1]).text();
        schedule = indexes[2] == -1 ? null : row.child(indexes[2]).text();
        clssrm = indexes[3] == -1 ? null : row.child(indexes[3]).text();

        //Also unrecoverable if not found
        String gradeString = row.child(indexes[4]).text();
        try {
            termGrade = Float.parseFloat(gradeString.replaceAll("[^\\d.]", ""));
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

    public String getTeacher() {
        return teacher;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getClssrm() {
        return clssrm;
    }

    public float getTermGrade() {
        return termGrade;
    }

    @Override
    @NonNull
    public String toString(){
        return id + ", " + description + ", " + teacher + ", " + schedule + ", " + clssrm + ", " + termGrade;
    }
}
