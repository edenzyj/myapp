package myapp.pack;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileReader;

public class NewsPage extends AppCompatActivity {

    TextView title, date, text;
    Mynews mynews;

    private Button btn;
    private boolean isCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);
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

        title = findViewById(R.id.ctitle);
        date = findViewById(R.id.cdate);
        text = findViewById(R.id.carticle);

        mynews = Mynews.getnowNews();
        title.setText(mynews.getTitleString());
        date.setText(mynews.getDateString());
        text.setText(mynews.getTextString());

        newsHistoryReader.getInstance().getInsert(mynews.curNew);

        btn = findViewById(R.id.CollectionButton);
        isCollect = newsFileReader.getInstance().getAsk(mynews.curNew.newsId);

        if(!isCollect)btn.setText("收藏新闻");
        else btn.setText("取消收藏");

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!isCollect){
                    newsFileReader.getInstance().getInsert(mynews.curNew);
                    btn.setText("取消收藏");
                    isCollect = true;
                }
                else{
                    newsFileReader.getInstance().getDelete(mynews.curNew.newsId);
                    isCollect = false;
                    btn.setText("收藏新闻");
                }
            }
        });
        Toast.makeText(this, "正在准备新闻", Toast.LENGTH_SHORT).show();
    }

}
