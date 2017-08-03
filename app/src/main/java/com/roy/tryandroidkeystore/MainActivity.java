package com.roy.tryandroidkeystore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etKeyInput;
    private Button btSave;
    private TextView tvShowKey;
    private Button btShow;
    private KeyStore keyStore;

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

        try {
            keyStore = KeyStore.getInstance(Constants.KEY_STORE_NAME);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private ArrayList<String> getTheStoreKeys(KeyStore keyStore) {
        ArrayList<String> keyLists = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyLists.add(aliases.nextElement());
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } finally {
            return keyLists;
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                break;

            case R.id.bt_show:
                if (keyStore == null)
                    return;

                ArrayList<String> keyList = getTheStoreKeys(keyStore);
                String tempString = "";
                for (String key : keyList) {
                    tempString = tempString + key + "\n";
                }

                if (keyList.isEmpty()) {
                    tvShowKey.setText("empty");
                } else {
                    tvShowKey.setText(tempString);
                }


                break;
        }

    }
}


