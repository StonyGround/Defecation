package com.kjy.care.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.kjy.care.R;
import com.kjy.care.activity.BaseApp;


public class ButtonChangeUtil {


  /**
   * 停止
   *
   * @param view
   * @param context
   * @param sendCmd
   */
  public static void changeStop(ImageView view, Context context, boolean sendCmd, MySqlUtil mysql) {

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.stop_0).getConstantState()) {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.stop_1));
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.stop_0));
        }
      }, 1000);

    } else {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.stop_0));
    }

    if (sendCmd) {
      ComCmdUtil.sendStopCmd();
    }
    MyDataUtil.addRecordList(mysql, "按下停止键", BaseApp.User.getId());
  }

  /**
   * 水压
   *
   * @param view
   * @param context
   */
  public static void changeWaterPress(ImageView view, Context context, MySqlUtil mysql) {

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.water_press_1).getConstantState()) {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_press_0));
      changeWaterPressIco(view, context, 0);
      ComCmdUtil.sendWaterPressCmd(0);
      MyDataUtil.addRecordList(mysql, "水压弱 调节", BaseApp.User.getId());
    } else {
      // view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_press_1));
      changeWaterPressIco(view, context, 1);
      ComCmdUtil.sendWaterPressCmd(1);
      MyDataUtil.addRecordList(mysql, "水压强 调节", BaseApp.User.getId());
    }


  }

  /**
   * 改变水压图标等级
   *
   * @param view
   * @param context
   * @param lev
   */
  public static void changeWaterPressIco(ImageView view, Context context, int lev) {
    switch (lev) {
      case 0:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_press_0));
        SPUtil.set(context, SPUtil.WATER_PRESS, "0");
        IotUtil.ioTSDKManager.sendDP(101, 3, 1);
        break;
      case 1:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_press_1));
        SPUtil.set(context, SPUtil.WATER_PRESS, "1");
        IotUtil.ioTSDKManager.sendDP(101, 3, 0);
        break;
      default:
        break;

    }

  }


  /**
   * 水温
   *
   * @param view
   * @param context
   */
  public static void changeWaterTemper(ImageView view, Context context, MySqlUtil mysql) {

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.water_temper_0).getConstantState()) {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_1));
      changeWaterTemperIco(view, context, 1);
      ComCmdUtil.sendWaterTempCmd(1);
      MyDataUtil.addRecordList(mysql, "水温低 调节", BaseApp.User.getId());
    } else if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.water_temper_1).getConstantState()) {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_2));
      changeWaterTemperIco(view, context, 2);
      ComCmdUtil.sendWaterTempCmd(2);
      MyDataUtil.addRecordList(mysql, "水温中 调节", BaseApp.User.getId());
    } else if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.water_temper_2).getConstantState()) {
      // view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_3));
      changeWaterTemperIco(view, context, 3);
      ComCmdUtil.sendWaterTempCmd(3);
      MyDataUtil.addRecordList(mysql, "水温高 调节", BaseApp.User.getId());
    } else {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_0));
      changeWaterTemperIco(view, context, 0);
      ComCmdUtil.sendWaterTempCmd(0);
      MyDataUtil.addRecordList(mysql, "水温关 调节", BaseApp.User.getId());
    }

  }


  /**
   * 改变水温图标等级
   *
   * @param view
   * @param context
   * @param lev
   */
  public static void changeWaterTemperIco(ImageView view, Context context, int lev) {
    switch (lev) {
      case 0:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_0));
        SPUtil.set(context, SPUtil.WATER_TEMPER, "0");
        IotUtil.ioTSDKManager.sendDP(102, 3, 3);
        break;
      case 1:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_1));
        SPUtil.set(context, SPUtil.WATER_TEMPER, "1");
        IotUtil.ioTSDKManager.sendDP(102, 3, 2);
        break;
      case 2:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_2));
        SPUtil.set(context, SPUtil.WATER_TEMPER, "2");
        IotUtil.ioTSDKManager.sendDP(102, 3, 1);
        break;
      case 3:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.water_temper_3));
        SPUtil.set(context, SPUtil.WATER_TEMPER, "3");
        IotUtil.ioTSDKManager.sendDP(102, 3, 0);
        break;
      default:
        break;

    }

  }


  /**
   * 暖风
   *
   * @param view
   * @param context
   */
  public static void changeWarmAir(ImageView view, Context context, MySqlUtil mysql) {

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.warm_air_1).getConstantState()) {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.warm_air_0));
      changeWarmAirIco(view, context, 0);
      ComCmdUtil.sendWarmAirCmd(0);
      MyDataUtil.addRecordList(mysql, "暖风关 调节", BaseApp.User.getId());
    } else {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.warm_air_1));
      changeWarmAirIco(view, context, 1);
      ComCmdUtil.sendWarmAirCmd(1);
      MyDataUtil.addRecordList(mysql, "暖风开 调节", BaseApp.User.getId());
    }

  }

  /**
   * 改变暖风图标等级
   *
   * @param view
   * @param context
   * @param lev
   */
  public static void changeWarmAirIco(ImageView view, Context context, int lev) {
    switch (lev) {
      case 0:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.warm_air_0));
        SPUtil.set(context, SPUtil.WARM_AIR, "0");
        IotUtil.ioTSDKManager.sendDP(103, 3, 1);
        break;
      case 1:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.warm_air_1));
        SPUtil.set(context, SPUtil.WARM_AIR, "1");
        IotUtil.ioTSDKManager.sendDP(103, 3, 0);
        break;
      default:
        break;

    }

  }


  /**
   * 定时干燥
   *
   * @param view
   * @param context
   */
  public static void changeTimerDry(ImageView view, Context context, MySqlUtil mysql) {

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.timer_dry_0).getConstantState()) {
      // view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_1));

      changeTimerDryIco(view, context, 1);
      ComCmdUtil.sendTimerDryCmd(1);
      MyDataUtil.addRecordList(mysql, "定时干燥1h 调节", BaseApp.User.getId());

    } else if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.timer_dry_1).getConstantState()) {
      // view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_2));
      changeTimerDryIco(view, context, 2);
      ComCmdUtil.sendTimerDryCmd(2);
      MyDataUtil.addRecordList(mysql, "定时干燥2h 调节", BaseApp.User.getId());

    } else if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.timer_dry_2).getConstantState()) {
      // view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_3));
      changeTimerDryIco(view, context, 3);
      ComCmdUtil.sendTimerDryCmd(3);
      MyDataUtil.addRecordList(mysql, "定时干燥3h 调节", BaseApp.User.getId());

    } else if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.timer_dry_3).getConstantState()) {
      //  view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_4));

      changeTimerDryIco(view, context, 4);
      ComCmdUtil.sendTimerDryCmd(4);
      MyDataUtil.addRecordList(mysql, "定时干燥4h 调节", BaseApp.User.getId());

    } else {
      // view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_0));

      changeTimerDryIco(view, context, 0);
      ComCmdUtil.sendTimerDryCmd(0);
      MyDataUtil.addRecordList(mysql, "定时干燥关 调节", BaseApp.User.getId());

    }

  }


  /**
   * 改变定时干燥图标等级
   *
   * @param view
   * @param context
   * @param lev
   */
  public static void changeTimerDryIco(ImageView view, Context context, int lev) {
    switch (lev) {
      case 0:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_0));
        SPUtil.set(context, SPUtil.TIMER_DRY, "0");
        IotUtil.ioTSDKManager.sendDP(104, 3, 4);
        break;
      case 1:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_1));
        SPUtil.set(context, SPUtil.TIMER_DRY, "1");
        IotUtil.ioTSDKManager.sendDP(104, 3, 0);
        break;
      case 2:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_2));
        SPUtil.set(context, SPUtil.TIMER_DRY, "2");
        IotUtil.ioTSDKManager.sendDP(104, 3, 1);
        break;
      case 3:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_3));
        SPUtil.set(context, SPUtil.TIMER_DRY, "3");
        IotUtil.ioTSDKManager.sendDP(104, 3, 2);
        break;
      case 4:
        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.timer_dry_4));
        SPUtil.set(context, SPUtil.TIMER_DRY, "4");
        IotUtil.ioTSDKManager.sendDP(104, 3, 3);
        break;
      default:
        break;

    }

  }


  /**
   * 大便
   *
   * @param view
   * @param context
   */
  public static void changeDefecate(ImageView view, Context context, boolean sendCmd) {

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.defeate_2).getConstantState()) {
      return;
    }

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.defeate_0).getConstantState()) {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.defeate_1));
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.defeate_2));
        }
      }, 100);
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.defeate_0));
        }
      }, 1000);


    } else {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.defeate_0));
    }
    if (sendCmd) {
      ComCmdUtil.sendDefecateCmd();

    }
  }


  /**
   * 小便
   *
   * @param view
   * @param context
   */
  public static void changePee(ImageView view, Context context, boolean sendCmd) {
    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.pee_2).getConstantState()) {
      return;
    }

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.pee_0).getConstantState()) {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.pee_1));
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.pee_2));
        }
      }, 100);
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.pee_0));
        }
      }, 1000);
    } else {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.pee_0));
    }
    if (sendCmd) {
      ComCmdUtil.sendPeeCmd();
    }

  }


  /**
   * 强吸
   *
   * @param view
   * @param context
   */
  public static void changeWabsorption(ImageView view, Context context, boolean sendCmd,
      MySqlUtil mysql) {
    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.absorb_2).getConstantState()) {
      return;
    }

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.absorb_0).getConstantState()) {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.absorb_1));
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.absorb_2));
        }
      }, 100);
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.absorb_0));
        }
      }, 1000);

    } else {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.absorb_0));
    }

    if (sendCmd) {
      ComCmdUtil.sendAbsorptionCmd();
      MyDataUtil.addRecordList(mysql, "手动 强吸", BaseApp.User.getId());
    }
  }

  /**
   * 干燥
   *
   * @param view
   * @param context
   * @param sendCmd
   */
  public static void changeDry(ImageView view, Context context, boolean sendCmd, MySqlUtil mysql) {
    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.dry_2).getConstantState()) {
      return;
    }

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.dry_0).getConstantState()) {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.dry_1));
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.dry_2));
        }
      }, 100);
      view.postDelayed(new Runnable() {
        @Override
        public void run() {
          view.setImageDrawable(context.getResources().getDrawable(R.mipmap.dry_0));
        }
      }, 1000);
    } else {
      view.setImageDrawable(context.getResources().getDrawable(R.mipmap.dry_0));
    }

    if (sendCmd) {
      ComCmdUtil.sendDryCmd();
      MyDataUtil.addRecordList(mysql, "手动 干燥", BaseApp.User.getId());
    }

  }


  /**
   * 按摩
   *
   * @param view
   * @param context
   */
  public static void changeMassage(ImageView view, Context context, MySqlUtil mysql) {
    // if((view.getDrawable().getConstantState())== context.getResources().getDrawable(R.mipmap.massage_2).getConstantState()) {return;}

    if ((view.getDrawable().getConstantState()) == context.getResources()
        .getDrawable(R.mipmap.massage_0).getConstantState()) {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.massage_1));

      changeMassageIco(view, context, 1);
      MyDataUtil.addRecordList(mysql, "按摩开 调节", BaseApp.User.getId());
    } else {
      //view.setImageDrawable(context.getResources().getDrawable(R.mipmap.massage_0));

      changeMassageIco(view, context, 0);
      MyDataUtil.addRecordList(mysql, "按摩关 调节", BaseApp.User.getId());
    }

  }


  /**
   * 改变定时干燥图标等级
   *
   * @param view
   * @param context
   * @param lev
   */
  public static void changeMassageIco(ImageView view, Context context, int lev) {
    switch (lev) {
      case 0:

        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.massage_0));
        SPUtil.set(context, SPUtil.MASSAGE, "0");
        break;
      case 1:

        view.setImageDrawable(context.getResources().getDrawable(R.mipmap.massage_1));
        SPUtil.set(context, SPUtil.MASSAGE, "1");
        break;
      default:
        break;

    }

  }


  /**
   * 消息显示
   *
   * @param tips
   * @param tips_none
   * @param msg
   * @param context
   */
  public static void changeShowTips(RelativeLayout tips, RelativeLayout tips_none, TextView view,
      String msg, Context context) {

    view.setText(msg);

    Animation tips_none_Animation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

    Animation tips_Animation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);

    tips_none.startAnimation(tips_none_Animation);
    tips_none_Animation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        tips_none.setVisibility(View.GONE);
        tips.setVisibility(View.VISIBLE);
        tips.startAnimation(tips_Animation);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });

    tips_Animation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        tips.postDelayed(new Runnable() {
          @Override
          public void run() {
            changeHiddenTips(tips, tips_none, context);
          }
        }, 1500);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });


  }


  /**
   * 消息隐藏
   *
   * @param tips
   * @param tips_none
   * @param context
   */
  public static void changeHiddenTips(RelativeLayout tips, RelativeLayout tips_none,
      Context context) {

    Animation tips_none_Animation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

    Animation tips_Animation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);

    tips.startAnimation(tips_none_Animation);
    tips_none_Animation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        tips.setVisibility(View.GONE);
        tips_none.setVisibility(View.VISIBLE);
        tips_none.startAnimation(tips_Animation);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });


  }


  /**
   * 修改净水桶位置
   *
   * @param view
   * @param lev
   * @param context
   */
  public static void changeWaterClean(ImageView view, int lev, Context context) {
    switch (lev) {
      case 0:
        view.setImageResource(R.mipmap.bucket_null);
        IotUtil.ioTSDKManager.sendDP(105, 3, 3);
        break;
      case 1:
        view.setImageResource(R.mipmap.clean_water_0);
        IotUtil.ioTSDKManager.sendDP(105, 3, 2);
        break;
      case 2:
        view.setImageResource(R.mipmap.clean_water_1);
        IotUtil.ioTSDKManager.sendDP(105, 3, 1);
        break;
      case 3:
        view.setImageResource(R.mipmap.clean_water_2);
        IotUtil.ioTSDKManager.sendDP(105, 3, 0);
        break;
      default:
        //iew.setImageResource(R.mipmap.clean_water_2);
        break;
    }

  }


  /**
   * 修改污水罐位置
   *
   * @param view
   * @param lev
   * @param context
   */
  public static void changeWaterDirtyIco(ImageView view, Context context, int lev) {
    switch (lev) {
      case 1:
        view.setImageResource(R.mipmap.dirty_water_1);
        IotUtil.ioTSDKManager.sendDP(106, 3, 0);
        break;
      case 2:
        view.setImageResource(R.mipmap.dirty_water_2);
        IotUtil.ioTSDKManager.sendDP(106, 3, 1);
        break;
      default:
        view.setImageResource(R.mipmap.dirty_water_0);
        IotUtil.ioTSDKManager.sendDP(106, 3, 2);
        break;
    }
  }


  /**
   * 运行动画
   *
   * @param view
   * @param context
   */
  public static void runAnimation(ImageView view, TextView text, Context context) {

    Animation tips_none_Animation = AnimationUtils.loadAnimation(context, R.anim.run_rotate);

    view.startAnimation(tips_none_Animation);
    tips_none_Animation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {

        // resetRunAnimation(view,text,context);

      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });



       /* view.postDelayed(new Runnable() {
            @Override
            public void run() {
               // view.clearAnimation();

            }
        },3000);*/

  }


  public static void resetRunAnimation(ImageView view, TextView text, Context context) {

    view.post(new Runnable() {
      @Override
      public void run() {
        view.clearAnimation();
        view.setImageResource(R.mipmap.safe_3x);
        text.setText("守护模式");
        IotUtil.ioTSDKManager.sendDP(109, 3, 0);
      }
    });


  }

}
