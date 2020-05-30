package sahin.apps.ArmonAppV2;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArmonApiClient {
    public JSONObject requestHeaders;
    RawRequest rawRequest;
    public boolean loggedIn;

    String _organizationId;
    String _grantId;
    String organizationRoot;
    String _token;
    String _refreshToken;
    String _tokenExpireTime;
    ArmonApiClient(){
        requestHeaders = new JSONObject();
        try {
            loggedIn = false;
            requestHeaders.put("content-type","application/json");
            requestHeaders.put("user-agent","armon-api-android-client");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Login on Armon
     */
    @SuppressLint("StaticFieldLeak")
    public void initAndLogin(String username, String password, final String domain){
        String url = "https://" + domain + "/auth/user";
        rawRequest = new RawRequest(){
            @Override
            protected void onPostExecute(String res){
                MainActivity.txtTagContent.setText(res);
                try {
                    JSONObject resultJsonFromInit = new JSONObject(res);
                    JSONArray organizations = resultJsonFromInit.getJSONArray("organizations");
                    JSONObject organization = organizations.getJSONObject(0);//TODO index might change
                    _organizationId = organization.getString("id");
                    _grantId = organization.getJSONArray("authentications").getJSONObject(0).getString("id");
                    organizationRoot = "https://"+domain+"/u/v1/" + _organizationId;


                } catch (JSONException e) {
                    loggedIn = false;
                    e.printStackTrace();
                }
                super.onPostExecute(res);
            }
        };
        JSONObject usernameJson = new JSONObject();
        try {
            usernameJson.put("username",username);
        } catch (JSONException e) {
            MainActivity.mainPageText.setText(e.toString());
            e.printStackTrace();
        }
        rawRequest.setRequestHeader(requestHeaders);
        rawRequest.execute("POST",url,usernameJson.toString());
    }
}
