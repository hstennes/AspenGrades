package com.aspengrades.data;

import android.os.AsyncTask;

import java.io.IOException;

public class ClassInfo {

    private CategoryList cList;
    private AssignmentList aList;

    private ClassInfo(CategoryList cList, AssignmentList aList){
        this.cList = cList;
        this.aList = aList;
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

    private static class ClassInfoTask extends AsyncTask<TaskParams, Void, ClassInfo>{
        private ClassInfoListener listener;

        public ClassInfoTask(ClassInfoListener listener){
            this.listener = listener;
        }

        @Override
        protected ClassInfo doInBackground(TaskParams... params) {
            try {
                new ClassSelector().selectClass(params[0]);
            }catch (IOException e){
                e.printStackTrace();
            }
            CategoryList cList = new CategoryList().readCategories(params[0].cookies);
            AssignmentList aList = new AssignmentList().readAssignments(params[0].cookies, params[0].classesToken);
            return new ClassInfo(cList, aList);
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
