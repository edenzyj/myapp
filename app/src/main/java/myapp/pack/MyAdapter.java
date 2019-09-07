package myapp.pack;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import myapp.pack.MainActivity;
import myapp.pack.Mynews;
import myapp.pack.News;
import myapp.pack.UrlGet;
import myapp.pack.urlAddress;

public class MyAdapter extends BaseAdapter {
    private String word = "";
    private ArrayList<Mynews> list = null;
    private LayoutInflater layoutInflater = null;
    private Context context = null;
    private AppCompatActivity ma;
    private int size = 8;
    private String curKey = null;
    private String curCat = null;


    public MyAdapter(Context _context, AppCompatActivity _ma) {
        super();
        ma = _ma;
        context = _context;
        layoutInflater = LayoutInflater.from(context);
        list = new ArrayList<Mynews>();
    }

    protected void initData(List<News> listNews) {
        Toast.makeText(ma, "获得新闻" + listNews.size() + "条", Toast.LENGTH_SHORT).show();
        list.clear();
        for (News news : listNews) {
            Mynews mynews = new Mynews(layoutInflater, context);
            mynews.set(news);
            list.add(mynews);
        }
        notifyDataSetChanged();
    }

    void update(String key, String cat, Handler mHandler) {
        size = 8;
        curKey = key;
        curCat = cat;
        UrlGet askJson = new UrlGet(mHandler);
        urlAddress add = new urlAddress(size, "2000-01-01", "2020-12-31", key, cat);
        askJson.urlSet(add.getAddress());
        askJson.getJsonFromInternet(-1);
    }

    void refresh(int size_, Handler mHandler){
        size += 8;
        UrlGet askJson = new UrlGet(mHandler);
        urlAddress add = new urlAddress(size, "2000-01-01", "2020-12-31", curKey, curCat);
        askJson.urlSet(add.getAddress());
        askJson.getJsonFromInternet(size_);
    }


    void setString(String _word) {
        word = _word;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            return list.get(i).toView();
        } catch(Exception e) {
            return null;
        }
    }
}