package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapRegionDecoder;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private BigView bigView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bigView = findViewById(R.id.bigview);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("big.png");
            bigView.setImage(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
