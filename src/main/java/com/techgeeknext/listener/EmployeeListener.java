package com.techgeeknext.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeListener {
    @JmsListener(destination = "",
            containerFactory = "empJmsContFactory",
            subscription = "")
    public void onMessage(String message) {
        log.info("Message listener: " + message);
    }
}
