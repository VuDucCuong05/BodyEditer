package com.gos.body.editor.photo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gos.body.editor.photo.beauty.BeautyDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BeautyDialog.OnBeautySave {
    private Button btnTest;
    private Bitmap bitmapOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        btnTest = findViewById(R.id.btn_test);
        btnTest.setOnClickListener(this);
        bitmapOrigin = BitmapFactory.decodeResource(getResources(),
                R.drawable.img_test);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_test) {
            BeautyDialog.show(this, bitmapOrigin, this);
        }
    }

    @Override
    public void onBeautySave(Bitmap bitmap) {
        Toast.makeText(this, "finish edit body", Toast.LENGTH_SHORT).show();
    }
}