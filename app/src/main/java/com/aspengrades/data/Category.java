package com.aspengrades.data;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Element;

/**
 * A class that represents a single Category in Aspen
 */
public class Category {

    /**
     * The text used for a category representing the cumulative grade
     */
    private final static String CUMULATIVE_TEXT = "Cumulative grade";

    /**
     * The name of the category
     */
    private String name;

    /**
     * The weight of the category in calculating the overall grade, taken directly from the text on the Details page
     */
    private String weight;

    /**
     * The student's grade in the category
     */
    private float grade;

    /**
     * True if this category represents the cumulative grade, which occurs if the constructor Category(float grade) is used
     */
    private boolean cumulative = false;

    /**
     * Creates a new Category based on the given JSoup elements from the details page of Aspen
     * @param nameRow The first row in the description of the category, which contains the name and weight
     * @param gradeRow The second row in the description of the category, which contains the grade
     */
    public Category(Element nameRow, Element gradeRow){
        name = nameRow.child(0).text();
        weight = nameRow.child(2).text();

        String gradeString = gradeRow.child(1).text();
        grade = SchoolClass.BLANK_GRADE;
        if(gradeString.length() > 0) grade = Float.parseFloat(gradeString.substring(0, gradeString.length() - 2));
    }

    /**
     * Creates a new Category that represents the cumulative grade for the class. The category name is set to "Cumulative grade" and weight is set to an empty string.
     * @param grade The cumulative grade in the class
     */
    public Category(float grade){
        cumulative = true;
        name = CUMULATIVE_TEXT;
        weight = "";
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public String getWeight() {
        return weight;
    }

    public float getGrade() {
        return grade;
    }

    public boolean isCumulative(){
        return cumulative;
    }

    @Override
    @NonNull
    public String toString(){
        return name + ", " + weight + ", " + grade;
    }
}
