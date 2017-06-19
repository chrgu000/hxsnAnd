package com.hxsn.hf.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFragment extends Fragment {
	
	protected void showToast(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}
}
