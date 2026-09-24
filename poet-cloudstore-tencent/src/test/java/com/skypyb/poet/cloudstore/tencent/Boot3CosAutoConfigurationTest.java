package com.skypyb.poet.cloudstore.tencent;

import com.skypyb.poet.core.client.LocalFileServerClient;
import com.skypyb.poet.core.client.PoetAnnexClient;
import com.skypyb.poet.spring.boot.PoetAnnexContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class Boot3CosAutoConfigurationTest {
    @TempDir
    Path storage;

    private ApplicationContextRunner runner() {
        return new ApplicationContextRunner().withUserConfiguration(ConsumerApplication.class)
                .withPropertyValues("poet.storage-location=" + storage, "poet.enable-db-store=false");
    }

    private ApplicationContextRunner tencentRunner() {
        // 只创建客户端以验证装配和销毁，不调用腾讯云接口。
        return runner().withPropertyValues("poet.store-mode=TENCENT", "poet.tencent.secret-id=test-id",
                "poet.tencent.secret-key=test-key", "poet.tencent.region=ap-guangzhou",
                "poet.tencent.default-bucket=test-1250000000");
    }

    @Test
    void localModeDoesNotRequireCloudCredentials() {
        runner().run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(PoetAnnexContext.class);
            assertThat(context.getBean(PoetAnnexClient.class)).isInstanceOf(LocalFileServerClient.class);
            assertThat(context).doesNotHaveBean(PoetCosAutoConfiguration.class);
        });
    }

    @Test
    void discoversCloudExtensionWithDashedProperties() {
        tencentRunner().run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(PoetAnnexClient.class)
                    .hasSingleBean(PoetAnnexContext.class);
            assertThat(context.getBean(PoetAnnexClient.class)).isInstanceOf(TencentCosClient.class);
            assertThat(context.getBean(PoetCosProperties.class).getDefaultBucket()).isEqualTo("test-1250000000");
        });
    }

    @Test
    void acceptsLegacyCamelCaseMode() {
        runner().withPropertyValues("poet.storeMode=TENCENT", "poet.tencent.secretId=test-id",
                        "poet.tencent.secretKey=test-key", "poet.tencent.region=ap-guangzhou",
                        "poet.tencent.defaultBucket=test-1250000000")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(PoetAnnexClient.class)).isInstanceOf(TencentCosClient.class);
                });
    }

    @Test
    void rejectsMissingCloudCredentials() {
        runner().withPropertyValues("poet.store-mode=TENCENT").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasStackTraceContaining("jakarta.validation.ValidationException");
        });
    }

    @Test
    void preservesCustomCloudClient() {
        tencentRunner().withUserConfiguration(CustomClient.class).run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(PoetAnnexClient.class);
            assertThat(context.getBean(PoetAnnexClient.class)).isSameAs(context.getBean("customClient"));
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    static class ConsumerApplication {
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClient {
        @Bean
        PoetAnnexClient customClient() {
            return mock(PoetAnnexClient.class);
        }
    }
}
