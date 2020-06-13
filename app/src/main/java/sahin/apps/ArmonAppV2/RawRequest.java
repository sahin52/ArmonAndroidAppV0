package sahin.apps.ArmonAppV2;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class  RawRequest extends AsyncTask<String,String,String> {
    private JSONObject requestHeader;
    private String result = "";
    String errors = "";
    String Authorization;
    RawRequest(){
        requestHeader = new JSONObject();
        Authorization = "";
        try {
            requestHeader.put("content-type","application/json");
            requestHeader.put("user-agent","armon-api-android-client");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String method = strings[0];
        String url = strings[1];
        String data = strings[2];

        try {
            URL Url = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) Url.openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("content-type",requestHeader.getString("content-type"));
            httpURLConnection.setRequestProperty("user-agent",requestHeader.getString("user-agent"));
            try{
                httpURLConnection.setRequestProperty("Authorization",requestHeader.getString("Authorization"));
            }catch(Exception ignored){

            }

            if(!data.equals("")){
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String jsonResult = "";
            String line = "";
            while(line!=null){
                jsonResult += line;
                line = bufferedReader.readLine();
            }
            return jsonResult;
        } catch (JSONException | IOException e) {
            errors+=e.toString();
            e.printStackTrace();
        }
        return errors;
    }
    public void setRequestHeader(JSONObject rhsRequestHeader){
        this.requestHeader = rhsRequestHeader;
    }
    public JSONObject getRequestHeader() {
        return requestHeader;
    }
}
