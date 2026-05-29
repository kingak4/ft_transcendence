package code.shared.config;

import io.github.springwolf.asyncapi.v3.bindings.stomp.StompServerBinding;
import io.github.springwolf.asyncapi.v3.model.AsyncAPI;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.channel.message.Message;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageHeaders;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessagePayload;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.components.ComponentSchema;
import io.github.springwolf.asyncapi.v3.model.components.Components;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.schema.SchemaObject;
import io.github.springwolf.asyncapi.v3.model.schema.SchemaReference;
import io.github.springwolf.asyncapi.v3.model.security_scheme.HttpSecurityScheme;
import io.github.springwolf.asyncapi.v3.model.security_scheme.SecurityScheme;
import io.github.springwolf.asyncapi.v3.model.server.Server;
import io.github.springwolf.core.asyncapi.AsyncApiCustomizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty(name = "springwolf.docket.base-package")
@RequiredArgsConstructor
public class SpringwolfAsyncApiConfig implements AsyncApiCustomizer {

  private final Environment environment;

  @Override
  public void customize(AsyncAPI asyncAPI) {
    addSecuritySchemes(asyncAPI);
    addServers(asyncAPI);
    cleanupOperationIds(asyncAPI);
    simplifyNames(asyncAPI);
  }

  private void addSecuritySchemes(AsyncAPI asyncAPI) {
    if (asyncAPI.getComponents() == null) {
      asyncAPI.setComponents(new Components());
    }

    SecurityScheme jwtScheme =
        HttpSecurityScheme.httpBuilder()
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT Bearer token authentication. Required for WebSocket handshake.")
            .build();

    Map<String, SecurityScheme> securitySchemes = new HashMap<>();
    if (asyncAPI.getComponents().getSecuritySchemes() != null) {
      securitySchemes.putAll(asyncAPI.getComponents().getSecuritySchemes());
    }
    securitySchemes.put("bearerAuth", jwtScheme);
    asyncAPI.getComponents().setSecuritySchemes(securitySchemes);

    if (asyncAPI.getOperations() != null) {
      asyncAPI
          .getOperations()
          .values()
          .forEach(
              operation -> {
                SecurityScheme securityRequirement =
                    SecurityScheme.builder().ref("bearerAuth").build();
                List<SecurityScheme> security = new ArrayList<>();
                if (operation.getSecurity() != null) {
                  security.addAll(operation.getSecurity());
                }
                security.add(securityRequirement);
                operation.setSecurity(security);
              });
    }
  }

  private void addServers(AsyncAPI asyncAPI) {
    Map<String, Server> servers = new HashMap<>();

    String activeProfile = getActiveProfile();
    String serverUrl = getServerUrl(activeProfile);

    Server server =
        Server.builder()
            .host(extractHost(serverUrl) + ":" + extractPort(serverUrl))
            .protocol(serverUrl.startsWith("wss") ? "wss" : "ws")
            .protocolVersion("13")
            .description(getServerDescription(activeProfile))
            .bindings(Map.of("stomp", new StompServerBinding()))
            .build();

    servers.put(activeProfile + "-websocket", server);
    asyncAPI.setServers(servers);
  }

  private void cleanupOperationIds(AsyncAPI asyncAPI) {
    if (asyncAPI.getOperations() == null) {
      return;
    }

    Map<String, Operation> cleanedOperations = new HashMap<>();
    asyncAPI
        .getOperations()
        .forEach(
            (operationId, operation) -> {
              String cleanId = extractMethodName(operationId);
              cleanedOperations.put(cleanId, operation);
            });

    asyncAPI.setOperations(cleanedOperations);
  }

  private void simplifyNames(AsyncAPI asyncAPI) {
    if (asyncAPI.getComponents() == null) {
      return;
    }

    Map<String, String> mapping = new HashMap<>();

    Map<String, ComponentSchema> schemas = asyncAPI.getComponents().getSchemas();
    if (schemas != null) {
      Map<String, ComponentSchema> simplifiedSchemas = new HashMap<>();
      schemas.forEach(
          (fullName, schema) -> {
            String simpleName = extractSimpleName(fullName);
            simplifiedSchemas.put(simpleName, schema);
            mapping.put("#/components/schemas/" + fullName, "#/components/schemas/" + simpleName);
          });
      asyncAPI.getComponents().setSchemas(simplifiedSchemas);
    }

    Map<String, Message> messages = asyncAPI.getComponents().getMessages();
    if (messages != null) {
      Map<String, Message> simplifiedMessages = new HashMap<>();
      messages.forEach(
          (fullName, message) -> {
            String simpleName = extractSimpleName(fullName);
            simplifiedMessages.put(simpleName, message);
            mapping.put("#/components/messages/" + fullName, "#/components/messages/" + simpleName);
          });
      asyncAPI.getComponents().setMessages(simplifiedMessages);
    }

    updateReferences(asyncAPI, mapping);
  }

