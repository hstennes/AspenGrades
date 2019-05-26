package com.aspengrades.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class CategoryList extends ArrayList<Category> {

    public static final String DETAILS_URL = "https://aspen.cps.edu/aspen/portalClassDetail.do?navkey=academics.classes.list.detail";
    private static final int STARTING_ROWS = 1;
    private static final int ENDING_ROWS = 2;

    public CategoryList readCategories(Cookies cookies) throws IOException{
        Document doc;
        doc = getDoc(cookies);
        Element tbody = doc.getElementsByClass("listGridFixed").get(1).child(0).child(0);
        for(int i = STARTING_ROWS; i < tbody.childNodeSize() / 2 - ENDING_ROWS; i += 2){
            add(new Category(tbody.children().get(i), tbody.children().get(i + 1)));
        }
        return this;
    }

    private Document getDoc(Cookies cookies) throws IOException {
        return Jsoup.connect(DETAILS_URL).timeout(10000).cookies(cookies.getCookieMap()).get();
    }
}
