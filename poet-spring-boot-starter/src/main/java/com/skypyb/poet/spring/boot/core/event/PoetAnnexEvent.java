package com.skypyb.poet.spring.boot.core.event;

import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public abstract class PoetAnnexEvent extends ApplicationEvent {

    private String name;//作为事件发布时必定存在

    public PoetAnnexEvent(Object source) {
        super(source);
    }

    public final void setAnnexName(String name) {
        this.name = name;
    }

    public final String getAnnexName() {
        return name;
    }

    public Optional<PoetAnnex> getAnnexMetaInfo() {
        return Optional.empty();
    }

    public boolean hasAnnexMetaInfo() {
        return false;
    }
}
