package com.aspengrades.testing;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class FetchItemsTask extends AsyncTask<Void, Void, String> {

    private final String loginUrl = "https://aspen.cps.edu/aspen/logon.do";
    private final String classesUrl = "https://aspen.cps.edu/aspen/portalClassList.do?navkey=academics.classes.list";
    private final String detailsUrl = "https://aspen.cps.edu/aspen/portalClassDetail.do?navkey=academics.classes.list.detail";
    private final String assignmentsUrl = "https://aspen.cps.edu/aspen/portalAssignmentList.do?navkey=academics.classes.list.gcd";
    private final String loginEvent = "930";
    private final String classesEvent = "2100";
    private final String assignmentsEvent = "2210";
    private final String username = "hpstennes";
    private final String password = "Gl31415;";

    @Override
    protected String doInBackground(Void... params){
        try{
            Connection.Response loginForm = Jsoup.connect(loginUrl)
                    .method(Connection.Method.GET)
                    .timeout(10000)
                    .execute();

            Document doc = loginForm.parse();
            String loginToken = doc.select("input[name=org.apache.struts.taglib.html.TOKEN]")
                    .attr("value");

            doc = Jsoup.connect(loginUrl)
                    .data("org.apache.struts.taglib.html.TOKEN", loginToken)
                    .data("userEvent", loginEvent)
                    .data("deploymentId", "aspen")
                    .data("username", username)
                    .data("password", password)
                    .cookies(loginForm.cookies())
                    .post();

            doc = Jsoup.connect(classesUrl)
                    .timeout(10000)
                    .cookies(loginForm.cookies())
                    .get();

            String classesToken = doc.select("input[name=org.apache.struts.taglib.html.TOKEN]")
                    .attr("value");
            String studentOid = doc.select("input[name=selectedStudentOid]")
                    .attr("value");

            doc = Jsoup.connect(classesUrl)
                    .data("org.apache.struts.taglib.html.TOKEN", classesToken)
                    .data("userEvent", classesEvent)
                    .data("userParam", "ssc01001160956")
                    .data("operationId", "")
                    .data("deploymentId", "aspen")
                    .data("scrollX", "0")
                    .data("scrollY", "0")
                    .data("formFocusField", "")
                    .data("formContents", "")
                    .data("formContentsDirty", "")
                    .data("maximized", "false")
                    .data("menuBarFindInputBox", "")
                    .data("selectedStudentOid", studentOid)
                    .data("jumpToSearch", "")
                    .data("initialSearch", "")
                    .data("yearFilter", "current")
                    .data("termFilter", "current")
                    .data("allowMultipleSelection", "true")
                    .data("scrollDirection", "")
                    .data("fieldSetName", "Default Fields")
                    .data("fieldSetOid", "fsnX2Cls")
                    .data("filterDefinitionId", "###all")
                    .data("basedOnFilterDefinitionId", "")
                    .data("filterDefinitionName", "filter.allRecords")
                    .data("sortDefinitionId", "default")
                    .data("sortDefinitionName", "Schedule term")
                    .data("editColumn", "")
                    .data("editEnabled", "false")
                    .data("runningSelection", "")
                    .cookies(loginForm.cookies())
                    .post();

            System.out.println("DETAILS");
            doc = Jsoup.connect(detailsUrl)
                    .timeout(10000)
                    .cookies(loginForm.cookies())
                    .get();
            System.out.println(doc.html());

            doc = Jsoup.connect(assignmentsUrl)
                    .data("org.apache.struts.taglib.html.TOKEN", classesToken)
                    .data("userEvent", assignmentsEvent)
                    .data("userParam", "")
                    .data("operationId", "")
                    .data("deploymentId", "aspen")
                    .data("scrollX", "0")
                    .data("scrollY", "0")
                    .data("formFocusField", "gradeTermOid")
                    .data("formContents", "")
                    .data("formContentsDirty", "")
                    .data("maximized", "false")
                    .data("menuBarFindInputBox", "")
                    .data("categoryOid", "")
                    .data("gradeTermOid", "")
                    .data("jumpToSearch", "")
                    .data("initialSearch", "")
                    .data("allowMultipleSelection", "true")
                    .data("scrollDirection", "")
                    .data("fieldSetName", "Default Fields")
                    .data("fieldSetOid", "fsnX2Cls")
                    .data("filterDefinitionId", "###all")
                    .data("basedOnFilterDefinitionId", "")
                    .data("filterDefinitionName", "filter.allRecords")
                    .data("sortDefinitionId", "default")
                    .data("sortDefinitionName", "Date date")
                    .data("editColumn", "")
                    .data("editEnabled", "false")
                    .data("runningSelection", "")
                    .cookies(loginForm.cookies())
                    .post();

            System.out.println("ASSIGNMENTS");
            System.out.println(doc.html());

        } catch (IOException e){
            e.printStackTrace();
        }

        return "";
    }
}
