package com.victor.data;

import java.io.Serializable;

/**
 * Created by victor on 2015/12/25.
 */
public class Channel implements Serializable{
    public int category;
    public int gravity;
    public String name;
    public String icon;
    public String epg;
    public String[] playUrls;
}