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
     * The URL of the home page in CPS Aspen
     */
    private static final String HOME_PAGE_URL = "https://aspen.cps.edu/aspen/home.do";

    /**
     * The default name for the user when the name cannot be found
     */
    public static final String DEFAULT_NAME = "GradeLeaf";

    /**
     * The length of time the app will wait for a response when attempting to connect to a page
     */
    public static int TIMEOUT = 15000;

    /**
     * The userEvent that must be submitted with the login form
     */
    private static final String LOGIN_FORM_EVENT = "930";

    /**
     * A keyword that appears in the header of Aspen when signed into a parent account but NOT when signed into a student account
     * Should probably think of a more reliable system
     */
    private static final String PARENT_ACCOUNT_KEYWORD = "Family";

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
            String name = DEFAULT_NAME;
            try {
                Connection.Response loginForm = Jsoup.connect(LOGIN_URL)
                        .method(Connection.Method.GET)
                        .timeout(TIMEOUT)
                        .execute();
                Document doc = attemptLogin(loginForm, params[0], params[1]);

                if(!doc.title().equals("Aspen"))
                    return new LoginResult(null, INVALID_CREDENTIALS, name, false);
                String headerText = doc.getElementById("header").text();
                boolean isParentAccount = headerText.contains(PARENT_ACCOUNT_KEYWORD);
                String[] headerStrs = headerText.split(" ");
                for(int i = 0; i < headerStrs.length - 1; i++){
                    if(headerStrs[i].contains(",")) name = headerStrs[i] + " " + headerStrs[i + 1];
                }

                return new LoginResult(new Cookies(loginForm.cookies()), SUCCESSFUL, name, isParentAccount);
            }catch (IOException | IndexOutOfBoundsException e){
                e.printStackTrace();
                return new LoginResult(null, ASPEN_UNAVAILABLE, name, false);
            }
        }

        private Document attemptLogin(Connection.Response loginForm, String username, String password) throws IOException{
            String loginToken = loginForm.parse()
                    .select("input[name=org.apache.struts.taglib.html.TOKEN]")
                    .attr("value");
            Jsoup.connect(LOGIN_URL)
                    .data("org.apache.struts.taglib.html.TOKEN", loginToken)
                    .data("userEvent", LOGIN_FORM_EVENT)
                    .data("deploymentId", "aspen")
                    .data("username", username)
                    .data("password", password)
                    .data("mobile", "false")
                    .cookies(loginForm.cookies())
                    .post();
            return Jsoup.connect(HOME_PAGE_URL)
                    .cookies(loginForm.cookies())
                    .get();
        }

        @Override
        protected void onPostExecute(LoginResult result){
            if(result.status == SUCCESSFUL) listener.onLoginSuccessful(result.cookies, result.name, result.isParentAccount);
            else if(result.status == INVALID_CREDENTIALS) listener.onInvalidCredentials();
            else if(result.status == ASPEN_UNAVAILABLE) listener.onLoginFailed();
        }
    }

    private static class LoginResult{
        public Cookies cookies;
        public AspenTaskStatus status;
        public String name;
        public boolean isParentAccount;

        public LoginResult(Cookies cookies, AspenTaskStatus status, String name, boolean isParentAccount){
            this.cookies = cookies;
            this.status = status;
            this.name = name;
            this.isParentAccount = isParentAccount;
        }
    }
}
