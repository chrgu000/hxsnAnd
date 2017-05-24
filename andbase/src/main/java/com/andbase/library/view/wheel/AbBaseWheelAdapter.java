package com.andbase.library.view.wheel;


import com.andbase.library.model.AbEntity;

/**
 * Copyright amsoft.cn
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 轮子适配器接口
 */
public interface AbBaseWheelAdapter extends AbWheelAdapter{

	
	/**
	 * 获取条目的值.
	 * @param index 索引
	 */
	public AbEntity getEntity(int index);



}