  private void updateReferences(AsyncAPI asyncAPI, Map<String, String> mapping) {
    updateChannelReferences(asyncAPI, mapping);
    updateOperationReferences(asyncAPI, mapping);
    updateComponentMessageReferences(asyncAPI, mapping);
    updateComponentSchemaReferences(asyncAPI, mapping);
  }

  private void updateChannelReferences(AsyncAPI asyncAPI, Map<String, String> mapping) {
    if (asyncAPI.getChannels() != null) {
      asyncAPI
          .getChannels()
          .forEach(
              (channelId, channel) -> {
                if (channel.getMessages() != null) {
                  Map<String, Message> simplifiedChannelMessages = new HashMap<>();
                  channel
                      .getMessages()
                      .forEach(
                          (messageId, msg) -> {
                            String simpleMessageId = extractSimpleName(messageId);
                            simplifiedChannelMessages.put(
                                simpleMessageId, updateMessageReference(msg, mapping));
                            mapping.put(
                                "#/channels/" + channelId + "/messages/" + messageId,
                                "#/channels/" + channelId + "/messages/" + simpleMessageId);
                          });
                  channel.setMessages(simplifiedChannelMessages);
                }
              });
    }
  }

  private void updateOperationReferences(AsyncAPI asyncAPI, Map<String, String> mapping) {
    if (asyncAPI.getOperations() != null) {
      asyncAPI
          .getOperations()
          .values()
          .forEach(
              operation -> {
                if (operation.getMessages() != null) {
                  List<MessageReference> updatedRefs = new ArrayList<>();
                  operation
                      .getMessages()
                      .forEach(
                          msgRef -> {
                            updatedRefs.add(
                                (MessageReference) updateMessageReference(msgRef, mapping));
                          });
                  operation.setMessages(updatedRefs);
                }
                updateChannelReference(operation.getChannel(), mapping);
              });
    }
  }

  private void updateComponentMessageReferences(AsyncAPI asyncAPI, Map<String, String> mapping) {
    if (asyncAPI.getComponents() != null && asyncAPI.getComponents().getMessages() != null) {
      asyncAPI
          .getComponents()
          .getMessages()
          .values()
          .forEach(
              msg -> {
                if (msg instanceof MessageObject message) {
                  updateMessageObjectReferences(message, mapping);
                }
              });
    }
  }

  private void updateMessageObjectReferences(MessageObject message, Map<String, String> mapping) {
    if (message.getPayload() != null) {
      if (message.getPayload().getReference() != null) {
        String ref = message.getPayload().getReference().getRef();
        if (ref != null && mapping.containsKey(ref)) {
          message.setPayload(MessagePayload.of(new SchemaReference(mapping.get(ref))));
        }
      } else if (message.getPayload().getSchema() != null) {
        updateSchemaObjectReferences(message.getPayload().getSchema(), mapping);
      }
    }
    if (message.getHeaders() != null) {
      if (message.getHeaders().getReference() != null) {
        String ref = message.getHeaders().getReference().getRef();
        if (ref != null && mapping.containsKey(ref)) {
          message.setHeaders(MessageHeaders.of(new SchemaReference(mapping.get(ref))));
        }
      } else if (message.getHeaders().getSchema() != null) {
        updateSchemaObjectReferences(message.getHeaders().getSchema(), mapping);
      }
    }
  }

  private void updateComponentSchemaReferences(AsyncAPI asyncAPI, Map<String, String> mapping) {
    if (asyncAPI.getComponents() != null && asyncAPI.getComponents().getSchemas() != null) {
      asyncAPI
          .getComponents()
          .getSchemas()
          .values()
          .forEach(cs -> updateComponentSchema(cs, mapping));
    }
  }

  private void updateComponentSchema(ComponentSchema cs, Map<String, String> mapping) {
    if (cs == null) {
      return;
    }
    if (cs.getSchema() != null) {
      updateSchemaObjectReferences(cs.getSchema(), mapping);
    }
  }

