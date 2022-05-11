package com.example.stockm;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UrlRequest {
    public static StringBuilder GetReq(String URLs) throws IOException {
        URL url = new URL(URLs);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            StringBuilder informationString = new StringBuilder();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                scanner.close();
            }
            System.out.println(informationString);
            return informationString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            conn.disconnect();
        }
        return null;
    };

}
