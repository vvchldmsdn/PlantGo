package com.ssafy.plantgo.util;

import com.amazonaws.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@SpringBootTest
public class NewApiTest {
    /*
     *
     * org.json libraries are required, java standard library can not handle Json.
     * Maven : https://mvnrepository.com/artifact/org.json/json
     * Please put this in a package to compile.
     *
     */

    private static String base64EncodeFromFile(String fileString) throws Exception {
        File file = new File(fileString);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = IOUtils.toByteArray(fis);
        String res = Base64.getEncoder().encodeToString(bytes);
        fis.close();
        return res;
    }

    public static String sendPostRequest(String urlString, JSONObject data) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes());
        os.close();

        InputStream is = con.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);
        String response = new String(bytes);

        System.out.println("Response code : " + con.getResponseCode());
        System.out.println("Response : " + response);
        con.disconnect();
        return response;
    }

    @Test
    public static void main(String[] args) throws Exception {

        String apiKey = "XCKZNhKQEmswDX98LCbXAjVNHdATgoyQ5pJaGz5i3tBbYAbvnf";

        // read image from local file system and encode
//        String [] flowers = new String[] {"../img/photo1.jpg", "../img/photo2.jpg", "../img/photo3.jpg"};


        JSONObject data = new JSONObject();
        data.put("api_key", apiKey);

        // add images
        JSONArray images = new JSONArray();
//        for(String filename : flowers) {
//            String fileData = base64EncodeFromFile(filename);
//            images.put(fileData);
//        }
        images.put("data:image/jpeg;base64,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");
        data.put("images", images);

        // add modifiers
        // modifiers info: https://github.com/flowerchecker/Plant-id-API/wiki/Modifiers
        JSONArray modifiers = new JSONArray()
                .put("crops_fast")
                .put("similar_images");
        data.put("modifiers", modifiers);

        // add language
        data.put("plant_language", "en");

        // add plant details
        // more info here: https://github.com/flowerchecker/Plant-id-API/wiki/Plant-details
        JSONArray plantDetails = new JSONArray()
                .put("common_names");
        data.put("plant_details", plantDetails);

        System.out.println(sendPostRequest("https://api.plant.id/v2/identify", data));
    }
}
