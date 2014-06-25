package com.goodow.android.drive.tvcontroller;

import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.json.Json;
import com.goodow.realtime.json.JsonObject;

import com.google.inject.Inject;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * @Author:DingPengwei
 * @Email:dingpengwei@goodow.com
 * @DateCrate:May 28, 2014 5:55:23 PM
 * @DateUpdate:May 28, 2014 5:55:23 PM
 * @Des:description
 */
public class TVControllerActivity extends RoboActivity implements OnClickListener {

  private class KeyboardCursor extends RelativeLayout {

    private static final int CURSOR_WIDTH = 360;
    private static final int CURSOR_HEIGHT = 360;
    private static final int RADIUS = 60;
    private static final int LEVEL_0 = 0;
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_4 = 4;
    private static final int LEVEL_5 = 5;

    private ImageView cursor = null;

    public KeyboardCursor(Context context) {
      super(context);
      this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
      this.setBackgroundResource(R.drawable.monitor_beginer_guide_bg);
      this.cursor = new ImageView(context);
      this.cursor.setImageResource(R.drawable.select_remotecontrol_cursor_bg);
      this.cursor.setImageLevel(LEVEL_0);
      LayoutParams cursorParams = new LayoutParams(CURSOR_WIDTH, CURSOR_HEIGHT);
      cursorParams.addRule(RelativeLayout.CENTER_IN_PARENT);
      this.addView(this.cursor, cursorParams);

      SCREEN_WIDTH = DeviceInformationTools.getScreenWidth(context);
      SCREEN_HEIGHT = DeviceInformationTools.getScreenHeight(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      float x = event.getX();
      float y = event.getY();
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          int downLevel = this.getCursorImageLevel(x, y);
          this.cursor.setImageLevel(downLevel);
          onKeyBoardCursor(downLevel);
          return true;
        case MotionEvent.ACTION_MOVE:
          int moveLevel = this.getCursorImageLevel(x, y);
          if (moveLevel != LEVEL_5) {
            this.cursor.setImageLevel(moveLevel);
            onKeyBoardCursor(moveLevel);
          }
          return true;
        case MotionEvent.ACTION_UP:
          this.cursor.setImageLevel(LEVEL_0);
          return true;
        default:
          break;
      }
      return super.onTouchEvent(event);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
      return super.onCreateDrawableState(extraSpace);
    }

    /**
     * 根据点击位置获取点击的是图片的那个区域
     * 
     * @param x
     * @param y
     * @return
     */
    private int getCursorImageLevel(float x, float y) {
      double distance =
          Math.sqrt(Math.pow(x - SCREEN_WIDTH / 2, 2) + Math.pow(y - SCREEN_HEIGHT / 2, 2));
      if (distance <= RADIUS) {
        // 点击是中间
        return LEVEL_5;
      }

      if (distance > RADIUS && distance < CURSOR_WIDTH / 2) {
        // 点击是环形
        float tan = (y - (float) SCREEN_HEIGHT / 2) / (x - (float) SCREEN_WIDTH / 2);
        if ((tan < 0 && tan >= -1 || tan > 0 && tan < 1) && (x - (float) SCREEN_WIDTH / 2) < 0) {
          // 左边
          return LEVEL_1;
        }
        if ((tan < -1 || tan >= 1) && (y - (float) SCREEN_HEIGHT / 2) < 0) {
          // 上边
          return LEVEL_2;
        }
        if ((tan < 0 && tan >= -1 || tan > 0 && tan < 1) && (x - (float) SCREEN_WIDTH / 2) > 0) {
          // 右边
          return LEVEL_3;
        }
        if ((tan < -1 || tan >= 1) && (y - (float) SCREEN_HEIGHT / 2) > 0) {
          // 下边
          return LEVEL_4;
        }
      }
      return LEVEL_0;
    }
  }

  private class OnModeControllerClickListener implements OnClickListener {
    @Override
    public void onClick(View v) {
      if (currentMode == R.drawable.monitor_beginer_guide_bg) {
        act_tvcontroller_container.setBackgroundResource(R.drawable.remotecontrol_bg);
        currentMode = R.drawable.remotecontrol_bg;
        rl_tv_controller_container.removeAllViews();
        rl_tv_controller_container.addView(touchCursor);
      } else {
        act_tvcontroller_container.setBackgroundResource(R.drawable.monitor_beginer_guide_bg);
        currentMode = R.drawable.monitor_beginer_guide_bg;
        rl_tv_controller_container.removeAllViews();
        rl_tv_controller_container.addView(keyboardCursor);
      }
    }
  }

  private class TouchCursor extends RelativeLayout {
    private static final int CURSOR_WIDTH = 120;
    private static final int CURSOR_HEIGHT = 120;
    private ImageView cursor = null;
    private LayoutParams cursorParams = new LayoutParams(CURSOR_WIDTH, CURSOR_HEIGHT);
    private AnimationSet animationSet = new AnimationSet(false);
    private AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);

    private int action = 0;
    private int startx = 0;
    private int starty = 0;

