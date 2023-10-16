package com.programmers.vouchermanagement;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsoleAppRunner implements ApplicationRunner {
    private final Client client;

    public ConsoleAppRunner(Client client) {
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean isRunning = true;
        while (isRunning) {
            isRunning = client.selectMenu();
        }
    }
}