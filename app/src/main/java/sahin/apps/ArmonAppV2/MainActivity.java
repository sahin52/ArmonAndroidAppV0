package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String username="";
    private String password="";
    private String organization="";

    public static TextView mainPageText;
    public static EditText TCinput;
    public static TextView girisYapildiView;
    public static TextView fullNameResultTextView;
    public static TextView uniqueIdResultTextView;
    public static TextView userGroupsResultTextView;
    public static TextView organizationUnitsResultTextView;
    public static TextView odtuNumbersResultTextView;
    public static TextView credentialsResultTextView;

    public Button retryButton;
    NfcAdapter nfcAdapter;
    public static ArmonApiClient armon;
    static Activity thisActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Tam ekran yapar
        thisActivity = this;
        Button settingsButton = findViewById(R.id.settingsButton);
        Button htmlReqButton = findViewById(R.id.getData);


        girisYapildiView = findViewById(R.id.girisYapildiView);
        mainPageText = findViewById(R.id.HtmlReqText);
        TCinput  =  findViewById(R.id.TCinput);
        fullNameResultTextView = findViewById(R.id.fullNameResultTextView);
        uniqueIdResultTextView = findViewById(R.id.uniqueIdResultTextView);
        userGroupsResultTextView = findViewById(R.id.userGroupsResultTextView);
        organizationUnitsResultTextView = findViewById(R.id.organizationUnitsResultTextView);
        odtuNumbersResultTextView = findViewById(R.id.odtuNumbersResultTextView);
        credentialsResultTextView = findViewById(R.id.credentialsResultTextView);

        armon = new ArmonApiClient("odtupass-dev.metu.edu.tr");
        final SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username","");
        password = sharedPreferences.getString("password","");
        organization = sharedPreferences.getString("organization","");
        armon.initAndLogin(username,password,organization);

        settingsButton.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {    openSettings();   }});
        htmlReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPageText.setText("");
                fullNameResultTextView.setText("");
                uniqueIdResultTextView.setText("");
                userGroupsResultTextView.setText("");
                organizationUnitsResultTextView.setText("");
                odtuNumbersResultTextView.setText("");
                credentialsResultTextView.setText("");
                if(armon.isLoggedIn){
                    final String tcInput = String.valueOf(TCinput.getText());
                    TCinput.setHint(TCinput.getText());
                    TCinput.setText("");
                    armon.findByTCNumberAndDisplay(tcInput);
                    toast(tcInput);
                }else{
                    toast("Giris yapılmadan denemeyiniz!");
                }
            }
        });

        retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = sharedPreferences.getString("username","");
                password = sharedPreferences.getString("password","");
                organization = sharedPreferences.getString("organization","");
                armon.initAndLogin(username,password,organization);
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            toast("NFC AÇIK :)");
        }else{
            toast("NFC Okuma Aktif Değil  :( \nLütfen NFC özelliğini aktive edin!");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NfcUtil.isTag(intent)){
            mainPageText.setText("");
            TCinput.setText("");
            fullNameResultTextView.setText("");
            uniqueIdResultTextView.setText("");
            userGroupsResultTextView.setText("");
            organizationUnitsResultTextView.setText("");
            odtuNumbersResultTextView.setText("");
            credentialsResultTextView.setText("");
            armon.findByCredentialNumberAndDisplay(NfcUtil.Read(intent));
        }else{
            toast("Bilinmeyen Kart veya İstek Türü");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    private void openSettings(){
        Intent openingSettingsIntent = new Intent(this, SettingsV2.class);
        startActivity(openingSettingsIntent);
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

    public static void toast(String textToToast) {
        Toast.makeText(thisActivity, textToToast, Toast.LENGTH_SHORT).show();
    }
}
