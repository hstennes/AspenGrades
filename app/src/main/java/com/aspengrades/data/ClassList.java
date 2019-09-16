package com.aspengrades.data;

import android.os.AsyncTask;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.NO_DATA;
import static com.aspengrades.data.AspenTaskStatus.PARSING_ERROR;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

/**
 * A list that contains basic info about each class in specific term
 */
public class ClassList extends ArrayList<SchoolClass> {

    /**
     * The number of terms in the dropdown menu on the classes page
     */
    public static final int NUM_TERMS = 4;

    /**
     * The URL of the "Classes" page
     */
    public static final String CLASSES_URL = "https://aspen.cps.edu/aspen/portalClassList.do?navkey=academics.classes.list";

    /**
     * The code that must be given for "userEvent" when selecting a class
     */
    public static final String CLASS_FORM_EVENT = "2100";

    /**
     * The code that must be given for "userEvent" when selecting a term
     */
    public static final String TERM_SELECT_EVENT = "950";

    /**
     * The codes that must be given for "termFilter" when selecting a term
     */
    public static final String[] TERM_CODES = new String[] {"current", "gtmQ10000000Q1", "gtmQ20000000Q2", "gtmQ30000000Q3", "gtmQ40000000Q4"};

    /**
     * The term that was selected on the classes page
     */
    private int term;

    /**
     * The token read from the classes page
     */
    private String token;

    /**
     * Maps the names of available students to their student IDs if the user is on a parent account
     */
    private HashMap<String, String> students;

    /**
     * The result of the attempt to read the classes
     */
    private AspenTaskStatus status;

    /**
     * Creates a new ClassList object. The students HashMap should be null unless a parent account is being used.
     * @param term The term the classes were read from
     * @param token The token read from the classes page
     * @param status The result of attempting to read data
     */
    private ClassList(int term, String token, AspenTaskStatus status){
        this.term = term;
        this.token = token;
        this.status = status;
    }

    /**
     * Reads the classes for the given term and student OID. This method need only be used instead of readClasses(ClassesListener, int,
     * Cookies) when the app is logged into a parent account and selecting a specific student.
     * @param listener The listener to notify when the task is complete
     * @param term The term to read classes from
     * @param studentOid The student OID
     * @param cookies The cookies from LoginManager
     */
    public static void readClasses(ClassesListener listener, int term, String studentOid, Cookies cookies){
        new ReadClassesTask(listener, term, studentOid).execute(cookies);
    }

    public int getTerm(){
        return term;
    }

    public String getToken() {
        return token;
    }

    public HashMap<String, String> getStudents(){
        return students;
    }

    public boolean isParentAccount(){
        return !(students == null);
    }

    public AspenTaskStatus getStatus(){
        return status;
    }

    /**
     * An AsyncTask for reading data from Aspen
     */
    private static class ReadClassesTask extends AsyncTask<Cookies, Void, ClassList>{

        /**
         * The term to read classes from
         */
        private int term;

        /**
         * The student OID (may be set to null)
         */
        private String studentOid;

        /**
         * The listener to be notified when the classes are read
         */
        private ClassesListener listener;

        /**
         * Creates a new ReadClassesTask
         * @param listener The listener
         * @param term The term
         * @param studentOid The student OID (if needed)
         */
        private ReadClassesTask(ClassesListener listener, int term, String studentOid){
            this.listener = listener;
            this.term = term;
            this.studentOid = studentOid;
        }

        @Override
        protected final ClassList doInBackground(Cookies... cookies) {
            Document doc;
            try{
                doc = new TermSelector().selectTerm(cookies[0], term, studentOid);
            }catch (IOException e){
                return new ClassList(term, null, ASPEN_UNAVAILABLE);
            }

            try {
                String token = doc.select("input[name=org.apache.struts.taglib.html.TOKEN]").attr("value");
                Element studentSelect = doc.selectFirst("select[name=selectedStudentOid]");
                Element tbody = doc.getElementById("dataGrid").child(0).child(0);
                return makeClassList(token, studentSelect, tbody);
            }
            catch(IndexOutOfBoundsException | NumberFormatException e){
                return new ClassList(term, null, PARSING_ERROR);
            }
        }

        private ClassList makeClassList(String token, Element studentSelect, Element tbody){
            ClassList classes = new ClassList(term, token, SUCCESSFUL);
            if(studentSelect != null){
                HashMap<String, String> students = new HashMap<>();
                for(int i = 0; i < studentSelect.children().size(); i++){
                    Element studentData = studentSelect.child(i);
                    students.put(studentData.text(), studentData.attr("value"));
                }
                classes.students = students;
            }

            if(tbody.children().size() == 2) {
                classes.status = NO_DATA;
                return classes;
            }
            int[] indexes = getInfoIndexes(tbody.child(0));
            for(int i = 1; i < tbody.children().size() - 1; i++){
                classes.add(new SchoolClass(tbody.child(i), indexes[0], indexes[1]));
            }
            return classes;
        }

        /**
         * Determines the indexes in the classes table for each piece of information. The first value is the index for the
         * description and the second for the grade.
         * @param firstRow The first row of the table, which contains the titles for each column
         * @return The indexes (column numbers) for each piece of important information
         */
        private int[] getInfoIndexes(Element firstRow){
            int[] indexes = new int[] {-1, -1};
            for(int i = 0; i < firstRow.children().size(); i++){
                String text = firstRow.child(i).text();
                if(text.equals("Description")) indexes[0] = i;
                else if(text.equals("Term Performance")) indexes[1] = i;
            }
            return indexes;
        }

        @Override
        protected void onPostExecute(ClassList classList){
            listener.onClassesRead(classList);
        }
    }
}
