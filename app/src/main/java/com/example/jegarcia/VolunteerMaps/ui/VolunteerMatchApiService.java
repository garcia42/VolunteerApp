package com.example.jegarcia.VolunteerMaps.ui;


import android.util.Log;

import org.apache.axis.encoding.Base64;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class VolunteerMatchApiService {

    private WSSECredentials wsse = null;
    private static final DateFormat DATETIME_FORMAT =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String CHARSET = "UTF-8";
    static final String apiUrl = "http://www.volunteermatch.org/api/call";

    static final String ACCOUNT_NAME = "garciaj42";
    static final String PASSWORD = "0ed901afd6584a580e3aaf55484dec04";

    public static final String HTTP_METHOD_GET = "GET";
    private static final String TAG = VolunteerMatchApiService.class.getName();

    /**
     * Generate a random nonce.
     *
     * @return
     */
    private static byte[] generateNonce() {
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
    private static byte[] sha256(byte[] payload) {
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
    static WSSECredentials buildWSSECredentials(String accountName, String password) {
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

    private static HashMap<String, String> buildMap(WSSECredentials wsse) {

        StringBuilder credentials = new StringBuilder();
        credentials.append("UsernameToken Username=\"").append(ACCOUNT_NAME).append("\", ");
        credentials.append("PasswordDigest=\"").append(wsse.passwordDigest).append("\", ");
        credentials.append("Nonce=\"").append(wsse.nonce).append("\", ");
        credentials.append("Created=\"").append(wsse.timestamp).append("\", ");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept-Charset", CHARSET);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "WSSE profile=\"UsernameToken\"");
//        Log.d(TAG, "Sending request with credentials = " + credentials);
        headers.put("X-WSSE", credentials.toString());
        return headers;
    }

    public static ConnectionInfo createConnectionInfo(WSSECredentials wsse, String url, String apiMethod, String query, String httpMethod) {
        StringBuilder q = new StringBuilder();
        ConnectionInfo connectionInfo = new ConnectionInfo();
        try {
            q.append("action=").append(URLEncoder.encode(apiMethod, CHARSET));
            q.append("&query=").append(URLEncoder.encode(query, CHARSET));
            connectionInfo.headers = buildMap(wsse);
            connectionInfo.url = url + "?" + q.toString();
            return connectionInfo;
        } catch (Exception e) {
            Log.e(TAG, "An unknown error occurred while processing an API call for method " + apiMethod + ", query " + query);
        }
        return null;
    }

    static class ConnectionInfo {
        public HashMap<String, String> headers;
        public String url;
    }

    /**
     * Structure representing a set of WSSE credentials.
     */
    static class WSSECredentials {
        public String userName= "";
        public String passwordDigest= "";
        public String nonce= "";
        public String timestamp= "";
    }
}
