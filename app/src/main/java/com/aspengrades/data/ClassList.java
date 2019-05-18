package com.aspengrades.data;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class ClassList {

    public static final int NUM_TERMS = 4;
    public static final String CLASSES_URL = "https://aspen.cps.edu/aspen/portalClassList.do?navkey=academics.classes.list";
    public static final String CLASS_FORM_EVENT = "2100";
    public static final String TERM_SELECT_EVENT = "950";
    private static final int NUM_CLASS_ATTRIBUTES = 11;
    private static final String[] TERM_CODES = new String[] {"current", "gtmQ10000000Q1", "gtmQ20000000Q2", "gtmQ30000000Q3", "gtmQ40000000Q4"};

    private ArrayList<SchoolClass> classes;
    private int term;
    private String token;

    private ClassList(ArrayList<SchoolClass> classes, int term, String token){
        this.classes = classes;
        this.term = term;
        this.token = token;
    }

    public static void readClasses(ClassesListener listener, Cookies cookies){
        new ReadClassesTask(listener).execute(cookies);
    }

    public static void readClasses(ClassesListener listener, int term, Cookies cookies){
        new ReadClassesTask(listener, term).execute(cookies);
    }

    public ArrayList<SchoolClass> getClasses() {
        return classes;
    }

    public int getTerm(){
        return term;
    }

    public String getToken() {
        return token;
    }

    private static class ReadClassesTask extends AsyncTask<Cookies, Void, ClassList>{

        private ClassesListener listener;
        private int term = 0;

        private ReadClassesTask(ClassesListener listener){
            this.listener = listener;
        }

        private ReadClassesTask(ClassesListener listener, int term){
            this.listener = listener;
            this.term = term;
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
            return new ClassList(classes, term, token);
        }

        private Document getDoc(Cookies cookies) throws IOException {
            Document doc = Jsoup.connect(CLASSES_URL).timeout(10000).cookies(cookies.getCookieMap()).get();
            if(term != 0){
                return Jsoup.connect(CLASSES_URL)
                        .data("org.apache.struts.taglib.html.TOKEN", getToken(doc))
                        .data("userEvent", TERM_SELECT_EVENT)
                        .data("yearFilter", "current")
                        .data("termFilter", TERM_CODES[term])
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
