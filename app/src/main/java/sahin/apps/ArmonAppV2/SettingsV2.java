package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsV2 extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    private EditText username;
    private EditText password;
    private EditText organizationInput;
    private String text;
    private String organization;
    public TextView loginResInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_v2);
        Button saveButton = findViewById(R.id.savebutton);
        organizationInput = findViewById(R.id.organizationInput);
        loginResInput = findViewById(R.id.loginResInput);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveDataAndLogin();
            }
        });
        username = findViewById(R.id.editTect);
        password = findViewById(R.id.inputPassword);
        loadData();
    }


    private void saveDataAndLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.putString("organization",organizationInput.getText().toString());
        editor.apply();
        MainActivity.armon.initAndLogin(sharedPreferences.getString("username",""),sharedPreferences.getString("password",""),sharedPreferences.getString("organization",""));
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        text = sharedPreferences.getString("username","");
        organization = sharedPreferences.getString("organization","");
        applyThem();
    }
    private void applyThem(){
        organizationInput.setText(organization);
        username.setText(text);
    }
}