  private void updateSchemaObjectReferences(SchemaObject schema, Map<String, String> mapping) {
    if (schema == null) {
      return;
    }

    if (schema.getProperties() != null) {
      Map<String, Object> updatedProperties = new HashMap<>(schema.getProperties());
      updatedProperties
          .entrySet()
          .forEach(
              entry -> {
                if (entry.getValue() instanceof ComponentSchema cs) {
                  entry.setValue(updateComponentSchemaReference(cs, mapping));
                }
              });
      schema.setProperties(updatedProperties);
    }

    if (schema.getItems() != null) {
      schema.setItems(updateComponentSchemaReference(schema.getItems(), mapping));
    }

    if (schema.getAdditionalProperties() != null) {
      schema.setAdditionalProperties(
          updateComponentSchemaReference(schema.getAdditionalProperties(), mapping));
    }

    if (schema.getAllOf() != null) {
      schema.setAllOf(updateComponentSchemaList(schema.getAllOf(), mapping));
    }
    if (schema.getAnyOf() != null) {
      schema.setAnyOf(updateComponentSchemaList(schema.getAnyOf(), mapping));
    }
    if (schema.getOneOf() != null) {
      schema.setOneOf(updateComponentSchemaList(schema.getOneOf(), mapping));
    }

    if (schema.getNot() != null) {
      schema.setNot(updateComponentSchemaReference(schema.getNot(), mapping));
    }
  }

  private ComponentSchema updateComponentSchemaReference(
      ComponentSchema cs, Map<String, String> mapping) {
    if (cs == null) {
      return null;
    }
    if (cs.getReference() != null) {
      String ref = cs.getReference().getRef();
      if (ref != null && mapping.containsKey(ref)) {
        return ComponentSchema.of(new SchemaReference(mapping.get(ref)));
      }
      return cs;
    }
    if (cs.getSchema() != null) {
      updateSchemaObjectReferences(cs.getSchema(), mapping);
    }
    return cs;
  }

  private List<ComponentSchema> updateComponentSchemaList(
      List<ComponentSchema> list, Map<String, String> mapping) {
    if (list == null) {
      return null;
    }
    List<ComponentSchema> updatedList = new ArrayList<>();
    for (ComponentSchema cs : list) {
      updatedList.add(updateComponentSchemaReference(cs, mapping));
    }
    return updatedList;
  }

  private Message updateMessageReference(Message msg, Map<String, String> mapping) {
    if (msg instanceof MessageReference reference) {
      String ref = reference.getRef();
      if (ref != null && mapping.containsKey(ref)) {
        return new MessageReference(mapping.get(ref));
      }
    }
    return msg;
  }

  private void updateChannelReference(ChannelReference ref, Map<String, String> mapping) {
    if (ref != null) {
      String refStr = ref.getRef();
      if (refStr != null && mapping.containsKey(refStr)) {
        ref.setRef(mapping.get(refStr));
      }
    }
  }

  private String extractMethodName(String operationId) {
    String[] parts = operationId.split("_receive_");
    if (parts.length > 1) {
      return parts[1];
    }
    return operationId;
  }

  private String extractSimpleName(String fullName) {
    if (fullName.contains(".")) {
      return fullName.substring(fullName.lastIndexOf(".") + 1);
    }
    return fullName;
  }

  private String getActiveProfile() {
    String[] profiles = environment.getActiveProfiles();
    return (profiles.length > 0) ? profiles[0] : "local";
  }

  private String getServerUrl(String activeProfile) {
    return switch (activeProfile) {
      default -> "ws://localhost:8080/ws";
    };
  }

  private String getServerDescription(String activeProfile) {
    return switch (activeProfile) {
      case "prod" -> "Production WebSocket server for real-time chat and presence updates";
      case "dev" -> "Development WebSocket server for testing";
      default -> "Local WebSocket server for development and testing";
    };
  }

  private String extractHost(String serverUrl) {
    return serverUrl.replaceAll("^wss?://", "").replaceAll("/.*", "").split(":")[0];
  }

  private String extractPort(String serverUrl) {
    String[] parts = serverUrl.replaceAll("^wss?://", "").split("/")[0].split(":");
    if (parts.length > 1) {
      return parts[1];
    }
    return serverUrl.startsWith("wss") ? "443" : "80";
  }
}
