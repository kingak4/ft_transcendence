package code.shared.entrypoints;

import org.springframework.web.util.UriComponentsBuilder;

public final class UrlBuilderUtil {

  private UrlBuilderUtil() {}

  public static String buildUrl(String baseUrl, String endpoint, Object... uriVariables) {
    String path = baseUrl != null ? baseUrl : "";
    if (endpoint != null && !endpoint.isEmpty()) {
      if (!path.isEmpty() && !path.endsWith("/") && !endpoint.startsWith("/")) {
        path += "/";
      }
      path += endpoint;
    }
    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    return UriComponentsBuilder.fromUriString(path).buildAndExpand(uriVariables).toUriString();
  }
}