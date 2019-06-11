package com.aspengrades.data;

import android.support.annotation.NonNull;

import org.jsoup.nodes.Element;

/**
 * A class that represents a single Category in Aspen
 */
public class Category {

    /**
     * The name of the category
     */
    private String name;

    /**
     * The weight of the category in calculating the overall grade, expressed as a percent
     */
    private float weight;

    /**
     * The student's grade in the category
     */
    private float grade;

    /**
     * Creates a new Category based on the given JSoup elements from the details page of Aspen
     * @param nameRow The first row in the description of the category, which contains the name and weight
     * @param gradeRow The second row in the description of the category, which contains the grade
     */
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
