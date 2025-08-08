package ru.netology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;
    private List<String> queryParams = new ArrayList<>();

    public Request(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public Request(String method, String path, String version, Map<String, String> headers, String body,
                   List<String> queryParamList) {
        this(method, path, version, headers, body);
        queryParams = queryParamList.stream()
                .filter(s -> s.contains("="))
                .collect(Collectors.toList());
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


    public List<String> getQueryParams() {
        List<String> names = new ArrayList<>(queryParams.stream()
                .map(s -> s.substring(0, s.indexOf("=")))
                .collect(Collectors.toSet())
        );
        return names;
    }

    public List<String> getQueryParamsAndValues() {
        return queryParams;
    }

    public List<String> getQueryParam(String name) {
        List<String> result = queryParams.stream()
                .filter(s -> s.contains(name))
                .map(s -> s.substring(name.length()+1))
                .collect(Collectors.toList());
        return result;
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


