package com.skypyb.poet.spring.boot.core.event;

import org.springframework.context.ApplicationEvent;

public class AnnexDeleteEvent extends ApplicationEvent {

    public AnnexDeleteEvent(Object source) {

        super(source);
    }
}
