package code;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

// Spring scanning utilities are used directly where needed

public class StructurizrGenerator {

    public static void main(String[] args) {
        try {
            generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generate() throws Exception {
        String basePackage = "code"; // Change to your base package

        // 1. Initialize Workspace and Model
        Workspace workspace = new Workspace("System Name", "Description of the system");
        Model model = workspace.getModel();

        SoftwareSystem mySystem = model.addSoftwareSystem("Transcend", "Description");
        Container webApplication = mySystem.addContainer("Backend API", "Spring Boot Application", "Java and Spring Boot");

        // 2. Component Scanning (Spring Logic)
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));

        Set<String> added = new HashSet<>();
        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            String className = bd.getBeanClassName();
            if (className != null && !added.contains(className)) {
                try {
                    Class<?> clazz = Class.forName(className);
                    String pkg = clazz.getPackageName();
                    String compName = clazz.getSimpleName();

                    // Add component to the container
                    webApplication.addComponent(compName, "Spring Bean: " + className, pkg);
                    added.add(className);
                } catch (Exception ex) {
                    System.err.println("Could not add component " + className + ": " + ex.getMessage());
                }
            }
        }

        // 3. Create Views
        ViewSet views = workspace.getViews();

        // System Context View
        SystemContextView contextView = views.createSystemContextView(mySystem, "SystemContext", "System Context Diagram");
        contextView.addAllSoftwareSystems();
        contextView.enableAutomaticLayout();

        // Container View
        ContainerView containerView = views.createContainerView(mySystem, "Containers", "Container Diagram");
        containerView.addAllContainers();
        containerView.enableAutomaticLayout();

        // Component View
        ComponentView componentView = views.createComponentView(webApplication, "Components", "Component Diagram");
        componentView.addAllComponents();
        componentView.enableAutomaticLayout();

        File workspaceFile = new File("docker/structurizr/data/workspace.json");
        workspaceFile.getParentFile().mkdirs();

        String json = WorkspaceUtils.toJson(workspace, true);
        Files.writeString(workspaceFile.toPath(), json, StandardCharsets.UTF_8);

        System.out.println("Workspace exported to: " + workspaceFile.getAbsolutePath());
    }
}