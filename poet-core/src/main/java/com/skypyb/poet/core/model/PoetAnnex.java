package com.skypyb.poet.core.model;


import java.io.Serializable;

public interface PoetAnnex extends Serializable {

    //default name==uuid
    String getName();

    void setName(String name);

    String getRealName();

    void setRealName(String realName);

    String getSuffix();

    void setSuffix(String suffix);

    //定位到一个文件的key，即一个抽象路径， 不重复
    String getKey();

    void setKey(String key);

    Long getLength();

    void setLength(Long length);
}
