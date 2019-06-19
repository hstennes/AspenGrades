package com.aspengrades.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import static com.aspengrades.data.LoginManager.TIMEOUT;

/**
 * An ArrayList of Categories that can read category data from Aspen
 */
public class CategoryList extends ArrayList<Category> {

    /**
     * The URL of the "Details" page in CPS Aspen
     */
    public static final String DETAILS_URL = "https://aspen.cps.edu/aspen/portalClassDetail.do?navkey=academics.classes.list.detail";

    /**
     * The number of unused elements at the start of the category table
     */
    private static final int STARTING_ROWS = 1;

    /**
     * The number of unused elements at the end of the category table
     */
    private static final int ENDING_ROWS = 2;

    /**
     * Returns a CategoryList based on the currently selected class. This method should only be called from inside and AsyncTask.
     * @param cookies The cookies from LoginManager
     * @return The list of categories for the given class
     * @throws IOException If Aspen could not be reached for any reason
     */
    public CategoryList readCategories(Cookies cookies) throws IOException{
        Document doc;
        doc = getDoc(cookies);
        Element tbody = doc.getElementsByClass("listGridFixed").get(1).child(0).child(0);
        for(int i = STARTING_ROWS; i < tbody.children().size() - ENDING_ROWS; i += 2){
            add(new Category(tbody.children().get(i), tbody.children().get(i + 1)));
        }
        return this;
    }

    /**
     * Returns the "Details" page as a JSoup Document
     * @param cookies The Cookies from LoginManager
     * @return The "Details" page as a JSoup Document
     * @throws IOException If Aspen could not be reached for any reason
     */
    private Document getDoc(Cookies cookies) throws IOException {
        return Jsoup.connect(DETAILS_URL).timeout(TIMEOUT).cookies(cookies.getCookieMap()).get();
    }
}
