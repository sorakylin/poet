package com.skypyb.poet.spring.boot.core.client.route;

import java.io.Serializable;
import java.util.Objects;

public class Navigation implements Serializable {

    private String name;

    private String module;

    private String fullPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Navigation that = (Navigation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(module, that.module) &&
                Objects.equals(fullPath, that.fullPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, module, fullPath);
    }
}
