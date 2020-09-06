package com.skypyb.poet.spring.boot.core;


import com.skypyb.poet.spring.boot.core.client.LocalFileServerClient;
import com.skypyb.poet.spring.boot.core.client.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport;
import com.skypyb.poet.spring.boot.core.exception.AnnexOperationException;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository;
import com.skypyb.poet.spring.boot.core.util.HttpResourceViewUtils;
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
public abstract class AbstractPoetAnnexContext implements ApplicationContextAware, PoetAnnexContext {

    private static ApplicationEventPublisher eventPublisher;

    private PoetAnnexClient annexClient;

    private PoetAnnexClientHttpSupport annexHttpClient;

    /**
     * 是否启用Http支持， 若为false, 则Http相关的操作接口无法使用。 调用时会抛出 {@link UnsupportedOperationException}
     *
     * @see PoetAnnexClientHttpSupport
     */
    private boolean enableHttpSupport = true;

    //名字生成器
    abstract Optional<PoetAnnexNameGenerator> getNameGenerator();

    //储存器
    abstract Optional<PoetAnnexRepository> getRepository();


    //TODO 加密器


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


    private void checkHttpClientEnableState() {
        if (!enableHttpSupport || Objects.isNull(annexHttpClient)) {
            throw new UnsupportedOperationException("HTTP support is not enabled!");
        }
    }

    private String nameGenerator(String realName) {
        return getNameGenerator()
                .map(PoetAnnexNameGenerator::generate)
                .map(name -> new StringBuilder(name).append(".").append(HttpResourceViewUtils.splitSuffix(realName)).toString())
                .orElse(realName);
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
        getRepository().ifPresent(r -> r.save(result));
        return result;
    }

    @Override
    public PoetAnnex save(InputStream in, String name, String module) {
        final PoetAnnex result = annexClient.save(in, nameGenerator(name), module);
        result.setRealName(name);
        getRepository().ifPresent(r -> r.save(result));
        return result;
    }

    @Override
    public PoetAnnex save(byte[] data, String name) {
        final PoetAnnex result = annexClient.save(data, nameGenerator(name));
        result.setRealName(name);
        getRepository().ifPresent(r -> r.save(result));
        return result;
    }

    @Override
    public PoetAnnex save(byte[] data, String name, String module) {
        final PoetAnnex result = annexClient.save(data, nameGenerator(name), module);
        result.setRealName(name);
        getRepository().ifPresent(r -> r.save(result));
        return result;
    }

    @Override
    public boolean exist(String name) {
        return getRepository().map(r -> r.findByName(name)).map(PoetAnnex::getKey).map(annexClient::exist).orElse(false);
    }

    @Override
    public void delete(String name) {
        Optional<PoetAnnex> annex = getRepository().map(r -> r.findByName(name));

        annex.ifPresent(a -> {
            getRepository().ifPresent(r -> r.deleteByName(a.getName()));
            annexClient.delete(a.getKey());
        });

    }

    @Override
    public byte[] getBytes(String name) {
        Optional<PoetAnnex> annex = getRepository().map(r -> r.findByName(name));
        if (!annex.isPresent()) {
            throw new AnnexOperationException("File(" + name + ") does not exist!");
        }

        return annexClient.getBytes(annex.get().getKey());
    }

    @Override
    public void view(String name, HttpServletResponse response) {
        checkHttpClientEnableState();

        Optional<PoetAnnex> annex = getRepository().map(r -> r.findByName(name));
        if (!annex.isPresent()) {
            throw new AnnexOperationException("File(" + name + ") does not exist!");
        }

        annexHttpClient.view(annex.get().getKey(), response);
    }

    @Override
    public void viewMedia(String name, HttpServletResponse response) {
        checkHttpClientEnableState();

        Optional<PoetAnnex> annex = getRepository().map(r -> r.findByName(name));
        if (!annex.isPresent()) {
            throw new AnnexOperationException("File(" + name + ") does not exist!");
        }

        annexHttpClient.viewMedia(annex.get().getKey(), response);
    }

    @Override
    public void viewMedia(String name, HttpServletRequest request, HttpServletResponse response) {
        checkHttpClientEnableState();

        Optional<PoetAnnex> annex = getRepository().map(r -> r.findByName(name));
        if (!annex.isPresent()) {
            throw new AnnexOperationException("File(" + name + ") does not exist!");
        }

        annexHttpClient.viewMedia(annex.get().getKey(), request, response);
    }

    @Override
    public void down(String name, HttpServletResponse response) {
        checkHttpClientEnableState();

        Optional<PoetAnnex> annex = getRepository().map(r -> r.findByName(name));
        if (!annex.isPresent()) {
            throw new AnnexOperationException("File(" + name + ") does not exist!");
        }

        annexHttpClient.down(annex.get().getKey(), annex.get().getRealName(), response);
    }

    @Override
    public void down(String name, String realName, HttpServletResponse response) {
        checkHttpClientEnableState();

        Optional<PoetAnnex> annex = getRepository().map(r -> r.findByName(name));
        if (!annex.isPresent()) {
            throw new AnnexOperationException("File(" + name + ") does not exist!");
        }

        annexHttpClient.down(annex.get().getKey(), realName, response);
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
