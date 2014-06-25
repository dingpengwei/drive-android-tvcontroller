package com.goodow.android.drive.tvcontroller;

import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.json.Json;
import com.goodow.realtime.json.JsonObject;

import com.google.inject.Inject;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;

/**
 * 遥控器(暂定版)
 * 
 * @author dpw
 * 
 */
public class TVControllerActivity_backup extends RoboActivity implements View.OnClickListener {
  /**
   * 键盘方向键
   * 
   * @author dpw
   * 
   */
  private class KeyboardCursor extends RelativeLayout {

    private static final int CURSOR_WIDTH = 360;
    private static final int CURSOR_HEIGHT = 360;
    private static final int RADIUS = 60;
    private int SCREEN_WIDTH = 0;
    private int SCREEN_HEIGHT = 0;
    private static final int LEVEL_0 = 0;
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_4 = 4;
    private static final int LEVEL_5 = 5;

    private ImageView cursor = null;

    private OnDismissMenuListener dismissMenuListener = null;

    public KeyboardCursor(Context context, OnDismissMenuListener dismissMenuListener) {
      super(context);
      this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
      this.setBackgroundResource(R.drawable.monitor_beginer_guide_bg);
      this.cursor = new ImageView(context);
      this.cursor.setImageResource(R.drawable.select_remotecontrol_cursor_bg);
      this.cursor.setImageLevel(LEVEL_0);
      LayoutParams cursorParams = new LayoutParams(CURSOR_WIDTH, CURSOR_HEIGHT);
      cursorParams.addRule(RelativeLayout.CENTER_IN_PARENT);
      this.addView(this.cursor, cursorParams);

      this.SCREEN_WIDTH = DeviceInformationTools.getScreenWidth(context);
      this.SCREEN_HEIGHT = DeviceInformationTools.getScreenHeight(context);

      this.dismissMenuListener = dismissMenuListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      if (this.dismissMenuListener != null && !this.dismissMenuListener.isDismiss()) {
        this.dismissMenuListener.onDismiss();
        return false;
      }
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
  /**
   * 唤出菜单
   * 
   * @author dpw
   * 
   */
  private class OnCallMenuClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      if (TAG_LEFT.equals(v.getTag().toString())) {
        // 点击左菜单按钮
        if (CURRENT_STATUS != STATUS_LEFT) {
          // 由正常状态到左菜单状态
          CURRENT_STATUS = STATUS_LEFT;
          leftMenuLayout.setVisibility(View.VISIBLE);
          ((LinearLayout) contentLayout.getParent()).updateViewLayout(contentLayout, contentLayout
              .getLayoutParams());
        } else {
          // 由左菜单状态到正常状态
          CURRENT_STATUS = STATUS_NORMAL;
          leftMenuLayout.setVisibility(View.GONE);
        }
      }
      if (TAG_RIGTH.equals(v.getTag().toString())) {
        // 点击右菜单按钮
        if (CURRENT_STATUS != STATUS_RIGHT) {
          // 由正常状态到右菜单状态
          CURRENT_STATUS = STATUS_RIGHT;
          rightMenuLayout.setVisibility(View.VISIBLE);
          ((LinearLayout.LayoutParams) rightMenuLayout.getLayoutParams()).leftMargin = -MENU_WIDTH;
          ((LinearLayout) rightMenuLayout.getParent()).updateViewLayout(rightMenuLayout,
              rightMenuLayout.getLayoutParams());
          ((LinearLayout.LayoutParams) contentLayout.getLayoutParams()).leftMargin = -MENU_WIDTH;
          ((LinearLayout.LayoutParams) contentLayout.getLayoutParams()).rightMargin = MENU_WIDTH;
          ((LinearLayout) contentLayout.getParent()).updateViewLayout(contentLayout, contentLayout
              .getLayoutParams());
        } else {
          // 由右菜单状态到正常状态
          CURRENT_STATUS = STATUS_NORMAL;
          rightMenuLayout.setVisibility(View.GONE);
          ((LinearLayout.LayoutParams) contentLayout.getLayoutParams()).leftMargin = 0;
          ((LinearLayout.LayoutParams) contentLayout.getLayoutParams()).rightMargin = 0;
          ((LinearLayout) contentLayout.getParent()).updateViewLayout(contentLayout, contentLayout
              .getLayoutParams());
        }
      }
    }
  }
  /**
   * 隐藏菜单的事件
   * 
   * @author dpw
   * 
   */
  private class OnDismissMenu implements OnDismissMenuListener {
    private View leftMenu = null;
    private View rightMenu = null;
    private View content = null;

