/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.app.Application;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;

import com.nextiva.nextivaapp.android.managers.interfaces.KeyStoreManager;
import com.nextiva.nextivaapp.android.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.security.auth.x500.X500Principal;

/**
 * Created by adammacdonald on 2/6/18.
 */

@Singleton
public class NextivaKeyStoreManager implements KeyStoreManager {

    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";

    private final Application mApplication;

    @Inject
    public NextivaKeyStoreManager(Application application) {
        mApplication = application;
    }

    // --------------------------------------------------------------------------------------------
    // KeyStoreManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void addAlias(@KeyStoreAlias String alias) {
        try {
            KeyStore keystore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keystore.load(null);

            // Create new key if needed
            if (!keystore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.set(Calendar.YEAR, start.get(Calendar.YEAR) - 1);
                end.add(Calendar.YEAR, 1);

                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", KEYSTORE_PROVIDER);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setCertificateSubject(new X500Principal("CN=Nextiva App, O=Nextiva"))
                            .setCertificateSerialNumber(BigInteger.ONE)
                            .setKeyValidityStart(start.getTime())
                            .setKeyValidityEnd(end.getTime())
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .build();
                    generator.initialize(spec);

                } else {
                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(mApplication)
                            .setAlias(alias)
                            .setSubject(new X500Principal("CN=Nextiva App, O=Nextiva"))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();
                    generator.initialize(spec);
                }
                generator.generateKeyPair();
            }

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            LogUtil.d("NextivaKeyStoreManager", "Error add alias: " + e.getMessage());
        }
    }

    @Override
    public void deleteAlias(@KeyStoreAlias String alias) {
        try {
            KeyStore keystore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keystore.load(null);
            if (keystore.containsAlias(alias)) {
                keystore.deleteEntry(alias);
            } else {
                LogUtil.d("NextivaKeyStoreManager", "Alias '" + alias + "' does not exist");
            }
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            LogUtil.d("NextivaKeyStoreManager", "Error delete alias: " + e.getMessage());
        }
    }

    @Override
    public String encryptString(@KeyStoreAlias String alias, String inputText) {
        try {
            if (TextUtils.isEmpty(inputText)) {
                throw new IllegalArgumentException("Input text cannot be empty");
            }

            KeyStore keystore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keystore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias, null);
            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

            Cipher input = Cipher.getInstance(RSA_MODE);
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = input.doFinal(inputText.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnrecoverableEntryException | NullPointerException | IllegalBlockSizeException | BadPaddingException e) {
            LogUtil.d("NextivaKeyStoreManager", "Error encrypting string: " + e.getMessage());
        }

        return null;
    }

    @Override
    public String decryptString(@KeyStoreAlias String alias, String cipherText) {
        try {
            KeyStore keystore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keystore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias, null);

            Cipher output = Cipher.getInstance(RSA_MODE);
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            byte[] encryptedBytes = Base64.decode(cipherText, Base64.DEFAULT);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedBytes);
                 CipherInputStream cipherInputStream = new CipherInputStream(inputStream, output);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LogUtil.d("NextivaKeyStoreManager", "Error decrypting string: " + e.getMessage());
            return null;
        }
    }
    // --------------------------------------------------------------------------------------------
}