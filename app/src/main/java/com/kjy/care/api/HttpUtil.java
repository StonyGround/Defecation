package com.kjy.care.api;

import com.kjy.care.activity.BaseApp;
import com.kjy.care.util.LogH;
import com.kjy.care.util.SPUtil;
import com.kjy.care.util.StringUtil;
import com.kjy.care.util.jsoncoverter.JsonConverterFactory;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Retrofit;


/**
 * Created by 420041900 on 2017/3/16.
 * 网络请求类
 */

public class HttpUtil {

   private static   HttpUtil  _HttpUtil=null;
    //获取实例
    private static Retrofit retrofit = null;
    private static ApiServer  api=null;
    public static String BaseUrl="hos.kjyun.net";//光明 192.168.101.155:8081  http://hos.kjyun.net


    public static String Token="";//请求头token

    /////////////////////////医院版本网版本接口///////////////////////////////
    public static  String submitHealthCheck="/android/submitHealthCheck";//提交数据
    public static  String getHealthCheck ="/android/getHealthCheck";//查询数据
    public static  String getAppVersion ="/android/getAppVersion";//查询数据
    public static  String getAppName ="AI小助手";//查询数据
    ////////////////////////////////////////////////////////

    /////////////////////////互联网版本接口///////////////////////////////
   // public static  String submitHealthCheck="/android/pad/submitHealthCheck";//提交数据
  //  public static  String getHealthCheck ="/android/pad/getHealthCheck ";//查询数据
    ////////////////////////////////////////////////////////





    public HttpUtil(){
        if(retrofit==null){


            Token   =   SPUtil.get(BaseApp.getAppContext(),SPUtil.TOKEN,"");


       String    baseUrl = SPUtil.get(BaseApp.getAppContext(),SPUtil.IP,BaseUrl);

            if(!baseUrl.startsWith("http")){
                baseUrl="http://"+baseUrl;
            }



            //if (BuildConfig.DEBUG) {

             /*   OkHttpClient.Builder client =new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        // Customize the request header
                        Request request = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("Authorization", "auth-token")
                                .method(original.method(), original.body())
                                .build();

                        Response response = chain.proceed(request);

                        // Customize or return the response
                        return response;
                    }
                });


            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();//使用 gson coverter，统一日期请求格式*/

//JsonConverterFactory.create()
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                    .build();
          //  OkHttpClient httpClient = new OkHttpClient();

            retrofit = new Retrofit.Builder()
                  //  .client(client.build())
                    //设置OKHttpClient,如果不设置会提供一个默认的
                    //设置baseUrl
                    .baseUrl(baseUrl)
                    //添加Gson转换器
                   // .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(JsonConverterFactory.create())
                    .client(httpClient)
                    .build();

        }

    }


    public static HttpUtil getinstance(){
        if(_HttpUtil==null){
            _HttpUtil=new HttpUtil();

            api=retrofit.create(ApiServer.class);
        }

        return _HttpUtil;


    }


    /**
     * Post异步请求
     * @param map
     * @param callback
     */
    public static void post(String path,Map map, Callback<JSONObject> callback){
      //  RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),map);

        LogH.e("token=》"+Token);


        if(StringUtil.isEmpty(Token)){
            api.main(path,map).enqueue(callback);
        }else{

            Map header=new HashMap();
            header.put("X-Requested-With", "XMLHttpRequest");
            header.put("Authorization", Token);
            api.main(header,path,map).enqueue(callback);
        }


    }


    /**
     * 文件下载
     * @param url
     * @param rang
     * @param callback
     */
    public static void download(String url, String rang, Callback<ResponseBody> callback){
        //  RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),map);


        api.download(rang,url).enqueue(callback);

    }



    /**
     * post同步请求
     * @param map
     * @return
     * @throws IOException
     */
    public static Object post(String path,Map map) throws IOException {


        Object object=api.main(path,map).execute().body();

        return object;

    }

    public void setBaseUrl(String baseUrl){

        if(baseUrl.trim().length()==0){
            baseUrl = BaseUrl;
        }

        if(!baseUrl.startsWith("http")){
            baseUrl="http://"+baseUrl;
        }


        LogH.e("设置服务器:"+baseUrl);
       //其他app 传过来的 地址
       SPUtil.set(BaseApp.getAppContext(),SPUtil.IP,baseUrl);

        if(retrofit!=null){
            RetrofitUrlManager.getInstance().setGlobalDomain(baseUrl);
           // LogH.e(BaseUrl+"==baseUrl=>"+baseUrl);

        }



    }



    public void setToken(String token){

        Token = token;
        SPUtil.set(BaseApp.getAppContext(),SPUtil.TOKEN,token);

    }






}
