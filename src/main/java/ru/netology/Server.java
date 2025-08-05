package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    public static final int MAX_THREADS = 64;
    public static final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
    private Map<String, Handler> mapGets = new ConcurrentHashMap<>();
    private Map<String, Handler> mapPosts = new ConcurrentHashMap<>();


    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }


    private static void anyRequest(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);

        if (!validPaths.contains(request.getPath())) {
            badRequest(out);
            return;
        }

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }


    private void clientHandler(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            final int limit = 4096;
            in.mark(limit);
            while (!in.ready()) ;
            char[] cbuf = new char[limit];
            int numChars = in.read(cbuf);
//            System.out.println(String.copyValueOf(cbuf, 0, numChars));

            Request request = HttpRequestParser.parseHttpRequest(String.copyValueOf(cbuf, 0, numChars));
            System.out.println("-------------------");
            System.out.println(request);
            System.out.println("===================");

            Handler handler = null;
            if (request.getMethod().equals("GET")) {
                handler = mapGets.get(request.getPath());
            } else if (request.getMethod().equals("POST")) {
                handler = mapPosts.get(request.getPath());
            } else {
                badRequest(out);
                return;
            }

            if (handler != null) {
                handler.handle(request, out);
            } else {
                anyRequest(request, out);
//                    badRequest(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                pool.submit(() -> clientHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void addHandler(String method, String path, Handler handler) throws IllegalArgumentException {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path error!");
        }
        if (method.equals("GET")) {
            mapGets.put(path, handler);
        } else if (method.equals("POST")) {
            mapPosts.put(path, handler);
        } else {
            throw new IllegalArgumentException("Method name error!");
        }
    }


    Path getFilePath(String path) {
        return Path.of(".", "public", path);
    }


    String getMimeType(String path) throws IOException {
        return Files.probeContentType(getFilePath(path));
    }


    void basicConfig() {
        addHandler("GET", "/classic.html",
                new Handler() {
                    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                        final var template = Files.readString(getFilePath(request.getPath()));
                        final var content = template.replace(
                                "{time}",
                                LocalDateTime.now().toString()
                        ).getBytes();
                        responseStream.write((
                                "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: " + getMimeType(request.getPath()) + "\r\n" +
                                        "Content-Length: " + content.length + "\r\n" +
                                        "Connection: close\r\n" +
                                        "\r\n"
                        ).getBytes());
                        responseStream.write(content);
                        responseStream.flush();
                    }
                }
        );

    }

}
