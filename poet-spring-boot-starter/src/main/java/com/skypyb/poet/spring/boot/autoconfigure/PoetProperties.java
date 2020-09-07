package com.skypyb.poet.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "poet")
@Component("poetProperties")
public class PoetProperties {

    //默认储存位置
    @NotNull
    private String storageLocation;

    //是否启用web资源层
    private Boolean enableWebResource = true;

    //web资源接口请求路径前缀
    private String webUrlPrefix = "/poet";

    //默认模块, 在文件保存时若不指定则将直接保存到此模块之中
    private String defaultModule;

    //路径分隔符,  以本地文件系统作为附件储存库时可使用 '/', 适配 unix&win
    @NotNull
    private String pathDelimiter = "/";

    //储存附件信息的表名
    @NotNull
    private String tableName = "tb_poet_annex";

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getWebUrlPrefix() {
        return webUrlPrefix;
    }

    public void setWebUrlPrefix(String webUrlPrefix) {
        this.webUrlPrefix = webUrlPrefix;
    }

    public String getDefaultModule() {
        return defaultModule;
    }

    public void setDefaultModule(String defaultModule) {
        this.defaultModule = defaultModule;
    }

    public String getPathDelimiter() {
        return pathDelimiter;
    }

    public void setPathDelimiter(String pathDelimiter) {
        this.pathDelimiter = pathDelimiter;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
