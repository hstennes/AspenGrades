package com.aspengrades.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An ArrayList of Assignments that provides methods for retrieving the assignment data from Aspen
 */
public class AssignmentList extends ArrayList<Assignment> {

    /**
     * The URL of the "Assignments" page in CPS Aspen
     */
    public static final String ASSIGNMENTS_URL = "https://aspen.cps.edu/aspen/portalAssignmentList.do?navkey=academics.classes.list.gcd";

    /**
     * The code that must be given for "userEvent" when the HTML form is submitted
     */
    private static final String ASSIGNMENT_FORM_EVENT = "2210";

    /**
     * Fills this AssignmentList with assignments from the selected class and all terms applicable to that class. his may not include
     * all assignments in the term depending on how many assignments are set to be shown in Aspen's settings. This method should only be
     * called from within an AsyncTask
     * @param cookies The cookies from LoginManager
     * @param classesToken The token from ClassList
     * @return This AssignmentList object
     * @throws IOException If Aspen could not be reached for any reason
     */
    public AssignmentList readAssignments(Cookies cookies, String classesToken) throws IOException{
        return readAssignments(cookies, "", classesToken);
    }

    /**
     * Fills this AssignmentList with assignments from the selected class and given term. This may not include all assignments in the
     * term depending on how many assignments are set to be shown in Aspen's settings. This method should only be called from within an
     * AsyncTask.
     * @param cookies The cookies from LoginManager
     * @param gradeTermOid The string representing the desired grade term, as defined by ClassList.TERM_CODES
     * @param classesToken The token from ClassList
     * @return This AssignmentList object
     * @throws IOException If Aspen could not be reached for any reason
     */
    public AssignmentList readAssignments(Cookies cookies, String gradeTermOid, String classesToken) throws IOException{
        Document doc;
        doc = getDoc(cookies, gradeTermOid, classesToken);

        Element tbody = doc.getElementById("dataGrid").child(0).child(0);
        int[] indexes = getInfoIndexes(tbody.child(0));
        for (int i = 1; i < tbody.children().size() - 1; i++) {
            add(new Assignment(tbody.child(i), indexes[0], indexes[1], indexes[2]));
        }
        return this;
    }

    /**
     * Determines the indexes in the assignments table for each piece of information. The first integer in the array is for the
     * assignment name, the second for the category, and the third for the score
     * @param firstRow The first row of the table, which contains the names of each column
     * @return The indexes (column numbers) for each piece of information in the table
     */
    private int[] getInfoIndexes(Element firstRow){
        int[] indexes = new int[] {-1, -1, -1};
        for(int i = 0; i < firstRow.children().size(); i++){
            String text = firstRow.child(i).text();
            if(text.equals("AssignmentName")) indexes[0] = i;
            else if(text.equals("Category > Desc")) indexes[1] = i;
            else if(text.equals("Score")) indexes[2] = i;
        }
        return indexes;
    }

    /**
     * Returns the JSoup Document for the assignments page based on the currently selected class
     * @param cookies The cookies from LoginManager
     * @param gradeTermOid The gradeTermOid for the desired term, blank if selecting "All" option
     * @param token The token from ClassList
     * @return The assignments page as a JSoup Document
     * @throws IOException If Aspen could not be reached for any reason
     */
    private Document getDoc(Cookies cookies, String gradeTermOid, String token) throws IOException {
        return Jsoup.connect(ASSIGNMENTS_URL)
                .data("org.apache.struts.taglib.html.TOKEN", token)
                .data("userEvent", ASSIGNMENT_FORM_EVENT)
                .data("gradeTermOid", gradeTermOid)
                .cookies(cookies.getCookieMap())
                .post();
    }
}
