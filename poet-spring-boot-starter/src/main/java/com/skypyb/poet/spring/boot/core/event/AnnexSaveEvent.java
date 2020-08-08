package com.skypyb.poet.spring.boot.core.event;

import org.springframework.context.ApplicationEvent;

public class AnnexSaveEvent extends ApplicationEvent {

    public AnnexSaveEvent(Object source) {

        super(source);
    }
}
