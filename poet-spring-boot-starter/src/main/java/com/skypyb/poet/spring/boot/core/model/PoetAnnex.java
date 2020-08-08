package com.skypyb.poet.spring.boot.core.model;


import java.io.Serializable;

public interface PoetAnnex extends Serializable {

    //default name==uuid+suffix
    String getName();

    String getRealName();

    String getModule();

    String getSuffix();

    String getPath();

    Long getLength();
}
