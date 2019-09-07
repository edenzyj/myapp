package myapp.pack;

import android.os.Handler;
import android.os.Message;
import android.text.BoringLayout;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Deque;

public class searchFileReader {
    private static searchFileReader sfr;

    private HashMap<String, Boolean> map;
    private Deque<String> deque;
    private String basePath;
    private boolean fileLock;

    private searchFileReader(){
        map = new HashMap<String, Boolean>();
        deque = new ArrayDeque<String>();
        fileLock = false;
    }
    public static synchronized searchFileReader getInstance(){
        if(sfr == null){
            sfr = new searchFileReader();
        }return sfr;
    }

    public boolean getAsk(final String str){
        return map.containsKey(str);
    }

    public void getClear(){
        map.clear();
        deque.clear();
        saveSearch();
    }

    public void getInsert(final String str){
        if(map.containsKey(str))return;
        if(str == null)return;
        map.put(str, true);
        deque.addFirst(str);
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    while(fileLock);
                    fileLock = true;
                }
                saveSearch();
            }
        }).start();
        fileLock = false;
    }

    private void saveSearch(){
        String Path = basePath+"/"+"search.txt";
        File file = new File(Path);
        try{
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String tmp:deque){
                bw.write(tmp+"\n");
            }
            bw.flush();
            bw.close();
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadSearch(final String basePath_){
        basePath = basePath_;
        deque.clear();
        map.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    while(fileLock);
                    fileLock = true;
                }
                String Path = basePath+"/"+"search.txt";
                File file = new File(Path);
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String tmp;
                    while ((tmp = br.readLine()) != null) {
                        deque.addLast(tmp);
                        map.put(tmp, true);
                    }
                    br.close();
                    fr.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
        fileLock = false;
    }
    public ArrayList<String> getTagList(){
        ArrayList<String> listString = new ArrayList<String>();
        Log.d("size", String.valueOf(deque.size()));
        for(String tmp:deque)listString.add(tmp);
        return listString;

    }
}
