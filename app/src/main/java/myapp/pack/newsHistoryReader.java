package myapp.pack;

import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashMap;
import java.util.Deque;

import java.io.*;

public class newsHistoryReader {
    private static newsHistoryReader nhr;

    private HashMap<String, Boolean> newsIdMap;
    private Deque<String> deque;
    private String basePath;
    private boolean fileLock;
    private String strDel;

    private final int maxSize = 30;
    private int size = 0;

    private newsHistoryReader(){
        newsIdMap = new HashMap<String, Boolean>();
        deque = new ArrayDeque<String>();
        fileLock = false;
    }

    public static synchronized newsHistoryReader getInstance() {
        if (nhr == null) {
            nhr = new newsHistoryReader();
        }
        return nhr;
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

    /*下面是非通用方法*/

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
        Log.d("insert", news.newsId);
        if(newsIdMap.containsKey(news.newsId))return;
        newsIdMap.put(news.newsId, true);
        deque.addLast(news.newsId);
        ++size;
        if(size > maxSize){--size; strDel = deque.pop();}
        else strDel = null;

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
                if(strDel != null)getDelete(strDel);
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

            Stack<String> stack = new Stack<String>();
            for(String tmp:deque)stack.push(tmp);

            while(!stack.empty()){
                bw.write(stack.pop()+"\n");
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
        deque.clear();
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
                        deque.addLast(tmp);
                        size++;
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

    public void getNewsList(final Handler mHandler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("getNewsList", "run");
                synchronized (this){
                    while(fileLock);
                    fileLock = true;
                }
                ArrayList<News> listNews = new ArrayList<News>();
                Stack<String> stack = new Stack<String>();
                for(String tmp:deque)stack.push(tmp);

                while(!stack.empty()){
                    String newsId = stack.pop();
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
                msg.what = 0x134;
                msg.obj = listNews;
                mHandler.sendMessage(msg);
            }
        }).start();
        fileLock = false;
    }

}
