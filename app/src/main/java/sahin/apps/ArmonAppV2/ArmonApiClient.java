package sahin.apps.ArmonAppV2;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArmonApiClient {
    public JSONObject requestHeaders;
    public boolean loggedIn;

    String _organizationId;
    String _grantId;
    String organizationRoot;
    String organizationName;
    String apiRoot;
    String _token;
    String _refreshToken;
    int _tokenExpireTime;
    String result;
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
        RawRequest rawRequest = new RawRequest(){
            @Override
            protected void onPostExecute(String res){
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
                JSONObject returnsFromLogin;
                try {
                    returnsFromLogin = new JSONObject(res);
                    _token = returnsFromLogin.getString("token");
                    _refreshToken = returnsFromLogin.getString("refreshToken");
                    _tokenExpireTime = returnsFromLogin.getInt("expiresIn");
                    requestHeaders.put("Authorization","Bearer " + _token);
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
    @SuppressLint("StaticFieldLeak")
    public String FindByCredentialNumber(String credentialData){
        result = "";
        String url = organizationRoot + "/member/findbycredential" ;
        final RawRequest rawRequest = new RawRequest(){
            @Override
            protected void onPostExecute(String res) {
                try {
                    JSONObject resultJson = new JSONObject(res);
                    String id = resultJson.getJSONObject("member").getString("id");
                    String fullname = resultJson.getJSONObject("member").getString("fullname");
                    String uniqueId = resultJson.getJSONObject("member").getString("uniqueId");
                    String firstOrganizationName = resultJson.getJSONObject("member").getJSONArray("organizationUnits").getJSONObject(0).getString("name");
                    String text="ID : "+uniqueId+"\nAd : "+fullname + "\nOrganizasyon : " + firstOrganizationName;
                    MainActivity.mainPageText.setText(text);
                } catch (JSONException e) {
                    String er = "ERROR ON Getting info, please reopen application";
                    MainActivity.mainPageText.setText(er);
                    e.printStackTrace();
                }

                super.onPostExecute(res);
            }
        };
        JSONObject credentialJson = new JSONObject();
        try {
            credentialJson.put("credentialType",0);
            credentialJson.put("credentialData",credentialData);
            rawRequest.setRequestHeader(requestHeaders);
            rawRequest.execute("POST",url,credentialJson.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    public void setResult(String s){
        result = s;
    }
}
