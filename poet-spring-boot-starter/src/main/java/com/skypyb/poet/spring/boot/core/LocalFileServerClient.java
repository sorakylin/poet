package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.core.route.Navigation;
import com.skypyb.poet.spring.boot.core.route.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository;
import com.skypyb.poet.spring.boot.core.util.StreamUtil;
import org.omg.CORBA.DATA_CONVERSION;
import org.springframework.util.StreamUtils;

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

    private PoetAnnexNameGenerator nameGenerator;


    //region setter/getter

    public LocalFileServerClient(PoetAccessRouter router, PoetAnnexNameGenerator nameGenerator) {
        this.router = router;
        this.nameGenerator = nameGenerator;
    }


    @Override
    public void setRouter(PoetAccessRouter router) {
        this.router = router;
    }

    @Override
    public void setNameGenerator(PoetAnnexNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    @Override
    public Optional<PoetAccessRouter> getRouter() {
        return Optional.ofNullable(router);
    }

    @Override
    public Optional<PoetAnnexNameGenerator> getNameGenerator() {
        return Optional.ofNullable(nameGenerator);
    }

    // endregion


    //region Implements PoetAnnexClient methods

    @Override
    public DefaultPoetAnnex save(InputStream in, String suffix) {
        return save(in, suffix, null);
    }

    @Override
    public DefaultPoetAnnex save(InputStream in, String suffix, String module) {
        Navigation routing = router.routing(module, nameGenerator.generate(), suffix);
        Path path = generatePath(routing);

        try {
            //创建、覆盖
            Files.copy(in, path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(in);
        }

        DefaultPoetAnnex annex = DefaultPoetAnnex.of(routing);

        try {
            annex.setLength(Files.size(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return annex;
    }

    @Override
    public DefaultPoetAnnex save(byte[] data, String suffix) {
        return save(data, suffix, null);
    }

    @Override
    public DefaultPoetAnnex save(byte[] data, String suffix, String module) {
        Navigation routing = router.routing(module, nameGenerator.generate(), suffix);
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
            e.printStackTrace();
        }

        return annex;
    }


    @Override
    public boolean exist(String name) {
        return false;
    }

    @Override
    public boolean exist(String name, String module) {
        return false;
    }

    @Override
    public void delete(String name) {

    }

    @Override
    public void delete(String name, String module) {

    }

    @Override
    public byte[] getBytes(String name) {
        return new byte[0];
    }

    @Override
    public byte[] getBytes(String name, String module) {
        return new byte[0];
    }

    //endregion


    private Path generatePath(Navigation routing) {
        String[] fullPath = routing.getFullPath();
        Path path = Paths.get(fullPath[0], Arrays.copyOfRange(fullPath, 1, fullPath.length));
        return path;
    }
}
