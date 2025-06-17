package com.example;

import java.io.*;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.*;

public class RajiniApp {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve HTML
        server.createContext("/", exchange -> {
            String html = """
                <html>
                  <head><title>Rajinikanth</title></head>
                  <body style='text-align:center; font-family:sans-serif;'>
                    <h1>போடா அண்டவனே நம்ம பக்கம் இருக்கான்</h1>
                    <img src='/rajini.jpg' width='400'/>
                  </body>
                </html>
            """;
            exchange.sendResponseHeaders(200, html.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        });

        // Serve image
        server.createContext("/rajini.jpg", exchange -> {
            File file = new File("static/rajini.jpg");
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes);
            fis.close();

            exchange.getResponseHeaders().add("Content-Type", "image/jpeg");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        });

        server.start();
        System.out.println("Server started on port 8080");
    }
}

