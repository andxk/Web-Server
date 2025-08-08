package ru.netology;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;


public class HttpRequestParser {

    public static Request parseHttpRequest(String requestData) {
        String[] lines = requestData.split("\r\n");

        String firstLine = lines[0].trim();
        if (firstLine.split(" ").length != 3) return null;

        String method = extractMethod(firstLine);
        String path = extractPath(firstLine);
        if (!path.startsWith("/")) return null;

        //Query string parse
        List<String> paramList = new ArrayList<>();
        try {
//            System.out.println(path);
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(path), "UTF-8");

            if (path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }
            System.out.println(path);

            for (NameValuePair param : params) {
                String paramStr = param.getName() + "=" + param.getValue();
//                System.out.println(paramStr);
                paramList.add(paramStr);
            }

            System.out.println(paramList);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        //---

        String version = extractVersion(firstLine);
        Map<String, String> headers = extractHeaders(lines);
        String body = extractBody(lines);

        return new Request(method, path, version, headers, body, paramList);
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
