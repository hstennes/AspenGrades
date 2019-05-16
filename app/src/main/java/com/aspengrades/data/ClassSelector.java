package com.aspengrades.data;

import org.jsoup.Jsoup;

import java.io.IOException;

public class ClassSelector {

    public void selectClass(ClassInfo.TaskParams params) {
        try {
            Jsoup.connect(ClassList.CLASSES_URL)
                    .data("org.apache.struts.taglib.html.TOKEN", params.getClassesToken())
                    .data("userEvent", ClassList.CLASS_FORM_EVENT)
                    .data("userParam", params.getClassId())
                    .cookies(params.getCookies().getCookieMap())
                    .post();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
