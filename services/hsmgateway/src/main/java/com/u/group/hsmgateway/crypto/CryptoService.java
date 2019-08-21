package com.u.group.hsmgateway.crypto;

import com.cavium.cfm2.CFM2Exception;
import com.cavium.cfm2.Util;
import com.cavium.key.CaviumKey;
import com.cavium.key.parameter.CaviumAESKeyGenParameterSpec;
import com.u.group.hsmgateway.config.AsymmetricKeys;
import com.u.group.hsmgateway.web.CryptoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.u.group.hsmgateway.config.Configuration.MASTER_KEY_LABEL;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class CryptoService {

    private final KeyStore keyStore;
    private String transformation = "AES/ECB/PKCS5Padding";


    @Autowired
    public CryptoService(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public String encryptDossier(CryptoRequest request) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException {
        final String dossierId = request.getId();
        final SecretKey masterKey = (SecretKey) keyStore.getKey(MASTER_KEY_LABEL, null);
        final String objectLabel = toHexString(getSHA(dossierId + masterKey.getAlgorithm()));

        Key objectKey;

        if (keyStore.containsAlias(objectLabel)) {
            objectKey = keyStore.getKey(objectLabel, null);
        } else {
            objectKey = generateExtractableKey(objectLabel);
        }

        byte[] cipherText = encryptAES(objectKey, request.getDossier().getBytes(UTF_8), dossierId);
        return cipherText != null ? Base64.getEncoder().encodeToString(cipherText) : null;
    }

    public String decryptDossier(CryptoRequest request) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        final String dossierId = request.getId();
        final Key masterKey = keyStore.getKey(MASTER_KEY_LABEL, null);
        final Key objectKey = keyStore.getKey(toHexString(getSHA(dossierId + masterKey.getAlgorithm())), null);
        try {
            final byte[] decodedDossier = Base64.getDecoder().decode(request.getDossier());
            byte[] result = decryptAES(decodedDossier, objectKey, dossierId);

            return new String(result, UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Boolean deleteDossier(String id) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CFM2Exception {
        final Key masterKey = keyStore.getKey(MASTER_KEY_LABEL, null);
        final String objectLabel = toHexString(getSHA(id + masterKey.getAlgorithm()));
        if (keyStore.containsAlias(objectLabel)) {
            final Key objectKey = keyStore.getKey(objectLabel, null);
            Util.deleteKey((CaviumKey) objectKey);
            return true;
        } else {
            return false;
        }
    }

    private void rsaAesWrap(SecretKey wrappingKey, Key extractableKey)
            throws Exception {

        CaviumKey caviumWrappingKey = importWrappingKey(wrappingKey);
        Util.persistKey(caviumWrappingKey);

        // Extractable keys must be marked extractable.
        // Using the Cavium wrapping key, wrap and unwrap the extractable key.
        wrap(caviumWrappingKey, extractableKey);


        // Wrap the extractable key using the wrappingKey.
//        return cipher.wrap(extractableKey);

//        // Unwrap using the SunJCE.
//        cipher.init(Cipher.UNWRAP_MODE, unwrappingKey);
//        Key unwrappedExtractableKey = cipher.unwrap(wrappedBytes, "AES", Cipher.SECRET_KEY);
//
//        // Compare the two keys.
//        // Notice that extractable keys can be exported from the HSM using the .getEncoded() method.
//        assert (Arrays.equals(extractableKey.getEncoded(), unwrappedExtractableKey.getEncoded()));
//        System.out.printf("\nVerified key when using RSAAES inside the HSM to wrap and unwrap: %s\n", Base64.getEncoder().encodeToString(unwrappedExtractableKey.getEncoded()));
    }

    private void wrap(Key wrappingKey, Key extractableKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "Cavium");
        cipher.init(Cipher.WRAP_MODE, wrappingKey);

        // Wrap the extractable key using the wrappingKey.
        byte[] wrappedBytes = cipher.wrap(extractableKey);
        final String wrappedKey = Base64.getEncoder().encodeToString(wrappedBytes);
        System.out.println("Wrapped Key:== " + Arrays.toString(wrappedBytes) + "\n");
        System.out.println("Wrapped Key:== " + wrappedKey + "\n");
    }

    private Key unwrap(byte[] wrappedBytes, Key wrappingKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "Cavium");

        // Unwrap the wrapped key using the wrapping key.
        cipher.init(Cipher.UNWRAP_MODE, wrappingKey);
        return cipher.unwrap(wrappedBytes, "AES", Cipher.SECRET_KEY);
    }

    private static List<byte[]> encrypt(Key key, byte[] plainText, byte[] aad) {
        try {
            // Create an encryption cipher.
            Cipher encCipher = Cipher.getInstance("AES/GCM/NoPadding", "Cavium");
            encCipher.init(Cipher.ENCRYPT_MODE, key);
            encCipher.updateAAD(aad);
            encCipher.update(plainText);
            byte[] ciphertext = encCipher.doFinal();

            // The IV is generated inside the HSM. It is needed for decryption, so
            // both the ciphertext and the IV are returned.
            return Arrays.asList(encCipher.getIV(), ciphertext);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] encryptAES(Key key, byte[] plainText, String dossierId) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Encrypt the string and display the base64 cipher text
        System.out.println("Encrypting dossier for .... " + dossierId + '\n');
        Cipher encryptCipher = Cipher.getInstance(transformation, "Cavium");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptCipher.doFinal(plainText);
    }

    private byte[] decryptAES(byte[] dossier, Key objectKey, String dossierId) throws NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        System.out.println("Decrypting dossier for .... " + dossierId + '\n');
        Cipher decryptCipher = Cipher.getInstance(transformation, "Cavium");
        decryptCipher.init(Cipher.DECRYPT_MODE, objectKey);
        return decryptCipher.doFinal(dossier);
    }

    private byte[] decrypt(byte[] dossier, byte[] iv, Key masterKey, Key objectKey) {
        Cipher decCipher;
        try {
            // Only 128 bit tags are supported
            GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * Byte.SIZE, iv);

            decCipher = Cipher.getInstance("AES/GCM/NoPadding", "Cavium");
            decCipher.init(Cipher.DECRYPT_MODE, objectKey, gcmSpec);
            decCipher.updateAAD(masterKey.toString().getBytes());
            decCipher.update(dossier);
            return decCipher.doFinal();

        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Key generateExtractableKey(String keyLabel) {

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES", "Cavium");

            CaviumAESKeyGenParameterSpec aesSpec = new CaviumAESKeyGenParameterSpec(256, keyLabel, true, true);
            keyGen.init(aesSpec);

            return keyGen.generateKey();

        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            if (CFM2Exception.isAuthenticationFailure(e)) {
                System.out.println("Detected invalid credentials");
            } else if (CFM2Exception.isClientDisconnectError(e)) {
                System.out.println("Detected daemon network failure");
            }

            e.printStackTrace();
        }

        return null;
    }


    private static CaviumKey importWrappingKey(SecretKey wrappingKey) throws Exception {
        KeyPair wrappingKeyPair = new AsymmetricKeys().generateRSAKeyPairWithParams(2048, "RSA Wrapping Test", true, true);

        // Wrap the key and delete it.
        OAEPParameterSpec spec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        Cipher wrapCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256ANDMGF1Padding", "SunJCE");
        wrapCipher.init(Cipher.WRAP_MODE, wrappingKeyPair.getPublic(), spec);
        byte[] wrappingKeyWrappedBytes = wrapCipher.wrap(wrappingKey);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256ANDMGF1Padding", "Cavium");
        cipher.init(Cipher.UNWRAP_MODE, wrappingKeyPair.getPrivate());
        Key caviumWrappingKey = cipher.unwrap(wrappingKeyWrappedBytes, "AES", Cipher.SECRET_KEY);

        // The keypair is no longer needed. We have the wrapping key in the HSM and locally.
        Util.deleteKey((CaviumKey) wrappingKeyPair.getPrivate());
        return (CaviumKey) caviumWrappingKey;
    }


    private static byte[] getSHA(String id) {
        // Static getInstance method is called with hashing SHA
//        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an id
        // and return array of byte
        return Base64.getEncoder().encode(id.getBytes());
    }

    private static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}
