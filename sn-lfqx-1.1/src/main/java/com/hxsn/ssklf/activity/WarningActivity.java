package com.hxsn.ssklf.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.andbase.library.util.AbDateUtil;
import com.hxsn.ssklf.R;
import com.hxsn.ssklf.TApplication;
import com.hxsn.ssklf.base.BaseTitle;
import com.hxsn.ssklf.model.WarningInfo;

public class WarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        BaseTitle.getInstance(this).setTitle("廊坊气象预警");
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(new MyAdapter(this));
    }


    class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            super();
            inflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return TApplication.warningInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return TApplication.warningInfoList.get(position);
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
                convertView = inflater.inflate(R.layout.item_warning, parent, false);
                viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
                viewHolder.txtCreateTime = (TextView) convertView.findViewById(R.id.txt_create_time);
                viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txt_content);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();

            }

            WarningInfo warningInfo = TApplication.warningInfoList.get(position);
            viewHolder.txtTitle.setText(warningInfo.getTitle());
            String time = AbDateUtil.getStringByFormat(warningInfo.getCreateTime(),"yyyy-MM-dd HH:mm");
            viewHolder.txtCreateTime.setText(time);
            viewHolder.txtContent.setText(warningInfo.getContent());
            return convertView;
        }
    }

    //     ViewHolder 模式, 效率提高 50%
    static class ViewHolder {
        TextView txtTitle,txtCreateTime,txtContent;
    }
}
