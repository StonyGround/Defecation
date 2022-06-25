package com.kjy.care.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kjy.care.R;
import com.kjy.care.activity.fragment.TopFragment;
import com.kjy.care.activity.ui.CustomWebView;
import com.kjy.care.api.HttpUtil;
import com.kjy.care.util.AlertUtil;
import com.kjy.care.util.AppUtil;
import com.kjy.care.util.WifiUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.fragment.app.FragmentManager;

//XWalkActivity
public class WebActivity extends  BaseActivity implements View.OnClickListener {



    private String url="";
    private String title="";

    /**
     * 没有允许定位的设置
     */
 /*   public void initSettings() {
        XWalkSettings webSettings = webview_google.getSettings();

        //启用JavaScript
        webSettings.setJavaScriptEnabled(true);
        //允许js弹窗alert等，window.open方法打开新的网页，默认不允许
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //localStorage和sessionStorage
        webSettings.setDomStorageEnabled(true);
        //Web SQL Databases
        webSettings.setDatabaseEnabled(true);
        //是否可访问Content Provider的资源，默认值 true
        webSettings.setAllowContentAccess(true);

        *//*
        是否允许访问文件系统，默认值 true
        file:///androMSG_asset和file:///androMSG_res始终可以访问，不受其影响
         *//*
        webSettings.setAllowFileAccess(true);
        //是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSettings.setAllowFileAccessFromFileURLs(true);
        //是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        //设置是否支持缩放
        webSettings.setSupportZoom(false);
        //设置内置的缩放控件
        webSettings.setBuiltInZoomControls(false);

        *//*
         当该属性被设置为false时，加载页面的宽度总是适应WebView控件宽度；
         当被设置为true，当前页面包含viewport属性标签，在标签中指定宽度值生效，如果页面不包含viewport标签，无法提供一个宽度值，这个时候该方法将被使用。
         *//*
        webSettings.setUseWideViewPort(false);
        //缩放至屏幕大小
        webSettings.setLoadWithOverviewMode(true);
        //支持多窗口
        webSettings.setSupportMultipleWindows(true);

        *//*
        缓存模式
        LOAD_CACHE_ONLY         不使用网络，只读取本地缓存
        LOAD_DEFAULT            根据cache-control决定是否从网络上获取数据
        LOAD_NO_CACHE           不使用缓存，只从网络获取数据
        LOAD_CACHE_ELSE_NETWORK 只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
         *//*
        webSettings.setCacheMode(XWalkSettings.LOAD_DEFAULT);
        //设置是否加载图片
        webSettings.setLoadsImagesAutomatically(true);

        //允许远程调试
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
    }
    @Override
    protected void onXWalkReady() {
        initSettings();

       // url="http://www.baidu.com/";

        webview_google.setUIClient(new XWUIClient(webview_google));
        webview_google.setResourceClient(new XWResourceClient(webview_google));
       //  webview_google.addJavascriptInterface(new AppShell(this), AppShell.TAG);

        url="http://hos.kjyun.net";
        webview_google.loadUrl(url);
        Log.e("加载","===============>"+url);
    }

    */



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);


        hookWebView();

        setContentView(R.layout.activity_web);

        try {
            Intent intent =getIntent();
            url = intent.getStringExtra("url");
            title = intent.getStringExtra("title");
        }catch (Exception e){

        }

        Log.e("url","===>"+url);


        initView();

        TextView_title.setText(title);


    }


    public   void hookWebView(){
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                // Log.i("sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                // Log.i("Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if(sdkInt < 26){//低于Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String)chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory!=null){
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null){
                field.set("sProviderInstance", sProviderInstance);
                // Log.i("Hook success!");
            } else {
                // Log.i("Hook failed!");
            }
        } catch (Throwable e) {
            // Log.w(e.getMessage());
        }
    }





    public static void luncher(){
        Intent intent =new Intent();
        intent.setClass(BaseApp.getAppContext(), WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getAppContext().startActivity(intent);
    }


    private boolean checkHostName(String url){


        return true;
    }
    /**
     * url请求,本地加载
     *
     * @param url url请求
     * @return 拦截结果
     */
    private WebResourceResponse interceptRes(String url) {
        if (url.contains("/res/") ) {
            //假设请求url包含res资源目录,则表明是去想服务器请求资源,而本地
            //已经包含了这个资源,则拦截这个请求,返回本地的资源
            //注意返回一个WebResourceResponse则表示拦截,如果返回null则表示不拦截,正常请求服务器
            WebResourceResponse webResourceResponse = null;
            try {
                int dianIndex = url.lastIndexOf(".");
                if (dianIndex != -1) {//获取资源文件的格式,例如这是一个.png,还是.js,还是.mp3
                    String mime = url.substring(dianIndex + 1);
                    if (mime.equals("js")) {//MimeTypeMap没有js格式需要特殊处理
                        mime = "application/x-javascript";
                    } else if (mime.equals("ttf")) {//MimeTypeMap没有字体格式需要特殊处理
                        mime = "application/octet-stream";
                    } else{//其他格式可以MimeTypeMap获取其mime类型
                        mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mime);
                    }
                    if (mime == null) {//假设没有获取到正常的mime类型,则设置为通用类型
                        mime = "*/*";
                    }
                    //     Log.e("siyehua-new", " mime: " + mime);
                    File file = new File(url);//获取本地的资源
                    if (!file.exists()) {
                        //假设本地没有这个资源,即开始没有下载好,则返回null
                        //表示不拦截,去请求服务器的资源
                        //  Log.e("siyehua", "file no found");
                        return null;
                    }
                    //一切正常,创建webResourceResponse
                    webResourceResponse = new WebResourceResponse(mime, "utf-8", new
                            FileInputStream(url));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return webResourceResponse;
        }
        return null;
    }
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    CustomWebView webview;
    AVLoadingIndicatorView AVLoadingIndicatorView_avi;

    TextView TextView_title;


    //  private XWalkView webview_google;

    private void initView(){

        RelativeLayout RelativeLayout_app =  findViewById(R.id.RelativeLayout_app);

        //界面旋转180度
      //  RelativeLayout_app.setRotation(180);


        FragmentManager fm = getSupportFragmentManager();
        TopFragment topFragment = (TopFragment) fm.findFragmentById(R.id.fragment_top);




        TextView_title =  findViewById(R.id.TextView_title);


        //   webview_google = findViewById(R.id.webview_google);




        webview =  findViewById(R.id.webview);


        AVLoadingIndicatorView_avi = findViewById(R.id.AVLoadingIndicatorView_avi);


        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webview.setWebViewClient(new WebViewClient() {




            @Override
            @Deprecated
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                try {// API21之前屏蔽广告
                    String hostName = new URL(url).getHost();
                    if (hostName != null) {
                        if (checkHostName(hostName)) {//checkHostName:检查是不是自己APP的域名,不是则不允许加载
                            return super.shouldInterceptRequest(view, url);
                        } else {
                            //   Log.e("siwebview", "no mine host!" + hostName);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //不是自己的APP域名,返回一个null,则屏蔽了广告
                return new WebResourceResponse(null, null, null);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest
                    request) {//高版本屏蔽广告同理
                String url = request.getUrl().toString();
                if (Build.VERSION.SDK_INT >= 21) {// API21后,屏蔽广告
                    String hostName = request.getUrl().getHost();
                    if (hostName != null) {
                        if (checkHostName(hostName)) {
                        } else {
                            return new WebResourceResponse(null, null, null);
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            /**
             * 覆写此方法是为了让webview仅加载自己的页面
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            /**
             * 页面加载开始
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                try {
                    //有的APP需要在网页里注入js,而这个js的注入是由客户端完成的,因为js里面包含的一些用户的信息等,无法通过网页结果获取.
                  /*  InputStream inputStream = getContext().getAssets().open("android_qqsd.js");
                    String a = convertStreamToString(inputStream);
                    loadUrl("javascript:" + a + "");*/
                } catch (Exception e) {
                    //  e.printStackTrace();
                }

            }


            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // super.onReceivedError(view, errorCode, description, failingUrl);
                AVLoadingIndicatorView_avi.setVisibility(View.GONE);
                Log.e("web", "errorCode="+errorCode+" \ndescription="+description+" \nfailingUrl="+failingUrl);
                //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
                // view.loadData(errorHtml, "text/html", "UTF-8");
                try {
                    //不行
                    //  view.loadData(URLEncoder.encode(errorHtml, "utf-8"), "text/html", "utf-8");
                    // view.setWebChromeClient(new MyWebChromeClient());
                    //可用
                    // view.loadDataWithBaseURL(null,errorHtml, "text/html",  "utf-8", null);


                    //+view.loadUrl("file:///android_asset/index.html");





                    if(errorCode==-8 || errorCode==-6){
                        //  description=net::ERR_CONNECTION_TIMED_OUT
                        //     description=net::ERR_CONNECTION_REFUSED
                        //连不上服务器
                        //连不上服务器
                        if(WifiUtils.isWifiConnect(WebActivity.this)){
                           view.loadUrl("file:///android_asset/error.html");
                        }else{
                          view.loadUrl("file:///android_asset/index.html");
                        }

                    }else{
                        if(errorCode==-2){
                            //description=net::ERR_ADDRESS_UNREACHABLE
                            //没有网络

                        }
                        if(!WifiUtils.isWifiConnect(WebActivity.this)){
                             view.loadUrl("file:///android_asset/index.html");
                        }
                    }




                }catch (Exception e){

                }
                // view.loadDataWithBaseURL(null,data, "text/html",  "utf-8", null);


            }

            /**
             * 页面加载完成(注意部分手机并不会执行该方法)
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                AVLoadingIndicatorView_avi.setVisibility(View.GONE);
            }

        });
        WebSettings settings = webview.getSettings(); //默认是false 设置true允许和js交互

        settings.setPluginState(WebSettings.PluginState.ON);//支持插件
/**
 * 输入法控制
 */
        settings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点
        settings.setSupportMultipleWindows(true);//设置支持多窗口
/**
 * 手机如果开启了缩放字体,网页显示的比例会失调
 */
        settings.setTextZoom(100);//禁止系统缩放字体大小
/**
 * 以下三项禁止设置,否则退出应用后会因为缩放组件没有及时取消广播而闪退
 */
//settings.setSupportZoom(true);//设置支持缩放
//settings.setBuiltInZoomControls(true);//设置支持缩放
//settings.setUseWideViewPort(true);//用户可以可以网页比例

        String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;


        //设置数据库缓存路径
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        settings.setAppCachePath(cacheDirPath);

        settings.setUseWideViewPort(true); // 关键点
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        settings.setJavaScriptEnabled(true);// 可以使用javaScriptEnalsed
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//js可以自动打开窗口
        settings.setAllowFileAccess(true);//支持引用文件
        settings.setAppCacheEnabled(true);//设置支持本地存储
        settings.setDomStorageEnabled(true);//可以手动开启DOM Storage
        if (Build.VERSION.SDK_INT >= 17) {// 不需要请求控制直接播放媒体文件
            //即可以自动播放音乐
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 设置浏览器可弹窗
        webview.setWebChromeClient(new MyWebChromeClient());


        AVLoadingIndicatorView_avi.setVisibility(View.VISIBLE);


        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // TODO: 2017-5-6 处理下载事件

             //   downloadByBrowser(url);

                downloadBySystem(url, contentDisposition, mimeType);
            }
        });


        //url="http://hsc.kjyun.net/#/detail?id=40&title=%E5%A4%B1%E7%9C%A0%E4%BA%86%EF%BC%8C%E8%AF%A5%E5%90%83%E8%8D%AF%E4%BA%86";

        webview.loadUrl(url);

    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName  = URLUtil.guessFileName(url, contentDisposition, mimeType);
       // log.debug("fileName:{}", fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);


        AlertUtil.show("开始下载:"+fileName+",请勿重复点击!");
       // log.debug("downloadId:{}", downloadId);
    }

    /*
    getSettings().setJavaScriptEnabled(true);// 可以使用javaScriptEnalsed

    //jsObjectStr是在网页中调用的对象
    addJavascriptInterface(new JsObject(), "jsObjectStr");

    class JsObject {
        // @JavascriptInterface是为了支4.2及以上的js交互
        @JavascriptInterface
        public void app_method() {
            //对应的逻辑操作
        }
    }

//对应的网页代码:
jsObjectStr.app_method();
*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }






    @Override
    protected void onStart() {
        // TODO Auto-generated method stub


        super.onStart();

        // AppUtil.hidSysNavigation(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.LinearLayout_back:
                finish();
                break;
        }




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webview!=null){
            webview.destroy();
        }
    }

    /**
     * 浏览器可弹窗
     *
     * @author Administrator
     */
    final class MyWebChromeClient extends WebChromeClient {






        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

           /* WebView   newWebView = new WebView(view.getContext());
            WebSettings settings = newWebView.getSettings();
            settings.setJavaScriptEnabled(true);//支持js


            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //拦截url，跳转新窗口
                    LogH.e( "shouldOverrideUrlLoading->"+url);
                    AppUtil.toWeb(url,view.getTitle());

                    newWebView.destroy();
                    //防止触发现有界面的WebChromeClient的相关回调
                    return true;
                }
            });


           WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();*/



            Message href = view.getHandler().obtainMessage();
            view.requestFocusNodeHref(href);

            String url = href.getData().getString("url");
            String title = href.getData().getString("title");

            AppUtil.toWeb(url,title);

           // LogH.e( "onCreateWindow: 新标签中打开网页->"+ url);
            return true;
        }

        /**
         * js的注入,除了前面的方法,还可以听过监听该方法来注入
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress > 80) {//80%是一个大概的数字,实际上该注入方法有可能失败
                try {
                    Log.e("加载","进度==》"+newProgress);
                    AVLoadingIndicatorView_avi.setVisibility(View.GONE);
                    //  InputStream inputStream = MainActivity.this.getAssets().open("android_qqsd.js");
                    //String a = convertStreamToString(inputStream);
                    // loadUrl("javascript:" + a + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message, final  JsResult result) {
            //return super.onJsAlert(view, url, message, result);

            new AlertDialog.Builder(WebActivity.this).setTitle(HttpUtil.getAppName).setMessage(message).setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).create().show();
            return true;
        }

        /**
         * 设置该方法网页的js可以弹出确认/取消对话框
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult
                result) {
            new AlertDialog.Builder(WebActivity.this).setTitle(HttpUtil.getAppName).setMessage(message).setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).create().show();
            return true;
        }

        //获得网页的标题，作为应用程序的标题进行显示
        @Override
        public void onReceivedTitle(WebView view, String title) {
            //MainActivity.this.setTitle(title);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);

            //  System.exit(0);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }


        /*** 视频播放相关的方法 **/

        @Override
        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(WebActivity.this);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            return frameLayout;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            showCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            hideCustomView();
        }
    }
    /** 视频全屏参数 */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private FrameLayout fullscreenContainer;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    /** 视频播放全屏 **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        WebActivity.this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(WebActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /** 隐藏视频全屏 */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        webview.setVisibility(View.VISIBLE);
    }

    /** 全屏容器界面 */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
                if (customView != null) {
                    hideCustomView();
                } else if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    /**
     * 4.4以上可用 evaluateJavascript 效率高
     * jsCode   javascript:callJS()
     */
    private void loadJs(String jsCode) {
        if(webview!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webview.evaluateJavascript(jsCode, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                        Log.e("返回值","网页已接收返回=="+value);
                    }
                });
            } else {
                webview.loadUrl(jsCode);
            }
        }
    }


    /**
     * js通信接口
     * kjy_android
     */
    public class MyJavascriptInterface {
        private Context context;

        public MyJavascriptInterface(Context context) {
            this.context = context;
        }

        /**
         * window.kjy_android.run
         * 网页使用的js，方法有参数，且参数名为data
         * @param data 网页js里的参数名
         */
        @JavascriptInterface
        public void run(String data) {
            Log.e("startFunction", "----有参" + data);



        }

        @JavascriptInterface
        public void open(String data) {
            String url="";
            switch (data){
                case "wifi":
                    url="com.android.settings.wifi.WifiSettings";
                    //  AppUtil.toSystemActivity(url,MainActivity.this);
                    break;


            }



        }

        @JavascriptInterface
        public void reload() {

            //   EventBus.getDefault().post(MessageEvent.WEB_REFRESH);
            // reloadWeb(true);


        }
    }






}
