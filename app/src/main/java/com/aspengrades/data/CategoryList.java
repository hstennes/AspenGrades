package com.aspengrades.data;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

/**
 * An ArrayList of Categories that can read category data from Aspen
 */
public class CategoryList extends ArrayList<Category> {

    /**
     * The logging tag for CategoryList
     */
    private static final String TAG = "CategoryList";

    /**
     * The number of unused elements at the start of the category table
     */
    private static final int STARTING_ROWS = 1;

    /**
     * The number of unused elements at the end of the category table
     */
    private static final int ENDING_ROWS = 2;

    /**
     * The cumulative grade for the class. This data is stored in CategoryList because it is found on the page where categories
     * are read.
     */
    private float cumulativeGrade;

    /**
     * The teacher email. This data is stored in CategoryList because it is found on the page where categories are read.
     */
    private String teacherEmail;

    /**
     * Returns a CategoryList for the given class. The class must be in the currently selected term or the result will default to the
     * first class in that term.
     * @param cookies The cookies from LoginManager
     * @param classId The classId of the desired class
     * @param token The classes token from ClassList
     * @return The list of categories in the given class
     * @throws IOException If Aspen could not be reached for any reason
     */
    public CategoryList readCategories(Cookies cookies, String classId, String token) throws IOException {
        Document doc = getDoc(cookies, classId, token);
        Element trCumulative = doc.select("tr:contains(Cumulative)").last();
        Element trEmail = doc.select("tr:matches((Primary email)|(Email 1))").last();
        try {
            cumulativeGrade = Float.parseFloat(trCumulative.text().replaceAll("[^.?0-9]+", ""));
        } catch (NumberFormatException | NullPointerException e){
            cumulativeGrade = SchoolClass.BLANK_GRADE;
        }
        if(trEmail != null && trEmail.childNodeSize() > 1) teacherEmail = "(" + trEmail.child(1).text() + ")";
        else teacherEmail = "";
        Element tbody = doc.select("tbody:contains(Category)").last();
        for(int i = STARTING_ROWS; i < tbody.children().size() - ENDING_ROWS; i += 2){
            add(new Category(tbody.children().get(i), tbody.children().get(i + 1)));
        }
        return this;
    }

    public float getCumulativeGrade(){
        return cumulativeGrade;
    }

    public String getTeacherEmail() {return teacherEmail;}

    /**
     * Returns the "Details" page as a JSoup Document
     * @param cookies The Cookies from LoginManager
     * @return The "Details" page as a JSoup Document
     * @throws IOException If Aspen could not be reached for any reason
     */
    private Document getDoc(Cookies cookies, String classId, String token) throws IOException {
        return Jsoup.connect(ClassList.CLASSES_URL)
                .data("org.apache.struts.taglib.html.TOKEN", token)
                .data("userEvent", ClassList.CLASS_FORM_EVENT)
                .data("userParam", classId)
                .cookies(cookies.getCookieMap())
                .post();
    }
}
