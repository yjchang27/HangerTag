//상품을 클릭했을 때 나오는 상세 설명 페이지 액티비티의 레이아웃

package kr.ac.sogang.hangertag;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;


public class DetailViewActivity extends Activity implements View.OnClickListener {

    View header;
    ImageView itemImage;
    TextView itemDescription;
    Gallery itemGallery;
    ImageButton itemOthers1;
    ImageButton itemOthers2;
    ImageButton itemOthers3;
    ArrayList<Integer> images;
    EditText replyFill;
    Button replySet;
    ArrayList<Reply> replies = new ArrayList<>();
    ListView replyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        header = getLayoutInflater().inflate(R.layout.activity_detail_header,null,false);
        replyList = (ListView)findViewById(R.id.lvReply);
        final ReplyAdapter replyAdapter = new ReplyAdapter(this, R.layout.reply, replies);
        replyList.addHeaderView(header);
        replyList.setAdapter(replyAdapter);

        itemGallery = (Gallery)findViewById(R.id.ItemGallery);
        itemImage = (ImageView)findViewById(R.id.ItemImage);
        itemDescription = (TextView)findViewById(R.id.ItemDescription);
        itemOthers1 = (ImageButton)findViewById(R.id.ibDetail1);
        itemOthers1.setOnClickListener(this);
        itemOthers2 = (ImageButton)findViewById(R.id.ibDetail2);
        itemOthers2.setOnClickListener(this);
        itemOthers3 = (ImageButton)findViewById(R.id.ibDetail3);
        itemOthers3.setOnClickListener(this);

        images = new ArrayList<>();

        replyFill = (EditText)findViewById(R.id.etReplyFill);
        replySet = (Button)findViewById(R.id.btReplySet);



        replySet.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Reply temp = new Reply();
                temp.UserId = "testing";
                temp.Content = replyFill.getText().toString();
                replies.add(temp);
                replyAdapter.notifyDataSetChanged();
            }
        });


        Button ItemGoBack = (Button)findViewById(R.id.ItemGoBack);
        ItemGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DetailViewActivity.this, SpecifyViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent != null){
            ItemSet itemSet;
            itemSet = (ItemSet)intent.getSerializableExtra("itemSet");
            itemDescription.setText(itemSet.description);
            images = itemSet.imageList;
        }


        itemGallery.setAdapter(new GalleryAdapter(this));
        itemGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemImage.setImageResource(images.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class GalleryAdapter extends BaseAdapter {
        Context context;

        public GalleryAdapter(Context context){
            this.context = context;
        }

        public int getCount(){
            return images.size();
        }

        public Object getItem(int position){
            return images.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            ImageView image;

            if(convertView == null){
                image = new ImageView(context);
            }
            else {
                image = (ImageView)convertView;
            }

            image.setImageResource(images.get(position));
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setLayoutParams(new Gallery.LayoutParams(200,200));

            return image;
        }

    }

    class ReplyAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        ArrayList<Reply> replylist;
        int layout;

        public ReplyAdapter(Context context, int layout, ArrayList<Reply> replyList){
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout = layout;
            this.replylist = replyList;
        }
        @Override
        public int getCount(){return replylist.size();}
        @Override
        public Object getItem(int position){return replylist.get(position);}
        @Override
        public long getItemId(int position){return position;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = inflater.inflate(layout,parent,false);

            TextView id = (TextView) convertView.findViewById(R.id.tvReplyId);
            id.setText(replylist.get(position).UserId);

            TextView con = (TextView) convertView.findViewById(R.id.tvReplyCon);
            con.setText(replylist.get(position).Content);

            return convertView;
        }
    }

    public void onClick(View v){
        Intent intent = new Intent(DetailViewActivity.this,DetailViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ItemSet itemSet = new ItemSet();
        if(v.getId()==R.id.ibDetail1) {
            itemSet.description = "1번 상품이다";
            itemSet.imageList.add(R.mipmap.blouson0);
            itemSet.imageList.add(R.mipmap.blouson1);
            itemSet.imageList.add(R.mipmap.blouson2);
            itemSet.imageList.add(R.mipmap.blouson3);
            intent.putExtra("itemSet",itemSet);
        }
        if(v.getId()==R.id.ibDetail2) {
            itemSet.description = "2번 상품이다";
            itemSet.imageList.add(R.mipmap.coat0);
            itemSet.imageList.add(R.mipmap.coat1);
            itemSet.imageList.add(R.mipmap.coat2);
            itemSet.imageList.add(R.mipmap.coat2);
            intent.putExtra("itemSet",itemSet);
        }
        if(v.getId()==R.id.ibDetail3) {
            itemSet.description = "3번 상품이다";
            itemSet.imageList.add(R.mipmap.denim0);
            itemSet.imageList.add(R.mipmap.denim1);
            itemSet.imageList.add(R.mipmap.denim2);
            itemSet.imageList.add(R.mipmap.denim3);
            intent.putExtra("itemSet",itemSet);
        }
        startActivity(intent);

    }

    public void onBackPressed() {
        Intent intent = new Intent(DetailViewActivity.this, SpecifyViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

}
