package com.roy.tryandroidkeystore;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etKeyInput;
    private Button btSave;
    private TextView tvShowKey;
    private Button btShow;
    private KeyStore keyStore;
    private TextView tvEncryptPassword;
    private TextView tvDePassword;
    private Button btShowPassword;

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
                String key = strings[0];
                try {
                    // Create new key if needed

                    if (!keyStore.containsAlias(Constants.ALIAS)) {
                        Calendar start = Calendar.getInstance();
                        Calendar end = Calendar.getInstance();
                        end.add(Calendar.YEAR, 99);
                        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(MainActivity.this)
                                .setAlias(Constants.ALIAS)
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
                    String encryptedPassword = encryptString(etKeyInput.getText().toString());
                    saveTheEncryptedPassword(encryptedPassword);
                } else {
                    Toast.makeText(MainActivity.this, "Saved error", Toast.LENGTH_LONG).show();
                }

            }
        }.execute(key);


    }

    private String encryptString(String theStringtoEncrypt) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(Constants.ALIAS, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            // Encrypt the text
            String initialText = theStringtoEncrypt;
            if (initialText.isEmpty()) {
                Toast.makeText(this, "Enter text in the 'Initial Text' widget", Toast.LENGTH_LONG).show();
                return "";
            }

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(initialText.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            String encryptedString = Base64.encodeToString(vals, Base64.DEFAULT);
            return encryptedString;
        } catch (Exception e) {
            Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            return "";
        }
    }

    private String decryptTheString(String encryptedString) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(Constants.ALIAS, null);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            String cipherText = encryptedString;
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String decryptedString = new String(bytes, 0, bytes.length, "UTF-8");
            return decryptedString;

        } catch (Exception e) {
            return "";
        }

    }

    private void saveTheEncryptedPassword(String encryptedPassword) {
        SharedPreferences.Editor editor = getSharedPreferences("share", MODE_PRIVATE).edit();
//SharedPreferences.Editor editor=getPreferences(MODE_PRIVATE).edit();
//SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString("password", encryptedPassword);
        editor.commit();


    }

    private String getTheEncryptedPassword() {
        SharedPreferences sp = getSharedPreferences("share", MODE_PRIVATE);
//SharedPreferences.Editor editor=getPreferences(MODE_PRIVATE).edit();
//SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        return sp.getString("password", "");


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

            case R.id.bt_show_password:
                String encrypgtedPassword = getTheEncryptedPassword();
                tvEncryptPassword.setText(encrypgtedPassword);

                String decryptedPassword = decryptTheString(encrypgtedPassword);
                tvDePassword.setText(decryptedPassword);
                break;
        }

    }

}


