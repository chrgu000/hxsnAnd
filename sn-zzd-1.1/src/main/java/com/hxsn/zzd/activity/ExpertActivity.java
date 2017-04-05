package com.hxsn.zzd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andbase.ssk.entity.AnswerInfo;
import com.andbase.ssk.entity.QuestionInfo;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.base.BaseTitle;
import com.hxsn.zzd.utils.Const;
import com.hxsn.zzd.utils.ImageUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        BaseTitle.getInstance(this).setTitle("专家页面");
        addView();
        addListener();

        //获取未回复问题列表
        obtainQuestionList(0);
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
            case R.id.txt_left:
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
            case R.id.txt_right:
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
            if(imgUrl.length()>0){
                viewHolder.imageView.setBackgroundDrawable(null);
                ImageUtil.displayImage(imgUrl,viewHolder.imageView);
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

    private void obtainQuestionList(final int i) {
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                if(i == 0){
                    questionList0 = AndJsonUtils.getQuestionList(TApplication.URL_CMS_STRING,response);
                    questionList = questionList0;
                    listview.setAdapter(adapter);
                }else {
                    questionList1 = AndJsonUtils.getQuestionList(TApplication.URL_CMS_STRING,response);
                    questionList = questionList1;
                    adapter.notifyDataSetChanged();
                }
            }
        }.doPost(Const.URL_GET_QUEST_LIST+ TApplication.user.getUserName()
                +"&isReplied="+i+"&pageStart=0&pageSize=50",null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i("ExpertActivity","requestCode="+requestCode+"resultCode="+requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
