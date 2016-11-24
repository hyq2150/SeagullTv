package com.victor.util;
/**
 * Created by victor on 2015/12/25.
 */
public class Constant {
    public static final boolean isDebug = true;
    public static final String MA_DATA                              = "madata";
    public static final String ACTION_KEY                           = "ACTION_KEY";
    public static final String INTENT_DATA_KEY                     = "INTENT_DATA_KEY";
    public static final String CHANNEL_DATA_KEY                    = "CHANNEL_DATA_KEY";
    public static final String STATUS_KEY                           = "STATUS_KEY";
    public static final String HEAD_IMG_URL                         = "HEAD_IMG_URL";
    public static final String REQUEST_MSG_KEY                      = "REQUEST_MSG_KEY";
    public static final String CURRENT_PLAY_URL                     = "CURRENT_PLAY_URL";
    public static final String CURRENT_PLAY_NAME                    = "CURRENT_PLAY_NAME";
    public static final String CURRENT_PLAY_EPG                     = "CURRENT_PLAY_EPG";
    public static final String CURRENT_PAGE_KEY                     = "CURRENT_PAGE_KEY";
    public static final String CURRENT_MEI_PAI_URL                  = "CURRENT_MEI_PAI_URL";
    public static final String CHANNEL_URL                           = "https://raw.githubusercontent.com/Victor2018/SeagullTv/master/docs/channels_json.txt";
    public static final String DEFAULT_PLAY_URL                      = "http://222.191.24.5:6610/2/2/ch00000090990000001251/index.m3u8?ispcode=3";
    public static final String DEFAULT_EPG_URL                       = "http://www.tvsou.com/epg/hubeidianshitai-hbws?class=weishi";
    public static final String CATEGORY_URL                          = "https://raw.githubusercontent.com/Victor2018/SeagullTv/master/docs/category_json.txt";
    public static final int PAGE_SIZE                                = 30;
    public static class Msg {
        public static final int REQUEST_SUCCESS                             = 0x102;//请求成功
        public static final int REQUEST_SUCCESS_NO_DATA                    = 0x103;//请求成功，没有数据
        public static final int REQUEST_FAILED                              = 0x104;//请求失败
        public static final int PARSING_EXCEPTION                           = 0x105;//数据解析异常
        public static final int NETWORK_ERROR                               = 0x106;//网络错误
        public static final int GIF_REQUEST                                 = 0x107;
        public static final int FUNNY_REQUEST                               = 0x108;
        public static final int VOICE_REQUEST                               = 0x109;
        public static final int VIDEO_REQUEST                               = 0x110;
        public static final int PLAY_VOICE                                  = 0x111;
        public static final int PLAY_VIDEO                                  = 0x112;
        public static final int HIDE_PLAY_CTRL_VIEW                        = 0x113;
        public static final int HIDE_PLAY_TITLE_VIEW                       = 0x114;
        public static final int BASE_REQUEST                                = 0x115;
        public static final int SOCKET_TIME_OUT                             = 0x116;
        public static final int CHANNEL_REQUEST                             = 0x117;
        public static final int PICTURE_REQUEST                             = 0x118;
        public static final int HIDE_ACTION_BAR                             = 0x119;
        public static final int DRAW_HEART_BUBBLE                           = 0x120;
        public static final int MEIPAI_REQUEST                              = 0x121;
        public static final int MEIPAI_CATEGORY_REQUEST                    = 0x122;
        public static final int REQUEST_LIVE_EPG                            = 0x123;
        public static final int SHOW_LIVE_EPG                               = 0x124;
        public static final int CATEGORY_REQUEST                            = 0x125;
        public static final int HIDE_VOICE_BRIGHT_VIEW                      = 0x126;
        public static final int GOTO_WONDERFUL_LIVE                         = 0x127;
    }

    public static class Action {
        public static final int ON_CATEGORY_ITEM_CLICK                      = 0x201;
        public static final int ON_CHANNEL_ITEM_CLICK                       = 0x202;
        public static final int PLAY_MEIPAI_VIDEO                            = 0x203;
        public static final int SHARE_MEIPAI                                 = 0x204;
        public static final int PLAY_NEXT_MEIPAI_VIDEO                      = 0x205;
    }
}
