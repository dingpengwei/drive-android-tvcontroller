package com.goodow.android.drive.tvcontroller;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by dpw on 5/22/14.
 */
public class DeviceInformationTools {

  /*
   * 获取屏幕高度
   */
  public static int getScreenHeight(Context context) {
    DisplayMetrics dm = new DisplayMetrics();
    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    int height = dm.heightPixels;
    return height;
  }

  /*
   * 获取屏幕宽度
   */
  public static int getScreenWidth(Context context) {
    DisplayMetrics dm = new DisplayMetrics();
    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    return width;
  }
}
