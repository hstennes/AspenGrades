package com.aspengrades.data;

import android.os.AsyncTask;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.NO_DATA;
import static com.aspengrades.data.AspenTaskStatus.PARSING_ERROR;
import static com.aspengrades.data.AspenTaskStatus.SESSION_EXPIRED;
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
     * An number that can be used to identify which call of readClasses this list resulted from
     */
    private int taskId;

    /**
     * The token read from the classes page
     */
    private String token;

    /**
     * The student OID supplied when calling the readClasses method, even if it was invalid or null
     */
    private String studentOid;

    /**
     * Maps the names of available students to their student IDs if the user is on a parent account
     */
    private StudentList students;

    /**
     * The result of the attempt to read the classes
     */
    private AspenTaskStatus status;

    /**
     * Creates a new ClassList object
     * @param term The term the classes were read from
     * @param token The token read from the classes page
     * @param studentOid The student OID (can be null)
     * @param status The result of attempting to read data
     * @param taskId An identifier for this ClassList
     */
    private ClassList(int term, String token, String studentOid, AspenTaskStatus status, int taskId){
        this.term = term;
        this.token = token;
        this.studentOid = studentOid;
        this.status = status;
        this.taskId = taskId;
    }

    /**
     * Reads the classes for the given term and student OID. This method need only be used instead of readClasses(ClassesListener, int,
     * Cookies) when the app is logged into a parent account and selecting a specific student.
     * @param listener The listener to notify when the task is complete
     * @param term The term to read classes from
     * @param studentOid The student OID
     * @param cookies The cookies from LoginManager
     * @param taskId An integer ID to associate this call of readClasses with a resulting call of onClassesRead
     */
    public static void readClasses(ClassesListener listener, int term, String studentOid, Cookies cookies, int taskId) {
        new ReadClassesTask(listener, term, studentOid, taskId).execute(cookies);
    }

    public int getTaskId(){
        return taskId;
    }

    public int getTerm(){
        return term;
    }

    public String getToken() {
        return token;
    }

    public StudentList getStudents(){
        return students;
    }

    public String getStudentOid(){
        return studentOid;
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
         * An identifier for the result of this task
         */
        private int taskId;

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
        private ReadClassesTask(ClassesListener listener, int term, String studentOid, int taskId){
            this.listener = listener;
            this.term = term;
            this.studentOid = studentOid;
            this.taskId = taskId;
        }

        @Override
        protected final ClassList doInBackground(Cookies... cookies) {
            Document doc;
            try{
                doc = new TermSelector().selectTerm(cookies[0], term, studentOid);
            }catch (IOException e){
                if(e.getClass().getName().equals("org.jsoup.HttpStatusException"))
                    return new ClassList(term, null, studentOid, SESSION_EXPIRED, taskId);
                return new ClassList(term, null, studentOid, ASPEN_UNAVAILABLE, taskId);
            }

            try {
                String token = doc.select("input[name=org.apache.struts.taglib.html.TOKEN]").attr("value");
                Element studentSelect = doc.selectFirst("select[name=selectedStudentOid]");
                Element tbody = doc.getElementById("dataGrid").child(0).child(0);
                return makeClassList(token, studentSelect, tbody);
            }
            catch(NullPointerException | IndexOutOfBoundsException | NumberFormatException e){
                return new ClassList(term, null, studentOid, PARSING_ERROR, taskId);
            }
        }

        private ClassList makeClassList(String token, Element studentSelect, Element tbody){
            ClassList classes = new ClassList(term, token, studentOid, SUCCESSFUL, taskId);
            if(studentSelect != null){
                StudentList students = new StudentList();
                for(int i = 0; i < studentSelect.children().size(); i++){
                    Element studentData = studentSelect.child(i);
                    students.addStudent(studentData.text(), studentData.attr("value"));
                }
                classes.students = students;
            }

            if(tbody.children().size() == 2) {
                classes.status = NO_DATA;
                return classes;
            }
            int[] indexes = getInfoIndexes(tbody.child(0));
            for(int i = 1; i < tbody.children().size() - 1; i++){
                classes.add(new SchoolClass(tbody.child(i), indexes));
            }
            return classes;
        }

        /**
         * Determines the indexes in the classes table for each piece of information. The first value is the index for the
         * description and the second for the grade.
         * @param firstRow The first row of the table, which contains the titles for each column
         * @return The indexes (column numbers) for each piece of important information (Class name, teacher, schedule, classroom, grade)
         */
        private int[] getInfoIndexes(Element firstRow){
            int[] indexes = new int[] {-1, -1, -1, -1, -1};
            for(int i = 0; i < firstRow.children().size(); i++){
                String text = firstRow.child(i).text();
                if(text.matches("Description|Descripción")) indexes[0] = i;
                else if(text.matches("Teacher|Profesor")) indexes[1] = i;
                else if(text.matches("Schedule|Horario")) indexes[2] = i;
                else if(text.matches("Clssrm")) indexes[3] = i;
                else if(text.matches("Term Performance|Actual Funcionamiento")) indexes[4] = i;
            }
            return indexes;
        }

        @Override
        protected void onPostExecute(ClassList classList){
            listener.onClassesRead(classList);
        }
    }
}
