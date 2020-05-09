package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsV2 extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH1 = "switch1";
    public static final String TEXT = "text";
    private Button saveButton;
    private EditText textname;
    private Switch aSwitch;

    private String text;
    private boolean switchbool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_v2);
        saveButton = findViewById(R.id.savebutton);
        textname = findViewById(R.id.editTect);
        aSwitch = findViewById(R.id.switch1);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveData();
            }
        });
        loadData();
    }


    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT,textname.getText().toString());
        editor.putBoolean(SWITCH1,aSwitch.isChecked());
        editor.apply();
        Toast.makeText(this,"Saved!",Toast.LENGTH_SHORT).show();
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT,"");
        switchbool = sharedPreferences.getBoolean(SWITCH1,false);
        applyThem();
    }
    private void applyThem(){
        textname.setText(text);
        aSwitch.setChecked(switchbool);
    }
}
