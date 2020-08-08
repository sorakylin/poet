package com.skypyb.poet.spring.boot.core.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PoetAnnexClientHttpSupport {

    void view(String name, HttpServletResponse response);

    void view(String name, String module, HttpServletResponse response);

    void viewMedia(String name, HttpServletResponse response);

    void viewMedia(String name, String module, HttpServletResponse response);

    void viewMedia(String name, HttpServletRequest request, HttpServletResponse response);

    void viewMedia(String name, String module, HttpServletRequest request, HttpServletResponse response);

    void down(String name, HttpServletResponse response);

    void down(String name, String module, HttpServletResponse response);
}
