package kr.ac.sogang.hangertag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Kidsnow on 2015-04-12.
 */


public class SpecifyViewActivity extends Activity implements View.OnClickListener{

    ImageView topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_view);
        topBar = (ImageView)findViewById(R.id.TopBar);
        topBar.setAdjustViewBounds(true);

        ImageButton btGoSpec1 = (ImageButton)findViewById(R.id.specification1);
        btGoSpec1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this,DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                itemSet.imageList.add(R.mipmap.blouson0);
                itemSet.imageList.add(R.mipmap.blouson1);
                itemSet.imageList.add(R.mipmap.blouson2);
                int index = 0;
                intent.putExtra("itemSet",itemSet);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });

        ImageButton btGoSpec2 = (ImageButton)findViewById(R.id.specification2);
        btGoSpec2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this,DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                itemSet.imageList.add(R.mipmap.coat0);
                itemSet.imageList.add(R.mipmap.coat1);
                itemSet.imageList.add(R.mipmap.coat2);
                int index = 1;
                intent.putExtra("itemSet",itemSet);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });

        ImageButton btGoSpec3 = (ImageButton)findViewById(R.id.specification3);
        btGoSpec3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this,DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                itemSet.imageList.add(R.mipmap.denim0);
                itemSet.imageList.add(R.mipmap.denim1);
                itemSet.imageList.add(R.mipmap.denim2);
                int index = 2;
                intent.putExtra("itemSet",itemSet);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });

        ImageButton btGoSpec4 = (ImageButton)findViewById(R.id.specification4);
        btGoSpec4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this,DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                //itemSet.description = "4번 상품이다";
                //itemSet.imageList.add(R.mipmap.coat0);
                //itemSet.imageList.add(R.mipmap.coat1);
                //itemSet.imageList.add(R.mipmap.coat2);
                int index = 3;
                intent.putExtra("itemSet",itemSet);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.specification1:
                Log.i("First", "OK");
                break;
            case R.id.specification2:
                Log.i("Second", "OK");
                break;
            case R.id.specification3:
                Log.i("Third", "OK");
                break;
            case R.id.specification4:
                Log.i("Fourth", "OK");
                break;
        }
    }
}
