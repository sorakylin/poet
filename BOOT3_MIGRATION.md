# Poet v4.1.1.RELEASE：Spring Boot 3 迁移说明

日期：2026-09-24。开发分支：`4.1.1.RELEASE`；Maven 版本沿用原有命名习惯，为 `v4.1.1.RELEASE`。

## 环境与依赖

| 项目 | 升级后 |
| --- | --- |
| Java 字节码与最低运行版本 | 17 |
| Spring Boot 构建 BOM | 3.5.16 |
| Kotlin 编译器、标准库与反射 | 1.9.25 |
| Servlet / Validation | `jakarta.servlet` / `jakarta.validation` |
| Jackson Kotlin module | 使用 Boot BOM 管理，移除旧 2.9.4.1 固定版本 |
| Java 编译 / 测试插件 | compiler 3.14.1 / Surefire 3.5.6 |
| 产物 | core、starter、腾讯 COS 扩展均为普通依赖 JAR，无 Boot repackage |

Boot 3.5.16 是 3.5.x 的最后一个 OSS 维护版本；这是本次可从公共仓库获取的迁移验证基线。需要持续官方修复的宿主应选用有权获取的维护版本，并重新执行兼容验证。本项目使用 Boot 3 API，不声明已验证全部 3.x 小版本。[官方版本说明](https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/)

## 宿主如何接入

```xml
<dependency>
    <groupId>com.skypyb.poet</groupId>
    <artifactId>poet-spring-boot-starter</artifactId>
    <version>v4.1.1.RELEASE</version>
</dependency>
```

使用腾讯 COS 时，再引入同版本 `poet-cloudstore-tencent`。三个 Poet 模块需要保持同一版本。

