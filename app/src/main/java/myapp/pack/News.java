package myapp.pack;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class News {
    public String title;
    public String content;
    public String date;
    public String source;
    public String newsId;
    public List<String> imageUrl;
    public Bitmap pic;

    News(String title_, String content_, String date_,
         String source_, String newsId_, String imageUrl_){
        title = title_;
        content = "      " + content_.replace("\n", "\n      ");
        date = date_;
        source = source_;
        newsId = newsId_;
        imageUrl = new ArrayList<String>();
        getSpilit(imageUrl_);
        getImage();
    }

    private void getSpilit(String str) {
        if(str == null || str.length() < 3)return;
        str = str.substring(1, str.length() - 1) + ",";
        int lastPos = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == ',') {
                imageUrl.add(str.substring(lastPos, i));
                lastPos = i + 1;
            }
        }
    }

    private void getImage() {
        String stringFirst = (imageUrl.size() > 0? imageUrl.get(0):null);
        if(stringFirst == null){pic = null; return;}
        pic = ImageLoader.getInstance().loadImageSync(stringFirst);
    }

    public Bitmap getLoad(int index){
        if(imageUrl.size() <= index)return null;
        return ImageLoader.getInstance().loadImageSync(imageUrl.get(index));
    }
}