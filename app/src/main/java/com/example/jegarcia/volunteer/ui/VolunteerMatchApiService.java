package com.example.jegarcia.volunteer.ui;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jegarcia.volunteer.models.restModels.OppSearchResult;

import org.apache.axis.encoding.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VolunteerMatchApiService extends AsyncTask<String, Void, String> {

    private final Context mContext;
    private WSSECredentials wsse = null;
    private static final DateFormat DATETIME_FORMAT =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String CHARSET = "UTF-8";
    private static String apiUrl = "http://www.volunteermatch.org/api/call";

    private static String accountName = "garciaj42";
    private static String password = "0ed901afd6584a580e3aaf55484dec04";

    public static final String HTTP_METHOD_GET = "GET";
    private static final String TAG = VolunteerMatchApiService.class.getName();

    public VolunteerMatchApiService(Context context) {
        this.mContext = context;
    }

    // SEARCH_OPPORTUNITIES, searchOppsQuery, "GET", user, key
    @Override
    protected String doInBackground(String... strings) {
        String apiMethod = strings[0];
        String query = strings[1];
        String httpMethod = strings[2];

        HttpURLConnection urlConnection = null;
        try {
            wsse = buildWSSECredentials(accountName, password);
        } catch (Exception e) {
            try {
                return "Code " + urlConnection.getResponseCode() + " : " + urlConnection.getResponseMessage();
            } catch (Exception e2) {
                Log.e(TAG, "An unknown error occurred while processing an API call for method " + apiMethod + ", query " + query, e2);
                return null;
            }
        }
        return callAPI(apiUrl, apiMethod, query, httpMethod);
    }

    @Override
    protected void onPostExecute(String s) {
        OppSearchResult result = SearchOpportunitiesExample.parseResult(s);
        Log.d(TAG, "Number of Opportunities Found: " + result.getOpportunities().size());
        RealmHelper.saveOpportunitiesAndGetData(result.getOpportunities(), mContext);
        super.onPostExecute(s);
    }

    /**
     * Generate a random nonce.
     *
     * @return
     */
    private byte[] generateNonce() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte nonce[] = new byte[20];
            random.nextBytes(nonce);
            return nonce;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to generate nonce", e);
        }
        return null;
    }

    /**
     * Generates a SHA-256 hash of a payload message.
     *
     * @param payload
     * @return
     */
    private byte[] sha256(byte[] payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            return digest.digest(payload);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to generate SHA256 hash", e);
        }
        return null;
    }

    /**
     * Given a name and a password, build a WSSECredentials object.
     * @param accountName
     * @param password
     * @return
     */
    private WSSECredentials buildWSSECredentials(String accountName, String password) {
        WSSECredentials wsse = new WSSECredentials();
        wsse.userName = accountName;
        byte [] nonce = generateNonce();
        if (nonce == null) {
            Log.e(TAG, "Failed to generate nonce");
            return null;
        }

        wsse.nonce = Base64.encode(nonce);
        wsse.timestamp = DATETIME_FORMAT.format(new Date(System.currentTimeMillis()));

        String digestInput = wsse.nonce + wsse.timestamp + password;
        wsse.passwordDigest = Base64.encode(sha256(digestInput.getBytes()));

        return wsse;
    }

    /**
     * Builds an HTTP connection based on some WSSE credentials, a JSON query and some API key information.
     * @param wsse
     * @param query
     * @param httpMethod
     * @param accountName
     * @return
     */
    private HttpURLConnection buildConnection(WSSECredentials wsse, String query, String httpMethod, String accountName) {
        try {

            URL url = new URL(apiUrl + "?" + query);

            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod(httpMethod);
            urlConnection.setRequestProperty("Accept-Charset", CHARSET);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "WSSE profile=\"UsernameToken\"");

            if ("POST".equals(httpMethod)){
                urlConnection.setDoOutput(true);
            }

            StringBuilder credentials= new StringBuilder();
            credentials.append("UsernameToken Username=\"").append(accountName).append("\", ");
            credentials.append("PasswordDigest=\"").append(wsse.passwordDigest).append("\", ");
            credentials.append("Nonce=\"").append(wsse.nonce).append("\", ");
            credentials.append("Created=\"").append(wsse.timestamp).append("\", ");

            Log.d(TAG, "Sending request with credentials = "+ credentials);

            //this header is used to authenticate the request
            urlConnection.setRequestProperty("X-WSSE", credentials.toString());

            return urlConnection;
        } catch (MalformedURLException mue) {
            Log.e(TAG, "Failed to build connection to API", mue);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to build connection to API", ioe);
        }

        return null;
    }

    /** Call the VolunteerMatch API for the specified method
     * If calling this method directly from your application you must use the constructor with credentials
     * and be aware that the credentials will time out after some set time. The advantage is better performance
     * since we don't need to rebuild the security header so if you know you will be doing multiple back to back
     * API calls you may consider using this method.
     *
     * @param url
     * @param apiMethod
     * @param query
     * @param httpMethod
     * @return
     */
    public String callAPI(String url, String apiMethod, String query, String httpMethod) {
        StringBuilder q = new StringBuilder();
        HttpURLConnection urlConnection = null;
        InputStream response = null;

        if(wsse == null) {
            Log.e(TAG, "Error no credentials, this method should only be called directly if the constructor with credentials was used.");
            return null;
        }
        apiUrl = url;
        try {
            q.append("action=").append(URLEncoder.encode(apiMethod, CHARSET));
            q.append("&query=").append(URLEncoder.encode(query, CHARSET));


            urlConnection = buildConnection(wsse, q.toString(), httpMethod, accountName);

            // fire the call
            response = urlConnection.getInputStream();

            // collect response
            BufferedReader reader = null;
            StringBuilder buf = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(response, CHARSET));

            // add return code and message
            buf.append("Code ").append(urlConnection.getResponseCode()).append(" : ").append(urlConnection.getResponseMessage()).append("\n");

            for (String line; (line = reader.readLine()) != null;) {
                buf.append(line).append("\n");
            }

            if (reader != null) try {
                reader.close();
            } catch (IOException logOrIgnore) {
                Log.e(TAG, "Failed to close response reader", logOrIgnore);
            }

            return buf.toString();

        } catch (Exception e) {
            try {
                return "Code " + urlConnection.getResponseCode() + " : " + urlConnection.getResponseMessage();
            } catch (Exception e2) {
                Log.e(TAG, "An unknown error occurred while processing an API call for method " + apiMethod + ", query " + query, e2);
            }
        }

        return null;
    }

    public void setApiUrl(String url) {
        apiUrl = url;
    }

    /**
     * Structure representing a set of WSSE credentials.
     */
    class WSSECredentials {
        public String userName= "";
        public String passwordDigest= "";
        public String nonce= "";
        public String timestamp= "";
    }
}
