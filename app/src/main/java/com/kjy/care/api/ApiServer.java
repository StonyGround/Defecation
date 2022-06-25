package com.kjy.care.api;



import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by 420041900 on 2017/3/16.
 */

public interface ApiServer {

    @POST("{url}")
    @FormUrlEncoded
    Call<JSONObject> main(@Path(value = "url",encoded = true) String url, @FieldMap Map<String, String> options);


    @POST("{url}")
    @FormUrlEncoded
    Call<JSONObject> main(@HeaderMap Map<String, String>  headerMap, @Path(value = "url",encoded = true) String url, @FieldMap Map<String, String> options);



    @GET
    @Streaming
    Call<ResponseBody> download(@Header("Range") String range, @Url String url);
}
