package com.algaita.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.algaita.R;

public class NoInternet extends AppCompatActivity {
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_no_item_internet_image);

        Button bt_retry = findViewById(R.id.bt_retry);
        bt_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
}
