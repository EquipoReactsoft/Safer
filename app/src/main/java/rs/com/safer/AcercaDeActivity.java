package rs.com.safer;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.InputStream;

public class AcercaDeActivity extends AppCompatActivity {

    TextView  txt_infoSafer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        txt_infoSafer = findViewById(R.id.infoSafer_text);

        try {
            Resources res = getResources(); InputStream in_s = res.openRawResource(R.raw.term);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            txt_infoSafer.setText(new String(b));
        } catch (Exception e) {
            txt_infoSafer.setText("INFO ERROR");
        }

    }
}
