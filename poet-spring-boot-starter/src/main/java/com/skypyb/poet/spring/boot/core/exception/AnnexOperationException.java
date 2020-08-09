package com.skypyb.poet.spring.boot.core.exception;

import java.nio.file.Path;

/**
 * 附件操作相关 (CRUD) 报的异常
 */
public class AnnexOperationException extends RuntimeException {

    private String path;

    private String msg;


    public AnnexOperationException() {

    }

    public AnnexOperationException(String message) {
        super(message);
        this.msg = message;
    }

    public AnnexOperationException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }

    public static AnnexOperationException ofPath(Path path) {
        return ofPath(path.toString());
    }

    public static AnnexOperationException ofPath(String path) {
        AnnexOperationException e = new AnnexOperationException();
        e.setPath(path);
        return e;
    }

    public AnnexOperationException message(String msg) {
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
