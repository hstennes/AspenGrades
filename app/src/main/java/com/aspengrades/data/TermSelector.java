package com.aspengrades.data;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static com.aspengrades.data.LoginManager.TIMEOUT;

/**
 * A class for selecting a term on the classes page
 */
public class TermSelector {

    /**
     * Selects the term in the dropdown menu on the classes page of Aspen. A blank value will select the "current term" option
     * @param cookies the cookies from LoginManager
     * @param term the term to select.
     * @param studentOid the student OID (can be null)
     * @return the document of the classes page after selecting the term, or null if the given term was invalid
     * @throws IOException If Aspen could not be reached for any reason
     */
    public Document selectTerm(Cookies cookies, int term, String studentOid) throws IOException {
        Document doc = Jsoup.connect(ClassList.CLASSES_URL).timeout(TIMEOUT).cookies(cookies.getCookieMap()).get();
        if(term >= 0 && term <= ClassList.NUM_TERMS){
            Connection connection = Jsoup.connect(ClassList.CLASSES_URL)
                    .data("org.apache.struts.taglib.html.TOKEN", getToken(doc))
                    .data("userEvent", ClassList.TERM_SELECT_EVENT)
                    .data("yearFilter", "current")
                    .data("termFilter", ClassList.TERM_CODES[term])
                    .cookies(cookies.getCookieMap());
            if(studentOid != null) connection.data("selectedStudentOid", studentOid);
            return connection.post();
        }
        return null;
    }

    private String getToken(Document doc){
        return doc.select("input[name=org.apache.struts.taglib.html.TOKEN]").attr("value");
    }
}
