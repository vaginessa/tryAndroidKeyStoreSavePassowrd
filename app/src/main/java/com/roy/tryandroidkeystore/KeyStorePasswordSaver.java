package com.roy.tryandroidkeystore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import static android.content.Context.MODE_PRIVATE;
import static com.roy.tryandroidkeystore.Constants.SP_NAME;

/**
 * Created by roy.leung on 7/8/2017.
 */

public class KeyStorePasswordSaver {

    private static KeyStore keyStore;

    public static void saveThePassword(Context context, String passwordName, String password, SaveSuccessListener listener) {

        if (keyStore == null) {
            try {
                keyStore = KeyStore.getInstance(Constants.KEY_STORE_NAME);
                keyStore.load(null);
            } catch (Exception e) {
                e.printStackTrace();
                listener.success(false);
                return;
            }
        }


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
                        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
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
                    String encryptedPassword = encryptString(password);
                    saveTheEncryptedPassword(context, passwordName, encryptedPassword, listener);
                } else {
                    listener.success(false);
                }

            }
        }.execute(password);


    }

    public static String getTheSavedPassword(Context context, String passwordName) {

        String encrypgtedPassword = getTheEncryptedPassword(context, passwordName);

        String decryptedPassword = decryptTheString(encrypgtedPassword);


        return decryptedPassword;

    }

    private static String encryptString(String theStringtoEncrypt) {
        try {
            KeyStore.Entry entry = keyStore.getEntry(Constants.ALIAS, null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

            // Encrypt the text
            String initialText = theStringtoEncrypt;
            if (initialText.isEmpty()) {
                return "";
            }

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
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
            return "";
        }
    }


    private static void saveTheEncryptedPassword(Context context, String passwordName, String encryptedPassword, SaveSuccessListener listener) {


        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, MODE_PRIVATE).edit();
//SharedPreferences.Editor editor=getPreferences(MODE_PRIVATE).edit();
//SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor.putString(passwordName, encryptedPassword);
            editor.apply();
            listener.success(true);
        } catch (Exception e) {
            listener.success(false);

        }


    }

    private static String getTheEncryptedPassword(Context context, String passwordName) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String result = sp.getString(passwordName, "");
//SharedPreferences.Editor editor=getPreferences(MODE_PRIVATE).edit();
//SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        return result;


    }

    private static String decryptTheString(String encryptedString) {
        try {
            KeyStore.Entry entry = keyStore.getEntry(Constants.ALIAS, null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
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

    public interface SaveSuccessListener {
        void success(boolean success);
    }
}
