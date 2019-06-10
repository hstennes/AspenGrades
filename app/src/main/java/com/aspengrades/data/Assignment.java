package com.aspengrades.data;

import android.support.annotation.NonNull;

import org.jsoup.nodes.Element;

public class Assignment {

    private String name;
    private String category;
    private String score;

    public Assignment(Element row, int nameIndex, int categoryIndex, int scoreIndex){
        name = row.child(nameIndex).text();
        category = row.child(categoryIndex).text();
        score = row.child(scoreIndex).child(0).text();
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
