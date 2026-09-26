# Poet v5.1.1.RELEASE：Spring Boot 4.1.1 适配记录

更新日期：2026-09-27。执行方：Codex。开发、文档和后续适配由 Codex 按任务清单执行。

**当前状态：源码与已有回归用例已适配，Maven 有效 POM 和四模块依赖树解析通过。按项目约定，本轮未执行编译、测试、安装、发布或应用启动。** v5.1.1.RELEASE 是当前源码版本，尚无本轮生成的新制品或运行通过结论。

Boot 3 的 v4.1.1.RELEASE 制品与历史验证记录保留。主项目 `ym-galgame` 的 Boot 3.5.16 已由用户手动验收，页面和接口均正常，不需要补做旧版本验收。

## 环境与依赖

| 项目 | 当前配置 |
| --- | --- |
| 父 POM、core、starter、COS 版本 | v5.1.1.RELEASE |
| JDK / Java release / Kotlin jvmTarget | 21 |
| 本机 JDK | C:\0_develop\JDK\jdk-21.0.12+8 |
| Spring Boot BOM | 4.1.1 |
| Spring Framework | 7.0.9，由 BOM 管理 |
| Kotlin 编译器、标准库、反射、编译插件 | 2.3.21 |
| Jackson Kotlin 模块 | tools.jackson.module:jackson-module-kotlin，3.1.5，由 BOM 管理 |
| Web / 测试 starter | spring-boot-starter-webmvc / spring-boot-starter-webmvc-test |
| Servlet / Validation | Jakarta，版本由 BOM 管理 |
| Java 编译 / Surefire | 3.14.1 / 3.5.6，沿用现有配置 |
| 打包 | 三个普通 JAR，不使用 Boot repackage |

