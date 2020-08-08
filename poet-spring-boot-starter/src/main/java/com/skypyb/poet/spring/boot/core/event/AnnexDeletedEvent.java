package com.skypyb.poet.spring.boot.core.event;

import org.springframework.context.ApplicationEvent;

public class AnnexDeletedEvent extends ApplicationEvent {

    public AnnexDeletedEvent(Object source) {

        super(source);
    }
}
