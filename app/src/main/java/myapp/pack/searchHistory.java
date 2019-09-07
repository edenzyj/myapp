package myapp.pack;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;

import com.zhy.view.flowlayout.*;

import java.util.ArrayList;
import java.util.List;


public class searchHistory extends AppCompatActivity {

    private TagFlowLayout mFlowLayout = null;
    private EditText search;
    private ArrayList<String> listString;
    int listLen = 0;
    private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d("create", "create!");
        //if(mFlowLayout == null)Log.d("create", "null!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchhistory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFlowLayout = findViewById(R.id.tag);

        final LayoutInflater mInflater = LayoutInflater.from(searchHistory.this);
        listString = searchFileReader.getInstance().getTagList();
        mFlowLayout.setAdapter(new TagAdapter<String>(listString) {
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        search = findViewById(R.id.search);
        search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Intent intent = new Intent();
                setResult(134, intent);
                intent.putExtra("search", textView.getText().toString());
                finish();
                return true;
            }
        });

        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
        {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent)
            {
                String cur = listString.get(position);
                search.setText(cur.substring(1, cur.length()-1));
                return true;
            }
        });

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listString.clear();
                searchFileReader.getInstance().getClear();
                mFlowLayout.setAdapter(new TagAdapter<String>(listString) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s)
                    {
                        TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                                mFlowLayout, false);
                        tv.setText(s);
                        return tv;
                    }
                });
            }
        });
    }
}
