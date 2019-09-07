package myapp.pack;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class Mynews {
    private ImageView image;
    private TextView title, date, article;
    private boolean viewed = false;
    private View v;
    private Context context;
    static myapp.pack.Mynews now = null;

    public News curNew;

    static void setnowNews(myapp.pack.Mynews _now) {
        now = _now;
    }

    static myapp.pack.Mynews getnowNews() {
        now.reverse();
        return now;
    }
    public Mynews(LayoutInflater layoutInflater, Context _context) {
        super();
        context = _context;
        v = layoutInflater.inflate(R.layout.news_view, null);
        title = v.findViewById(R.id.titleid);
        date = v.findViewById(R.id.dateid);
        article = v.findViewById(R.id.contid);
        image = v.findViewById(R.id.image);
        v.setTag(this);
    }
    void set(News news) {
        curNew = news;
        title.setText(curNew.title);
        date.setText(curNew.source+"  "+ curNew.date);
        article.setText(curNew.content);
        if(news.pic == null)image.setVisibility(View.GONE);
        else image.setImageBitmap(news.pic);

        checkActive();
    }
    View toView() {
        return v;
    }
    ImageView getImage() {
        return image;
    }
    TextView getTitle() {
        return title;
    }
    TextView getDate() {
        return date;
    }
    TextView getArticle() {
        return article;
    }

    private void reverse() {
        viewed = true;
        title.setTextColor(Color.rgb(128, 128, 128));
    }

    private void checkActive() {
        if(!newsHistoryReader.getInstance().getAsk(curNew.newsId)) {
            viewed = false;
            title.setTextColor(Color.rgb(63, 63, 255));
        }else{
            viewed = true;
            title.setTextColor(Color.rgb(128, 128, 128));
        }
    }
    @Override
    public String toString() {
        return title.getText().toString();
    }
    String getTitleString() {return (String) title.getText();}
    String getDateString() {return (String) date.getText();}
    String getTextString() {return (String) article.getText();}
}