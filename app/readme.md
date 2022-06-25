#2022-2-24
1.手动大小便，无论是否是在手动状态下，你都累计次数了，这是不对的，应该是自动大便和手动大便，开始协议和结束协议，计算一次，小便同样如此。  √
2.状态图片只运行了几秒就没有了，动作还没结束 √
3.“无人体“的语音太吓人了，应该改“未正确穿戴”  √
4.“漏水检测”语音改为，发现机器漏水，请立即断电，并联系售后 √
5.坐便器未装改“坐便器未正确安装” √
6.清水空了，改“清水空了，请先加水再开机操作” √
7.清水桶水位低，改“请先加满水”√
8.HOT_UP_KEY这的语音提示语也放出来，“清水桶已满，请停止加水”
9.WW-DOWN污水罐满了，改污"水罐满了，请倒掉污水" √
10.WW-up污水罐溢出了，改"污水罐异常，并联系售后" √
11.负压罐有水改“负压罐有水，请及时打开底部旋钮，将负压罐内水放完。”√
12.坐便器水溢出改”坐便器水溢出，请联系售后“ √


#2022-02-21
1.增加  净水桶 水位低  提示
2.增加语音提示


**重要说明
libs 包下面包含：
   libserial_port.so  配合 android_serialport_api 串口组件 使用
   kjy_blue@1.0.3_release.aar  设备蓝牙连接框架
   hellocharts-library-release.aar 折线图框架

#目录结构
```bash
            |---android_serialport_api 串口组件
            |
            |
            |
com.kjy.care-
            |    |-activity   UI 界面
            |    |       |-fragment   功能模块包
            |    |       |        |-BaseFragment
            |    |       |        |-ControlAutoFragment   [自动状态]弹出界面布局
            |    |       |        |-ControlUserFragment   [手动状态]弹出界面布局
            |    |       |        |-TopFragment   顶部界面布局
            |    |       |-ui  自定义UI包
            |    |       |     |-CustomWebView   自定义继承webview
            |    |       |     |-SlideButtom   自定义滑动ui按钮
            |    |       |
            |    |       |-BaseActivity   基础 Activity
            |    |       |-BaseApp   Application 入口
            |    |       |-DeviceActivity  设备扫描绑定界面
            |    |       |-HealthActivity   健康检测界面
            |    |       |-HelpActivity   帮助界面
            |    |       |-MainActivity   主界面入口
            |    |       |-SetActivity    设置界面
            |    |       |-UserActivity    用户中心包含【健康中心、设备管理、工作记录、设置、关于、帮助入口】
            |    |       |-UserInfoActivity   用户信息界面
            |    |       |-WebActivity    网页浏览界面
            |    |       |-WorkActivity   工作记录界面
            |    |-adapter  组件适配Adapter
            |    |-api  网络请求
            |    |-bean  各个接口实体类
            |    |-service  服务注册
            |    |-widget  自定义日历组件
            |    |---util 工具类包
```