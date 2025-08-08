package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpRequestParser {

    public static Request parseHttpRequest(String requestData) {
        String[] lines = requestData.split("\r\n");

        String firstLine = lines[0].trim();
        if (firstLine.split(" ").length != 3) return null;

        String method = extractMethod(firstLine);
        String path = extractPath(firstLine);
        if (!path.startsWith("/")) return null;

        //Query string parse
        List<String> paramList = extractParams(path);
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        System.out.println(path);
        System.out.println("Params: " + paramList);
        //---

        String version = extractVersion(firstLine);
        Map<String, String> headers = extractHeaders(lines);
        String body = extractBody(lines);

        System.out.println("Post params: " + extractParams(body));

        return new Request(method, path, version, headers, body, paramList);
    }


    public static List<String> extractParams(String text) {
        List<String> paramList = new ArrayList<>();
        if (text.isEmpty()) return paramList;

        if (!text.contains("?") && !text.startsWith("/")) {
            text = "?"+text;
        }

        List<NameValuePair> params = null;
        try {
            params = URLEncodedUtils.parse(new URI(text), "UTF-8");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("text = " + text);

        for (NameValuePair param : params) {
            String paramStr = param.getName() + "=" + param.getValue();
//                System.out.println(paramStr);
            paramList.add(paramStr);
        }

//        System.out.println(paramList);
        return paramList;
    }


    private static String extractMethod(String firstLine) {
        return firstLine.split("\\s+")[0];
    }

    private static String extractPath(String firstLine) {
        return firstLine.split("\\s+")[1];
    }

    private static String extractVersion(String firstLine) {
        return firstLine.split("\\s+")[2];
    }


    private static Map<String, String> extractHeaders(String[] lines) {
        Map<String, String> headers = new HashMap<>();
        boolean inHeaderSection = false;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                break;
            }
            if (!inHeaderSection && !line.contains(" HTTP")) {
                continue;
            }
            inHeaderSection = true;
            String[] parts = line.split(":\\s+", 2);
            if (parts.length >= 2) {
                headers.put(parts[0], parts[1]);
            }
        }
        return headers;
    }


    private static String extractBody(String[] lines) {
        StringBuilder body = new StringBuilder();
        boolean foundEmptyLine = false;
        for (String line : lines) {
            if (foundEmptyLine) {
                body.append(line).append("\n");
            } else if (line.trim().isEmpty()) {
                foundEmptyLine = true;
            }
        }
        return body.toString().trim();
    }
}
