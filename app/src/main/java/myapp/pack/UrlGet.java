package myapp.pack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class urlAddress {
    private Integer size;
    private String startDate;
    private String endDate;
    private String words;
    private String categories;
    private String url;

    urlAddress(Integer size_, String startDate_, String endDate_,
               String words_, String categories_) {
        this.size = size_;
        this.startDate = startDate_;
        this.endDate = endDate_;
        this.words = getURLEncoderString(words_);
        this.categories = getURLEncoderString(categories_);
    }

    private String getURLEncoderString(String str) {
        if (str == null) return "";
        String result = "";
        try {
            result = java.net.URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;

    }

    private boolean setArgument(String str, String arg, boolean flag) {
        if (arg == null) return false;
        if (flag) url = url + "&";
        else url = url + "?";
        url = url + str + arg;
        return true;
    }

    public String getAddress() {
        boolean firstFlag = false;
        url = "https://api2.newsminer.net/svc/news/queryNewsList";
        firstFlag |= setArgument("size=", String.valueOf(size), firstFlag);
        firstFlag |= setArgument("startdate=", startDate, firstFlag);
        firstFlag |= setArgument("enddate=", endDate, firstFlag);
        firstFlag |= setArgument("words=", words, firstFlag);
        firstFlag |= setArgument("categories=", categories, firstFlag);
        return url;
    }
}

class UrlGet {
    public static final int PARSESUCCESS = 0x2001;
    private String url;
    private Handler handler;
    private int size;

    public UrlGet(Handler handler) {
        this.handler = handler;
    }

    public void urlSet(String str) {
        this.url = str;
    }

    public void getJsonFromInternet(int size_) {
        size = size_;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        List<News> listNews = parseJson(is);
                        Message msg = new Message();
                        msg.what = PARSESUCCESS;
                        msg.obj = listNews;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected List<News> parseJson(InputStream inputStream) {
        List<News> listNews = new ArrayList<>();
        byte[] jsonBytes = convertIsToByteArray(inputStream);
        String json = new String(jsonBytes);
        try {
            JSONObject summary = new JSONObject(json);
            String data = summary.getString("data");
            JSONArray jsonArray = new JSONArray(data);
            int st, ed;
            int len = jsonArray.length();
            if(size == -1 || size > len){
                st = 0;
                ed = len;
            }else{
                st = len-size;
                ed = len;
            }
            for (int i = st; i < ed; ++i){
                JSONObject jObject = jsonArray.getJSONObject(i);
                String title = jObject.getString("title");
                String content = jObject.getString("content");
                String date = jObject.getString("publishTime");
                String imageUrl = jObject.getString("image");
                String source = jObject.getString("publisher");
                String newsId = jObject.getString("newsID");
                News tmp = new News(title, content, date, source, newsId, imageUrl);
                //Log.d("content", content);
                listNews.add(tmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listNews;
    }

    private byte[] convertIsToByteArray(InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            inputStream.close();
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}