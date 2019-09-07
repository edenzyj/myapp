package myapp.pack;

import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.*;

public class newsFileReader {
    private static newsFileReader nfr;

    private HashMap<String, Boolean> newsIdMap;
    private String basePath;
    private boolean fileLock;

    private newsFileReader(){
        newsIdMap = new HashMap<String, Boolean>();
        fileLock = false;
    }
    public static synchronized newsFileReader getInstance() {
        if (nfr == null) {
            nfr = new newsFileReader();
        }
        return nfr;
    }

    public boolean getAsk(final String str){
        return newsIdMap.containsKey(str);
    }

    private void getWrite(File file, String str){
        try {
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(file, false);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(str);
                bw.flush();
                bw.close();
                fw.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void delAllFile(String str){
        File file = new File(str);
        if(!file.exists())return;
        if(!file.isDirectory())return;
        String[] tempList = file.list();
        File temp = null;
        for(int i = 0; i < tempList.length; ++i){
            temp = new File(str+"/"+tempList[i]);
            temp.delete();
        }return;
    }

    public void getDelete(final String str){
        newsIdMap.remove(str);
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    while(fileLock);
                    fileLock = true;
                }
                String newsIdPath = basePath+"/"+str;
                try {
                    delAllFile(newsIdPath);
                    File file = new File(newsIdPath);
                    file.delete();

                }catch (Exception e){
                    e.printStackTrace();
                }
                saveNewsId();

            }
        }).start();
        fileLock = false;
    }

    public void getInsert(final News news){
        newsIdMap.put(news.newsId, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    while(fileLock);
                    fileLock = true;
                }
                String newsIdPath = basePath+"/"+news.newsId;
                File file = new File(newsIdPath);
                if(!file.exists()){
                    file.mkdir();
                    File titleFile = new File(newsIdPath+"/title.txt");
                    File contentFile = new File(newsIdPath+"/content.txt");
                    File dateFile = new File(newsIdPath+"/dateFile");
                    File sourceFile = new File(newsIdPath+"/sourceFile");
                    getWrite(titleFile, news.title);
                    getWrite(contentFile, news.content);
                    getWrite(dateFile, news.date);
                    getWrite(sourceFile, news.source);
                }
                saveNewsId();
            }
        }).start();
        fileLock = false;
    }

    private void saveNewsId(){
        String newsIdPath = basePath+"/newsId.txt";
        File newsIdFile = new File(newsIdPath);
        try {
            FileWriter fw = new FileWriter(newsIdFile);
            BufferedWriter bw = new BufferedWriter(fw);

            for(String tmp:newsIdMap.keySet()){
                bw.write(tmp+"\n");
            }

            bw.flush();
            bw.close();
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadNewsId(final String basePath_){
        basePath = basePath_;
        newsIdMap.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    while(fileLock);
                    fileLock = true;
                }
                String newsIdPath = basePath+"/newsId.txt";
                File newsIdFile = new File(newsIdPath);
                try {
                    FileReader fr = new FileReader(newsIdFile);
                    BufferedReader br = new BufferedReader(fr);
                    String tmp;
                    while ((tmp = br.readLine()) != null){
                        newsIdMap.put(tmp, true);
                    }
                    br.close();
                    fr.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        fileLock = false;
    }

    public String getRead(File file){
        StringBuilder ans = new StringBuilder();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String tmp;
            while((tmp = br.readLine()) != null) {
                ans.append(tmp);
            }
            br.close();
            fr.close();
        }catch (Exception e){
            e.printStackTrace();
        }return ans.toString();
    }

    public void getNewsList(final Handler mHandler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    while(fileLock);
                    fileLock = true;
                }
                ArrayList<News> listNews = new ArrayList<News>();
                for(String newsId:newsIdMap.keySet()){
                    String newsIdPath = basePath+"/"+newsId;
                    try{
                        File titleFile = new File(newsIdPath + "/title.txt");
                        File contentFile = new File(newsIdPath + "/content.txt");
                        File dateFile = new File(newsIdPath + "/dateFile");
                        File sourceFile = new File(newsIdPath + "/sourceFile");
                        String title = getRead(titleFile);
                        String content = getRead(contentFile);
                        String date = getRead(dateFile);
                        String source = getRead(sourceFile);
                        News tmp = new News(title, content, date, source, newsId, null);
                        listNews.add(tmp);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                Message msg = new Message();
                msg.what = 0x521;
                msg.obj = listNews;
                mHandler.sendMessage(msg);
            }
        }).start();
        fileLock = false;
    }

}
