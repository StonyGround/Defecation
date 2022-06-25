package com.kjy.care.util.jsoncoverter;


import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    JsonResponseBodyConverter() {

    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(value.string());
           // jsonObj .parseObject(value.string());
            return (T) jsonObj;
        } catch(Exception e) {
            return null;
        }
    }
}
