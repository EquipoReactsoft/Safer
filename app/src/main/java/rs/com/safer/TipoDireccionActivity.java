package rs.com.safer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TipoDireccionActivity extends AppCompatActivity {
    CardView cv1, cv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_direccion);

        cv1 = findViewById(R.id.cardView1);
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TipoDireccionActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        cv2 = findViewById(R.id.cardView2);
        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TipoDireccionActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

    }
}
