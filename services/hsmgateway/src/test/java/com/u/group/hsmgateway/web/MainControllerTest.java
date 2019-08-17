package com.u.group.hsmgateway.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class MainControllerTest {

    private MainController mainController = new MainController();
    private ResponseEntity<String> responseEntity;

    @Before
    public void setUp() throws Exception {
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
}