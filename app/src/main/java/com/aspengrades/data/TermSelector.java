package com.aspengrades.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * A class for selecting a term on the classes page
 */
public class TermSelector {

    /**
     * Selects the term in the dropdown menu on the classes page of Aspen. A blank value will select the "current term" option
     * @param cookies the cookies from LoginManager
     * @param term the term to select.
     * @return the document of the classes page after selecting the term, or null if the given term was invalid
     * @throws IOException If Aspen could not be reached for any reason
     */
    public Document selectTerm(Cookies cookies, int term) throws IOException {
        Document doc = Jsoup.connect(ClassList.CLASSES_URL).timeout(10000).cookies(cookies.getCookieMap()).get();
        if(term != 0){
            return Jsoup.connect(ClassList.CLASSES_URL)
                    .data("org.apache.struts.taglib.html.TOKEN", getToken(doc))
                    .data("userEvent", ClassList.TERM_SELECT_EVENT)
                    .data("yearFilter", "current")
                    .data("termFilter", ClassList.TERM_CODES[term])
                    .cookies(cookies.getCookieMap())
                    .post();
        }
        return null;
    }

    private String getToken(Document doc){
        return doc.select("input[name=org.apache.struts.taglib.html.TOKEN]").attr("value");
    }
}
