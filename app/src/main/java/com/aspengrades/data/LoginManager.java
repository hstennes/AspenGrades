package com.aspengrades.data;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.INVALID_CREDENTIALS;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

/**
 * A class that uses the given credentials to log into Aspen
 */
public class LoginManager {

    /**
     * The URl of the login page in CPS Aspen
     */
    private static final String LOGIN_URL = "https://aspen.cps.edu/aspen/logon.do";

    /**
     * The length of time the app will wait for a response when attempting to connect to a page
     */
    public static int TIMEOUT = 15000;

    /**
     * The userEvent that must be submitted with the login form
     */
    private static final String LOGIN_FORM_EVENT = "930";

    /**
     * A keyword that appears in the header of Aspen when signed into a student account but NOT when signed into a parent account
     * Should probably think of a more reliable system
     */
    private static final String studentAccountKeyword = "NETWORK";

    /**
     * Attempts to login using the given credentials
     * @param listener The listener to notify when the task is complete
     * @param username The username given by the user
     * @param password The password given by the user
     */
    public static void attemptLogin(LoginListener listener, String username, String password){
        new LoginTask(listener).execute(username, password);
    }

    /**
     * An AsyncTask for logging into Aspen
     */
    private static class LoginTask extends AsyncTask<String, Void, LoginResult>{

        /**
         * The listener to notify when the task is complete
         */
        private LoginListener listener;

        /**
         * Creates a new LoginTask
         * @param listener the listener
         */
        private LoginTask(LoginListener listener){
            this.listener = listener;
        }

        @Override
        protected LoginResult doInBackground(String... params) {
            try {
                Connection.Response loginForm = Jsoup.connect(LOGIN_URL)
                        .method(Connection.Method.GET)
                        .timeout(TIMEOUT)
                        .execute();
                Document doc = attemptLogin(loginForm, params[0], params[1]);
                if(!doc.title().equals("Aspen")) return new LoginResult(null, INVALID_CREDENTIALS, false);
                boolean isParent = !doc.getElementById("header")
                        .child(2)
                        .text()
                        .contains(studentAccountKeyword);
                return new LoginResult(new Cookies(loginForm.cookies()), SUCCESSFUL, isParent);
            }catch (IOException e){
                e.printStackTrace();
                return new LoginResult(null, ASPEN_UNAVAILABLE, false);
            }
        }

        private Document attemptLogin(Connection.Response loginForm, String username, String password) throws IOException{
            String loginToken = loginForm.parse()
                    .select("input[name=org.apache.struts.taglib.html.TOKEN]")
                    .attr("value");
            return Jsoup.connect(LOGIN_URL)
                    .data("org.apache.struts.taglib.html.TOKEN", loginToken)
                    .data("userEvent", LOGIN_FORM_EVENT)
                    .data("deploymentId", "aspen")
                    .data("username", username)
                    .data("password", password)
                    .cookies(loginForm.cookies())
                    .post();
        }

        @Override
        protected void onPostExecute(LoginResult result){
            if(result.status == SUCCESSFUL) listener.onLoginSuccessful(result.cookies, result.isParentAccount);
            else if(result.status == INVALID_CREDENTIALS) listener.onInvalidCredentials();
            else if(result.status == ASPEN_UNAVAILABLE) listener.onLoginFailed();
        }
    }

    private static class LoginResult{
        public Cookies cookies;
        public AspenTaskStatus status;
        public boolean isParentAccount;

        public LoginResult(Cookies cookies, AspenTaskStatus status, boolean isParentAccount){
            this.cookies = cookies;
            this.status = status;
            this.isParentAccount = isParentAccount;
        }
    }
}
