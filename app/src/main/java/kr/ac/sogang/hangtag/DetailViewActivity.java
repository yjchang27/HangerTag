//상품을 클릭했을 때 나오는 상세 설명 페이지 액티비티의 레이아웃

package kr.ac.sogang.hangtag;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.util.Log;


public class DetailViewActivity extends Activity implements View.OnClickListener {

    View header;        // 리스트뷰 상단에 고정 레이아웃을 추가하기 위한 뷰.
    ImageView topBar;
    TextView itemName;
    ImageView itemImage;    // 상품의 사진을 게시하는 이미지 뷰.
    TextView itemDescription;   // 상품 설명
    //Gallery itemGallery;    // 상품의 사진을 넘겨 이미지 뷰에 올리는 갤러리.
    ImageButton toKorean;
    ImageButton toChinese;
    ImageButton itemOthers3;    // 상단에 위치하여 다른 상품으로 넘어갈 수 있게 하는 이미지 버튼.
    ArrayList<Integer> images;
    EditText replyFill;
    Button replySet;
    ArrayList<Reply> replies = new ArrayList<>();   // 댓글 리스트.
    ListView replyList;
    ReplyAdapter replyAdapter;
    int itemIndex;
    ItemSet itemThis;    // 현 페이지에 표시할 아이템
    ArrayList<ItemSet> itemList;
    ArrayList<String> nameList;
    ArrayList<Integer> userIdList;
    String user_name=null;
    int user_id;
    int postItemId;
    String jsonPageComment;
    String jsonPageUser;
    String kdesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        header = getLayoutInflater().inflate(R.layout.activity_detail_header,null,false);
        replyList = (ListView)findViewById(R.id.lvReply);
        replyAdapter = new ReplyAdapter(this, R.layout.reply, replies);
        replyList.addHeaderView(header);
        replyList.setAdapter(replyAdapter);
        if(Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        images = new ArrayList<>();
        Intent intent = getIntent();
        ItemSet itemSet = new ItemSet();
        if (intent != null){

            itemSet = (ItemSet)intent.getSerializableExtra("itemSet");
            itemIndex = (int)intent.getSerializableExtra("id");
            images = itemSet.imageList;
            user_name = (String)intent.getSerializableExtra("name");
        }

        itemName = (TextView)findViewById(R.id.tvItemName);
        itemDescription = (TextView)findViewById(R.id.ItemDescription);
        itemThis = new ItemSet();
        itemList = new ArrayList<>();
        nameList = new ArrayList<>();
        userIdList = new ArrayList<>();

        //itemGallery = (Gallery)findViewById(R.id.ItemGallery);
        itemImage = (ImageView)findViewById(R.id.ItemImage);
        itemImage.setImageResource(itemSet.imageList.get(0));
/*
        itemOthers1 = (ImageButton)findViewById(R.id.ibDetail1);
        itemOthers1.setOnClickListener(this);
        itemOthers2 = (ImageButton)findViewById(R.id.ibDetail2);
        itemOthers2.setOnClickListener(this);
        itemOthers3 = (ImageButton)findViewById(R.id.ibDetail3);
        itemOthers3.setOnClickListener(this);

*/      toKorean = (ImageButton)findViewById(R.id.ibKorean);
        toKorean.setOnClickListener(this);
        toChinese = (ImageButton)findViewById(R.id.ibChinese);
        toChinese.setOnClickListener(this);

        replyFill = (EditText)findViewById(R.id.etReplyFill);
        replySet = (Button)findViewById(R.id.btReplySet);

        (new JsonLoadingTask()).execute();

        replySet.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(user_name!=null) {

                    Reply temp = new Reply();
                    temp.UserId = user_id;
                    temp.UserName = user_name;
                    temp.Content = replyFill.getText().toString();
                    replies.add(temp);
                    replyAdapter.notifyDataSetChanged();
                    JSONObject jSon = new JSONObject();
                    try {
                        jSon.put("customer_id", user_id);
                        jSon.put("product_id",postItemId);
                        jSon.put("title",user_name);
                        jSon.put("body",temp.Content);
                    } catch (JSONException e) {e.printStackTrace();}


                    try{
                        HttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost(getString(R.string.server_url) + "customer_comments.json");
                        StringEntity ent = new StringEntity(jSon.toString(),"UTF-8");
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

                replyFill.setText(null);
                }
            }
        });



        /*itemGallery.setAdapter(new GalleryAdapter(this));
        itemGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemImage.setImageResource(images.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


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

    } // 갤러리 어댑터

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
            id.setText(replylist.get(position).UserName);

            TextView con = (TextView) convertView.findViewById(R.id.tvReplyCon);
            con.setText(replylist.get(position).Content);

            return convertView;
        }
    } // 댓글 어댑터

    public void onClick(View v){
        Intent intent = new Intent(DetailViewActivity.this,DetailViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if(v.getId()==R.id.ibChinese) {
            kdesc = itemDescription.getText().toString();
            String cdesc = "价格 : " + itemThis.price +
                    "\n类别 : 简单" +
                    "\n尺寸 : 特大" +
                    "\n描述 : 这是很酷的调子T恤匹配夏天";
            itemName.setText("蓝色T恤");
            itemDescription.setText(cdesc);
            replySet.setText("登记");
            replyFill.setHint("输入回复请");
        }
        if(v.getId()==R.id.ibKorean) {
            itemName.setText(itemThis.name);
            itemDescription.setText(kdesc);
            replySet.setText("등록");
            replyFill.setHint("댓글을 입력하세요...");
        }
        /*
            itemSet.imageList.add(R.mipmap.blouson0);
            itemSet.imageList.add(R.mipmap.blouson1);
            itemSet.imageList.add(R.mipmap.blouson2);
            itemSet.imageList.add(R.mipmap.blouson3);
            int index = 1;
            intent.putExtra("itemSet",itemSet);
            intent.putExtra("index",index);
            intent.putExtra("name",user_name);
        }
        if(v.getId()==R.id.ibDetail2) {
            itemSet.imageList.add(R.mipmap.coat0);
            itemSet.imageList.add(R.mipmap.coat1);
            itemSet.imageList.add(R.mipmap.coat2);
            itemSet.imageList.add(R.mipmap.coat2);
            int index = 2;
            intent.putExtra("itemSet",itemSet);
            intent.putExtra("index",index);
            intent.putExtra("name",user_name);
        }
        if(v.getId()==R.id.ibDetail3) {
            itemSet.imageList.add(R.mipmap.denim0);
            itemSet.imageList.add(R.mipmap.denim1);
            itemSet.imageList.add(R.mipmap.denim2);
            itemSet.imageList.add(R.mipmap.denim3);
            int index = 3;
            intent.putExtra("itemSet",itemSet);
            intent.putExtra("index",index);
            intent.putExtra("name",user_name);
        }*/

        //startActivity(intent);

    } // 다른 상품 이미지 버튼 리스너

