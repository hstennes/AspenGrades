package com.aspengrades.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TermSelector {

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
        return doc;
    }

    private String getToken(Document doc){
        return doc.select("input[name=org.apache.struts.taglib.html.TOKEN]").attr("value");
    }
}
