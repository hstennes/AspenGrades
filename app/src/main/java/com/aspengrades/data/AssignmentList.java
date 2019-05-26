package com.aspengrades.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class AssignmentList extends ArrayList<Assignment> {

    public static final String ASSIGNMENTS_URL = "https://aspen.cps.edu/aspen/portalAssignmentList.do?navkey=academics.classes.list.gcd";
    private static final String ASSIGNMENT_FORM_EVENT = "2210";

    public AssignmentList readAssignments(Cookies cookies, String classesToken) throws IOException{
        return readAssignments(cookies, "", classesToken);
    }

    public AssignmentList readAssignments(Cookies cookies, String gradeTermOid, String classesToken) throws IOException{
        Document doc;
        doc = getDoc(cookies, gradeTermOid, classesToken);
        Element tbody = doc.getElementById("dataGrid").child(0).child(0);
        for (int i = 1; i < tbody.childNodeSize() / 2 - 1; i++) {
            add(new Assignment(tbody.children().get(i)));
        }
        return this;
    }

    private Document getDoc(Cookies cookies, String gradeTermOid, String token) throws IOException {
        return Jsoup.connect(ASSIGNMENTS_URL)
                .data("org.apache.struts.taglib.html.TOKEN", token)
                .data("userEvent", ASSIGNMENT_FORM_EVENT)
                .data("gradeTermOid", gradeTermOid)
                .cookies(cookies.getCookieMap())
                .post();
    }
}
