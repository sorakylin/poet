package com.skypyb.poet.spring.boot.core.exception;

import java.nio.file.Path;

/**
 * 附件访问异常
 */
public class AnnexAccessException extends RuntimeException {

    private String path;

    private String msg;

    public AnnexAccessException() {

    }

    public AnnexAccessException(String message) {
        super(message);
        this.msg = message;
    }

    public AnnexAccessException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }

    public static AnnexAccessException ofPath(Path path) {
        return ofPath(path.toString());
    }

    public static AnnexAccessException ofPath(String path) {
        AnnexAccessException e = new AnnexAccessException();
        e.setPath(path);
        return e;
    }

    public AnnexAccessException message(String msg) {
        setMsg(msg);
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}