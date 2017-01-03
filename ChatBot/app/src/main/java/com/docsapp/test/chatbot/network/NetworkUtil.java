package com.docsapp.test.chatbot.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.support.annotation.NonNull;
import android.util.Log;
import com.docsapp.test.chatbot.model.ChatMessage;
import com.docsapp.test.chatbot.model.Response;
import com.google.gson.Gson;

/**
 * Utility Class for handling Network request-response
 */
public class NetworkUtil {

    private static final String LOG_TAG = NetworkUtil.class.getSimpleName();

    private static final String SEND_MESSAGE_BASE_URL =
            "http://www.personalityforge.com/api/chat/?apiKey=6nt5d1nJHkqbkphe&chatBotID=63906&externalID" +
                    "=chirag1&message=";

    private NetworkUtil() {

    }

    /**
     * Send the current message to server and get the response
     *
     * @param pChatMessage The ChatMessage Object, containing the message to be sent
     * @return Response of the message if successful
     */
    public static Response sendMessage(@NonNull ChatMessage pChatMessage) {
        URL url;
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        Response response = null;
        try {
            url = new URL(SEND_MESSAGE_BASE_URL + URLEncoder.encode(pChatMessage.getMessage(), "UTF-8"));
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder sb = new StringBuilder();
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
            Gson gson = new Gson();
            response = gson.fromJson(sb.toString(), Response.class);
        }
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        return response;
    }
}
