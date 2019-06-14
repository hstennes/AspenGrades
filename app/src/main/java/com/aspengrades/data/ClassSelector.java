package com.aspengrades.data;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * A class for selecting a course to read assignment data from
 */
public class ClassSelector {

    /**
     * Selects a class so that subsequent calls to CategoryList and Assignment list will retrieve data for the given class. Only call
     * this method from inside an AsyncTask and after the proper term has been selected using TermSelector
     * @param params The ClassInfo.TaskParams used for supplying the token, cookies, and class ID
     * @throws IOException If Aspen could not be reached for any reason
     */
    public void selectClass(ClassInfo.TaskParams params) throws IOException{
        Jsoup.connect(ClassList.CLASSES_URL)
                .data("org.apache.struts.taglib.html.TOKEN", params.getClassesToken())
                .data("userEvent", ClassList.CLASS_FORM_EVENT)
                .data("userParam", params.getClassId())
                .cookies(params.getCookies().getCookieMap())
                .post();
    }
}
