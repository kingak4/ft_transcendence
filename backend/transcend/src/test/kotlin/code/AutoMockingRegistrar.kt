package code

import org.mockito.Mockito
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.AnnotationBeanNameGenerator
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.java

class AutoMockingRegistrar : BeanDefinitionRegistryPostProcessor {
   private val nameGenerator = AnnotationBeanNameGenerator()

   override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
      val scanner = ClassPathScanningCandidateComponentProvider(false).apply {
         addIncludeFilter(AnnotationTypeFilter(Service::class.java))
         addIncludeFilter(AnnotationTypeFilter(Component::class.java))
         addIncludeFilter(AnnotationTypeFilter(Repository::class.java))
         addExcludeFilter(AnnotationTypeFilter(Configuration::class.java))
         addExcludeFilter(AnnotationTypeFilter(Controller::class.java))
         addExcludeFilter(AnnotationTypeFilter(RestController::class.java))
      }

      scanner.findCandidateComponents("code").forEach { candidate ->
         val beanClass = Class.forName(candidate.beanClassName)
         val beanName = nameGenerator.generateBeanName(candidate, registry)

         val mockDefinition = RootBeanDefinition(beanClass)

         mockDefinition.setInstanceSupplier { Mockito.mock(beanClass) }
         mockDefinition.isPrimary = true
         mockDefinition.scope = BeanDefinition.SCOPE_SINGLETON

         registry.registerBeanDefinition(beanName, mockDefinition)
      }
   }

   override fun postProcessBeanFactory(factory: ConfigurableListableBeanFactory) {
   }
}