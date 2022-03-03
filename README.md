<div align="center">

## poet
这是由 Kotlin 编写的 SpringBoot Starter 附件操作库。 一键集成进你的应用使其能够更便捷的操作附件

[![version](https://img.shields.io/badge/version-v1.1.0.RELEASE-orange.svg)](../)
[![License](https://img.shields.io/badge/License-MIT-red.svg)](https://mit-license.org/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.3.72-green)](https://github.com/JetBrains/kotlin/releases/tag/v1.3.72)
[![JDK](https://img.shields.io/badge/JDK-1.8-orange)](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

</div>
<br>
<br>




## 概述  
在系统开发中对于附件的操作多少得废些功夫， 尤其体现在个人开发者业余时间写项目时,  脱离了公司基础服务的情况下基本只能一个个去手写文件的增删改查。

使用云服务时如果你的附件不太正经，为了避免触发风控你还是得在本地保存一份。

此项目即是为了提升开发者的效率而诞生，基于**本地文件系统**。 一键引入即可实现对附件的各种操作。 由个人开发并在个人项目中使用过一段时间后开源。

使用 Kotlin & Java 混编开发，所有的逻辑都基于 Kotlin 实现。 为了方便没有使用过 Kotlin 的开发者也能够轻松的看源码+自定义组件的扩展,  所有接口抽象层使用了 Java 来定义



<br>
<br>

## 功能特性
* 程序中直接使用 `@Autowired` 注入附件操作上下文即可使用众多附件操作方法
* 提供 HTTP 端点进行附件的 查看、预览(仅限媒体)、删除、上传、下载
* 支持数据库储存附件元信息,  目前默认仅提供 `MySQL`、 `PostgreSQL` 两种支持
* 自定义附件操作的拦截器
* 包含图片处理模块，可压缩质量、修改图片大小和格式，简单方便
* 可基于配置文件方便定制各项功能的细节及开/关
* 插拔式组件化结构，包括但不限于:
  * 文件名&模块 (文件目录) 生成策略
  * 附件访问路由
  * 操作客户端
  * 持久化控制
  * 等balabala 一大堆..  基本上所有组件均可自行实现
* 清晰并友善的中文注释, 你如果不知道怎么扩展/自定义,  点进接口查看即可



<br>
<br>



## 快速开始:
上面说起来似乎东西不少,  但实际上用默认的组件+傻瓜式配置已经足以满足大部分需求

<br>

#### 1、首先通过Maven引入依赖
```xml
<dependency>
  <groupId>com.skypyb.poet</groupId>
  <artifactId>poet-spring-boot-starter</artifactId>
  <version>v1.1.0.RELEASE</version>
</dependency>
```
<br>


<br>

#### 2、然后在项目的`application.yml`中进行最小配置

```yaml
poet:
  #存储路径 支持windows系统, 如E:\tempfile\annex
  storage-location: /home/static/poet
```


<br>

#### 3、即可开始愉快使用

* 使用上下文进行附件操作  

    **例子(保存):** 
    
    ```java
    @RestController
    @RequestMapping("/img")
    public class TestResource {
    
        @Autowired
        private PoetAnnexContext poetAnnexContext;
    
        //文件流会安全的关闭
        @PostMapping("/upload-main")
        public ResponseEntity uploadMainImage(@RequestParam("file") MultipartFile file) throws IOException {
    
            //自定义模块(路径)
            String module = "main/customize1";
            PoetAnnex save = poetAnnexContext.save(file.getInputStream(), file.getOriginalFilename(), module);
    
            //上传到默认模块
    //        PoetAnnex save = poetAnnexContext.save(file.getInputStream(), file.getOriginalFilename());
    
            save.getName();//生成的文件名 全局唯一
            save.getRealName();//文件原名
            save.getSuffix(); //后缀
            save.getKey(); //抽象路径
            save.getLength(); //文件长度
    
            return ResponseEntity.ok(save);
        }
    }
    ```

<br>

* 通过 HTTP 端点进行附件操作

    端点可以查看下面的 HTTP 端点说明， 或者进入 `PoetResource` 类查看实现

    使用 HTTP 端点时**必须开启** (默认开启)数据库储存模块

    预览公开的图片这种需求， 建议上传到指定目录后使用反代来动静分离。 **不是很推荐直接使用 HTTP 端点** , 毕竟要走一道DB查询操作，当然那几毫秒在普通项目里其实也没啥关系， 懒得配Nginx的直接用端点也可

    文件的上传、删除建议使用上下文手动控制(避免被刷接口)  或实现内置的拦截器 `PoetHandlerInterceptor` 做鉴权操作



<br>

* 直接使用附件操作客户端对附件进行操作 (直接操作文件, 不会经过**任何**流程如拦截器、存储 等。 不推荐)  
    值得注意的是, 使用context操作附件只需要根据文件名name进行操作,  使用client则需要使用文件的key (module+name)
    
    **例子**
    
    ```java
    @RestController
    @RequestMapping("/img")
    public class TestResource {
    
        @Autowired
        private PoetAnnexClient poetAnnexClient;//基本操作客户端
        @Autowired
        private PoetAnnexClientHttpSupport poetAnnexClientHttpSupport; //HTTP支持客户端
    
        @PostMapping("/upload-main")
        public ResponseEntity uploadMainImage(@RequestParam("file") MultipartFile file) throws IOException {
    
            PoetAnnex save = poetAnnexClient.save(file.getInputStream(), file.getOriginalFilename());
    
            //true
            Assert.isTrue(Objects.equals(save.getRealName(), save.getName()), "eq");
    
            return ResponseEntity.ok(save);
        }
    }
    ```





<br><br>



## 配置项

| key                    | 默认值        | 说明                                                         |
| ---------------------- | ------------- | ------------------------------------------------------------ |
| poet.storageLocation   |               | 非空. 附件默认默认储存位置。 附件最终保存的路径为 storageLocation+module+name                         |
| poet.enableDBStore     | true          | 是否使用DB储存附件信息,  关闭的话则无法使用 HTTP 相关操作    |
| poet.enableWebResource | true          | 是否启用web资源层.                                           |
| poet.webUrlPrefix      | /poet         | web资源接口请求路径前缀                                      |
| poet.defaultModule     |               | 默认模块, 在文件保存时若不指定则将直接保存到此模块之中.  **为空时**直接保存在 `storageLocation` 上 |
| poet.tableName         | poet_annex | 储存附件信息的表名                                           |





<br><br>



## 默认HTTP端点

| 端点                        | 说明                                                        | 入参                                |
| --------------------------- | ----------------------------------------------------------- | ----------------------------------- |
| GET /poet/view/{name}       | 预览文件,   解析后缀响应对应的Content-Type。 可直接显示图片 |                                     |
| GET /poet/view-media/{name} | 预览媒体,   包括视频和音频                                  |                                     |
| GET /poet/download/{name}   | 下载附件                                                    |                                     |
| POST /up                    | 上传附件, 可指定模块(非必须)                                | file: MultipartFile, module: String? |
| GET /bs/{name}              | 获取DB中存储的附件业务信息                                  |                                     |
| DELETE /bs/{name}           | 删除一个附件                                                |                                     |





<br><br>



## 图片处理


图片处理的统一接口， 可自行拓展图片处理方式。  
自带默认的处理实现`DefaultImageHandler`，可自定义宽、高、压缩比、输出格式(不支持webp)
```java
@FunctionalInterface
public interface PoetImageHandler {

    /**
     * 涉及到图片处理,  原名已经无意义了。
     * 这个图片的原名在流程中将会被抹去,  自己保存时定义新的名字
     *
     * @param bytes 图片字节数组
     * @return 新图片的字节数组
     */
    byte[] handle(@NotNull byte[] bytes);
}
```

<br>

#### 使用方法:


```java
//得到原图的 Bytes， 也可以是 InputStream。InputStream在传入warp()后会被自动关闭
byte[] imgBytes = getBytes(originImage);

//创建一个将图片处理为宽度980px + 压缩比0.8 + 格式化为jpg的处理器
DefaultImageHandler handler = new DefaultImageHandler(980, Integer.MAX_VALUE, 0.8f, "jpg");

//得到的结果Map中，key即为use时传入的key, value是处理完毕的结果。 
//可多次调用use(), invoke()内的回调将会循环触发。Map结果是所有use的key + handler处理结果
Map<String, PoetAnnex> invokeResult = PoetImageWrap.<String>warp(imgBytes)
    .use("w980", handler)
    .invoke((k, bytes) -> {//k = "w980"
        return poetAnnexContext.save(bytes, "name.jpg", "module");
    });

//通过指定的key得到Map中对应经过对应处理的图片
PoetAnnex thumbnailAnnex = invokeResult.get("w980");
```

<br><br>


## 拦截器

实现 `PoetHandlerInterceptor` 接口+交给Spring控制 即可注册拦截器, 支持排序  

返回 true 表示通过,  也可以自定义异常抛出来中止操作

 `Mode` 有三种， 分别为 ACCESS、DELETE、SAVE ,  更多细节注释可以查看源码



<br>



**示例:**

```java
@Slf4j
@Component
public class LogInterceptor implements PoetHandlerInterceptor {

    @Override
    public boolean preHandle(Mode mode, String name, @Nullable String module) {
        log.info("Annex operation record. mode:{}, name:{}, module:{} ", mode, name, module);
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
```



<br><br>



## 组件扩展



#### 这是一个基本的扩展配置示例

```java
@Configuration
public class PoetAnnexConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //使用自带的Pg存储替换默认的MySQL
    @Bean
    public PoetAnnexRepository poetAnnexRepository() {
        return new PostgresPoetAnnexRepository(jdbcTemplate);
    }

    //自定义附件的目录结构分割方式
    @Bean
    public PoetAnnexSlicer poetAnnexSlicer() {
        return new Hash2AnnexSlicer();
    }

    //自定义附件名字生成策略 (默认UUID,  需保证全局唯一)
    @Bean
    public PoetAnnexNameGenerator poetAnnexNameGenerator() {
        return new RandomNameGenerator();
    }
}
```

看出来了吧?  只要你实现了对应的接口,  并且将其作为一个 Bean 交给 Spring 管理。  那么就会替换掉默认的实现

<br>

#### 目前所有支持自定义的组件

* `PoetAccessRouter` 访问路由器,   没特殊需求的话默认即可
* `PoetAnnexRepository` DB存储控件, 目前支持MySQL和PostgreSQL,  需要支持其他DB比如Oracle、NoSQL 之类的可以自行实现
* `PoetAnnexClient` 附件操作客户端,  使用文件系统时直接默认即可
* `PoetAnnexClientHttpSupport `  附件操作HTTP支持,  使用文件系统时直接默认即可
* `PoetAnnexSlicer`  附件分割器,  可自定义路径分割。  默认不做任何操作直接组装 module&name
* `PoetAnnexNameGenerator`  名字生成器,  默认UUID。  **需保证全局唯一**





<br>

<br>





## 附录

  附件操作上下文提供的所有操作附件方法

```java
public interface PoetAnnexContext {

    PoetAnnex save(InputStream in, String name);


    PoetAnnex save(InputStream in, String name, String module);


    PoetAnnex save(byte[] data, String name);


    PoetAnnex save(byte[] data, String name, String module);


    boolean exist(String name);


    void delete(String name);


    byte[] getBytes(String name);


    void view(String name, HttpServletResponse response);


    void viewMedia(String name, HttpServletResponse response);


    void viewMedia(String name, HttpServletRequest request, HttpServletResponse response);


    void down(String name, HttpServletResponse response);


    void down(String name, String realName, HttpServletResponse response);

}
```

<br>
<br>




## 说在最后
嘛... 总之无限欢迎PR  
如果帮到了你，请点个star~

<br>
<br>