//    public void onBackPressed() {
//        Intent intent = new Intent(DetailViewActivity.this, SpecifyViewActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);
//    } // 뒤로

    private class JsonLoadingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            return getJsonText();
        } // doInBackground : 백그라운드 작업을 진행한다.
        @Override
        protected void onPostExecute(String result) {
            itemThis = itemList.get(itemIndex-1);
            postItemId = itemThis.id;
            result = "가격 : " + itemThis.price +
                    "\n종류 : " + itemThis.type +
                    "\n사이즈 : " + itemThis.size +
                    "\n상세설명 : " + itemThis.description;
            itemName.setText(itemThis.name);
            itemDescription.setText(result);
            getJSONComments(jsonPageComment);
            for(int i=0;i<userIdList.size();i++)
                if(nameList.get(i).equals(user_name))
                    user_id = userIdList.get(i);
            postView();
        } // onPostExecute : 백그라운드 작업이 끝난 후 UI 작업을 진행한다.
    } // JsonLoadingTask

    public String getJsonText() {

        String jsonPage;

        StringBuilder sb = new StringBuilder();
        try {

            //주어진 URL 문서의 내용을 문자열로 얻는다.
            jsonPage = getStringFromUrl(getString(R.string.server_url) + "products.json");
            jsonPageComment = getStringFromUrl(getString(R.string.server_url) + "customer_comments.json");

            //읽어들인 JSON포맷의 데이터를 JSON객체로 변환
            JSONObject json = new JSONObject(jsonPage);
            //

            //list의 값은 배열로 구성 되어있으므로 JSON 배열생성
            JSONArray jArr = json.getJSONArray("products");
            //
            for (int i=0; i<jArr.length(); i++){

                //i번째 배열 할당
                ItemSet item = new ItemSet();
                json = jArr.getJSONObject(i);
                String string = json.getString("product");
                string.substring(11);
                JSONObject json2 = new JSONObject(string);

                item.id = Integer.parseInt(json2.getString("id"));
                item.name = json2.getString("name");
                item.price = Integer.parseInt(json2.getString("price"));
                item.type = json2.getString("type");
                item.size = json2.getString("size");
                item.description = json2.getString("description");

                itemList.add(item);

            }

            jsonPageUser = getStringFromUrl(getString(R.string.server_url) + "customers.json");
            JSONObject jsonU = new JSONObject(jsonPageUser);
            JSONArray jArr2 = jsonU.getJSONArray("customers");
            for (int i=0; i<jArr2.length(); i++) {
                jsonU = jArr2.getJSONObject(i);
                String string = jsonU.getString("customer");
                string.substring(12);
                JSONObject jsonU2 = new JSONObject(string);
                userIdList.add(Integer.parseInt(jsonU2.getString("id")));
                nameList.add(jsonU2.getString("name"));
            }
            //

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
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

    public void getJSONComments(String jPC) {
        try {
            JSONObject jsonComment = new JSONObject(jPC);

            JSONArray jArrComment = jsonComment.getJSONArray("customer_comments");

            for (int i = 0; i < jArrComment.length(); i++) {

                //i번째 배열 할당
                jsonComment = jArrComment.getJSONObject(i);
                String string = jsonComment.getString("customer_comment");
                string.substring(20);
                JSONObject json2 = new JSONObject(string);
                int j = Integer.parseInt(json2.getString("product_id"));

                if (j == itemIndex) {
                    Reply reply = new Reply();
                    reply.UserId = Integer.parseInt(json2.getString("customer_id"));
                    reply.UserName = json2.getString("title");
                    reply.Content = json2.getString("body");
                    replies.add(reply);
                    replyAdapter.notifyDataSetChanged();
                }

            }
        } catch (JSONException e){e.printStackTrace(); }
    } // 기 댓글 등록

    public void postView(){
        JSONObject jSon = new JSONObject();
        try {
            jSon.put("Customer_id", user_id);
            jSon.put("Product_id",postItemId);
            jSon.put("Point","1");
        } catch (JSONException e) {e.printStackTrace();}


        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(getString(R.string.server_url) + "views.json");
            StringEntity ent = new StringEntity(jSon.toString(),"UTF-8");
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
