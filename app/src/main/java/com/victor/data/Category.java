package com.victor.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by victor on 2015/12/25.
 */
public class Category implements Serializable{
    public int gravity;
    public String category;
    public List<Channel> channels;
}