    public TouchCursor(Context context) {
      super(context);
      this.animationSet.setFillAfter(false);
      this.animationSet.setDuration(500);
      this.animationSet.addAnimation(this.alphaAnimation);
      this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
      this.setBackgroundResource(R.drawable.remotecontrol_bg);
      this.cursor = new ImageView(context);
      this.cursor.setImageResource(R.drawable.ic_remotecontrol_cursor);
      this.cursor.setVisibility(View.GONE);
      this.cursor.setAnimation(animationSet);
      this.cursorParams.width = CURSOR_WIDTH;
      this.cursorParams.height = CURSOR_HEIGHT;
      this.addView(this.cursor, this.cursorParams);

      SCREEN_WIDTH = DeviceInformationTools.getScreenWidth(context);
      SCREEN_HEIGHT = DeviceInformationTools.getScreenHeight(context);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      int x = (int) event.getX();
      int y = (int) event.getY();
      this.cursorParams.setMargins(x - CURSOR_WIDTH / 2, y - CURSOR_HEIGHT / 2, SCREEN_WIDTH
          - (x + CURSOR_WIDTH / 2), SCREEN_HEIGHT - (y + CURSOR_HEIGHT / 2));
      this.updateViewLayout(this.cursor, this.cursorParams);
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          this.cursor.setVisibility(View.VISIBLE);
          this.action = MotionEvent.ACTION_DOWN;
          this.startx = (int) event.getRawX();
          this.starty = (int) event.getRawY();
          return true;
        case MotionEvent.ACTION_MOVE:
          this.action = MotionEvent.ACTION_MOVE;
          int disx = (int) (event.getRawX() - startx);
          int disy = (int) (event.getRawY() - starty);
          this.onMoving(disx, disy);
          return true;
        case MotionEvent.ACTION_UP:
          this.cursor.startAnimation(animationSet);
          this.cursor.setVisibility(View.GONE);
          if (this.action == MotionEvent.ACTION_DOWN) {
            onSendKeyAction(KeyEvent.KEYCODE_ENTER);
          }
          this.action = MotionEvent.ACTION_UP;
          return true;
        default:
          break;
      }
      return super.onTouchEvent(event);
    }

    private void onMoving(int disx, int disy) {
      if (disx < 0) {
        // left
        if (disy < 0) {
          // left up(2)
          if (Math.abs(disx) < Math.abs(disy)) {
            // up
            onKeyBoardCursor(2);
          } else {
            // left
            onKeyBoardCursor(1);
          }
        } else {
          // left down(3)
          if (Math.abs(disx) < Math.abs(disy)) {
            // down
            onKeyBoardCursor(4);
          } else {
            // left
            onKeyBoardCursor(1);
          }
        }
      } else {
        // right
        if (disy < 0) {
          // right up(1)
          if (Math.abs(disx) < Math.abs(disy)) {
            // up
            onKeyBoardCursor(2);
          } else {
            // right
            onKeyBoardCursor(3);
          }
        } else {
          // update down(4)
          if (Math.abs(disx) < Math.abs(disy)) {
            // down
            onKeyBoardCursor(4);
          } else {
            // right
            onKeyBoardCursor(3);
          }
        }
      }
    }
  }

  @Inject
  Bus bus;
  @InjectView(R.id.act_tvcontroller_container)
  private RelativeLayout act_tvcontroller_container = null;
  @InjectView(R.id.ll_title)
  private LinearLayout ll_title = null;
  @InjectView(R.id.iv_mode_controller)
  private ImageView iv_mode_controller = null;
  @InjectView(R.id.ll_bottom)
  private LinearLayout ll_bottom = null;
  @InjectView(R.id.iv_bottom_home)
  private ImageView iv_bottom_home = null;
  @InjectView(R.id.iv_bottom_back)
  private ImageView iv_bottom_back = null;
  @InjectView(R.id.iv_bottom_menu)
  private ImageView iv_bottom_menu = null;
  @InjectView(R.id.rl_tv_controller_container)
  private RelativeLayout rl_tv_controller_container = null;
  @InjectView(R.id.et_address)
  private EditText et_address = null;
  private static String sid = null;
  private final static String address_input = ".drive.input";
  private final static String address_view = ".drive.view";

  private int currentMode = R.drawable.monitor_beginer_guide_bg;
  private KeyboardCursor keyboardCursor = null;
  private TouchCursor touchCursor = null;

  private int SCREEN_WIDTH = 0;
  private int SCREEN_HEIGHT = 0;

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.iv_bottom_home:
        sid = this.et_address.getText().toString();
        if (sid == null) {
          Toast.makeText(this, sid + address_input, Toast.LENGTH_SHORT).show();
        }
        this.bus.send(sid + address_view, Json.createObject().set("redirectTo", "home"), null);

        break;
      case R.id.iv_bottom_back:
        this.onSendKeyAction(KeyEvent.KEYCODE_BACK);
        break;
      case R.id.iv_bottom_menu:
        this.onSendKeyAction(KeyEvent.KEYCODE_MENU);
        break;

      default:
        break;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.activity_tvcontroller);
    this.SCREEN_WIDTH = DeviceInformationTools.getScreenWidth(this);
    this.SCREEN_HEIGHT = DeviceInformationTools.getScreenHeight(this);
    this.iv_mode_controller.setOnClickListener(new OnModeControllerClickListener());
    this.keyboardCursor = new KeyboardCursor(this);
    this.touchCursor = new TouchCursor(this);
    this.rl_tv_controller_container.addView(this.keyboardCursor);

    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

    }
  }

  private void onKeyBoardCursor(int level) {
    int key = 0;
    if (level == 1) {
      key = KeyEvent.KEYCODE_DPAD_LEFT;
    }
    if (level == 2) {
      key = KeyEvent.KEYCODE_DPAD_UP;
    }
    if (level == 3) {
      key = KeyEvent.KEYCODE_DPAD_RIGHT;
    }
    if (level == 4) {
      key = KeyEvent.KEYCODE_DPAD_DOWN;
    }
    if (level == 5) {
      key = KeyEvent.KEYCODE_ENTER;
    }
    this.onSendKeyAction(key);
  }

  private void onSendKeyAction(int key) {
    sid = this.et_address.getText().toString();
    if (sid == null) {
      Toast.makeText(this, sid + address_input, Toast.LENGTH_SHORT).show();
    }
    JsonObject msg = Json.createObject();
    msg.set("key", key);
    bus.send(sid + address_input, msg, null);
  }

}
