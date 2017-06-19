package com.hxsn.iot.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andbase.library.util.AbAppUtil;
import com.andbase.ssk.entity.AnswerInfo;
import com.andbase.ssk.entity.QuestionInfo;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;
import com.hxsn.iot.base.BaseTitle;
import com.hxsn.iot.uitls.Const;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 专家界面
 */
public class ExpertActivity extends Activity implements View.OnClickListener{

    private TextView txtLeft,txtRight;
    private ListView listview;
    private int mode = 1;
    private List<QuestionInfo> questionList,questionList0,questionList1;
    private MyAdapter adapter;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        BaseTitle.getInstance(this).setTitle("专家页面");
        addView();
        addListener();

        //获取未回复问题列表
        obtainQuestionList(0);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_empty)
                .showImageOnFail(R.mipmap.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    private void addView() {
        txtLeft = (TextView)findViewById(R.id.txt_left);
        txtRight = (TextView)findViewById(R.id.txt_right);
        listview = (ListView) findViewById(R.id.list);


        adapter = new MyAdapter();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                LogUtil.i("Answer","listview-onItemClick");
                TApplication.questionInfo = questionList.get(position);
                Intent intent = new Intent();
                intent.setClass(ExpertActivity.this,ReplayActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addListener() {
        txtLeft.setOnClickListener(this);
        txtRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_left://未回复
                if(mode != 1){
                    mode = 1;
                    txtLeft.setBackgroundColor(getResources().getColor(R.color.sky_blue));
                    txtLeft.setTextColor(getResources().getColor(R.color.white));
                    txtRight.setBackgroundColor(getResources().getColor(R.color.white));
                    txtRight.setTextColor(getResources().getColor(R.color.black_text_n));
                    questionList = questionList0;
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.txt_right://已回复
                if(mode != 2){
                    mode = 2;
                    txtRight.setBackgroundColor(getResources().getColor(R.color.sky_blue));
                    txtRight.setTextColor(getResources().getColor(R.color.white));
                    txtLeft.setBackgroundColor(getResources().getColor(R.color.white));
                    txtLeft.setTextColor(getResources().getColor(R.color.black_text_n));
                    if(questionList1 == null){
                        obtainQuestionList(1);
                    }else {
                        questionList = questionList1;
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    /**
     * 问题类别适配器
     */
    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter() {
            super();
            inflater = LayoutInflater.from(ExpertActivity.this);
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

        @Override
        public Object getItem(int position) {
            return questionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_wen, parent, false);
                viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
                viewHolder.txtReplay = (TextView) convertView.findViewById(R.id.txt_replay);
                viewHolder.txtTime = (TextView)convertView.findViewById(R.id.txt_time);
                viewHolder.txtName = (TextView)convertView.findViewById(R.id.txt_name);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_picture);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.txtTitle.setText(questionList.get(position).getTitle());
            viewHolder.txtTime.setText(questionList.get(position).getTime());
            viewHolder.txtName.setText(questionList.get(position).getUsername());
            String imgUrl = questionList.get(position).getUrl();
            viewHolder.imageView.setImageBitmap(null);

            if(imgUrl.length()>0){
                DisplayMetrics displayMetrics = AbAppUtil.getDisplayMetrics(ExpertActivity.this);
                LogUtil.i(ExpertActivity.class,"imgUrl="+imgUrl+",position="+position+",displayMetrics.density="+displayMetrics.density+",displayMetrics.densityDpi="+displayMetrics.densityDpi);

                ImageLoader.getInstance().displayImage(imgUrl, viewHolder.imageView, options, new AnimateFirstDisplayListener());

               // ImageUtil.displayImage(imgUrl,viewHolder.imageView,(int)(95*displayMetrics.density),(int)(95*displayMetrics.density));
            }else {
                LogUtil.i(ExpertActivity.class,"imgUrl="+imgUrl+",position="+position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.imageView.setBackground(getResources().getDrawable(R.mipmap.ic_launcher));
                }
            }

            if(mode == 1){
                viewHolder.txtReplay.setVisibility(View.GONE);
            }else if (mode == 2){
                viewHolder.txtReplay.setVisibility(View.VISIBLE);
                List<AnswerInfo> answerInfoList = questionList.get(position).getAnswerList();
                StringBuilder sb = new StringBuilder();
                for(AnswerInfo answerInfo:answerInfoList){
                    sb.append(answerInfo.getContent()+"\n");
                }
                viewHolder.txtReplay.setText(sb.toString());
            }
            return convertView;

        }

        //     ViewHolder 模式, 效率提高 50%
        class ViewHolder {
            TextView txtTitle,txtTime,txtName,txtReplay;
            ImageView imageView;
        }
    }

    /**
     * 获取问题列表
     * @param i 0，初次获取 1,第二次获取列表要更新适配器
     */
    private void obtainQuestionList(final int i) {
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                if(i == 0){//初次获取
                    questionList0 = AndJsonUtils.getQuestionList(TApplication.URL_CMS_STRING,response);
                    questionList = questionList0;
                    listview.setAdapter(adapter);
                }else {//第二次获取列表要更新适配器
                    questionList1 = AndJsonUtils.getQuestionList(TApplication.URL_CMS_STRING,response);
                    questionList = questionList1;
                    adapter.notifyDataSetChanged();
                }
            }
        }.doPost(Const.URL_GET_QUEST_LIST+ TApplication.user.getUserName()
                +"&isReplied="+i+"&pageStart=0&pageSize=50",null);
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }


}
