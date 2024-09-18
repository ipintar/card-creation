package com.task.client.card.app.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Service for encrypting and decrypting data using the AES algorithm.
 * This service fetches the encryption key from an environment variable
 * and applies encryption or decryption when required.
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    /**
     * Constructor that initializes the encryption key from the environment variable.
     * If the environment variable 'ENC_KEY' is not found or is empty, an exception is thrown.
     */
    public EncryptionService() {
        final String secretKeyFromEnv = System.getenv("ENC_KEY");
        if (secretKeyFromEnv == null || secretKeyFromEnv.isEmpty()) {
            throw new IllegalArgumentException("Encryption key not found in environment variables");
        }
        this.secretKey = new SecretKeySpec(secretKeyFromEnv.getBytes(), ALGORITHM);
    }

    /**
     * Encrypts the given data using the AES algorithm and returns the encrypted string.
     *
     * @param data the plain text data to be encrypted
     * @return the encrypted data as a Base64-encoded string
     * @throws Exception if encryption fails
     */
    public String encrypt(final String data) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        final byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Decrypts the given encrypted data back to plain text using the AES algorithm.
     *
     * @param encryptedData the Base64-encoded encrypted data
     * @return the decrypted plain text data
     * @throws Exception if decryption fails
     */
    public String decrypt(final String encryptedData) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        final byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        final byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }
}
