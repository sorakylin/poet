package com.skypyb.poet.spring.boot.core;


import com.skypyb.poet.spring.boot.core.client.LocalFileServerClient;
import com.skypyb.poet.spring.boot.core.client.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * 附件操作上下文, 拥有所有对附件操作的机能
 */
public abstract class AbstractPoetAnnexContext implements ApplicationContextAware, PoetAnnexClient, PoetAnnexClientHttpSupport {

    private static ApplicationEventPublisher eventPublisher;

    private PoetAnnexClient annexClient;

    private PoetAnnexClientHttpSupport annexHttpClient;

    /**
     * 是否启用Http支持， 若为false, 则Http相关的操作接口无法使用。 调用时会抛出 {@link UnsupportedOperationException}
     *
     * @see PoetAnnexClientHttpSupport
     */
    private boolean enableHttpSupport = true;

    /**
     * 是否启用名字生成器, 若为false, 则所有操作的入参名字均直接作为文件保存时的名字
     * 由于默认的附件客户端{@link LocalFileServerClient}相关操作均为覆盖操作, 不启用可能会引发一些预料之外的误操作。
     * 需要调用方仔细斟酌
     *
     * @see this#getNameGenerator;
     */
    private boolean enableNameGenerator = true;

    //名字生成器
    abstract Optional<PoetAnnexNameGenerator> getNameGenerator();


    //TODO 加密器

    //TODO 储存器

    //TODO ACL 访问控制器  [write,read,info,delete,admin]

    /**
     * 启动功能前必须要调用的核心配置方法
     *
     * @param annexClient     附件基础操作支持
     * @param annexHttpClient 附件Http相关操作支持
     */
    public void configure(PoetAnnexClient annexClient, PoetAnnexClientHttpSupport annexHttpClient) {
        this.annexClient = annexClient;
        this.annexHttpClient = annexHttpClient;
    }

    public static ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public static void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        AbstractPoetAnnexContext.eventPublisher = eventPublisher;
    }

    public PoetAnnexClient getAnnexClient() {
        return annexClient;
    }

    public void setAnnexClient(PoetAnnexClient annexClient) {
        this.annexClient = annexClient;
    }

    public PoetAnnexClientHttpSupport getAnnexHttpClient() {
        return annexHttpClient;
    }

    public void setAnnexHttpClient(PoetAnnexClientHttpSupport annexHttpClient) {
        this.annexHttpClient = annexHttpClient;
    }

    public boolean isEnableHttpSupport() {
        return enableHttpSupport;
    }

    public void setEnableHttpSupport(boolean enableHttpSupport) {
        this.enableHttpSupport = enableHttpSupport;
    }

    public boolean isEnableNameGenerator() {
        return enableNameGenerator;
    }

    public void setEnableNameGenerator(boolean enableNameGenerator) {
        this.enableNameGenerator = enableNameGenerator;
    }


    private void checkHttpClientEnableState() {
        if (!enableHttpSupport || Objects.isNull(annexHttpClient)) {
            throw new UnsupportedOperationException("HTTP support is not enabled!");
        }
    }

    private String nameGenerator(String realName) {
        return enableNameGenerator ? getNameGenerator().map(PoetAnnexNameGenerator::generate).orElse(realName) : realName;
    }

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (AbstractPoetAnnexContext.eventPublisher == null) {
            AbstractPoetAnnexContext.eventPublisher = applicationContext;
        }
    }


    @Override
    public PoetAnnex save(InputStream in, String name) {
        final PoetAnnex result = annexClient.save(in, nameGenerator(name));
        result.setRealName(name);
        return result;
    }

    @Override
    public PoetAnnex save(InputStream in, String name, String module) {
        final PoetAnnex result = annexClient.save(in, nameGenerator(name), module);
        result.setRealName(name);

        return result;
    }

    @Override
    public PoetAnnex save(byte[] data, String name) {
        final PoetAnnex result = annexClient.save(data, nameGenerator(name));
        result.setRealName(name);

        return result;
    }

    @Override
    public PoetAnnex save(byte[] data, String name, String module) {
        final PoetAnnex result = annexClient.save(data, nameGenerator(name), module);
        result.setRealName(name);

        return result;
    }

    @Override
    public boolean exist(String name) {
        return annexClient.exist(name);
    }

    @Override
    public boolean exist(String name, String module) {
        return annexClient.exist(name, module);
    }

    @Override
    public void delete(String name) {
        annexClient.delete(name);
    }

    @Override
    public void delete(String name, String module) {
        annexClient.delete(name, module);
    }

    @Override
    public byte[] getBytes(String name) {
        return annexClient.getBytes(name);
    }

    @Override
    public byte[] getBytes(String name, String module) {
        return annexClient.getBytes(name, module);
    }

    @Override
    public void view(String name, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.view(name, response);
    }

    @Override
    public void view(String name, String module, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.view(name, module, response);

    }

    @Override
    public void viewMedia(String name, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.viewMedia(name, response);
    }

    @Override
    public void viewMedia(String name, String module, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.viewMedia(name, module, response);
    }

    @Override
    public void viewMedia(String name, HttpServletRequest request, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.viewMedia(name, request, response);

    }

    @Override
    public void viewMedia(String name, String module, HttpServletRequest request, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.viewMedia(name, module, request, response);
    }

    @Override
    public void down(String name, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.down(name, response);
    }

    @Override
    public void down(String name, String module, HttpServletResponse response) {
        checkHttpClientEnableState();
        annexHttpClient.down(name, response);
    }

    @Override
    public void setRouter(PoetAccessRouter router) {
        checkHttpClientEnableState();
        annexClient.setRouter(router);
        if (Objects.nonNull(annexHttpClient)) {
            annexHttpClient.setRouter(router);
        }
    }

    @Override
    public Optional<PoetAccessRouter> getRouter() {
        return annexClient.getRouter();
    }

}
