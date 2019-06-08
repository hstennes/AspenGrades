package com.aspengrades.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.PARSING_ERROR;
import static com.aspengrades.data.AspenTaskStatus.SESSION_EXPIRED;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

public class ClassInfo {

    private CategoryList cList;
    private AssignmentList aList;
    private AspenTaskStatus status;

    private ClassInfo(CategoryList cList, AssignmentList aList, AspenTaskStatus status){
        this.cList = cList;
        this.aList = aList;
        this.status = status;
    }

    public static void readClassInfo(ClassInfoListener listener, String classId, String token, Cookies cookies){
        new ClassInfoTask(listener).execute(new TaskParams(cookies, classId, token));
    }

    public AssignmentList fromCategory(int categoryIndex){
        return fromCategory(cList.get(categoryIndex).getName());
    }

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

    private static class ClassInfoTask extends AsyncTask<TaskParams, Void, ClassInfo>{
        private ClassInfoListener listener;

        public ClassInfoTask(ClassInfoListener listener){
            this.listener = listener;
        }

        @Override
        protected ClassInfo doInBackground(TaskParams... params) {
            try {
                new ClassSelector().selectClass(params[0]);
                CategoryList cList = new CategoryList().readCategories(params[0].cookies);
                AssignmentList aList = new AssignmentList().readAssignments(params[0].cookies, params[0].classesToken);
                return new ClassInfo(cList, aList, SUCCESSFUL);
            }catch (IOException e){
                Log.d("ClassInfo", "IOException reading info (" + e.getClass().getName() + ")");
                if(e.getClass().getName().equals("org.jsoup.HttpStatusException"))
                    return new ClassInfo(null, null, SESSION_EXPIRED);
                return new ClassInfo(null, null, ASPEN_UNAVAILABLE);
            }catch (IndexOutOfBoundsException e){
                Log.d("ClassInfo", "IndexOutOfBoundsException reading info (parsing error)");
                return new ClassInfo(null, null, PARSING_ERROR);
            }
        }

        @Override
        protected void onPostExecute(ClassInfo classInfo){
            listener.onClassInfoRead(classInfo);
        }
    }

    public static class TaskParams{
        private Cookies cookies;
        private String classId;
        private String classesToken;

        private TaskParams(Cookies cookies, String classId, String classesToken){
            this.cookies = cookies;
            this.classId = classId;
            this.classesToken = classesToken;
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
