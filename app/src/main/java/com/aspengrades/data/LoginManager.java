package com.aspengrades.data;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.INVALID_CREDENTIALS;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

public class LoginManager {

    private static final String LOGIN_URL = "https://aspen.cps.edu/aspen/logon.do";
    private static final String LOGIN_FORM_EVENT = "930";

    public static void attemptLogin(LoginListener listener, String username, String password){
        new LoginTask(listener).execute(username, password);
    }

    private static class LoginTask extends AsyncTask<String, Void, Cookies>{

        private LoginListener listener;
        private AspenTaskStatus status;

        private LoginTask(LoginListener listener){
            this.listener = listener;
        }

        @Override
        protected Cookies doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                Connection.Response loginForm = Jsoup.connect(LOGIN_URL)
                        .method(Connection.Method.GET)
                        .timeout(10000)
                        .execute();
                Document doc = loginForm.parse();
                String loginToken = doc.select("input[name=org.apache.struts.taglib.html.TOKEN]")
                        .attr("value");
                doc = Jsoup.connect(LOGIN_URL)
                        .data("org.apache.struts.taglib.html.TOKEN", loginToken)
                        .data("userEvent", LOGIN_FORM_EVENT)
                        .data("deploymentId", "aspen")
                        .data("username", username)
                        .data("password", password)
                        .cookies(loginForm.cookies())
                        .post();

                if(!doc.title().equals("Aspen")){
                    status = INVALID_CREDENTIALS;
                    return null;
                }
                else status = SUCCESSFUL;
                return new Cookies(loginForm.cookies());
            }catch (IOException e){
                e.printStackTrace();
                status = ASPEN_UNAVAILABLE;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Cookies cookies){
            if(status == SUCCESSFUL) listener.onLoginSuccessful(cookies);
            else if(status == INVALID_CREDENTIALS) listener.onInvalidCredentials();
            else if(status == ASPEN_UNAVAILABLE) listener.onLoginFailed();
        }
    }
}