    public OnDismissMenu(View leftMenu, View content, View rightMenu) {
      this.leftMenu = leftMenu;
      this.content = content;
      this.rightMenu = rightMenu;
    }

    @Override
    public boolean isDismiss() {
      if (this.leftMenu.getVisibility() == View.GONE && this.rightMenu.getVisibility() == View.GONE) {
        return true;
      }
      return false;
    }

    @Override
    public void onDismiss() {
      CURRENT_STATUS = STATUS_NORMAL;
      this.leftMenu.setVisibility(View.GONE);
      this.rightMenu.setVisibility(View.GONE);
      ((LinearLayout.LayoutParams) this.content.getLayoutParams()).leftMargin = 0;
      ((LinearLayout.LayoutParams) this.content.getLayoutParams()).rightMargin = 0;
      ((LinearLayout) this.content.getParent()).updateViewLayout(this.content, this.content
          .getLayoutParams());
    }

  }

  /**
   * 隐藏菜单
   * 
   * @author dpw
   * 
   */
  private interface OnDismissMenuListener {
    boolean isDismiss();

    void onDismiss();
  }

  /**
   * 菜单的点击事件
   * 
   * @author dpw
   * 
   */
  private class OnMenuItemClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      if (MENU_ITEM_NAME0.equals(v.getTag().toString())) {
        layoutCursor = new KeyboardCursor(TVControllerActivity_backup.this, onDismissMenu);
        ((RelativeLayout) contentLayout.getChildAt(0)).removeAllViews();
        ((RelativeLayout) contentLayout.getChildAt(0)).addView(layoutCursor);
      }
      if (MENU_ITEM_NAME1.equals(v.getTag().toString())) {
        layoutCursor = new TouchCursor(TVControllerActivity_backup.this, onDismissMenu);
        ((RelativeLayout) contentLayout.getChildAt(0)).removeAllViews();
        ((RelativeLayout) contentLayout.getChildAt(0)).addView(layoutCursor);
      }
      if (MENU_ITEM_NAME2.equals(v.getTag().toString())) {
        //
      }
      if (MENU_ITEM_NAME3.equals(v.getTag().toString())) {
        //
      }
      if (MENU_ITEM_NAME4.equals(v.getTag().toString())) {
        //
      }
      if (MENU_ITEM_NAME5.equals(v.getTag().toString())) {
        //
      }
      if (MENU_ITEM_NAME6.equals(v.getTag().toString())) {
        //
      }
      if (MENU_ITEM_NAME7.equals(v.getTag().toString())) {
        //
      }
      CURRENT_STATUS = STATUS_NORMAL;
      leftMenuLayout.setVisibility(View.GONE);
      rightMenuLayout.setVisibility(View.GONE);
      ((LinearLayout.LayoutParams) contentLayout.getLayoutParams()).leftMargin = 0;
      ((LinearLayout.LayoutParams) contentLayout.getLayoutParams()).rightMargin = 0;
      ((LinearLayout) contentLayout.getParent()).updateViewLayout(contentLayout, contentLayout
          .getLayoutParams());
    }
  }

  /**
   * 触摸方向键
   * 
   * @author dpw
   * 
   */
  private class TouchCursor extends RelativeLayout {
    private static final int CURSOR_WIDTH = 120;
    private static final int CURSOR_HEIGHT = 120;
    private int SCREEN_WIDTH = 0;
    private int SCREEN_HEIGHT = 0;
    private ImageView cursor = null;
    private LayoutParams cursorParams = new LayoutParams(CURSOR_WIDTH, CURSOR_HEIGHT);
    private AnimationSet animationSet = new AnimationSet(false);
    private AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
    private OnDismissMenuListener dismissMenuListener = null;

    private int action = 0;
    private int startx = 0;
    private int starty = 0;

    public TouchCursor(Context context, OnDismissMenuListener dismissMenuListener) {
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

      this.SCREEN_WIDTH = DeviceInformationTools.getScreenWidth(context);
      this.SCREEN_HEIGHT = DeviceInformationTools.getScreenHeight(context);

      this.dismissMenuListener = dismissMenuListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      if (this.dismissMenuListener != null && !this.dismissMenuListener.isDismiss()) {
        this.dismissMenuListener.onDismiss();
        return false;
      }
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
  public static String REDIRECTTO_MOUSE = "mouse";
  private static int CURRENT_STATUS = 1;
  private static final int STATUS_NORMAL = 0;
  private static final int STATUS_LEFT = 1;
  private static final int STATUS_RIGHT = 2;

  private static final String TAG_LEFT = "left";
  private static final String TAG_RIGTH = "right";

  private LinearLayout leftMenuLayout = null;
  private RelativeLayout contentLayout = null;
  private LinearLayout rightMenuLayout = null;

  private RelativeLayout layoutCursor = null;

  private static final String MENU_ITEM_NAME0 = "按键遥控器";
  private static final String MENU_ITEM_NAME1 = "触摸遥控器";
  private static final String MENU_ITEM_NAME2 = "赛车模式";
  private static final String MENU_ITEM_NAME3 = "游戏手柄";
  private static final String MENU_ITEM_NAME4 = "扫一扫";
  private static final String MENU_ITEM_NAME5 = "互动应用";
  private static final String MENU_ITEM_NAME6 = "本地投影";
  private static final String MENU_ITEM_NAME7 = "设置";
  private static final String FUN_TAG_HOME = "home";
  private static final String FUN_TAG_BACK = "back";
  private static final String FUN_TAG_MENU = "menu";

  private static final int MENU_WIDTH = 0;// 菜单宽度
  private static final List<? extends MenuItem> leftMenuData = Arrays.asList(new MenuItem[] {
      new MenuItem(R.drawable.keying_normal, MENU_ITEM_NAME0),
      new MenuItem(R.drawable.touch_normal, MENU_ITEM_NAME1),
      new MenuItem(R.drawable.steer_wheel_normal, MENU_ITEM_NAME2),
      new MenuItem(R.drawable.joystick_normal, MENU_ITEM_NAME3)});
  private static final List<? extends MenuItem> rightMenuData = Arrays.asList(new MenuItem[] {
      new MenuItem(R.drawable.menu_right_scan_normal, MENU_ITEM_NAME4),
      new MenuItem(R.drawable.menu_right_apps_normal, MENU_ITEM_NAME5),
      new MenuItem(R.drawable.menu_right_project_normal, MENU_ITEM_NAME6),
      new MenuItem(R.drawable.menu_right_set_normal, MENU_ITEM_NAME7)});

  private OnDismissMenu onDismissMenu = null;

  private EditText search = null;
  private static String sid = null;
  private final static String address_input = ".drive.input";
  private final static String address_view = ".drive.view";

  @Override
  public void onClick(View v) {
    if (this.leftMenuLayout.getVisibility() != View.GONE
        || this.rightMenuLayout.getVisibility() != View.GONE) {
      CURRENT_STATUS = STATUS_NORMAL;
      this.leftMenuLayout.setVisibility(View.GONE);
      this.rightMenuLayout.setVisibility(View.GONE);
      ((LinearLayout.LayoutParams) this.contentLayout.getLayoutParams()).leftMargin = 0;
      ((LinearLayout.LayoutParams) this.contentLayout.getLayoutParams()).rightMargin = 0;
      ((LinearLayout) this.contentLayout.getParent()).updateViewLayout(this.contentLayout,
          this.contentLayout.getLayoutParams());
    }
    if (FUN_TAG_HOME.equals(v.getTag().toString())) {
      sid = this.search.getText().toString();
      if (sid == null) {
        Toast.makeText(this, sid + address_input, Toast.LENGTH_SHORT).show();
      }
      this.bus.send(sid + address_view, Json.createObject().set("redirectTo", "home"), null);
      return;
    }
    if (FUN_TAG_BACK.equals(v.getTag().toString())) {
      this.onSendKeyAction(KeyEvent.KEYCODE_BACK);
      return;
    }
    if (FUN_TAG_MENU.equals(v.getTag().toString())) {
      this.onSendKeyAction(KeyEvent.KEYCODE_MENU);
      return;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.HORIZONTAL);
    root.setBackgroundResource(R.drawable.ramote_gamebg);

    LinearLayout.LayoutParams leftMenuParams =
        new LinearLayout.LayoutParams(MENU_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT);

    LinearLayout.LayoutParams rightMenuParams =
        new LinearLayout.LayoutParams(MENU_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT);

    LinearLayout.LayoutParams contentParams =
        new LinearLayout.LayoutParams(DeviceInformationTools.getScreenWidth(this),
            ViewGroup.LayoutParams.MATCH_PARENT);

    root.addView(this.leftMenuLayout =
        this.inflaterMenuLayout(leftMenuData, R.drawable.menu_shadow), leftMenuParams);
    root.addView(this.contentLayout = this.inflaterContentLayout(), contentParams);
    root.addView(this.rightMenuLayout =
        this.inflaterMenuLayout(rightMenuData, R.drawable.menu__right_shadow), rightMenuParams);
    this.setContentView(root);

    this.onDismissMenu =
        new OnDismissMenu(this.leftMenuLayout, this.contentLayout, this.rightMenuLayout);
    this.layoutCursor = new KeyboardCursor(this, this.onDismissMenu);

    RelativeLayout.LayoutParams cursorParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);
    cursorParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    ((RelativeLayout) this.contentLayout.getChildAt(0)).addView(this.layoutCursor, cursorParams);

  }

  /**
   * 初始化中间布局
   * 
   * @return
   */
  private RelativeLayout inflaterContentLayout() {
    // 中间布局根布局
    RelativeLayout content = new RelativeLayout(this);

    // ////////////////////初始化中间鼠标区域////////////////////
    RelativeLayout.LayoutParams cursorParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);
    cursorParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    RelativeLayout cursorView = new RelativeLayout(this);
    content.addView(cursorView, cursorParams);
    // ////////////////////////////////////////

    // ////////////////////初始化顶部功能栏////////////////////
    RelativeLayout.LayoutParams titleParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
    titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    LinearLayout title = new LinearLayout(this);
    title.setOrientation(LinearLayout.HORIZONTAL);
    content.addView(title, titleParams);

    // 左边菜单Caller
    Button leftMenuCaller = new Button(this);
    leftMenuCaller.setLayoutParams(new LinearLayout.LayoutParams(30,
        ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    leftMenuCaller.setBackgroundResource(R.drawable.selector_left_menu_caller);
    /*
     * 当作模式切换
     */
    leftMenuCaller.setTag(MENU_ITEM_NAME0);
    leftMenuCaller.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (MENU_ITEM_NAME0.equals(v.getTag().toString())) {
          layoutCursor = new TouchCursor(TVControllerActivity_backup.this, onDismissMenu);
          ((RelativeLayout) contentLayout.getChildAt(0)).removeAllViews();
          ((RelativeLayout) contentLayout.getChildAt(0)).addView(layoutCursor);
          v.setTag(MENU_ITEM_NAME1);
        } else if (MENU_ITEM_NAME1.equals(v.getTag().toString())) {
          layoutCursor = new KeyboardCursor(TVControllerActivity_backup.this, onDismissMenu);
          ((RelativeLayout) contentLayout.getChildAt(0)).removeAllViews();
          ((RelativeLayout) contentLayout.getChildAt(0)).addView(layoutCursor);
          v.setTag(MENU_ITEM_NAME0);
        }
      }
    });
    // leftMenuCaller.setOnClickListener(new OnCallMenuClickListener());
    // leftMenuCaller.setTag(TAG_LEFT);
    title.addView(leftMenuCaller);

    this.search = new EditText(this);
    search.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, 22));
    title.addView(search);

    // 右边菜单Caller
    Button rightMenuCaller = new Button(this);
    rightMenuCaller.setVisibility(View.GONE);
    rightMenuCaller.setLayoutParams(new LinearLayout.LayoutParams(30,
        ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    rightMenuCaller.setBackgroundResource(R.drawable.selector_right_menu_caller);
    rightMenuCaller.setOnClickListener(new OnCallMenuClickListener());
    rightMenuCaller.setTag(TAG_RIGTH);
    title.addView(rightMenuCaller);
    // ////////////////////////////////////////

    // ///////////////////初始化底部功能栏/////////////////////selector_back.xml
    RelativeLayout.LayoutParams bottomParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
    bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    LinearLayout bottom = new LinearLayout(this);
    bottom.setOrientation(LinearLayout.HORIZONTAL);
    content.addView(bottom, bottomParams);

    ImageView home = new ImageView(this);
    home.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    home.setBackgroundResource(R.drawable.selector_home);
    home.setTag(FUN_TAG_HOME);
    home.setOnClickListener(this);
    bottom.addView(home);

    ImageView back = new ImageView(this);
    back.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, 90));
    back.setBackgroundResource(R.drawable.selector_back);
    back.setTag(FUN_TAG_BACK);
    back.setOnClickListener(this);
    bottom.addView(back);

    ImageView menu = new ImageView(this);
    menu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    menu.setBackgroundResource(R.drawable.selector_menu);
    menu.setTag(FUN_TAG_MENU);
    menu.setOnClickListener(this);
    bottom.addView(menu);
    // ////////////////////////////////////////

    return content;
  }

  /**
   * 初始化菜单布局
   * 
   * @param menuItems
   * @return
   */
  private <T extends MenuItem> LinearLayout inflaterMenuLayout(List<T> menuItems, int resid) {
    LinearLayout menuRoot = new LinearLayout(this);
    menuRoot.setBackgroundResource(resid);
    menuRoot.setOrientation(LinearLayout.VERTICAL);
    LinearLayout.LayoutParams iconParmas =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams nameParmas =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
    iconParmas.leftMargin = 10;
    iconParmas.topMargin = 10;
    iconParmas.bottomMargin = 10;
    nameParmas.leftMargin = 10;
    nameParmas.topMargin = 10;
    nameParmas.bottomMargin = 10;
    for (int i = 0; i < menuItems.size(); i++) {
      LinearLayout menuItem = new LinearLayout(this);
      menuItem.setOrientation(LinearLayout.HORIZONTAL);
      ImageView icon = new ImageView(this);
      icon.setLayoutParams(iconParmas);
      icon.setImageResource(menuItems.get(i).getIcon());
      TextView name = new TextView(this);
      name.setLayoutParams(nameParmas);
      name.setText(menuItems.get(i).getName());
      name.setTextColor(Color.WHITE);
      name.setTextSize(15);
      menuItem.addView(icon);
      menuItem.addView(name);
      menuItem.setClickable(true);
      menuItem.setTag(menuItems.get(i).getName());
      menuItem.setOnClickListener(new OnMenuItemClickListener());
      menuRoot.addView(menuItem);
    }
    return menuRoot;
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

  /**
   * 发送动作
   * 
   * @param key
   */
  private void onSendKeyAction(int key) {
    sid = this.search.getText().toString();
    if (sid == null) {
      Toast.makeText(this, sid + address_input, Toast.LENGTH_SHORT).show();
    }
    JsonObject msg = Json.createObject();
    msg.set("key", key);
    bus.send(sid + address_input, msg, null);
  }
}

/**
 * 条目
 * 
 * @author dpw
 * 
 */
class MenuItem {
  private int icon;
  private String name;

  public MenuItem(int icon, String name) {
    super();
    this.icon = icon;
    this.name = name;
  }

  public int getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  public void setName(String name) {
    this.name = name;
  }
}
