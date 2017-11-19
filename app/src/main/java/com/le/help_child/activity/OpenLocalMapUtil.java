package com.le.help_child.activity;

/**
 * 描述：打开手机已安装地图相关工具类
 */
public class OpenLocalMapUtil {

//from=&to=116.3246,39.966577,endpoint&via=&mode=car

    public static String getWebGaoDeMapUri( String sLon, String sLat,String dLon, String dLat) {
       // http://m.amap.com/navi/?start=116.403124,39.940693&dest=116.481488,39.990464&destName=阜通西&naviBy=car&key=您的Key
//        String uri = "http://m.amap.com/navi/?start="+sLon+","+sLat
//                + "&dest=" + dLon + "," + dLat + "&destName=孩子位置&naviBy=car&key=3f37e6f1e695c5d718dd3619e19cf25a";
//        此方式的公共交通不友好  放弃
//         http://uri.amap.com/navigation?from=116.478346,39.997361,我的位置&to=116.3246,39.966577
// ,孩子位置&via=&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=1
//
        String uri ="http://uri.amap.com/navigation?from=" +
                sLon + "," + sLat + ",我的位置&to=" + dLon + "," + dLat +
                ",孩子位置&via=&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=1";
        return String.format(uri);
    }


}
