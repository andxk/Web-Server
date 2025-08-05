package ru.netology;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.basicConfig();
        server.start(9999);
    }
}


