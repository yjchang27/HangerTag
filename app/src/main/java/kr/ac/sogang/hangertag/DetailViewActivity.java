//상품을 클릭했을 때 나오는 상세 설명 페이지 액티비티의 레이아웃

package kr.ac.sogang.hangertag;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Profile;


public class DetailViewActivity extends Activity implements View.OnClickListener {

    View header;        // 리스트뷰 상단에 고정 레이아웃을 추가하기 위한 뷰.
    ImageView topBar;
    TextView itemName;
    ImageView itemImage;    // 상품의 사진을 게시하는 이미지 뷰.
    TextView itemDescription;   // 상품 설명
    Gallery itemGallery;    // 상품의 사진을 넘겨 이미지 뷰에 올리는 갤러리.
    ImageButton itemOthers1;
    ImageButton itemOthers2;
    ImageButton itemOthers3;    // 상단에 위치하여 다른 상품으로 넘어갈 수 있게 하는 이미지 버튼.
    ArrayList<Integer> images;
    EditText replyFill;
    Button replySet;
    ArrayList<Reply> replies = new ArrayList<>();   // 댓글 리스트.
    ListView replyList;
    int itemIndex;
    ItemSet itemThis;    // 현 페이지에 표시할 아이템
    ArrayList<ItemSet> itemList;
    String user_name=null;
    int postItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        topBar = (ImageView)findViewById(R.id.TopBar);
        topBar.setAdjustViewBounds(true);
        header = getLayoutInflater().inflate(R.layout.activity_detail_header,null,false);
        replyList = (ListView)findViewById(R.id.lvReply);
        final ReplyAdapter replyAdapter = new ReplyAdapter(this, R.layout.reply, replies);
        replyList.addHeaderView(header);
        replyList.setAdapter(replyAdapter);
        if(Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        itemName = (TextView)findViewById(R.id.tvItemName);
        itemDescription = (TextView)findViewById(R.id.ItemDescription);
        itemThis = new ItemSet();
        itemList = new ArrayList<>();
        images = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null){
            ItemSet itemSet;
            itemSet = (ItemSet)intent.getSerializableExtra("itemSet");
            itemIndex = (int)intent.getSerializableExtra("index");
            images = itemSet.imageList;
            user_name = (String)intent.getSerializableExtra("name");
        }

        new JsonLoadingTask().execute();

        itemGallery = (Gallery)findViewById(R.id.ItemGallery);
        itemImage = (ImageView)findViewById(R.id.ItemImage);

        itemOthers1 = (ImageButton)findViewById(R.id.ibDetail1);
        itemOthers1.setOnClickListener(this);
        itemOthers2 = (ImageButton)findViewById(R.id.ibDetail2);
        itemOthers2.setOnClickListener(this);
        itemOthers3 = (ImageButton)findViewById(R.id.ibDetail3);
        itemOthers3.setOnClickListener(this);


        replyFill = (EditText)findViewById(R.id.etReplyFill);
        replySet = (Button)findViewById(R.id.btReplySet);


