package myapp.pack;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lv = null;
    private MyAdapter ma = null;
    private TextView search = null;
    private TabLayout myTab = null;
    private String curCat = null;
    private ImageButton iBtn = null;
    private List<News> listNews;
    private final String[] cat = {"娱乐","军事","教育","文化","健康","财经","体育","汽车","科技","社会"};
    private ArrayList<String>mySort;
    private ArrayList<String>otherSort;
    private RefreshLayout mRefreshLayout;
    private boolean loadLock = false;
    private boolean refreshLock = false;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UrlGet.PARSESUCCESS:
                    listNews = (List<News>) msg.obj;
                    ma.initData(listNews);
                    if(loadLock){
                        mRefreshLayout.finishLoadMore(true);
                        loadLock = false;
                    }
                    if(refreshLock){
                        mRefreshLayout.finishRefresh(true);
                        refreshLock = false;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageLoader();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, searchHistory.class);
                startActivityForResult(intent, 133);
            }
        });

        ListView lv = findViewById(R.id.listv);
        ma = new MyAdapter(this, this);
        lv.setAdapter(ma);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Mynews.setnowNews((Mynews) view.getTag());
                Toast.makeText(MainActivity.this, "现在是第"+i+"条新闻", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NewsPage.class);
                startActivity(intent);

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        myTab = findViewById(R.id.mytab);
        mySort = new ArrayList<String>();
        otherSort = new ArrayList<String>();

        getSortTab();

        //for(String catNow:cat)myTab.addTab(myTab.newTab().setText(catNow));
        for(String catNow:mySort)myTab.addTab(myTab.newTab().setText(catNow));

        myTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                curCat = tab.getText().toString();
                ma.update(null, curCat, mHandler);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){}

            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });

        myTab.setTabMode(TabLayout.MODE_SCROLLABLE);

        iBtn = findViewById(R.id.imageButton);
        iBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, sortNews.class);
                intent.putStringArrayListExtra("mySort", mySort);
                intent.putStringArrayListExtra("otherSort", otherSort);
                startActivityForResult(intent, 1000);
            }
        });

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() { //下拉刷新
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLock = true;
                ma.refresh(8, mHandler);
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() { //上拉加载更多
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                loadLock = true;
                ma.refresh(-1, mHandler);
            }
        });

        initNewsCollection();
        newsFileReader.getInstance().loadNewsId(getExternalFilesDir("collection").getPath());

        initNewsHistory();
        newsHistoryReader.getInstance().loadNewsId(getExternalFilesDir("history").getPath());

        initSearch();
        searchFileReader.getInstance().loadSearch(getExternalFilesDir("search").getPath());

        ma.update(null, curCat, mHandler);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        /*btn_collections.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, newsCollection.class);
                startActivity(intent);
                return false;
            }
        });*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, newsCollection.class);
            startActivityForResult(intent, 521);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MainActivity.this, newsHistory.class);
            startActivity(intent);

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

            String str = "myapp";
            ArrayList uri = new ArrayList();
            ShareToSCA.shareQQContentUrl(MainActivity.this, uri, str);

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1001) {
            mySort = (ArrayList<String>)data.getStringArrayListExtra("mySort");
            otherSort = (ArrayList<String>)data.getStringArrayListExtra("otherSort");
            myTab.removeAllTabs();
            for(String catNow:mySort)myTab.addTab(myTab.newTab().setText(catNow));
            writeSortTab();
        }
        if(requestCode == 133 && resultCode == 134){
            String keyWord = data.getStringExtra("search");
            ma.update(keyWord, curCat, mHandler);
            searchFileReader.getInstance().getInsert(" "+keyWord+" ");
        }
    }

    private void initImageLoader() {
            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                    .Builder(getApplicationContext())
                    .defaultDisplayImageOptions(options())
                    .build();
            ImageLoader.getInstance().init(configuration);
     }

     private DisplayImageOptions options() {
         DisplayImageOptions options = new DisplayImageOptions
                 .Builder()
                 .cacheOnDisk(true)
                 .cacheInMemory(true)
                 .displayer(new SimpleBitmapDisplayer())
                 .build();
         return options;
     }

     private void getSortTab(){
         File file = getExternalFilesDir("sort");
         if(!file.exists())file.mkdir();
         String path = file.getPath()+"/mySort.txt";
         String otherPath = file.getPath()+"/otherSort.txt";
         File sortFile = new File(path);
         File otherSortFile = new File(otherPath);
         if(!sortFile.exists()){
             try {
                 sortFile.createNewFile();
                 otherSortFile.createNewFile();
                 FileWriter fw=new FileWriter(sortFile,false);
                 BufferedWriter bw=new BufferedWriter(fw);
                 for(String tmp:cat)bw.write(tmp+"\n");
                 bw.close();
                 fw.close();
             }catch(Exception e){
                 e.printStackTrace();
             }
         }
         try {
             FileReader fr = new FileReader(sortFile);
             BufferedReader br = new BufferedReader(fr);
             FileReader otherFr = new FileReader(otherSortFile);
             BufferedReader otherBr = new BufferedReader(otherFr);
             String tmp;
             while((tmp = br.readLine()) != null)mySort.add(tmp);
             while((tmp = otherBr.readLine()) != null)otherSort.add(tmp);
             br.close(); otherBr.close();
             fr.close(); otherFr.close();
         }catch (Exception e){
             e.printStackTrace();
         }
     }

     private void writeSortTab(){
         File file = getExternalFilesDir("sort");
         String path = file.getPath()+"/mySort.txt";
         String otherPath = file.getPath()+"/otherSort.txt";
         File sortFile = new File(path);
         File otherSortFile = new File(otherPath);
         try{
             FileWriter fw=new FileWriter(sortFile,false);
             BufferedWriter bw=new BufferedWriter(fw);
             FileWriter otherFw = new FileWriter(otherSortFile, false);
             BufferedWriter otherBw = new BufferedWriter(otherFw);
             for(String tmp:mySort)bw.write(tmp+"\n");
             for(String tmp:otherSort)otherBw.write(tmp+"\n");
             bw.close(); otherBw.close();
             fw.close(); otherFw.close();
         }catch(Exception e){
             e.printStackTrace();
         }
     }

     private void initNewsCollection(){
        File file = getExternalFilesDir("collection");
        if(!file.exists())file.mkdir();
        String path = file.getPath();
        File newsId = new File(path+"/newsId.txt");
        if(!newsId.exists()) {
            try {
                newsId.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
     }

     private void initNewsHistory(){
        File file = getExternalFilesDir("history");
        if(!file.exists())file.mkdir();
        String path = file.getPath();
        File newsId = new File(path+"/newsId.txt");
        if(!newsId.exists()){
            try{
                newsId.createNewFile();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
     }

     private void initSearch(){
        File file = getExternalFilesDir("search");
        if(!file.exists())file.mkdir();
        String path = file.getPath();
        File search = new File(path+"/search.txt");
        if(!search.exists()){
            try{
                search.createNewFile();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
     }
}
