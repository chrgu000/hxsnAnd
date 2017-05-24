package com.andbase.library.model;

import java.io.Serializable;

/**
 * Created by jiely on 2017/4/21.
 */
public class AbEntity implements Serializable{

    private String id;
    private String name;

    public AbEntity(){}
    public AbEntity(String id,String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
