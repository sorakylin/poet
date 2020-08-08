package com.skypyb.poet.spring.boot.core.event;

import org.springframework.context.ApplicationEvent;

public class AnnexAccessEvent extends ApplicationEvent {
    
    public AnnexAccessEvent(Object source) {
        super(source);
    }
}
