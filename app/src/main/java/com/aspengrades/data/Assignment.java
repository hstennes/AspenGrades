package com.aspengrades.data;

import android.support.annotation.NonNull;

import org.jsoup.nodes.Element;

public class Assignment {

    private String name;
    private String dateAsgn;
    private String dateDue;
    private String category;
    private float weight;
    private String score;
    private String feedback;

    public Assignment(Element row){
        name = row.child(1).child(0).text();
        if(row.child(7).childNodeSize() == 1) score = row.child(8).child(0).text();
        else score = row.child(7).child(0).text();
        category = row.child(4).text();
        dateAsgn = row.child(2).text();
        dateDue = row.child(3).text();
        weight = Float.parseFloat(row.child(5).text());
        feedback = row.child(8).text();
    }

    public String getName() {
        return name;
    }

    public String getDateAsgn() {
        return dateAsgn;
    }

    public String getDateDue() {
        return dateDue;
    }

    public String getCategory() {
        return category;
    }

    public float getWeight() {
        return weight;
    }

    public String getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    @Override
    @NonNull
    public String toString(){
        return name + ", " + dateAsgn + ", " + dateDue + ", " + category + ", " + weight + ", " + score + ", " + feedback;
    }
}
