package com.aididalam.endorsevalidator.util;

import android.content.Context;
import android.content.Intent;

import com.aididalam.endorsevalidator.SplashActivity;
import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.shashank.sony.fancytoastlib.FancyToast;

public class Method {
    public static void dangerAlert(Context context,String msg){
        FancyToast.makeText(context,msg,FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
    }

    public static void successAlert(Context context,String msg){
        FancyToast.makeText(context,msg,FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
    }

    public static String getToken(Context context){
        String token=null;
        DBHelper dbHelper=new DBHelper(context);

        if(dbHelper.getData(1)==null){
            return null;
        }else{
            token=dbHelper.getData(1).get("token_type").toString()+" "+dbHelper.getData(1).get("access_token").toString();
        }
        return token;
    }


    public static String getPublicKey(Context context){
        String key=null;
        DBHelper dbHelper=new DBHelper(context);

        if(dbHelper.getData(1)==null){
            return null;
        }else{
            key=dbHelper.getData(1).get("publickey").toString();
        }
        return key;
    }
}
