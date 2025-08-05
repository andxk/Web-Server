package ru.netology;

import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    public Request(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private String mapToString(Map<String, String> map) {
        String s = map.keySet().stream()
                .map(key -> key + " = " + map.get(key).trim())
                .collect(Collectors.joining("\n    "));
        return s;
    }

    @Override
    public String toString() {
        return "Request {\n" +
                "  method = '" + method + "\'\n" +
                "  path = '" + path + "\'\n" +
                "  version = '" + version + "\'\n" +
                "  headers:\n    " + mapToString(headers) + "\n" +
                "  body = '" + body + "\'\n" +
                "}";
    }

}


