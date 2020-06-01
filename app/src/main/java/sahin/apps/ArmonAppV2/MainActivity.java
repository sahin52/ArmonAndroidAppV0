package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String username="";//TODOADDUNPW
    private String password="";
    private Button settingsButton;
    private Button htmlReqButton;
    public static TextView mainPageText;
    public static EditText TCinput;
    public static TextView girisYapildiView;
    public Button retryButton;
    NfcAdapter nfcAdapter;
    ArmonApiClient armon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsButton = findViewById(R.id.settingsButton);
        mainPageText = findViewById(R.id.HtmlReqText);
        TCinput  =  findViewById(R.id.TCinput);
        htmlReqButton = findViewById(R.id.getData);
        girisYapildiView = findViewById(R.id.girisYapildiView);

        armon = new ArmonApiClient("odtupass-dev.metu.edu.tr");
        final SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username","");
        password = sharedPreferences.getString("password","");
        armon.initAndLogin(username,password);

        settingsButton.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {    openSettings();   }});
        htmlReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                armon.initAndLogin(username,password);
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC AÇIK :)",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"NFC Okuma Aktif Değil  :( \nLütfen NFC özelliğini aktive edin!",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NfcUtil.isTag(intent)){
            armon.findByCredentialNumber(NfcUtil.Read(intent));
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

    private void toast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
