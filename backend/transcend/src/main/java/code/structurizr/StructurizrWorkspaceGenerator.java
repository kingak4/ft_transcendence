package code.structurizr;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.util.WorkspaceUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class StructurizrWorkspaceGenerator {

  private final ClasspathClassDiscovery classDiscovery;
  private final ComponentModelBuilder componentModelBuilder;
  private final ViewConfigurationBuilder viewConfigurationBuilder;

  public StructurizrWorkspaceGenerator() {
    this.classDiscovery = new ClasspathClassDiscovery();
    this.componentModelBuilder = new ComponentModelBuilder();
    this.viewConfigurationBuilder = new ViewConfigurationBuilder();
  }

  public void generate(String basePackage, File outputFile) throws Exception {
    Workspace workspace = createWorkspace();
    addBasicModelDetails(workspace);

    Map<String, Class<?>> classes = classDiscovery.discover(basePackage);
    componentModelBuilder.populate(workspace, basePackage, classes);
    viewConfigurationBuilder.configure(workspace);

    writeWorkspaceFile(workspace, outputFile);
  }

  private Workspace createWorkspace() {
    return new Workspace("Transcend", "Architecture workspace for the Transcend service");
  }

  private void addBasicModelDetails(Workspace workspace) {
    Model model = workspace.getModel();

    SoftwareSystem system =
        model.addSoftwareSystem("Transcend", "Back-end services for Transcend application");
    Container webApplication =
        system.addContainer(
            "Backend API", "Handles HTTP API requests and business logic", "Java and Spring Boot");
    Container database =
        system.addContainer("Database", "Stores persistent application data", "PostgreSQL");

    model.addPerson("User", "End user of the Transcend application");
    model.getPersonWithName("User").uses(webApplication, "Uses the backend API", "HTTPS");
    model.getPersonWithName("User").uses(system, "Uses the system", "HTTPS");

    webApplication.uses(database, "Reads from and writes to", "JDBC");
  }

  private void writeWorkspaceFile(Workspace workspace, File file) throws Exception {
    file.getParentFile().mkdirs();
    String json = WorkspaceUtils.toJson(workspace, true);
    Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);
  }
}
