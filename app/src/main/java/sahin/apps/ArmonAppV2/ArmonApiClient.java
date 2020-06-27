package sahin.apps.ArmonAppV2;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

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
    private String _name;
    private String _surname;
    //private  loginTime;
    ArmonApiClient(String domain){
        requestHeaders = new JSONObject();
        apiRoot="https://" + domain;
        try {
            isLoggedIn = false;
            requestHeaders.put("content-type","application/json");
            requestHeaders.put("user-agent","armon-api-android-client");
        } catch (JSONException e) {
            MainActivity.mainPageText.setText("Hata oluştu: hata kodu H005");
            e.printStackTrace();
        }

    }


    /**
     * init and login on Armon, takes username password and organization to log in with and tries to login for being able to get info.
     * Users may have more than one organization, the 3rd parameter is for this.
     */
    @SuppressLint("StaticFieldLeak")
    public void initAndLogin(final String username, final String password, final String organizationToBeLoggedIn){ //DONE
        String url = apiRoot + "/auth/user";
        RawRequest rawRequest = new RawRequest(){
            @Override
            protected void onPostExecute(String res){
                try {
                    JSONObject resultJsonFromInit = new JSONObject(res);
                    JSONArray organizations = resultJsonFromInit.getJSONArray("organizations");

                    JSONObject organization = chooseOrganization(organizations,organizationToBeLoggedIn);//TODO index might change

                    _organizationId = organization.getString("id");
                    _grantId = organization.getJSONArray("authentications").getJSONObject(0).getString("id");
                    organizationRoot = apiRoot+"/u/v1/" + _organizationId;
                    login(username,password,organizationToBeLoggedIn);

                } catch (JSONException e) {
                    isLoggedIn = false;
                    MainActivity.toast("Giriş yapılamadı! Kullanıcı adı-şifrenizi kontrol edin! ");
                    e.printStackTrace();
                }
                super.onPostExecute(res);
            }
        };
        JSONObject usernameJson = new JSONObject();
        try {
            usernameJson.put("username",username);
        } catch (JSONException e) {
            MainActivity.mainPageText.setText("Hata oluştu: hata kodu H006\n"+e.toString());
            e.printStackTrace();
        }
        rawRequest.setRequestHeader(requestHeaders);
        rawRequest.execute("POST",url,usernameJson.toString());
    }



    /**
     * Main login after init. Here will use password to be able to log in
     */
    @SuppressLint("StaticFieldLeak")
    private void login(String username, String password, String organizationToBeLoggedIn) {//DONE
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
                    _name = returnsFromLogin.getJSONObject("organization").getJSONObject("organizationProfile").getString("name");
                    _surname = returnsFromLogin.getJSONObject("organization").getJSONObject("organizationProfile").getString("surname");
                    String girisYapildiViewText = "Giriş Yapıldı\n"+"Merhaba "+_name +" "+_surname;
                    _tokenExpireTime = returnsFromLogin.getInt("expiresIn");




                    MainActivity.girisYapildiView.setText(girisYapildiViewText);
                    MainActivity.toast("Giriş Yapıldı");
                } catch (JSONException e) {
                    MainActivity.girisYapildiView.setText("Giriş Yapılırken hata oluştu - şifrenizi kontrol edin");
                    MainActivity.toast("Giriş Yapılamadı!");
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
            MainActivity.mainPageText.setText("Armon Api'de belirsiz hata oluştu\n"+e.toString());
            e.printStackTrace();
        }

    }

    /**
     * When a card is read by NFC, this function will be called
     * @param credentialData credential numarasi
     */
    @SuppressLint("StaticFieldLeak")
    public void findByCredentialNumberAndDisplay(String credentialData){ //TODO display etmeyi duzenle ve logged in olup olmadigini kontrol et
        String url = organizationRoot + "/member/findbycredential" ;
        final RawRequest rawRequest = new RawRequest(){
            @Override
            protected void onPostExecute(String res) {
                try {
                    JSONObject resultJson = new JSONObject(res);
                    String userId = resultJson.getJSONObject("member").getString("id");
                    findByUserIdAndDisPlay(userId);
                    String fullname = resultJson.getJSONObject("member").getString("fullname");
                    String uniqueId = resultJson.getJSONObject("member").getString("uniqueId");
                    String allOrganizations="";
                    String allUserGroups="";
                    int orgUnitsLen = resultJson.getJSONObject("member").getJSONArray("organizationUnits").length();
                    for(int i=0;i<orgUnitsLen;i++){
                        allOrganizations= allOrganizations + resultJson.getJSONObject("member").getJSONArray("organizationUnits").getJSONObject(i).getString("name")+"\n";
                    }
                    String firstOrganizationName = resultJson.getJSONObject("member").getJSONArray("organizationUnits").getJSONObject(0).getString("name");
                    String text="ID : "+uniqueId+"\nAd : "+fullname + "\nOrganizasyon : " + firstOrganizationName;
                    int UserGorupsLen = resultJson.getJSONObject("member").getJSONArray("userGroups").length();
                    for(int i=0;i<UserGorupsLen;i++){
                        allUserGroups= allUserGroups + resultJson.getJSONObject("member").getJSONArray("userGroups").getJSONObject(i).getString("name")+"\n";
                    }

                    MainActivity.mainPageText.setText(text);
                    MainActivity.fullNameResultTextView.setText(fullname);
                    MainActivity.uniqueIdResultTextView.setText(uniqueId);
                    MainActivity.organizationUnitsResultTextView.setText(allOrganizations);
                    MainActivity.userGroupsResultTextView.setText(allUserGroups);

                } catch (JSONException e) {
                    String er = "Kart bilgisi bulunurken hata oluştu\n";
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
            MainActivity.mainPageText.setText("Hata oluştu: hata kodu H001");
            e.printStackTrace();
        }
    }
    @SuppressLint("StaticFieldLeak")
    public void findByTCNumberAndDisplay(String TC) { //TODO User Groupları ekle
        String url = organizationRoot + "/identity/search/exact";
        RawRequest rawRequest = new RawRequest() {
            @Override
            protected void onPostExecute(String res) {
                try {
                    JSONObject resultJson = new JSONObject(res);
                    JSONObject aPerson = resultJson.getJSONArray("items").getJSONObject(0);
                    String userId = aPerson.getString("id");
                    findByUserIdAndDisPlay(userId);
                    String fullname = aPerson.getString("fullname");
                    String uniqueID=aPerson.getString("uniqueId");
                    String organizationUnits="";
                    String set = aPerson.getString("fullname");
                    JSONArray orgUnits = aPerson.getJSONArray("organizationUnits");
                    int len = orgUnits.length();
                    for(int i=0;i<len;i++) {
                        organizationUnits+=aPerson.getJSONArray("organizationUnits").getJSONObject(i).getString("name");
                        set += "\n" + aPerson.getJSONArray("organizationUnits").getJSONObject(i).getString("name");
                    }

                    MainActivity.uniqueIdResultTextView.setText(uniqueID);
                    MainActivity.fullNameResultTextView.setText(fullname);
                    MainActivity.organizationUnitsResultTextView.setText(organizationUnits);
                    MainActivity.mainPageText.setText(set);
                } catch (JSONException e) {
                    String er = "Numaradan bilgi bulunurken hata oluştu\n";
                    MainActivity.mainPageText.setText(er+res);
                    e.printStackTrace();
                }
                super.onPostExecute(res);
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
            MainActivity.mainPageText.setText("Hata oluştu: hata kodu H002");
            e.printStackTrace();
        }
    }

    /**
     * Finds Details and Puts them in MainActivity TextViews
     * @param userId Id of the user
     */
    @SuppressLint("StaticFieldLeak")
    public void findByUserIdAndDisPlay(String userId){ //TODO Find By User Id
        String url=organizationRoot + "/identity/detailed/"+userId;
        String data="";
        String method="GET";
        RawRequest rawRequest = new RawRequest(){
            @Override
            protected void onPostExecute(String res) {
                try{
                    //MainActivity.mainPageText.setText(s);
                    String credentialsText = "";
                    String odtuNumbersText = "";
                    JSONObject resAsJson= new JSONObject(res);
                    int numbersLen = resAsJson.getJSONObject("organizationProfile").getJSONObject("extensionFields").getJSONArray("numbers").length();
                    for(int i=0;i<numbersLen;i++)
                        odtuNumbersText += resAsJson.getJSONObject("organizationProfile").getJSONObject("extensionFields").getJSONArray("numbers").getString(i)+"\n";

                    int credentialsLen = resAsJson.getJSONArray("credentials").length();
                    for(int i=0;i<credentialsLen;i++) {
                        credentialsText+= "Tip: " + resAsJson.getJSONArray("credentials").getJSONObject(i).getString("type");
                        credentialsText+= " Bilgi:"+ resAsJson.getJSONArray("credentials").getJSONObject(i).getString("data") +"\n";
                    }
                    MainActivity.credentialsResultTextView.setText(credentialsText);
                    MainActivity.odtuNumbersResultTextView.setText(odtuNumbersText);
                }catch (Exception e){
                    MainActivity.mainPageText.setText("Hata oluştu: hata kodu H003\n"+e.toString());
                    e.printStackTrace();
                }
                super.onPostExecute(res);
            }
        };
        rawRequest.setRequestHeader(requestHeaders);
        rawRequest.execute(method,url,data);
    }
    @SuppressLint("StaticFieldLeak")
    public void findByOdtuId(String odtuId){//TODO Find By Odtu Id
        RawRequest rawRequest = new RawRequest(){
            @Override
            protected void onPostExecute(String s) {
                try{
                }catch (Exception e){
                    MainActivity.mainPageText.setText("Hata oluştu: hata kodu H004");
                    e.printStackTrace();
                }
                super.onPostExecute(s);
            }
        };
        rawRequest.setRequestHeader(requestHeaders);
        rawRequest.execute();
    }


    //Helper functions -- Not main things
    private JSONObject chooseOrganization(JSONArray organizations,String organizationToBeLoggedIn) throws JSONException {
        JSONObject res = null;
        int sizeOfJson = organizations.length();
        for(int i=0;i<sizeOfJson;i++){
            if(organizations.getJSONObject(i).getString("name").equals(organizationToBeLoggedIn)){
                res = organizations.getJSONObject(i);
            }
        }
        if(res==null) {
            if(organizations.length()>1){
                String string = "Organizasyon bulunamadı, İlk organizasyon seçildi: "+organizations.getJSONObject(0).getString("name") +"Diğer organizasyonlar:";
                for(int i =0; i< organizations.length();i++){
                    string +=  "\n" + organizations.getJSONObject(i).getString("name");
                }
                MainActivity.toast(string);
            }else{
                MainActivity.toast("Organizasyon bulunamadı, var olan tek organizasyon seçildi: " + organizations.getJSONObject(0).getString("name"));
            }
            res = organizations.getJSONObject(0);
        }
        return res;
    }

    public boolean isLoggedIn() {//TODO
        //if(!isLoggedIn || ){ }
        return true;
    }
    public void trialFunc(Callable<Integer> myFunc){
        try {
            myFunc.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
