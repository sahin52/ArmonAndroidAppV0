package sahin.apps.ArmonAppV2;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//TODO display by id, so you can direct every search function to it
public class ArmonApiClient {
    public JSONObject requestHeaders;
    public boolean isLoggedIn;

    private String _organizationId;
    private String _grantId;
    private String organizationRoot;
    String organizationName;
    private String apiRoot;
    private String _token;
    private String _refreshToken;
    private int _tokenExpireTime;
    private String result;
    ArmonApiClient(String domain){
        requestHeaders = new JSONObject();
        apiRoot="https://" + domain;
        try {
            isLoggedIn = false;
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
                    isLoggedIn = false;
                    MainActivity.toast("Giriş yapılamadı! Kullanıcı adınızı kontrol edin!");
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
                    isLoggedIn=true;
                    MainActivity.girisYapildiView.setText("Giriş Yapıldı");
                    MainActivity.toast("Giriş Yapıldı");
                } catch (JSONException e) {
                    MainActivity.girisYapildiView.setText("Giriş Yapılırken hata oluştu");
                    MainActivity.toast("Giriş yapılamadı!");
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
            MainActivity.mainPageText.setText("Armon Api'de belirsiz hata oluştu"+e.toString());
            e.printStackTrace();
        }

    }
    @SuppressLint("StaticFieldLeak")
    public void findByCredentialNumberAndDisplay(String credentialData){
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
                    String erres = er+res;
                    MainActivity.mainPageText.setText(erres);
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
    }
    @SuppressLint("StaticFieldLeak")
    public void findByTCNumberAndDisplay(String TC) {
        String url = organizationRoot + "/identity/search/exact";
        RawRequest rawRequest = new RawRequest() {
            @Override
            protected void onPostExecute(String res) {
                try {
                    JSONObject resultJson = new JSONObject(res);
                    JSONObject aPerson = resultJson.getJSONArray("items").getJSONObject(0);
                    String set = aPerson.getString("fullname");
                    JSONArray orgUnits = aPerson.getJSONArray("organizationUnits");
                    int len = orgUnits.length();
                    for(int i=0;i<len;i++) {
                        set += "\n" + aPerson.getJSONArray("organizationUnits").getJSONObject(i).getString("name");
                    }
                    MainActivity.mainPageText.setText(set);
                } catch (JSONException e) {
                    String er = "ERROR ON Getting info, please reopen application";
                    MainActivity.mainPageText.setText(res+er);
                    e.printStackTrace();
                }
            }
        };
        JSONObject data = new JSONObject();
        try {
            data.put("take",30);
            data.put("skip",0);
            JSONObject tcJson = new JSONObject();
            tcJson.put("uniqueId",TC);
            data.put("userOrganizationProfile",tcJson);
            rawRequest.setRequestHeader(requestHeaders);
            rawRequest.execute("POST",url, data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setResult(String s){
        result = s;
    }
}
