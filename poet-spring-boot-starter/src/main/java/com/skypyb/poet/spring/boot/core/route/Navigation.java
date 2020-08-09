package com.skypyb.poet.spring.boot.core.route;

import java.io.Serializable;
import java.util.Objects;

public class Navigation implements Serializable {

    private String name;

    private String module;

    private String path;

    /**
     * fullPath 和 path 不同的是： fullPath包含文件系统基础路径,即存储路径
     * 如果是unix系统, 则 fullPath[0] 最前面带个斜杠 ‘/’
     */
    private String[] fullPath;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getFullPath() {
        return fullPath;
    }

    public void setFullPath(String[] fullPath) {
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
