package com.bomcodigo.popmovies.api;


import com.google.gson.Gson;

import java.lang.reflect.Type;

public class BaseModelParser<T> {

    public String parserObjectToString(T model){
        if (model != null) {
            Gson gson = new Gson();
            return gson.toJson(model);
        }else{
            return null;
        }
    }

    public T parserStringToObject(String modelString, Type typeOft){
        Gson gson = new Gson();
        return gson.fromJson(modelString,typeOft);
    }

}
