package com.roy.tryandroidkeystore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etKeyInput;
    private Button btSave;
    private TextView tvShowKey;
    private Button btShow;
    private KeyStore keyStore;
    private TextView tvEncryptPassword;
    private TextView tvDePassword;
    private Button btShowPassword;
    private final String PASSWORD_NAME = "PASSWORD_NAME_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etKeyInput = (EditText) findViewById(R.id.et_key_input);
        btSave = (Button) findViewById(R.id.bt_save);
        tvShowKey = (TextView) findViewById(R.id.tv_show_key);
        btShow = (Button) findViewById(R.id.bt_show);
        tvEncryptPassword = (TextView) findViewById(R.id.tv_encrypt_password);
        tvDePassword = (TextView) findViewById(R.id.tv_de_password);
        btShowPassword = (Button) findViewById(R.id.bt_show_password);

        btSave.setOnClickListener(this);
        btShow.setOnClickListener(this);
        btShowPassword.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                KeyStorePasswordSaver.saveThePassword(this, PASSWORD_NAME, etKeyInput.getText().toString(), (boolean success) -> {
                    Toast.makeText(MainActivity.this, "is saved: " + success, Toast.LENGTH_LONG).show();
                });

                break;

            case R.id.bt_show:


                break;

            case R.id.bt_show_password:
                String savedPassword = KeyStorePasswordSaver.getTheSavedPassword(this, PASSWORD_NAME);
                tvDePassword.setText(savedPassword);

                break;
        }

    }

}


