package com.temple.onit.account;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class AccountManager {

    SharedPreferences preferences;
    public boolean loggedIn = false;

    public AccountManager(Context context){
        preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
    }

    public boolean tokenLogin(){



        return true;
    }

    public void setToken(String token){
        preferences.edit().putString("Token", token).apply();
    }

    public boolean regularLogin(String username, String password){
        return true;
    }

    public boolean checkLogin(){
        if(!preferences.getAll().containsKey("key_token")){
            return false;
        }

        return true;
    }



}
