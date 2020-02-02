package com.aspengrades.data;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Element;

/**
 * A class that represents a single assignment in Aspen
 */
public class Assignment {

    /**
     * The name of the assignment in Aspen
     */
    private String name;

    /**
     * The category that the assignment is in
     */
    private String category;

    /**
     * The score received on the assignment as a String, copied directly from the score column in the table on Aspen
     */
    private String score;

    /**
     * Creates a new Assignment object using the given row from the assignments table
     * @param row The JSoup element containing the row containing the assignment
     * @param nameIndex The index of the cell that holds the assignment name
     * @param categoryIndex The index of the cell that holds the category name
     * @param scoreIndex The index of the cell that holds the score information
     */
    public Assignment(Element row, int nameIndex, int categoryIndex, int scoreIndex){
        name = row.child(nameIndex).text();
        category = row.child(categoryIndex).text();
        score = row.child(scoreIndex).text();
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getScore() {
        return score;
    }

    @Override
    @NonNull
    public String toString(){
        return name + ", " + category + ", " + score;
    }
}
