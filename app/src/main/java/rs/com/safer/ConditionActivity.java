package rs.com.safer;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;

public class ConditionActivity extends AppCompatActivity {

    Button btnIrRegister;
    TextView info_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        TextView ifText = (TextView) findViewById(R.id.info_text);
        // ifText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        try {
            Resources res = getResources(); InputStream in_s = res.openRawResource(R.raw.term);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            ifText.setText(new String(b));
        } catch (Exception e) {
            ifText.setText("Error: can't show terms.");
        }

        btnIrRegister = (Button) findViewById(R.id.btnIrRegisterActivity);
        btnIrRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConditionActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

    }
}
