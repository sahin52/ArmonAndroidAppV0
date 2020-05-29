package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.nfc.tech.MifareClassic;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button settingsbutton;
    private Button htmlReqButton;
    public static TextView mainPageText;
    String organizationId;
    String grantId;
    String organizationRoot;
    ToggleButton tglReadWrite;
    public static EditText txtTagContent;
    NfcAdapter nfcAdapter;
    String domain;
    String username = "sahinkasap";
    String password = "uWb1QnKB";
    public String token;
    public String refreshToken;
    public String expiresIn;
    public Map<String,String> requestHeaders;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestHeaders = new HashMap<>();
        requestHeaders.put("content-type","application/json");
        requestHeaders.put("user-agent","Sahin-Android");
        setContentView(R.layout.activity_main);
        settingsbutton = (Button) findViewById(R.id.settingsButton);
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        mainPageText = findViewById(R.id.HtmlReqText);

        htmlReqButton = findViewById(R.id.getData);
        htmlReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchData fetchData = new FetchData();

                fetchData.execute("E6DBDD3E");
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC ENABLED :)",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"NFC NOT ENABLED :(",Toast.LENGTH_SHORT).show();
            finish();
        }
        tglReadWrite = findViewById(R.id.tglReadWrite);
        txtTagContent  =  findViewById(R.id.txtTagContent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String content="";
        super.onNewIntent(intent);
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            if(intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] tagIdbytes = tag.getId();
                String CardId = bytesToHex(tagIdbytes);
                mainPageText.setText(CardId);
                FetchData fetchData = new FetchData();
                fetchData.execute(CardId);

            }
            else{
                Toast.makeText(this,"Unknown type of card",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Unknown type of intent",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilter,null);
    }

    private void openSettings(){
        Intent i = new Intent(this, SettingsV2.class);
        startActivity(i);
    }
    public void tglReadWriteOnClick(View view){
        txtTagContent.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return false;
    }
    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilters = new IntentFilter[]{} ;
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }
    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    private String getUserDetail(final String number){
        txtTagContent.setText("getUserDetail");
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        final String domain = "https://odtupass-dev.metu.edu.tr";
        this.domain = domain;
        String url ="https://odtupass-dev.metu.edu.tr/auth/user";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                txtTagContent.setText(response);
                try {
                    JSONObject responseJson = new JSONObject(response);
                    String jsonObject = responseJson.getString("organizations");
                    JSONArray organizations = new JSONArray(jsonObject);
                    JSONObject firstOrganization = organizations.getJSONObject(0);
                    String organizationName = firstOrganization.getString("name");
                    organizationId = firstOrganization.getString("id");
                    grantId = (new JSONArray(firstOrganization.getString("authentications"))).getJSONObject(0).getString("id");
                    organizationRoot = domain + "/u/v1/" + organizationId;
                    txtTagContent.setText(organizationName + "=name \n orgid =  " + organizationId + "\n grantid = "+ grantId);
                    login(number);

                } catch (JSONException e) {
                    txtTagContent.setText("ERROR");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String temp = error.getMessage() + " ERROR";
                txtTagContent.setText(temp);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put("username",username);
                return map;
            }


        };
        requestQueue.add(stringRequest);
        return number;
    }
    private void login(final String number){
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        txtTagContent.setText("login");
        final String url = domain + "/auth/usernamepass/" + grantId;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                txtTagContent.setText(response);
                try {
                    JSONObject responseJson = new JSONObject(response);
                    mainPageText.setText(response);
                    refreshToken= responseJson.getString("refreshToken");
                    expiresIn = responseJson.getString("expiresIn");
                    token = responseJson.getString("token");
                    requestHeaders.put("Authorization","Bearer "+token);
                    getStudentInfo(number);
                } catch (JSONException e) {
                    txtTagContent.setText("response is not json");
                    e.printStackTrace();
                }catch(Exception e){
                    txtTagContent.setText("error occurred");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    txtTagContent.setText("ERROR ON LOGGING IN " + error.toString() + error.getMessage() + error.getCause() + error.networkResponse.statusCode
                            +"\n" + url + (new JSONObject(error.networkResponse.data.toString())).toString() + " --- " + error.networkResponse.allHeaders);
                } catch (JSONException e) {
                    txtTagContent.setText("ERROR ON LOGGING IN " + error.toString() + error.getMessage() + error.getCause() + error.networkResponse.statusCode
                            +"\n" + url + " -+- "+  error.networkResponse.data + " --- " + error.networkResponse.allHeaders);
                }
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("username",username);
                map.put("password",password);
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestHeaders;
            }
        };

        requestQueue.add(stringRequest);
    }
    private void getStudentInfo(String number){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        final String url = organizationRoot+"/identity/detailed/" +number ;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                txtTagContent.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                txtTagContent.setText("ERROR-getStudentInfo " + error.toString() + error.getMessage() + error.getCause() + error.networkResponse.statusCode
                    +"\n" + url + error.networkResponse.data.toString() + " --- " + error.networkResponse.allHeaders
                );

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestHeaders;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void toast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            int place = bytes.length-j-1;
            hexChars[place * 2] = HEX_ARRAY[v >>> 4];
            hexChars[place * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
