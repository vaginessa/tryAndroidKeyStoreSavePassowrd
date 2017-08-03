package com.roy.tryandroidkeystore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etKeyInput;
    private Button btSave;
    private TextView tvShowKey;
    private Button btShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etKeyInput = (EditText) findViewById(R.id.et_key_input);
        btSave = (Button) findViewById(R.id.bt_save);
        tvShowKey = (TextView) findViewById(R.id.tv_show_key);
        btShow = (Button) findViewById(R.id.bt_show);

        btSave.setOnClickListener(this);
        btShow.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                break;

            case R.id.bt_show:
                break;
        }

    }
}


