package com.skypyb.poet.spring.boot.core.model;

import com.skypyb.poet.spring.boot.core.route.Navigation;

public class DefaultPoetAnnex implements PoetAnnex {

    private String name;

    private String realName;

    private String module;

    private String suffix;

    private String path;

    private Long length;

    /**
     * 没有 realName&length
     */
    public static DefaultPoetAnnex of(Navigation n) {

        String name = n.getName();

        DefaultPoetAnnex defaultPoetAnnex = new DefaultPoetAnnex();
        defaultPoetAnnex.setName(name);
        defaultPoetAnnex.setModule(n.getModule());
        defaultPoetAnnex.setPath(n.getPath());

        int suffixPoint = name.lastIndexOf(".");
        if (suffixPoint > 0){
            defaultPoetAnnex.setSuffix(name.substring(suffixPoint));
        }

        return defaultPoetAnnex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getRealName() {
        return null;
    }

    @Override
    public String getModule() {
        return null;
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public Long getLength() {
        return null;
    }
}
