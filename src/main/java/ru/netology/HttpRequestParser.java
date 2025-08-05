package ru.netology;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public static Request parseHttpRequest(String requestData) {
        String[] lines = requestData.split("\r\n");

        String firstLine = lines[0].trim();
        //todo проверить на кол-во частей ==3, если нет - вернуть нуль

        String method = extractMethod(firstLine);
        String path = extractPath(firstLine);
        //todo проверить начало с '/'

        String version = extractVersion(firstLine);
        Map<String, String> headers = extractHeaders(lines);
        String body = extractBody(lines);

        return new Request(method, path, version, headers, body);
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
