package com.aspengrades.data;

import android.support.annotation.NonNull;

import org.jsoup.nodes.Element;

public class Category {

    private String name;
    private float weight;
    private float grade;

    public Category(Element nameRow, Element gradeRow){
        name = nameRow.child(0).text();
        String weightString = nameRow.child(2).text();
        weight = Float.parseFloat(weightString.substring(0, weightString.length() - 1));

        String gradeString = gradeRow.child(1).text();
        grade = SchoolClass.BLANK_GRADE;
        if(gradeString.length() > 0) grade = Float.parseFloat(gradeString.substring(0, gradeString.length() - 2));
    }

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }

    public float getGrade() {
        return grade;
    }

    @Override
    @NonNull
    public String toString(){
        return name + ", " + weight + ", " + grade;
    }
}
