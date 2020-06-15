package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GetUserInfoFromCardId extends AsyncTask<String,String,String> {
    //////USELESSSS
    public String data;
    String result;
    String _organizationId;
    String _grantId;
    String organizationRoot;
    String apiRoot = "https://odtupass-dev.metu.edu.tr";
    String _token;
    String _refreshToken;
    int _tokenExpireTime;
    private static final String UTF_8 = "UTF-8";

    @Override
    protected String doInBackground(String... params) {
        try {
            String username = ""; //TODOADDUNPW
            String password = ""; //TODOADDUNPW
            data="started";
            result="";
            String credentialData = params[0];
            JSONObject requestParams=new JSONObject();
            requestParams.put("username",username);
            URL url = new URL("https://odtupass-dev.metu.edu.tr/auth/user");
            data+="*0*";
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type","application/json");
            httpURLConnection.setRequestProperty("user-agent","armon-api-android-client");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(requestParams.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            data+="*1*";
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String jsonResult = "";
            String line = "";
            while(line!=null){
                line = bufferedReader.readLine();
                jsonResult += line;
            }
            data+="*2*";
            //MainActivity.txtTagContent.setText(data);

            JSONObject returnsFromInit = new JSONObject(jsonResult);
            JSONArray organizations = returnsFromInit.getJSONArray("organizations");
            JSONObject organization = organizations.getJSONObject(0);
            _organizationId = organization.getString("id");
            _grantId = organization.getJSONArray("authentications").getJSONObject(0).getString("id");
            organizationRoot = "https://odtupass-dev.metu.edu.tr/u/v1/" + _organizationId;


            //******************************************LOGIN**************************************************
            url = new URL(apiRoot + "/auth/usernamepass/" + _grantId);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type","application/json");
            httpURLConnection.setRequestProperty("user-agent","armon-api-android-client");
            outputStream = httpURLConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            requestParams.put("password",password);
            bufferedWriter.write(requestParams.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream i = httpURLConnection.getInputStream();
            BufferedReader b = new BufferedReader(new InputStreamReader(i));
            line = "";
            data="";
            while(line!=null){
                line = b.readLine();
                data+=line;
            }



            JSONObject returnsFromLogin = new JSONObject(data);
            _token = returnsFromLogin.getString("token");
            _refreshToken = returnsFromLogin.getString("refreshToken");
            _tokenExpireTime = returnsFromLogin.getInt("expiresIn");


            //*********************************Find By Credential**************************
            url = new URL(organizationRoot + "/member/findbycredential");
            HttpURLConnection httpURLConnection2 = (HttpURLConnection) url.openConnection();
            httpURLConnection2.setRequestMethod("POST");
            httpURLConnection2.setRequestProperty("content-type","application/json");
            httpURLConnection2.setRequestProperty("user-agent","armon-api-android-client");
            httpURLConnection2.setRequestProperty("Authorization", "Bearer " + _token);

            JSONObject requestParams1 = new JSONObject();
            requestParams1.put("credentialType",0);
            requestParams1.put("credentialData",credentialData);

            OutputStream outputStream1 = httpURLConnection2.getOutputStream();
            BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(outputStream1,"UTF-8"));
            bufferedWriter1.write(requestParams1.toString());
            bufferedWriter1.flush();
            bufferedWriter1.close();
            outputStream1.close();

            InputStream inputStream2 = httpURLConnection2.getInputStream();
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
            String line2 = "";
            data="";
            while(line2!=null){
                data += line2;
                line2 = bufferedReader2.readLine();
            }



            JSONObject credential = new JSONObject(data);
            String id = credential.getJSONObject("member").getString("id");
            String fullname = credential.getJSONObject("member").getString("fullname");
            String uniqueId = credential.getJSONObject("member").getString("uniqueId");
            //boolean isDisabled = credential.getJSONObject("member").getBoolean("isDisabled");
            String firstOrganizationName = credential.getJSONObject("member").getJSONArray("organizationUnits").getJSONObject(0).getString("name");
            result +="FULL NAME: " + fullname+"\n";
            result += "ID: " + uniqueId +"\n";
            //data += "Is Active: " + !isDisabled +"\n";
            result += "Organization: " +firstOrganizationName;
            /*url =new URL(organizationRoot + "/identity/detailed/" + id);
            HttpURLConnection httpURLConnection1 = (HttpURLConnection) url.openConnection();
            httpURLConnection1.setRequestMethod("GET");
            httpURLConnection1.setRequestProperty("content-type","application/json");
            httpURLConnection1.setRequestProperty("user-agent","armon-api-android-client");
            /*data = _token;
            httpURLConnection1.setRequestProperty("Authorization", "Bearer " + _token);

            //httpURLConnection1.connect();
            //data +=" respcode=" + httpURLConnection1.getResponseCode();
            //MainActivity.mainPageText.setText("HERE1"  + data + "bura" );
            InputStream input = httpURLConnection1.getInputStream();

            //InputStream errors = httpURLConnection1.getErrorStream();
            //BufferedReader errorBuffer = new BufferedReader(new InputStreamReader(errors));

            BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
            line = "";
            data="";
            while(line!=null){
                line =  buffer.readLine();
                data+=line;
            }*/

        } catch (MalformedURLException e) {
            result+="MalformedURLException "  + e.toString();
            //MainActivity.txtTagContent.setText(data);
            e.printStackTrace();
        } catch (IOException e) {
            result+="IOEXCEPTION "+e.toString();
            //MainActivity.txtTagContent.setText(data);
            e.printStackTrace();
        } catch (JSONException e) {
            result+="JSONEXCEPTION " + e.toString();
            //MainActivity.txtTagContent.setText(data);

            e.printStackTrace();
        } catch(Exception e){
            data += "ERROR + " + e.toString();
            //MainActivity.txtTagContent.setText(data);
            //ffhfghfh
        }
        return null;
    }
    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        //MainActivity.txtTagContent.setText("EXECUTION DONE" + data);
        MainActivity.mainPageText.setText(result+"");

    }

}
