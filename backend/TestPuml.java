package temp;

import de.elnarion.util.plantuml.generator.classdiagram.config.*;
import de.elnarion.util.plantuml.generator.classdiagram.*;
import java.util.List;

public class TestPuml {
    public static void main(String[] args) throws Exception {
        var config = new PlantUMLClassDiagramConfigBuilder(
              ".*(Exception|ExceptionHandler|Mapper|Config|Dao).*", List.of("code.users"))
          .withRemoveFields(true)
          .withHideMethods(true)
          .build();
        var gen = new PlantUMLClassDiagramGenerator(config);
        System.out.println(gen.generateDiagramText());
    }
}
