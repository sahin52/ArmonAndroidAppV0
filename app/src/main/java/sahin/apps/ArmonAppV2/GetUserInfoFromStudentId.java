package sahin.apps.ArmonAppV2;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUserInfoFromStudentId extends AsyncTask<String, String, String> {
    public String data = "";
    String result;
    String _organizationId;
    String _grantId;
    String organizationRoot;
    String apiRoot = "https://odtupass-dev.metu.edu.tr";
    String _token;
    String _refreshToken;
    String _tokenExpireTime;
    private static final String UTF_8 = "UTF-8";

    @Override
    protected String doInBackground(String... params) {
        String uniqueId="";
        String password = "";
        String username = "";
        data = "started";
        try {
            //TODO
            /*result = "";
            //String credentialData = params[0];
            JSONObject requestParams = new JSONObject();
            requestParams.put("username", username);
            URL url = new URL("https://odtupass-dev.metu.edu.tr/auth/user");
            data += "*0*";
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setRequestProperty("user-agent", "armon-api-android-client");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(requestParams.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            data += "*1*";
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String jsonResult = "";
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                jsonResult += line;
            }
            data += "*2*";
            //MainActivity.txtTagContent.setText(data);
            JSONObject returnsFromInit = new JSONObject(jsonResult);
            JSONArray organizations = returnsFromInit.getJSONArray("organizations");
            JSONObject organization = organizations.getJSONObject(0);
            _organizationId = organization.getString("id");
            _grantId = organization.getJSONArray("authentications").getJSONObject(0).getString("id");
            organizationRoot = "https://odtupass-dev.metu.edu.tr/u/v1/" + _organizationId;

            url = new URL(apiRoot + "/auth/usernamepass/" + _grantId);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setRequestProperty("user-agent", "armon-api-android-client");
            outputStream = httpURLConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            requestParams.put("password", password);
            bufferedWriter.write(requestParams.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream i = httpURLConnection.getInputStream();
            BufferedReader b = new BufferedReader(new InputStreamReader(i));
            line = "";
            data = "";
            while (line != null) {
                line = b.readLine();
                data += line;
            }


            JSONObject returnsFromLogin = new JSONObject(data);
            _token = returnsFromLogin.getString("token");

            _refreshToken = returnsFromLogin.getString("refreshToken");
            _tokenExpireTime = returnsFromLogin.getString("expiresIn");



            URL url1 = new URL(organizationRoot+"/identity/search/exact");
            HttpURLConnection httpURLConnection1 = (HttpURLConnection) url1.openConnection();
            httpURLConnection1.setRequestMethod("POST");
            httpURLConnection1.setRequestProperty("content-type", "application/json");
            httpURLConnection1.setRequestProperty("user-agent", "armon-api-android-client");
            httpURLConnection1.setRequestProperty("Authorization", "Bearer " + _token);

            JSONObject requestParams1 = new JSONObject();
            requestParams1.put("take",30);
            requestParams1.put("skip",0);
            JSONArray numbers = new JSONArray();
            numbers.put("701763");
            JSONObject extensionFields;
            JSONObject user = new JSONObject();
            user.put("username",uniqueId);
            requestParams1.put("user",user);
            result+= requestParams1.toString();
            OutputStream outputStream1 = httpURLConnection1.getOutputStream();
            BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(outputStream1, "UTF-8"));
            bufferedWriter1.write(requestParams1.toString());
            bufferedWriter1.flush();
            bufferedWriter1.close();
            outputStream1.close();

            InputStream inputStream1 = httpURLConnection1.getInputStream();
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1));
            //httpURLConnection1.connect();
            //data +=" respcode=" + httpURLConnection1.getResponseCode();
            line = "";
            data = "";
            while (line != null) { data += line;line = bufferedReader1.readLine(); }
                    /**/

        }catch (Exception e){
            data += "ERROR " + e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        MainActivity.TCinput.setText(result+ "\n------\n"+data);
    }
}
