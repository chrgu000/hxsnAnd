package com.hxsn.ssklf.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.andbase.ssk.entity.Nongsh;
import com.hxsn.ssklf.R;

import java.util.List;


/**
 * 农事汇页面
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




        return view;
    }



}
