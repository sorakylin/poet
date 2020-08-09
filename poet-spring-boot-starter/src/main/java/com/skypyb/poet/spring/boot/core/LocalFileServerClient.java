package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.exception.AnnexOperationException;
import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex;
import com.skypyb.poet.spring.boot.core.route.Navigation;
import com.skypyb.poet.spring.boot.core.route.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import com.skypyb.poet.spring.boot.core.util.StreamUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * 以本地文件系统作为服务基础来进行附件操作的客户端
 */
public class LocalFileServerClient implements PoetAnnexClient {

    private PoetAccessRouter router;


    //region setter/getter

    public LocalFileServerClient(PoetAccessRouter router, PoetAnnexNameGenerator nameGenerator) {
        Assert.notNull(router, "router not be null!");
        this.router = router;
    }


    @Override
    public void setRouter(PoetAccessRouter router) {
        Assert.notNull(router, "router not be null!");
        this.router = router;
    }


    @Override
    public Optional<PoetAccessRouter> getRouter() {
        return Optional.ofNullable(router);
    }

    // endregion


    //region Implements PoetAnnexClient methods

    @Override
    public DefaultPoetAnnex save(InputStream in, String name) {
        return save(in, name, null);
    }

    @Override
    public DefaultPoetAnnex save(InputStream in, String name, String module) {
        Navigation routing = router.routing(module, name);
        Path path = generatePath(routing);

        try {
            //创建、覆盖
            Files.copy(in, path);
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to save file!", e);
            ex.setPath(routing.getPath());
            throw ex;
        } finally {
            StreamUtil.close(in);
        }

        DefaultPoetAnnex annex = DefaultPoetAnnex.of(routing);

        try {
            annex.setLength(Files.size(path));
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to read file length!", e);
            ex.setPath(routing.getPath());
            throw ex;
        }

        return annex;
    }

    @Override
    public DefaultPoetAnnex save(byte[] data, String name) {
        return save(data, name, null);
    }

    @Override
    public DefaultPoetAnnex save(byte[] data, String name, String module) {
        Navigation routing = router.routing(module, name);
        Path path = generatePath(routing);

        try {
            //创建、覆盖
            Files.write(path, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DefaultPoetAnnex annex = DefaultPoetAnnex.of(routing);

        try {
            annex.setLength(Files.size(path));
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to save file!", e);
            ex.setPath(routing.getPath());
            throw ex;
        }

        return annex;
    }


    @Override
    public boolean exist(String name) {
        return exist(name, null);
    }

    @Override
    public boolean exist(String name, String module) {
        Navigation routing = router.routing(module, name);
        Path path = generatePath(routing);
        return Files.exists(path);
    }

    @Override
    public void delete(String name) {
        delete(name, null);
    }

    @Override
    public void delete(String name, String module) {
        Navigation routing = router.routing(module, name);
        Path path = generatePath(routing);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to delete file!", e);
            ex.setPath(routing.getPath());
            throw ex;
        }
    }

    @Override
    public byte[] getBytes(String name) {
        return getBytes(name, null);
    }

    @Override
    public byte[] getBytes(String name, String module) {
        Navigation routing = router.routing(module, name);
        Path path = generatePath(routing);

        //exist ?

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to read file bytes!", e);
            ex.setPath(routing.getPath());
            throw ex;
        }

    }

    //endregion


    private Path generatePath(Navigation routing) {
        String[] fullPath = routing.getFullPath();
        Path path = Paths.get(fullPath[0], Arrays.copyOfRange(fullPath, 1, fullPath.length));
        return path;
    }
}