        replySet.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(user_name!=null) {

                    Reply temp = new Reply();
                    String upload;
                    temp.UserId = user_name;
                    temp.Content = replyFill.getText().toString();
                    replies.add(temp);
                    replyAdapter.notifyDataSetChanged();
                    JSONObject jSon = new JSONObject();
                    try {
                        jSon.put("customer_id", "2");
                        jSon.put("product_id",postItemId);
                        jSon.put("body",temp.Content);
                    } catch (JSONException e) {e.printStackTrace();}


                    try{
                        HttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost("http://trn.iptime.org:3000/customer_comments.json");
                        StringEntity ent = new StringEntity(jSon.toString());
                        post.setEntity(ent);
                        post.setHeader("Content-Type","application/json");
                        HttpResponse httpResponse = client.execute(post);
                        HttpEntity resEn = httpResponse.getEntity();

                        if(resEn != null)
                            Log.i("RESPONSE", EntityUtils.toString(resEn));
                    }
                    catch (UnsupportedEncodingException e) {e.printStackTrace();}
                    catch (ClientProtocolException e) {e.printStackTrace();}
                    catch (IOException e) {e.printStackTrace();}


                }
            }
        });

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
            itemSet.imageList.add(R.mipmap.blouson0);
            itemSet.imageList.add(R.mipmap.blouson1);
            itemSet.imageList.add(R.mipmap.blouson2);
            itemSet.imageList.add(R.mipmap.blouson3);
            int index = 0;
            intent.putExtra("itemSet",itemSet);
            intent.putExtra("index",index);
            intent.putExtra("name",user_name);
        }
        if(v.getId()==R.id.ibDetail2) {
            itemSet.imageList.add(R.mipmap.coat0);
            itemSet.imageList.add(R.mipmap.coat1);
            itemSet.imageList.add(R.mipmap.coat2);
            itemSet.imageList.add(R.mipmap.coat2);
            int index = 1;
            intent.putExtra("itemSet",itemSet);
            intent.putExtra("index",index);
            intent.putExtra("name",user_name);
        }
        if(v.getId()==R.id.ibDetail3) {
            itemSet.imageList.add(R.mipmap.denim0);
            itemSet.imageList.add(R.mipmap.denim1);
            itemSet.imageList.add(R.mipmap.denim2);
            itemSet.imageList.add(R.mipmap.denim3);
            int index = 2;
            intent.putExtra("itemSet",itemSet);
            intent.putExtra("index",index);
            intent.putExtra("name",user_name);
        }
        startActivity(intent);

    }

    public void onBackPressed() {
        Intent intent = new Intent(DetailViewActivity.this, SpecifyViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private class JsonLoadingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            return getJsonText();
        } // doInBackground : 백그라운드 작업을 진행한다.
        @Override
        protected void onPostExecute(String result) {
            itemThis = itemList.get(itemIndex);
            postItemId = itemThis.id;
            result = "가격 : " + itemThis.price +
                    "\n종류 : " + itemThis.type +
                    "\n사이즈 : " + itemThis.size +
                    "\n상세설명 : " + itemThis.description;
            itemName.setText(itemThis.name);
            itemDescription.setText(result);
        } // onPostExecute : 백그라운드 작업이 끝난 후 UI 작업을 진행한다.
    } // JsonLoadingTask

    public String getJsonText() {

        String jsonPage;
        StringBuilder sb = new StringBuilder();
        try {

            //주어진 URL 문서의 내용을 문자열로 얻는다.
            jsonPage = getStringFromUrl("http://trn.iptime.org:3000/products.json");


            //읽어들인 JSON포맷의 데이터를 JSON객체로 변환
            JSONObject json = new JSONObject(jsonPage);

            //list의 값은 배열로 구성 되어있으므로 JSON 배열생성
            JSONArray jArr = json.getJSONArray("products");

            //배열의 크기만큼 반복하면서, ksNo과 korName의 값을 추출함
            for (int i=0; i<jArr.length(); i++){

                //i번째 배열 할당
                ItemSet item = new ItemSet();
                json = jArr.getJSONObject(i);
                String string = json.getString("product");
                string.substring(11);
                JSONObject json2 = new JSONObject(string);
                // jArr = json.getJSONArray("product");
                // json = jArr.getJSONObject(0);

                item.id = Integer.parseInt(json2.getString("id"));
                item.name = json2.getString("name");
                item.price = Integer.parseInt(json2.getString("price"));
                item.type = json2.getString("type");
                item.size = json2.getString("size").charAt(0);
                item.description = json2.getString("description");

                itemList.add(item);

            }


        } catch (Exception e) {
            // TODO: handle exception
        }

        return sb.toString();
    }//getJsonText()-----------

    public String getStringFromUrl(String pUrl){

        BufferedReader bufreader=null;
        HttpURLConnection urlConnection = null;

        StringBuffer page=new StringBuffer(); //읽어온 데이터를 저장할 StringBuffer객체 생성

        try {

            //[Type1]
/*
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(pUrl));
            InputStream contentStream = response.getEntity().getContent();
*/

            //[Type2]
            URL url= new URL(pUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream contentStream = urlConnection.getInputStream();

            bufreader = new BufferedReader(new InputStreamReader(contentStream,"UTF-8"));
            String line = null;

            //버퍼의 웹문서 소스를 줄단위로 읽어(line), Page에 저장함
            while((line = bufreader.readLine())!=null){
                Log.d("line:", line);
                page.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //자원해제
            try {
                bufreader.close();
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return page.toString();
    }// getStringFromUrl()-------------------------

}
