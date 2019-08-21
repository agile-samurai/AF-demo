package com.u.group.hsmgateway.web;

import com.cavium.cfm2.CFM2Exception;
import com.u.group.hsmgateway.crypto.CryptoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainControllerTest {

    @Mock
    private CryptoService cryptoService;
    @InjectMocks
    private MainController mainController;
    private ResponseEntity<String> responseEntity;

    @Before
    public void setUp() {
        responseEntity = mainController.hello();
    }

    @Test
    public void shouldReturnHello() {
        assertEquals(responseEntity.getBody(), "Hello");
    }

    @Test
    public void shouldReturn200StatusCode() {
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldReturnEncryptedData() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        CryptoRequest request = new CryptoRequest("id", "This is the data to encrypt");
        final String expected = "EncryptedData";
        when(cryptoService.encryptDossier(request)).thenReturn(expected);
        final ResponseEntity<String> responseEntity = mainController.encrypt(request);

        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void shouldReturnDecryptedData() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        CryptoRequest request = new CryptoRequest("id", "sdlnfpiouh29ohs8d90hfjka98h;lkmn987");
        final String expected = "This is the decrypted data";
        when(cryptoService.decryptDossier(request)).thenReturn(expected);
        final ResponseEntity<String> responseEntity = mainController.decrypt(request);

        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void shouldDeleteKeyForId() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CFM2Exception {
        final String expected = "Key Deleted";
        final String id = "id123456";
        when(cryptoService.deleteDossier(id)).thenReturn(true);
        final ResponseEntity<String> responseEntity = mainController.deleteKey(id);

        assertEquals(expected, responseEntity.getBody());
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturn404WhenKeyNotFound() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CFM2Exception {
        final String expected = "Key Deleted";
        final String id = "id123456";
        when(cryptoService.deleteDossier(id)).thenReturn(false);
        final ResponseEntity<String> responseEntity = mainController.deleteKey(id);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}