package com.skypyb.poet.spring.boot.core.model;

public class DefaultPoetAnnex implements PoetAnnex {

    private String name;

    private String realName;

    private String suffix;

    private String key;

    private Long length;

    /**
     * 没有 realName&length
     */
    public static DefaultPoetAnnex of(Navigation n) {

        String name = n.getName();

        DefaultPoetAnnex defaultPoetAnnex = new DefaultPoetAnnex();
        defaultPoetAnnex.setName(name);
        defaultPoetAnnex.setKey(n.getPath());

        int suffixPoint = name.lastIndexOf(".");
        if (suffixPoint > 0) {
            //后缀名保存时不保留 "."
            defaultPoetAnnex.setSuffix(name.substring(suffixPoint + 1));
        }

        return defaultPoetAnnex;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setRealName(String realName) {
        this.realName = realName;
    }


    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRealName() {
        return realName;
    }


    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getKey() {
        return key;
    }


    @Override
    public Long getLength() {
        return length;
    }

}
