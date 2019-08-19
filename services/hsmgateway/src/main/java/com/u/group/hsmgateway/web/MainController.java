package com.u.group.hsmgateway.web;

import com.u.group.hsmgateway.crypto.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;

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
    public ResponseEntity<String> encrypt(@RequestBody CryptoRequest request) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        return ResponseEntity.ok(cryptoService.encryptDossier(request));
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decrypt(@RequestBody CryptoRequest request) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return ResponseEntity.ok(cryptoService.decryptDossier(request));
    }
}
