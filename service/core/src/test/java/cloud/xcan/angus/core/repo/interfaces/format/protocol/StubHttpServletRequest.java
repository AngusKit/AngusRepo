package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Stub implementation of {@link HttpServletRequest} for unit testing protocol controllers.
 * Provides configurable method, URI, body, and query parameters.
 */
class StubHttpServletRequest implements HttpServletRequest {

  private final String method;
  private final String requestURI;
  private final byte[] body;
  private Map<String, String> parameters = new HashMap<>();
  private Map<String, String> headers = new HashMap<>();
  private String scheme = "http";
  private String serverName = "localhost";
  private int serverPort = 8080;
  private String contentType;

  StubHttpServletRequest(String method, String requestURI, byte[] body) {
    this.method = method;
    this.requestURI = requestURI;
    this.body = body;
  }

  void setParameters(Map<String, String> params) {
    this.parameters = new HashMap<>(params);
  }

  void setHeaders(Map<String, String> headers) {
    this.headers = new HashMap<>(headers);
  }

  void setScheme(String scheme) {
    this.scheme = scheme;
  }

  void setServerName(String serverName) {
    this.serverName = serverName;
  }

  void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getRequestURI() {
    return requestURI;
  }

  @Override
  public String getParameter(String name) {
    return parameters.get(name);
  }

  @Override
  public String getHeader(String name) {
    return headers.get(name);
  }

  @Override
  public String getScheme() {
    return scheme;
  }

  @Override
  public String getServerName() {
    return serverName;
  }

  @Override
  public int getServerPort() {
    return serverPort;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    byte[] data = body != null ? body : new byte[0];
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return bais.available() == 0;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
      }

      @Override
      public int read() throws IOException {
        return bais.read();
      }
    };
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
  }

  // ===== Required but unused HttpServletRequest methods =====

  @Override public String getAuthType() { return null; }
  @Override public Cookie[] getCookies() { return new Cookie[0]; }
  @Override public long getDateHeader(String name) { return -1; }
  @Override public Enumeration<String> getHeaders(String name) { return Collections.emptyEnumeration(); }
  @Override public Enumeration<String> getHeaderNames() { return Collections.emptyEnumeration(); }
  @Override public int getIntHeader(String name) { return -1; }
  @Override public String getPathInfo() { return null; }
  @Override public String getPathTranslated() { return null; }
  @Override public String getContextPath() { return ""; }
  @Override public String getQueryString() { return null; }
  @Override public String getRemoteUser() { return null; }
  @Override public boolean isUserInRole(String role) { return false; }
  @Override public Principal getUserPrincipal() { return null; }
  @Override public String getRequestedSessionId() { return null; }
  @Override public StringBuffer getRequestURL() { return new StringBuffer(scheme + "://" + serverName + ":" + serverPort + requestURI); }
  @Override public String getServletPath() { return requestURI; }
  @Override public HttpSession getSession(boolean create) { return null; }
  @Override public HttpSession getSession() { return null; }
  @Override public String changeSessionId() { return null; }
  @Override public boolean isRequestedSessionIdValid() { return false; }
  @Override public boolean isRequestedSessionIdFromCookie() { return false; }
  @Override public boolean isRequestedSessionIdFromURL() { return false; }
  @Override public boolean authenticate(HttpServletResponse response) { return false; }
  @Override public void login(String username, String password) { }
  @Override public void logout() { }
  @Override public Collection<Part> getParts() { return Collections.emptyList(); }
  @Override public Part getPart(String name) { return null; }
  @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }
  @Override public Object getAttribute(String name) { return null; }
  @Override public Enumeration<String> getAttributeNames() { return Collections.emptyEnumeration(); }
  @Override public String getCharacterEncoding() { return "UTF-8"; }
  @Override public void setCharacterEncoding(String env) { }
  @Override public int getContentLength() { return body != null ? body.length : 0; }
  @Override public long getContentLengthLong() { return body != null ? body.length : 0; }
  @Override public Enumeration<String> getParameterNames() { return Collections.enumeration(parameters.keySet()); }
  @Override public String[] getParameterValues(String name) { String v = parameters.get(name); return v != null ? new String[]{v} : null; }
  @Override public Map<String, String[]> getParameterMap() { Map<String, String[]> m = new HashMap<>(); parameters.forEach((k, v) -> m.put(k, new String[]{v})); return m; }
  @Override public String getProtocol() { return "HTTP/1.1"; }
  @Override public String getRemoteAddr() { return "127.0.0.1"; }
  @Override public String getRemoteHost() { return "localhost"; }
  @Override public void setAttribute(String name, Object o) { }
  @Override public void removeAttribute(String name) { }
  @Override public Locale getLocale() { return Locale.getDefault(); }
  @Override public Enumeration<Locale> getLocales() { return Collections.enumeration(Collections.singletonList(Locale.getDefault())); }
  @Override public boolean isSecure() { return "https".equals(scheme); }
  @Override public RequestDispatcher getRequestDispatcher(String path) { return null; }
  @Override public int getRemotePort() { return 0; }
  @Override public String getLocalName() { return "localhost"; }
  @Override public String getLocalAddr() { return "127.0.0.1"; }
  @Override public int getLocalPort() { return serverPort; }
  @Override public ServletContext getServletContext() { return null; }
  @Override public AsyncContext startAsync() throws IllegalStateException { return null; }
  @Override public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) { return null; }
  @Override public boolean isAsyncStarted() { return false; }
  @Override public boolean isAsyncSupported() { return false; }
  @Override public AsyncContext getAsyncContext() { return null; }
  @Override public DispatcherType getDispatcherType() { return DispatcherType.REQUEST; }
  @Override public String getRequestId() { return "test-request-id"; }
  @Override public String getProtocolRequestId() { return "test-protocol-request-id"; }
  @Override public ServletConnection getServletConnection() { return null; }
}
