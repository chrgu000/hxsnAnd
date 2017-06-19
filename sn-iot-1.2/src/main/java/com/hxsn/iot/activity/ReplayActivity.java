package com.hxsn.iot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.util.AbDialogUtil;
import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.entity.AnswerInfo;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;
import com.hxsn.iot.base.BaseTitle;
import com.hxsn.iot.uitls.Const;
import com.hxsn.iot.uitls.Tools;

import java.util.ArrayList;
import java.util.List;


/**
 * 专家回复页面
 */
public class ReplayActivity extends Activity {
    /** 全局的LayoutInflater对象，已经完成初始化. */
    public LayoutInflater mInflater;
    private TextView txtTime,txtTitle,txtAnswer;
    private List<AnswerInfo> answerInfoList;
    private ListView answerListView;
    private AnswerAdapter answerAdapter;
    private boolean isReplay = false;//是否成功的回复了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);

        mInflater = LayoutInflater.from(this);

        isReplay = false;
        addView();
        BaseTitle.getInstance(this).setTitle("专家回复");

        //根据问题ID获取问题回复列表
        obtainAnswerList(TApplication.questionInfo.getId());
    }

    private void addView(){
        txtTime = (TextView)findViewById(R.id.txt_time);
        txtTitle = (TextView)findViewById(R.id.txt_info);
        txtAnswer = (TextView)findViewById(R.id.txt_answer);
        answerListView = (ListView) findViewById(R.id.list2);

        txtTitle.setText(TApplication.questionInfo.getTitle());
        txtTime.setText(TApplication.questionInfo.getTime());

        //添加回复弹出对话框
        txtAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View mView = mInflater.inflate(R.layout.dialog_answer,null);
                AbDialogUtil.showDialog(mView);

                final EditText editText = (EditText) mView.findViewById(R.id.edt_info);
                TextView positiveButton = (TextView) mView.findViewById(R.id.txt_send);
                TextView negativeButton = (TextView) mView.findViewById(R.id.txt_cancel);

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AbDialogUtil.removeDialog(ReplayActivity.this);
                    }
                });

                //提交回复
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = editText.getText().toString();
                        final AnswerInfo answerInfo = new AnswerInfo();
                        answerInfo.setTime(Tools.getCurTime());
                        answerInfo.setName(TApplication.user.getUserName());
                        answerInfo.setContent(msg);
                        AbRequestParams map = new AbRequestParams();
                        map.put("wzjid",TApplication.questionInfo.getId());
                        map.put("username", TApplication.user.getUserName());
                        map.put("content",msg);
                        if(msg.length()>0){
                            new AndHttpRequest(ReplayActivity.this) {
                                @Override
                                public void getResponse(String response) {
                                    String code = AndJsonUtils.getCode(response);
                                    if(code.equals("200")){
                                        if(answerInfoList == null){
                                            answerInfoList = new ArrayList<>();
                                        }
                                        answerInfoList.add(answerInfo);
                                        answerAdapter.notifyDataSetChanged();
                                        isReplay = true;
                                        AbDialogUtil.removeDialog(ReplayActivity.this);
                                    }else {
                                        AbToastUtil.showToast(ReplayActivity.this,AndJsonUtils.getDescription(response));
                                    }
                                }
                            }.doPost(Const.URL_SUBMIT_ANSWER,map);
                        }else {
                            AbToastUtil.showToast(ReplayActivity.this,"请回复");
                        }
                    }
                });
            }
        });
    }


    private void obtainAnswerList(String id) {
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                answerInfoList = AndJsonUtils.getAnswerList(response);
                LogUtil.i("AnswerFragment","answerInfoList.size="+answerInfoList.size());
                answerAdapter = new AnswerAdapter();
                answerListView.setAdapter(answerAdapter);
            }
        }.doPost(Const.URL_GET_ANSWER_LIST+ id);
    }

    /**
     * 回复列表adapter
     */
    class AnswerAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public AnswerAdapter() {
            super();
            inflater = LayoutInflater.from(ReplayActivity.this);
        }

        @Override
        public int getCount() {
            // Log.i("Answer","answerInfoList.size()="+answerInfoList.size());
            return answerInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return answerInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AnswerViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new AnswerViewHolder();
                convertView = inflater.inflate(R.layout.item_answer, parent, false);
                viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_info);
                viewHolder.txtTime = (TextView)convertView.findViewById(R.id.txt_time);
                viewHolder.txtName = (TextView)convertView.findViewById(R.id.txt_name);

                convertView.setTag(viewHolder);
            }else {
                viewHolder = (AnswerViewHolder)convertView.getTag();
            }

            viewHolder.txtTitle.setText(answerInfoList.get(position).getContent());
            viewHolder.txtTime.setText(answerInfoList.get(position).getTime());
            viewHolder.txtName.setText(answerInfoList.get(position).getName());

            return convertView;

        }

        //     ViewHolder 模式, 效率提高 50%
        class AnswerViewHolder {
            TextView txtTitle,txtTime,txtName;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isReplay){
            Intent intent = new Intent();
            intent.setClass(this,ExpertActivity.class);
            int code = isReplay?1:0;
            startActivityForResult(intent,code);
        }
    }
}
