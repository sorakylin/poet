package com.skypyb.poet.spring.boot.core.event;

import org.springframework.context.ApplicationEvent;

public class AnnexSavedEvent extends ApplicationEvent {

    public AnnexSavedEvent(Object source) {
        super(source);
    }
}
