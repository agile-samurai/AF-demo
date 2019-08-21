package com.u.group.hsmgateway.web;

import com.cavium.cfm2.CFM2Exception;
import com.u.group.hsmgateway.crypto.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class MainController {

    private CryptoService cryptoService;

    @Autowired
    public MainController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody CryptoRequest request) {
        try {
            return ResponseEntity.ok(cryptoService.encryptDossier(request));
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | NoSuchPaddingException | InvalidKeyException | NoSuchProviderException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decrypt(@RequestBody CryptoRequest request) {
        try {
            return ResponseEntity.ok(cryptoService.decryptDossier(request));
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteKey(@PathVariable String id) {

        try {
            return  cryptoService.deleteDossier(id)
                    ? ResponseEntity.accepted().body("Key Deleted")
                    : ResponseEntity.notFound().build();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | CFM2Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }
}
