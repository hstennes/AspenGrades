package com.aspengrades.data;

import android.os.AsyncTask;

import java.io.IOException;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.PARSING_ERROR;
import static com.aspengrades.data.AspenTaskStatus.SESSION_EXPIRED;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

/**
 * A class that gets all grade related information about certain course
 */
public class ClassInfo {

    /**
     * The list of Categories in the class
     */
    private CategoryList cList;

    /**
     * The list of Assignments in the class
     */
    private AssignmentList aList;

    /**
     * The result of attempting to retrieve the data
     */
    private AspenTaskStatus status;

    /**
     * Creates a new ClassInfo object
     * @param cList The list of Categories
     * @param aList The list of Assignments
     * @param status The result of attempting to retrieve the data
     */
    private ClassInfo(CategoryList cList, AssignmentList aList, AspenTaskStatus status){
        this.cList = cList;
        this.aList = aList;
        this.status = status;
    }

    /**
     * Reads grade related information for the given class and notifies the listener when the process is complete.
     * @param listener The listener to notify when the data has been retrieved or the task failed
     * @param term The term to get data for
     * @param classId The ID of the class to get data for
     * @param token The token from ClassList
     * @param cookies The cookies from LoginManager
     */
    public static void readClassInfo(ClassInfoListener listener, int term, String classId, String token, Cookies cookies){
        new ClassInfoTask(listener).execute(new TaskParams(term, cookies, classId, token));
    }

    /**
     * Returns all assignments that are in the category with the given index
     * @param categoryIndex The index of the category in the categories table
     * @return The AssignmentList with all assignments in the category
     */
    public AssignmentList fromCategory(int categoryIndex){
        return fromCategory(cList.get(categoryIndex).getName());
    }

    /**
     * Returns all assignments that are in the category with the given name
     * @param category The name of the category
     * @return The AssignmentList with all assignments in the category
     */
    public AssignmentList fromCategory(String category){
        AssignmentList result = new AssignmentList();
        for(Assignment a : aList){
            if(a.getCategory().equals(category)) result.add(a);
        }
        return result;
    }

    public CategoryList getCategoryList(){
        return cList;
    }

    public AssignmentList getAssignmentList(){
        return aList;
    }

    public AspenTaskStatus getStatus(){
        return status;
    }

    /**
     * An AsyncTask for getting the data from Aspen
     */
    private static class ClassInfoTask extends AsyncTask<TaskParams, Void, ClassInfo>{

        /**
         * The listener to notify when the task is complete
         */
        private ClassInfoListener listener;

        /**
         * Creates a ClassInfoTask that will notify the given listener when it is complete
         * @param listener The ClassInfoListener to notify
         */
        public ClassInfoTask(ClassInfoListener listener){
            this.listener = listener;
        }

        @Override
        protected ClassInfo doInBackground(TaskParams... params) {
            try {
                new TermSelector().selectTerm(params[0].cookies, params[0].term);
                new ClassSelector().selectClass(params[0]);
                CategoryList cList = new CategoryList().readCategories(params[0].cookies);
                AssignmentList aList = new AssignmentList().readAssignments(params[0].cookies, params[0].classesToken);
                return new ClassInfo(cList, aList, SUCCESSFUL);
            }catch (IOException e){
                e.printStackTrace();
                if(e.getClass().getName().equals("org.jsoup.HttpStatusException"))
                    return new ClassInfo(null, null, SESSION_EXPIRED);
                return new ClassInfo(null, null, ASPEN_UNAVAILABLE);
            }catch (IndexOutOfBoundsException | NumberFormatException e){
                e.printStackTrace();
                return new ClassInfo(null, null, PARSING_ERROR);
            }
        }

        @Override
        protected void onPostExecute(ClassInfo classInfo){
            listener.onClassInfoRead(classInfo);
        }
    }

    /**
     * Holds all information needed by ClassInfoTask
     */
    public static class TaskParams{
        private int term;
        private Cookies cookies;
        private String classId;
        private String classesToken;

        private TaskParams(int term, Cookies cookies, String classId, String classesToken){
            this.term = term;
            this.cookies = cookies;
            this.classId = classId;
            this.classesToken = classesToken;
        }

        public int getTerm(){
            return term;
        }

        public Cookies getCookies() {
            return cookies;
        }

        public String getClassId() {
            return classId;
        }

        public String getClassesToken() {
            return classesToken;
        }
    }
}
