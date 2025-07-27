package com.panda.flash_file_downloader.utils.network;

import com.panda.flash_file_downloader.utils.yt_dlp.YtDlpFormatFetcherJson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class LocalDownloadServer {

    public static void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/download", LocalDownloadServer::handleDownloadRequest);
        server.createContext("/formats", LocalDownloadServer::handleFormatRequest);
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Download server running at http://localhost:" + port);
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void handleDownloadRequest(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(204, -1); // No content for OPTIONS
            exchange.close();
            return;
        }

        addCorsHeaders(exchange);

        String response = DownloadRequestHandler.processRequest(exchange);
        byte[] responseBytes = response.getBytes();
        int statusCode = response.startsWith("Invalid") ? 400 : 200;

        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static void handleFormatRequest(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        addCorsHeaders(exchange);

        // Get the `url` parameter from query
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery(); // url=https://youtube.com/watch?v=xxx
        Map<String, String> params = ObjectUtil.queryToMap(query);
        String videoUrl = params.get("url");

        if (videoUrl == null || videoUrl.isEmpty()) {
            String response = "Missing 'url' query parameter";
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(400, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
            return;
        }

        List<YtDlpFormatFetcherJson.Format> formats = YtDlpFormatFetcherJson.getFormats(videoUrl).values().stream()
                .filter(format -> !format.getFilesize().equalsIgnoreCase("unknown")).toList();

        // Convert to JSON
        String json = ObjectUtil.toJson(formats);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = json.getBytes();
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
