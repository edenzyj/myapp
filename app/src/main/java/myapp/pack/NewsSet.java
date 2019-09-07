package myapp.pack;
import java.util.*;
//收藏功能

public class NewsSet {
    private Vector<String> newsVec = new Vector<>();


    private NewsSet() {
        // TODO 从本地获取收藏列表

    }
    private NewsSet newsSet = null;
    public NewsSet getNewsSet() {
        if (newsSet==null)
            newsSet = new NewsSet();
        return newsSet;
    }

    public void add(final String e) {
        newsVec.add(e);
    }

    public boolean romove(final String e) {
        return newsVec.remove(e);
    }
}
