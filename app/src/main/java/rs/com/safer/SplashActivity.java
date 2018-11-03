package rs.com.safer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isOnline()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 3000);

    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            if (exitValue != 0) {
                Handler handler = new Handler(Looper.getMainLooper());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.connect_internet_access_exception, Toast.LENGTH_SHORT).show();
                    }
                }, 1000 );
            }
            return (exitValue == 0);
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.connect_internet_access_exception), Toast.LENGTH_SHORT).show();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.connect_internet_exception), Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
