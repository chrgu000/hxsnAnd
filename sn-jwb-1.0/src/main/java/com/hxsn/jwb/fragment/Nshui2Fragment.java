package com.hxsn.jwb.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.ssk.entity.Nongsh;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.activity.NewsActivity;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.ImageUtil;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class Nshui2Fragment extends Fragment {

    private GridView gridView;
    private Context context;
    private List<String> urls;
    private List<Nongsh> nongshList;

    public Nshui2Fragment() {
    }

    public Nshui2Fragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ValidFragment")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_njhui2, container, false);

        addView(view);

        //更新农事汇图标
        updateNongshImage();

        return view;
    }

    //更新农事汇图标
    private void updateNongshImage() {
        String url = Const.URL_NONGSH_LIST;
        //nongshList = NongshService.getInstance(getActivity()).getNongshList();

        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                nongshList = AndJsonUtils.getNongshList(TApplication.URL_CMS_STRING,response);
                for(Nongsh n:nongshList){
                    LogUtil.i("Nshui2Fragment","************id="+n.getId()+"***************name"+n.getName());
                }
                addAdapter();
            }
        }.doGet(url);
    }

    private void addAdapter() {
        MyAdapter adapter = new MyAdapter();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Nongsh nongsh = nongshList.get(position);
                Intent intent = new Intent();
                intent.putExtra("id",nongsh.getId());
                intent.putExtra("name",nongsh.getName());
                intent.setClass(getActivity(), NewsActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void addView(View view) {
        gridView = (GridView) view.findViewById(R.id.grid_view);
    }

    class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public MyAdapter() {
            super();
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return nongshList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
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
                convertView = inflater.inflate(R.layout.item_image, parent, false);
                viewHolder.txtView = (TextView) convertView.findViewById(R.id.txt);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.image);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            //List<Nongsh> tempNongshList = NongshService.getInstance(context).getNongshList();
            viewHolder.txtView.setText(nongshList.get(position).getName());
            LogUtil.i("Hshui2Fragment","position="+position+",id="+nongshList.get(position).getId()+",imgurl="+nongshList.get(position).getImage());
            ImageUtil.displayImage(nongshList.get(position).getImage(),viewHolder.imageView);

            return convertView;
        }
    }

    //     ViewHolder 模式, 效率提高 50%
    static class ViewHolder {
        TextView txtView;
        ImageView imageView;
    }
}
