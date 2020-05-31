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
    String organizationName;
    String apiRoot;
    String _token;
    String _refreshToken;
    int _tokenExpireTime;
    ArmonApiClient(String domain){
        requestHeaders = new JSONObject();
        apiRoot="https://" + domain;
        try {
            loggedIn = false;
            requestHeaders.put("content-type","application/json");
            requestHeaders.put("user-agent","armon-api-android-client");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * init and login on Armon
     */
    @SuppressLint("StaticFieldLeak")
    public void initAndLogin(final String username, final String password){
        String url = apiRoot + "/auth/user";
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
                    organizationRoot = apiRoot+"/u/v1/" + _organizationId;
                    login(username,password);

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

    /**
     * Main login after init
     */
    @SuppressLint("StaticFieldLeak")
    private void login(String username, String password) {
        RawRequest r = new RawRequest(){
            @Override
            protected void onPostExecute(String res) {
                MainActivity.txtTagContent.setText(res);
                JSONObject returnsFromLogin = null;
                try {
                    returnsFromLogin = new JSONObject(res);
                    _token = returnsFromLogin.getString("token");
                    _refreshToken = returnsFromLogin.getString("refreshToken");
                    _tokenExpireTime = returnsFromLogin.getInt("expiresIn");
                    MainActivity.mainPageText.setText(Integer.toString(_tokenExpireTime));
                } catch (JSONException e) {
                    MainActivity.mainPageText.setText("Error After Login "+e.toString());
                    e.printStackTrace();
                }
                super.onPostExecute(res);
            }
        };
        JSONObject UnPw = new JSONObject();
        try {
            UnPw.put("username",username);
            UnPw.put("password",password);
            r.execute("POST",apiRoot+"/auth/usernamepass/" + _grantId,UnPw.toString());
        } catch (Exception e) {
            MainActivity.mainPageText.setText("ERROR ON AAC"+e.toString());
            e.printStackTrace();
        }

    }
    public String FindByCredential(String credential){

        return "";
    }
}
