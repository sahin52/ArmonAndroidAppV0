package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button settingsbutton;
    private Button htmlReqButton;
    public static TextView mainPageText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsbutton = (Button) findViewById(R.id.settingsButton);
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        mainPageText = findViewById(R.id.MainPageText);

        htmlReqButton = findViewById(R.id.getData);
        htmlReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchData fetchData = new FetchData();
                fetchData.execute();
            }
        });
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC ENABLED :)",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"NFC NOT ENABLED :(",Toast.LENGTH_LONG).show();
        }
    }
    private void openSettings(){
        Intent i = new Intent(this, SettingsV2.class);
        startActivity(i);
    }

}
