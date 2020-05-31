package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button settingsButton;
    private Button htmlReqButton;
    public static TextView mainPageText;
    public static EditText txtTagContent;
    NfcAdapter nfcAdapter;
    ArmonApiClient armon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsButton = findViewById(R.id.settingsButton);
        mainPageText = findViewById(R.id.HtmlReqText);
        txtTagContent  =  findViewById(R.id.txtTagContent);
        htmlReqButton = findViewById(R.id.getData);

        armon = new ArmonApiClient("odtupass-dev.metu.edu.tr");
        armon.initAndLogin("","");//TODOADDUNPW

        settingsButton.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {    openSettings();   }});
        htmlReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserInfoFromStudentId getUserInfoFromStudentId = new GetUserInfoFromStudentId();
                getUserInfoFromStudentId.execute();
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC ENABLED :)",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"NFC NOT ENABLED or your phone doesn't support nfc :( \nPlease enable Nfc",Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NfcUtil.isTag(intent)){
            //GetUserInfoFromCardId getUserInfoFromCardId = new GetUserInfoFromCardId();
            //getUserInfoFromCardId.execute(NfcUtil.Read(intent));
            armon.FindByCredentialNumber(NfcUtil.Read(intent));
        }else{
            toast("Unknown type of intent/card");
        }
        toast("Intent");
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
