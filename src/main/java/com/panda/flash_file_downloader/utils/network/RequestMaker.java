package com.panda.flash_file_downloader.utils.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.flash_file_downloader.utils.youtube.Format;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class RequestMaker {
    private final static String SERVER_PORT = "12345";
    public static void makeYoutubeDownloadRequest(String videoUrl, String formatId) {
        try {

            // Build URL
            String endpoint = "http://localhost:"+SERVER_PORT+"/download?url=" + videoUrl + "&formatId=" + formatId;

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("GET");

            // Read response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Print response
            System.out.println("Response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void makeGeneralDownloadRequest(String downloadUrl) {
        try {
            // Server URL
            URL url = new URL("http://localhost:"+SERVER_PORT+"/intercepted-download");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setup POST method
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); // Enable writing body
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // JSON body
            String jsonInputString = "{\"url\": \""+downloadUrl+"\"}";

            // Write request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                System.out.println("Response: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Format> makeYoutubeFormatRequest(String videoUrl) throws Exception {
        String urlStr = "http://localhost:12345/formats?url=" + videoUrl;
        URL url = new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            responseBuilder.append(line);
        }
        reader.close();

        System.out.println("Builder: "+responseBuilder.toString());
        JSONArray jsonArray = new JSONArray(responseBuilder.toString());
        List<Format> formats = new LinkedList<>();
        System.out.println(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Format format = new Format();
            format.setFormatId(obj.getString("id"));
            format.setResolution(obj.getString("resolution"));
            format.setType(obj.getString("type"));
            format.setFilesize(obj.getString("size"));
            formats.add(format);
            System.out.println(format);
        }

        return formats;

    }
}
