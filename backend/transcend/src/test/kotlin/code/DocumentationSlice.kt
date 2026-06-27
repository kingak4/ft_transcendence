package code

import io.github.springwolf.core.configuration.SpringwolfAutoConfiguration
import org.mockito.Mockito
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.java

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [StrictDocConfig::class])
@ImportAutoConfiguration(
   classes = [
      JacksonAutoConfiguration::class,
      HttpMessageConvertersAutoConfiguration::class,
      WebMvcAutoConfiguration::class,
      DispatcherServletAutoConfiguration::class,
      MockMvcAutoConfiguration::class,
      SpringDocWebMvcConfiguration::class,
      SpringwolfAutoConfiguration::class,
      SpringDocConfiguration::class,
      SpringDocConfigProperties::class,
      SpringDocWebMvcConfiguration::class,
      WebSocketMessagingAutoConfiguration::class
   ]
)
@OverrideAutoConfiguration(enabled = false)
@Import(AutoMockingRegistrar::class)
annotation class DocumentationSlice

@Configuration
@ComponentScan(
   basePackages = ["code"],
   useDefaultFilters = false,
   includeFilters = [
      ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [RestController::class, Controller::class])
   ]
)
class StrictDocConfig {
}