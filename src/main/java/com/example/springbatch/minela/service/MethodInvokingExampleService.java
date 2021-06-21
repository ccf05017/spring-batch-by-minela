package com.example.springbatch.minela.service;

public class MethodInvokingExampleService {
    public void sayMessage(final String message) {
        System.out.println(String.format("Service message received: %s", message));
    }
}
