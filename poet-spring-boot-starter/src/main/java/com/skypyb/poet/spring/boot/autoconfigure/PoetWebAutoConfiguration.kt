package com.skypyb.poet.spring.boot.autoconfigure

import com.skypyb.poet.spring.boot.PoetAnnexContext
import com.skypyb.poet.spring.boot.web.PoetResource
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Import

/** 只在 Servlet 应用中注册附件端点，端点开关继续由原有校验处理。 */
@AutoConfiguration(after = [PoetAutoConfiguration::class])
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(PoetAnnexContext::class)
@Import(PoetResource::class)
class PoetWebAutoConfiguration
