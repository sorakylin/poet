package com.skypyb.poet.spring.boot.core.client;

import com.skypyb.poet.spring.boot.core.model.Navigation;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultPoetAccessRouter implements PoetAccessRouter {

    private PoetAnnexSlicer slicer = PoetAnnexSlicer.DEFAULT_SLICER;
    private String delimiter = PoetAnnexSlicer.DELIMITER;
    private String storageLocation;
    private String defaultModule;

    @Override
    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public void setDefaultModule(String module) {
        this.defaultModule = defaultModule;
    }

    @Override
    public void setSlicer(PoetAnnexSlicer slicer) {
        this.slicer = slicer;
    }

    @Override
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public Navigation routing(String module, String name) {
        if (!StringUtils.hasText(module)){
            module = this.defaultModule;
        }

        Navigation navigation = new Navigation();
        navigation.setName(name);
        navigation.setModule(module);

        final String[] pathArray = this.slicer.slicePath(module, name);

        final String path = Arrays.stream(pathArray).collect(Collectors.joining(delimiter, "/", ""));
        navigation.setPath(path);

        final String fullPath = formatKey(path);
        navigation.setFullPath(fullPath);
        return navigation;
    }

    /**
     * 文件存储路径+文件路径 转换分隔符
     * key = temp/123.img
     * formatKey => /usr/temp/123.img
     * formatKey => D:\temp\123.img
     *
     * @param key 文件标识
     * @return 可直接定位到本机具体文件路径的地址
     */
    @Override
    public String formatKey(String key) {
        String path = this.storageLocation.concat(key);

        path = path.replaceAll(this.delimiter, File.separator);
        path = path.replaceAll(File.separator.concat(File.separator), File.separator);

        return path;
    }
}
