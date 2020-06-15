package sahin.apps.ArmonAppV2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenV2 extends AppCompatActivity {
    private int SplashScreenTimeToStay = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_v2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenV2.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SplashScreenTimeToStay);
    }
}