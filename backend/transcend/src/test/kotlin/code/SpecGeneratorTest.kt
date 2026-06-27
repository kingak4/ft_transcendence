package code

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@DocumentationSlice
@AutoConfigureMockMvc(addFilters = false)
class SpecGeneratorTest {

   @MockitoBean
   lateinit var simpMessagingTemplate: SimpMessagingTemplate

   @Autowired
   lateinit var mockMvc: MockMvc

   @Value("\${springdoc.api-docs.path}")
   lateinit var openApiPath: String

   @Value("\${springwolf.paths.docs}")
   lateinit var asyncApiPath: String

   @Test
   fun generateOpenApiSpec() {
      val content = mockMvc.get(openApiPath).andExpect {
         status { isOk() }
      }.andReturn().response.contentAsString
      saveToFile("openapi.json", content)
   }

   @Test
   fun generateAsyncApiSpec() {
      val content = mockMvc.get(asyncApiPath)
         .andExpect { status { isOk() } }
         .andReturn().response.contentAsString

      saveToFile("asyncapi.json", content)
   }

   private fun saveToFile(fileName: String, content: String) {
      val outputDir = "build/reports/specs"
      val directory = Paths.get(outputDir)
      if (!Files.exists(directory)) {
         Files.createDirectories(directory)
      }
      File(outputDir, fileName).writeText(content)
      println("Successfully generated $fileName in $outputDir")
   }
}