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
     * The list of Categories in the class. Also contains the cumulative grade from the categories page.
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
        new ClassInfoTask(listener, term, classId, token).execute(cookies);
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
    private static class ClassInfoTask extends AsyncTask<Cookies, Void, ClassInfo>{

        /**
         * The listener to notify when the task is complete
         */
        private ClassInfoListener listener;

        /**
         * The term to read info for
         */
        private int term;

        /**
         * The class ID
         */
        private String classId;

        /**
         * The token from ClassList
         */
        private String token;

        /**
         * Creates a new ClassInfoTask
         * @param listener The listener
         * @param term The term
         * @param classId The class ID
         * @param token The token
         */
        public ClassInfoTask(ClassInfoListener listener, int term, String classId, String token){
            this.listener = listener;
            this.term = term;
            this.classId = classId;
            this.token = token;
        }

        @Override
        protected ClassInfo doInBackground(Cookies... cookies) {
            try {
                new TermSelector().selectTerm(cookies[0], term, null);
                CategoryList cList = new CategoryList().readCategories(cookies[0], classId, token);
                AssignmentList aList = new AssignmentList().readAssignments(cookies[0], token);
                return new ClassInfo(cList, aList, SUCCESSFUL);
            }catch (IOException e){
                if(e.getClass().getName().equals("org.jsoup.HttpStatusException"))
                    return new ClassInfo(null, null, SESSION_EXPIRED);
                return new ClassInfo(null, null, ASPEN_UNAVAILABLE);
            }catch (IllegalArgumentException | IndexOutOfBoundsException | NullPointerException e){
                return new ClassInfo(null, null, PARSING_ERROR);
            }
        }

        @Override
        protected void onPostExecute(ClassInfo classInfo){
            listener.onClassInfoRead(classInfo);
        }
    }
}
