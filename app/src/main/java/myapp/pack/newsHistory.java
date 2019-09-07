package myapp.pack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class newsHistory extends AppCompatActivity {
    private MyAdapter ma;
    private Button btn;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x134:
                    ArrayList<News> listNews = (ArrayList<News>)msg.obj;
                    ma.initData(listNews);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newshistory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lv = findViewById(R.id.listv);
        ma = new MyAdapter(newsHistory.this, newsHistory.this);
        lv.setAdapter(ma);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Mynews.setnowNews((Mynews) view.getTag());
                Toast.makeText(newsHistory.this, "现在是第"+i+"条新闻", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(newsHistory.this, NewsPage.class);
                startActivity(intent);
            }
        });

        btn = findViewById(R.id.CollectionButton);
        btn.setText("刷新列表");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("debug", "click!");
                newsHistoryReader.getInstance().getNewsList(mHandler);
            }
        });

        newsHistoryReader.getInstance().getNewsList(mHandler);


    }
}
