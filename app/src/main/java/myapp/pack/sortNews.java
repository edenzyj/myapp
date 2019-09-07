package myapp.pack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class sortNews extends AppCompatActivity{

    private DragGridLayout dragGridLayout;
    private DragGridLayout otherDragGridLayout;
    private Intent intent;
    private Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sortnews);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dragGridLayout = findViewById(R.id.DragGridLayout);
        dragGridLayout.setCanDrag(true);
        ArrayList<String> items = (ArrayList<String>)getIntent().getStringArrayListExtra("mySort");
        dragGridLayout.setItems(items);

        otherDragGridLayout = findViewById(R.id.otherDragGridLayout);
        final ArrayList<String>otherItems = (ArrayList<String>)getIntent().getStringArrayListExtra("otherSort");
        otherDragGridLayout.setItems(otherItems);

        dragGridLayout.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                dragGridLayout.removeView(tv);
                otherDragGridLayout.addGridItem(tv.getText().toString());
            }
        });

        otherDragGridLayout.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                otherDragGridLayout.removeView(tv);
                dragGridLayout.addGridItem(tv.getText().toString());
            }
        });

        myButton = findViewById(R.id.mybutton);
        myButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent = new Intent();
                setResult(1001, intent);
                intent.putStringArrayListExtra("mySort",dragGridLayout.getString());
                intent.putStringArrayListExtra("otherSort", otherDragGridLayout.getString());
                finish();
            }
        });
    }
}
