package com.diandian.coolco.emilie.utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetHelper {

    private final static String TAG = NetHelper.class.getName();

    public static String sendRequest(String actionUrl, JSONObject jsonObject,
                                     String imageName, Bitmap bitmap) {
        List<String> imageNames = new ArrayList<>();
        imageNames.add(imageName);
        List<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(bitmap);
        return sendRequest(actionUrl, jsonObject, imageNames, bitmaps);
    }

    public static String sendRequest(String actionUrl, JSONObject jsonObject,
                                     List<String> imageNames, List<Bitmap> bitmaps) {
        //Log.v("sendreq","req");
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        HttpURLConnection con = null;
        DataOutputStream ds = null;
        InputStream is = null;
        StringBuffer sb = null;

        if (jsonObject == null){
            jsonObject = new JSONObject();
        }

        StringBuilder jsonSB = new StringBuilder();
        jsonSB.append(twoHyphens).append(boundary).append(end);
        jsonSB.append("Content-Disposition:form-data;name=\"json\"")
                .append(end).append(end);
        jsonSB.append(jsonObject.toString()).append(end);
        jsonSB.append(twoHyphens).append(boundary).append(twoHyphens)
                .append(end);

        try {
            URL url = new URL(actionUrl);
            con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

			/* 设置传送的method=POST */
            con.setRequestMethod("POST");
			/* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            ds = new DataOutputStream(con.getOutputStream());
            ds.write(jsonSB.toString().getBytes());

            if (imageNames != null && bitmaps != null) {
                for (int i = 0; i < imageNames.size(); i++) {
                    String picName = imageNames.get(i);
                    Bitmap bitmap = bitmaps.get(i);

                    ds.writeBytes(twoHyphens + boundary + end);
                    ds.writeBytes("Content-Disposition: form-data; "
                            + "name=\"upload_image\";filename=\"" + picName + "\""
                            + end);
                    ds.writeBytes(end);
                    ds.write(bitmap2Bytes(bitmap));//compress
                    ds.writeBytes(end);
                    ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
                }
            }

            ds.flush();//post image stream

			/* 取得Response内容 */
            int responseCode = con.getResponseCode();
            Log.e(TAG, "responseCode = " + responseCode);
            if (con.getResponseCode() == 200) {
                is = con.getInputStream();

                int ch;
                sb = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                ds.close();
                String responseString = sb.toString();
                Log.e(TAG, "responseString = " + responseString);
                return responseString;
            }

        } catch (MalformedURLException e) {
            Log.e("json", "url error");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("json", "file not found");
        } catch (IOException e) {
            Log.e("json", "io error");
            e.printStackTrace();
        } finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String request(HttpClient client, String url,
                                 String reqJsonStr) {
        try {
            HttpPost post = new HttpPost(new URI(url));
            post.setEntity(new StringEntity(reqJsonStr));
            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if (200 == status) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                } else {
                    Log.e(TAG, "response.getEntity() return null");
                    return null;
                }
            } else {
                Log.e(TAG, "response.getStatusLine().getStatusCode() = "
                        + status);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "exception " + e.toString());
            return null;
        }
    }

    public static boolean isNetConnected(Context context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
            return !(networkinfo == null || !networkinfo.isAvailable());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
//        int maxByteCount = 256*1024;
//        int originByteCount = bm.getByteCount();
//        int quality = (int) Math.min(((float) maxByteCount) / originByteCount * 100, 100);//error!!!quality not equals bits rate
        //Log.d("size",bm.getByteCount()/1024+"");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality=105;
        do{
            baos.reset();
            quality-= 5;
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
//            Log.d("quality", quality + "");
        }while(baos.toByteArray().length / 1024 > 100);//make uploaded image <100kB
        return baos.toByteArray();
    }
}
