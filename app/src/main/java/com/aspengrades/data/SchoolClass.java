package com.aspengrades.data;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jsoup.nodes.Element;

public class SchoolClass {

    public static final int BLANK_GRADE = -1;
    private String id;
    private String teacher;
    private String description;
    private String term;
    private String schedule;
    private String room;
    private float termGrade;
    private int abs;
    private int tdy;

    public SchoolClass(Element row){
        String gradeString = row.child(6).text();
        try {
            termGrade = Float.parseFloat(gradeString);
        } catch (NumberFormatException e){
            Log.d("ClassList", "Class grade blank");
            termGrade = BLANK_GRADE;
        }

        id = row.child(1).attr("id");
        description = row.child(1).text();
        teacher = row.child(2).text();
        term = row.child(3).text();
        schedule = row.child(4).text();
        room = row.child(5).text();
        abs = Integer.parseInt(row.child(7).text());
        tdy = Integer.parseInt(row.child(8).text());
    }

    public String getId(){
        return id;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getDescription() {
        return description;
    }

    public String getTerm() {
        return term;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getRoom() {
        return room;
    }

    public float getTermGrade() {
        return termGrade;
    }

    public int getAbs() {
        return abs;
    }

    public int getTdy() {
        return tdy;
    }

    @Override
    @NonNull
    public String toString(){
        return id + ", " + teacher + ", " + description + ", " + term + ", " + schedule + ", " + room + ", " +
                termGrade + ", " + abs + ", " + tdy;
    }
}
