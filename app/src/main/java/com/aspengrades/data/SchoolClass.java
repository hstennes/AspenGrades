package com.aspengrades.data;

import android.support.annotation.NonNull;

import org.jsoup.nodes.Element;

public class SchoolClass {

    public static final int BLANK_GRADE = -1;
    private String id;
    private String description;
    private float termGrade;

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
