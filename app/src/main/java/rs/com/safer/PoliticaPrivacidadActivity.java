package rs.com.safer;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.InputStream;

public class PoliticaPrivacidadActivity extends AppCompatActivity {

    TextView priSafer_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidad);

        priSafer_text = findViewById(R.id.priSafer_text);

        try {
            Resources res = getResources(); InputStream in_s = res.openRawResource(R.raw.term);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            priSafer_text.setText(new String(b));
        } catch (Exception e) {
            priSafer_text.setText("INFO ERROR");
        }


    }
}
