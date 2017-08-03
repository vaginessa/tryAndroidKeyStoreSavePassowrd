package com.roy.tryandroidkeystore;

import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

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

    public void createNewKeys(String key) {


        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                String alias = strings[0];
                try {
                    // Create new key if needed

                    if (!keyStore.containsAlias(alias)) {
                        Calendar start = Calendar.getInstance();
                        Calendar end = Calendar.getInstance();
                        end.add(Calendar.YEAR, 99);
                        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(MainActivity.this)
                                .setAlias(alias)
                                .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                                .setSerialNumber(BigInteger.ONE)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build();
                        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                        generator.initialize(spec);

                        KeyPair keyPair = generator.generateKeyPair();

                        return true;
                    }
                    return true;
                } catch (Exception e) {
                    return false;

                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    Toast.makeText(MainActivity.this, "Saved success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Saved error", Toast.LENGTH_LONG).show();
                }

            }
        }.execute(key);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                String keyString = etKeyInput.getText().toString().trim();
                if (keyString.isEmpty())
                    Toast.makeText(this, "The input is empty", Toast.LENGTH_LONG).show();

                createNewKeys(keyString);
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


