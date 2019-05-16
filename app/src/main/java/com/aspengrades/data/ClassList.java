package com.aspengrades.data;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class ClassList {

    public static final String CLASSES_URL = "https://aspen.cps.edu/aspen/portalClassList.do?navkey=academics.classes.list";
    public static final String CLASS_FORM_EVENT = "2100";
    public static final String TERM_SELECT_EVENT = "950";
    private static final int NUM_CLASS_ATTRIBUTES = 11;

    private ArrayList<SchoolClass> classes;
    private String token;

    private ClassList(ArrayList<SchoolClass> classes, String token){
        this.classes = classes;
        this.token = token;
    }

    public static void readClasses(ClassesListener listener, Cookies cookies){
        new ReadClassesTask(listener).execute(cookies);
    }

    public static void readClasses(ClassesListener listener, String termFilter, Cookies cookies){
        new ReadClassesTask(listener, termFilter).execute(cookies);
    }

    public ArrayList<SchoolClass> getClasses() {
        return classes;
    }

    public String getToken() {
        return token;
    }

    private static class ReadClassesTask extends AsyncTask<Cookies, Void, ClassList>{

        private ClassesListener listener;
        private String termFilter = "";

        private ReadClassesTask(ClassesListener listener){
            this.listener = listener;
        }

        private ReadClassesTask(ClassesListener listener, String termFilter){
            this.listener = listener;
            this.termFilter = termFilter;
        }

        @Override
        protected final ClassList doInBackground(Cookies... cookies) {
            Document doc;
            try{
                doc = getDoc(cookies[0]);
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }

            ArrayList<SchoolClass> classes = new ArrayList<>();
            for(Element row : doc.getElementById("dataGrid").child(0).child(0).children()){
                if(row.childNodeSize() >= NUM_CLASS_ATTRIBUTES && row.siblingIndex() != 0) classes.add(new SchoolClass(row));
            }
            String token = getToken(doc);
            return new ClassList(classes, token);
        }

        private Document getDoc(Cookies cookies) throws IOException {
            Document doc = Jsoup.connect(CLASSES_URL).timeout(10000).cookies(cookies.getCookieMap()).get();
            if(!termFilter.equals("")){
                return Jsoup.connect(CLASSES_URL)
                        .data("org.apache.struts.taglib.html.TOKEN", getToken(doc))
                        .data("userEvent", TERM_SELECT_EVENT)
                        .data("yearFilter", "current")
                        .data("termFilter", termFilter)
                        .cookies(cookies.getCookieMap())
                        .post();
            }
            return doc;
        }

        private String getToken(Document doc){
            return doc.select("input[name=org.apache.struts.taglib.html.TOKEN]").attr("value");
        }

        @Override
        protected void onPostExecute(ClassList classList){
            listener.onClassesRead(classList);
        }
    }
}
