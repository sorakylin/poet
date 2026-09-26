package com.skypyb.poet.spring.boot;

import tools.jackson.databind.json.JsonMapper;
import com.skypyb.poet.core.client.PoetAccessRouter;
import com.skypyb.poet.core.client.PoetAnnexClient;
import com.skypyb.poet.core.client.PoetAnnexClientHttpSupport;
import com.skypyb.poet.core.exception.AnnexAccessException;
import com.skypyb.poet.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.autoconfigure.PoetProperties;
import com.skypyb.poet.spring.boot.interceptor.PoetHandlerInterceptor;
import com.skypyb.poet.spring.boot.store.MySQLPoetAnnexRepository;
import com.skypyb.poet.spring.boot.store.PoetAnnexRepository;
import com.skypyb.poet.spring.boot.web.PoetResource;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class Boot4AutoConfigurationTest {

    @TempDir
    Path storage;

    // 仅开启消费者的自动配置，验证 JAR 入口发现，不手动导入 Poet 配置类。
    private ApplicationContextRunner runner() {
        return new ApplicationContextRunner()
                .withUserConfiguration(ConsumerApplication.class)
                .withPropertyValues("poet.storage-location=" + storage);
    }

    @Test
    void discoversStarterAndDefaultDatabaseRepository() {
        runner().run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(PoetAnnexContext.class)
                    .hasSingleBean(MySQLPoetAnnexRepository.class).hasSingleBean(Validator.class);
            assertThat(context.getBean("poetProperties")).isInstanceOf(PoetProperties.class);
            assertThat(context.getBean(PoetAnnexClientHttpSupport.class))
                    .isSameAs(context.getBean("poetAnnexClientHttpSupport"));
            assertThat(context).hasSingleBean(PoetAnnexClient.class);
            assertThat(context).doesNotHaveBean(PoetResource.class);
        });
    }

    @Test
    void supportsLocalStorageWithoutDatabase() {
        new ApplicationContextRunner().withUserConfiguration(NoDatabaseApplication.class)
                .withPropertyValues("poet.storage-location=" + storage, "poet.enable-db-store=false")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(PoetAnnexContext.class)
                            .doesNotHaveBean(PoetAnnexRepository.class);
                    PoetAnnexContext annexContext = context.getBean(PoetAnnexContext.class);
                    PoetAnnex annex = annexContext.save("附件内容".getBytes(StandardCharsets.UTF_8), "sample.txt");
                    PoetAnnexClient client = context.getBean(PoetAnnexClient.class);
                    assertThat(client.getBytes(annex.getKey())).isEqualTo("附件内容".getBytes(StandardCharsets.UTF_8));
                    client.delete(annex.getKey());
                    assertThat(client.exist(annex.getKey())).isFalse();
                });
    }

    @Test
    void preservesCustomRepository() {
        runner().withUserConfiguration(CustomRepository.class).run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(PoetAnnexRepository.class);
            assertThat(context.getBean(PoetAnnexRepository.class)).isSameAs(context.getBean("customRepository"));
            assertThat(context).doesNotHaveBean(MySQLPoetAnnexRepository.class);
        });
    }

    @Test
    void acceptsLegacyCamelCaseDatabaseSwitch() {
        runner().withPropertyValues("poet.enableDBStore=false").run(context -> {
            assertThat(context).hasNotFailed().doesNotHaveBean(PoetAnnexRepository.class);
            assertThat(context.getBean(PoetProperties.class).getEnableDBStore()).isFalse();
        });
    }

    @Test
    void preservesCustomLocalClientWithDifferentBeanName() {
        runner().withUserConfiguration(CustomLocalClient.class).run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(PoetAnnexClient.class);
            assertThat(context.getBean(PoetAnnexClient.class)).isSameAs(context.getBean("customClient"));
            assertThat(context.getBean(PoetAnnexClientHttpSupport.class)).isNotNull();
        });
    }

    @Test
    void webApplicationCanDisableDatabaseStorage() {
        new WebApplicationContextRunner().withUserConfiguration(NoDatabaseApplication.class)
                .withPropertyValues("poet.storage-location=" + storage, "poet.enable-db-store=false")
                .run(context -> assertThat(context).hasNotFailed().hasSingleBean(PoetResource.class)
                        .doesNotHaveBean(PoetAnnexRepository.class));
    }

    @Test
    void rejectsBlankStorageLocationWithJakartaValidation() {
        runner().withPropertyValues("poet.storage-location=").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasStackTraceContaining("Storage location not be empty!");
        });
    }

    @Test
    void attachesCustomInterceptorToContext() {
        runner().withUserConfiguration(DenySave.class).run(context -> {
            assertThat(context).hasNotFailed();
            assertThatThrownBy(() -> context.getBean(PoetAnnexContext.class).save(new byte[]{1}, "denied.txt"))
                    .isInstanceOf(AnnexAccessException.class);
            assertThat(storage.toFile().list()).isEmpty();
        });
    }

    @Test
    void servletEndpointsUploadReadDownloadAndDelete() {
        new WebApplicationContextRunner().withUserConfiguration(ConsumerApplication.class)
                .withPropertyValues("poet.storage-location=" + storage,
                        "poet.web-url-prefix=/files",
                        "spring.datasource.url=jdbc:h2:mem:poet_web;MODE=MySQL;NON_KEYWORDS=KEY")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(PoetResource.class)
                            .hasSingleBean(JsonMapper.class);
                    // 使用内存数据库与临时目录，实际走 MVC、JDBC 仓库和本地附件客户端。
                    context.getBean(JdbcTemplate.class).execute("CREATE TABLE poet_annex ("
                            + "name VARCHAR(255) PRIMARY KEY, real_name VARCHAR(255), suffix VARCHAR(32), "
                            + "`key` VARCHAR(1024), length BIGINT, create_time TIMESTAMP)");
                    MockMvc mvc = MockMvcBuilders.webAppContextSetup(context.getSourceApplicationContext()).build();
                    byte[] content = "Boot 4 附件".getBytes(StandardCharsets.UTF_8);
                    String json = mvc.perform(multipart("/files/up")
                                    .file(new MockMultipartFile("file", "hello.txt", "text/plain", content))
                                    .param("module", "test"))
                            .andExpect(status().isOk()).andExpect(jsonPath("realName").value("hello.txt"))
                            .andReturn().getResponse().getContentAsString();
                    String name = context.getBean(JsonMapper.class).readTree(json).get("name").asText();
                    mvc.perform(get("/files/bs/{name}", name)).andExpect(status().isOk())
                            .andExpect(jsonPath("name").value(name));
                    mvc.perform(get("/files/view/{name}", name)).andExpect(status().isOk())
                            .andExpect(content().bytes(content));
                    mvc.perform(get("/files/download/{name}", name)).andExpect(status().isOk())
                            .andExpect(content().bytes(content));
                    mvc.perform(delete("/files/bs/{name}", name)).andExpect(status().isOk());
                    assertThat(context.getBean(PoetAnnexRepository.class).findByName(name)).isNull();
                    assertThat(context.getBean(PoetAnnexClient.class)
                            .exist(context.getBean(JsonMapper.class).readTree(json).get("key").asText())).isFalse();
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class ConsumerApplication {
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    static class NoDatabaseApplication {
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomRepository {
        @Bean
        PoetAnnexRepository customRepository() {
            return mock(PoetAnnexRepository.class);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomLocalClient {
        @Bean
        PoetAnnexClient customClient() {
            return mock(PoetAnnexClient.class);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class DenySave {
        @Bean
        PoetHandlerInterceptor denySave() {
            return new PoetHandlerInterceptor() {
                @Override
                public boolean preHandle(Mode mode, String name, String module) {
                    return mode != Mode.SAVE;
                }

                @Override
                public int getOrder() {
                    return 0;
                }
            };
        }
    }
}