宿主继续使用自己的 Boot 3 BOM。无需扫描 `com.skypyb.poet`，也不需要手动导入自动配置。主 starter 和腾讯扩展均使用 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，并按 JDBC、Validation 和基础 Poet 配置的顺序装配。[Boot 自动配置规范](https://docs.spring.io/spring-boot/3.5/reference/features/developing-auto-configuration.html)

自定义接口实现中涉及 Servlet 或校验的 import 要改为 Jakarta，例如：

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
```

`PoetAnnexContext`、`PoetAnnexClientHttpSupport` 等接口的方法名称与用途保持原样，但 Servlet 参数的 JVM 类型已改变，**宿主自定义实现必须重新编译**。旧 Boot 2 二进制不能直接混用。`javax.annotation.Nullable` 属于现有 JSR-305 注解，继续保留；不对所有 `javax.*` 做机械替换。

本库不依赖 Spring Security，不接管宿主的登录、Session 或 CSRF。宿主应继续配置附件端点权限及其自定义附件拦截器。

### 数据库模式

```yaml
poet:
  storage-location: /srv/poet
  enable-db-store: true
  enable-web-resource: true
  web-url-prefix: /poet
```

默认 MySQL 仓库会在存在唯一 JdbcTemplate 且数据库开关开启时创建，宿主需提供驱动、数据源与表结构。本次修复了“配置对象默认开启 DB，但未显式写属性就不创建仓库”的不一致。

自定义 `PoetAnnexRepository`（例如原有 PostgreSQL 实现）优先于默认仓库，保留现有数据库实现和 SQL。本次没有执行任何真实数据库表结构变更。

### 纯本地模式

```yaml
poet:
  storage-location: /srv/poet
  enable-db-store: false
  enable-web-resource: false
spring:
  autoconfigure:
    # 仅当整个宿主不需要 JDBC 时排除；已有数据库的应用不需要此配置。
    exclude: org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

无需数据源即可装配本地客户端和附件上下文。按附件 name 查询、删除或访问 HTTP 资源仍依赖元信息仓库；关闭 DB 不会凭空提供这些能力。可直接通过 `PoetAnnexClient` 按 key 读取/删除本地文件，接口语义与原版本一致。

### 腾讯 COS

```yaml
poet:
  storage-location: /srv/poet
  store-mode: TENCENT
  tencent:
    secret-id: ${COS_SECRET_ID}
    secret-key: ${COS_SECRET_KEY}
    default-bucket: ${COS_BUCKET}
    region: ap-guangzhou
```

COS 客户端在基础 router 装配后创建；非腾讯模式不会要求云凭据。自定义 `PoetAnnexClient` 仍会覆盖默认客户端，也支持与默认名称不同的 Bean。真实 COS 操作和 bucket 权限需要部署环境联调。

### 原配置与扩展点兼容

- 继续支持已有 `poet.enableDBStore`、`poet.storeMode` 等驼峰配置；推荐新配置使用短横线格式。
- Web 路径同时兼容 `poet.webUrlPrefix` 和 `poet.web-url-prefix`，后者优先。
- 保留 `poetProperties` Bean 名称及本地默认 `poetAnnexClient`、`poetAnnexClientHttpSupport` 名称。
- HTTP 支持使用仅暴露对应接口的适配对象，避免客户端同时以两个 Bean 名称成为 `PoetAnnexClient` 候选；按类型注入可正常工作。
- `PoetResource` 只在 Servlet Web 应用中导入；原端点路径、数据返回和开关校验保留。
- Java 和 Kotlin 都保留方法参数名，保证 Spring 6 的参数绑定。
- 原附件拦截器机制、存储格式、数据库 SQL 和文件命名策略保持原实现。

## 本地构建与安装

在 JDK 17 或 21 环境、Poet 根目录运行：

```powershell
mvn -B -ntp clean verify
mvn -B -ntp install
```

`install` 安装父 POM 和三个模块到本地 Maven 仓库，便于宿主进行 Boot 3 迁移；不发布到远程。产物名称为：

- `poet-core/target/poet-core-v4.1.1.RELEASE.jar`
- `poet-spring-boot-starter/target/poet-spring-boot-starter-v4.1.1.RELEASE.jar`
- `poet-cloudstore-tencent/target/poet-cloudstore-tencent-v4.1.1.RELEASE.jar`

原 release profile 的旧 Kotlin 源目录覆盖已移除，Dokka 升级到 1.9.20。远程仓库、签名和发布流程没有执行，旧 OSSRH 地址能否继续使用需在正式发布前核对；普通本地构建不启用该 profile。

## 验证范围

本次实际完成：

| 环境/检查 | 结果 |
| --- | --- |
| Temurin 17.0.13，`mvn -B -ntp clean verify` | 三模块构建通过；基础 starter 9 项、COS 5 项测试通过 |
| Temurin 21.0.12，`mvn -B -ntp install dependency:tree`（过滤依赖展示） | 同一组 14 项测试全部通过，父 POM 和三个模块已安装到本地仓库 |
| JAR 结构与字节码 | 普通 JAR，无 BOOT-INF；检查的类版本为 61（Java 17） |
| 自动配置入口 | starter、COS 两个 JAR 均含 AutoConfiguration.imports，已移除旧 spring.factories |
| 旧 Servlet/Validation 类型 | 源码与三个产物的 jdeps 检查均未发现旧 javax.servlet / javax.validation 引用 |

本机安装目录为 `C:\0_develop\Maven\repository\com\skypyb\poet\`，各模块版本目录为 `v4.1.1.RELEASE`。构建记录在 `boot3-jdk17.log`、`boot3-jdk21-install.log`（按项目规则忽略提交）。预期配置非法的测试会输出启动失败日志，这是断言覆盖的一部分；最终测试结果均为 0 失败、0 错误。

自动化用例使用消费者的 `@EnableAutoConfiguration` 发现 Poet，而不是直接 `@Import` 配置类，覆盖实际 starter 发现机制。

- 基础 starter：默认仓库与 Bean 名称、无数据库本地读写、Servlet 应用关闭 DB、自定义仓库/客户端覆盖、驼峰配置、Jakarta 校验失败、自定义拦截器。
- Web 集成：用 MockMvc + H2 MySQL 模式 + 临时目录执行真实上传、元信息查询、预览、下载、删除。
- 腾讯扩展：本地模式不装配、腾讯模式装配、驼峰配置、缺少凭据拒绝启动、自定义客户端覆盖。只创建和销毁 SDK 客户端，不发起真实云请求。

未覆盖真实 MySQL/PostgreSQL 服务、真实 COS 上传、反向代理以及宿主 Spring Security 的端到端联调。主项目 `ym-galgame` 仍需按其 Boot 3 迁移方案升级后才能接入本版本。
