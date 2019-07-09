package com.xms.autostudy.constant;
/**
 * xumengsi
 */
public class AutoStudyConstant {

    public static final String USER_ISREAD = "auto-study:user:%s:isReadUrls";

    public static final String USER_ISVIDEO = "auto-study:user:%s:isVideoUrls";

    public static final String MY_STUDY = "https://pc.xuexi.cn/points/my-study.html";

    
    public static String formatKey(String targetStr, Object... content){
        return String.format(targetStr,content);
    }
}
