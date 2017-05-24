package com.andbase.library.view.wheel;

import com.andbase.library.model.AbEntity;
import com.andbase.library.util.AbStrUtil;

import java.util.List;


/**
 * Copyright amsoft.cn
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 轮子适配器（字符串）
 */
public class AbEntityWheelAdapter implements AbBaseWheelAdapter {

	/** 条目列表. */
	private List<AbEntity> itemStr;
	/** 条目列表. */
	private List<AbEntity> itemEntity;

	/** 长度. */
	private int length = -1;

	/**
	 * 构造函数.
	 * @param items the items
	 */
	public AbEntityWheelAdapter(List<AbEntity> items) {
		this.itemEntity = items;
        getMaximumLength();
	}


	@Override
	public String getItem(int index) {

		return itemEntity.get(index).getName();
	}


	@Override
	public int getItemsCount() {
		return itemEntity.size();
	}


	@Override
	public int getMaximumLength() {
		if(length!=-1){
			return length;
		}
		for(int i=0;i<itemEntity.size();i++){
			AbEntity cur = itemEntity.get(i);
			int l = AbStrUtil.strLength(cur.getName());
			if(length<l){
                length = l;
			}
		}
		return length;
	}

	@Override
	public AbEntity getEntity(int index) {
		if (index >= 0 && index < itemEntity.size()) {
			return itemEntity.get(index);
		}
		return null;
	}
}