Poet 的 JDK 21 基线是本项目选择；Boot 4 本身最低要求 Java 17。[Boot 系统要求](https://docs.spring.io/spring-boot/system-requirements.html)

## 已完成的适配

- 统一父子 POM 版本为 v5.1.1.RELEASE，不覆盖 Boot 3 使用的旧坐标。
- Java release 与 Kotlin jvmTarget 同步为 21；Kotlin 编译器、no-arg/all-open 插件、标准库和反射统一为 2.3.21，继续保留 Java/Kotlin 参数名。
- 增加 `-Xannotation-default-target=param-property`；配置校验继续显式使用 `@field:NotBlank`，保持原校验目标。[Boot Kotlin 说明](https://docs.spring.io/spring-boot/reference/features/kotlin.html)
- 主自动配置的 JDBC/Validation 引用迁至 `org.springframework.boot.jdbc.autoconfigure` 和 `org.springframework.boot.validation.autoconfigure`，保留原装配顺序。
- COS 自动配置的 Validation `afterName` 同步迁移；仍在基础 router 就绪、TENCENT 模式开启时装配。
- Web starter 改为 `spring-boot-starter-webmvc`，Jackson Kotlin 模块改为 Jackson 3 坐标；不再由 Poet 显式引入 Jackson 2 Kotlin 模块。
- 已有测试改名为 `Boot4AutoConfigurationTest`、`Boot4CosAutoConfigurationTest`，更新 DataSource 自动配置包名。Web 测试从上下文取得 Jackson 3 JsonMapper，并断言只有一个此类型 Bean，继续通过实际 MVC 上传/查询/下载/删除链路检查输出。
- 继续通过两个 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件发现 starter，不要求宿主额外扫描 Poet 包。

附件接口、URL、配置键、默认 Bean 名称、自定义覆盖规则、拦截器、文件命名/存储与数据库 SQL 保持原实现。现有 JSR-305 可空注解与 Kotlin 可空类型保留，不机械替换所有 `javax.*`。

## 宿主接入

宿主先使用 Boot 4.1.1 BOM，再引入已经构建并安装的制品：

```xml
<dependency>
    <groupId>com.skypyb.poet</groupId>
    <artifactId>poet-spring-boot-starter</artifactId>
    <version>v5.1.1.RELEASE</version>
</dependency>
```

使用腾讯 COS 时同时引入同版本 `poet-cloudstore-tencent`。主项目仍为 Boot 3.5.16 时继续引用 v4.1.1.RELEASE，不单独提前切换新 Poet。

### 数据库模式

```yaml
poet:
  storage-location: /srv/poet
  enable-db-store: true
  enable-web-resource: true
  web-url-prefix: /poet
```

宿主提供数据库驱动、数据源和附件表。默认仓库为 MySQL；PostgreSQL 宿主继续提供现有 PostgresPoetAnnexRepository Bean，以覆盖默认仓库。本次不变更表结构。

### 无数据库模式

```yaml
poet:
  storage-location: /srv/poet
  enable-db-store: false
  enable-web-resource: false
spring:
  autoconfigure:
    # 仅适用于整个宿主都不使用 JDBC 的情况。
    exclude: org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
```

本地客户端继续按 key 读写文件；按附件 name 查询元信息与 HTTP 端点仍依赖仓库。关闭数据库不会改变原接口语义。

### COS 与配置兼容

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

继续支持旧驼峰配置与短横线配置；Web 路径使用 `poet.web-url-prefix`，同时保留 `poet.webUrlPrefix` 兼容入口。COS SDK 版本保持 5.6.54，真实云请求不在本轮执行范围。

宿主 JSON 默认采用 Jackson 3。第三方仍需要 Jackson 2 时可以并存，但不得把旧 Kotlin module 或旧 mapper 当作 Jackson 3 扩展使用。附件端点权限继续由宿主安全配置和原自定义附件拦截器负责。

## 已执行的检查

- 在指定 JDK 21 环境运行 `mvn help:effective-pom`，有效 POM 位于 `target/boot4-effective-pom.xml`。
- 使用 `maven-dependency-plugin:3.8.1:tree` 解析父工程和三个模块，均成功；结果位于各模块 `target/boot4-dependency-tree.txt`。
- 核对框架、Kotlin 与 Jackson 依赖版本，以及旧 Boot JDBC/Validation 包名和两份自动配置注册入口。
- 依赖树确认基础 starter 使用 Jackson 3.1.5，COS SDK 另外引入 Jackson 2.21.5；这是 SDK 的传递依赖，未全局排除。两代共享的 jackson-annotations 仍使用原命名空间。

上述 Maven goal 不执行 compile/test/install 生命周期，日志中的 BUILD SUCCESS 只表示配置或依赖解析成功。`target` 下资料不提交，执行 clean 前按需归档。

## 后续执行清单

- [x] POM、自动配置、Jackson Kotlin 模块和测试源码适配。
- [x] 有效 POM、依赖树与静态差异检查。
- [x] README、Boot 3 历史说明、本文及宿主升级计划同步。
- [ ] 在允许执行构建的阶段，由 Codex 编译、执行现有回归用例并安装新坐标。
- [ ] 宿主切换 Boot 4.1.1 时同步切换 poet.version，再验证真实 PostgreSQL 和附件链路。

后续构建命令（本轮未执行）：

```powershell
# 在 Poet 根目录运行，仅在后续构建阶段执行。
$poetJavaHomeBefore = $env:JAVA_HOME
$poetPathBefore = $env:Path
try {
    $env:JAVA_HOME = 'C:\0_develop\JDK\jdk-21.0.12+8'
    $env:Path = "$env:JAVA_HOME\bin;$poetPathBefore"
    & 'C:\0_develop\Maven\apache-maven-3.8.4\bin\mvn.cmd' -B -ntp clean install
    if ($LASTEXITCODE -ne 0) { throw 'Poet 构建、测试或安装失败' }
} finally {
    $env:JAVA_HOME = $poetJavaHomeBefore
    $env:Path = $poetPathBefore
}
```

目标制品是三个模块 target 下的 `*-v5.1.1.RELEASE.jar`。安装仅写本地 Maven 仓库，不发布远程。现有 release profile 的签名、旧 OSSRH 地址与 Dokka 文档生成链未启用或验证，远程发布另行处理。

已有自动配置/Web/COS 测试的旧版本通过记录保留在 BOOT3_MIGRATION.md，不能当作 Boot 4 的通过记录。
