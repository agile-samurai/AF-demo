package com.u.group.hsmgateway.web;

public class CryptoRequest {
    private String id;
    private String dossier;

    CryptoRequest(String id, String dossier) {
        this.id = id;
        this.dossier = dossier;
    }

    public CryptoRequest() {}

    public String getId() {
        return id;
    }

    public String getDossier() {
        return dossier;
    }
}
