package com.temple.onit.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.temple.onit.Constants;

public class AccountManager {

    SharedPreferences preferences;
    public boolean loggedIn = false;
    public String username = "";
    public AccountListener listener;

    public AccountManager(Context context, AccountListener listener){
        this.listener = listener;
        preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        Log.d("AccountManager", "Contains Key" + preferences.contains("Token"));
        if(preferences.contains("Token")){
            tokenLogin(context, preferences.getString("Token", "none"));
        }
    }

    public void tokenLogin(Context context, String token){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Constants.API_TOKEN_LOGIN + "?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("Response: ", response);
            Toast.makeText(context, "Successfully Logged Back In with token", Toast.LENGTH_SHORT).show();
            setToken(token);
            setUsername(response);
            setLoggedIn(true);
            listener.onLoginResponse(true);
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                Log.d("Firebase Token Login", s);
                updateFBToken(s, context, username);
            });
        }, error -> {
            Log.d("TokenLogin", "Failed to log in with token");
        });
        queue.add(stringRequest);
    }

    public void setToken(String token){
        preferences.edit().putString("Token", token).apply();
        Log.d("TokenPreferences", preferences.getString("Token", "none"));
    }

    public void regularLogin(String username, String password, Context context){
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = Constants.API_LOGIN + "?username="+username+"&password="+password;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("Response: ", response);
            setToken(response);
            setUsername(username);
            setLoggedIn(true);
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                Log.d("Firebase Token Login", s);
                updateFBToken(s, context, username);
            });
            if(listener != null){
                listener.onLoginResponse(true);
            }
        }, error -> {
            Log.d("Error: ", error.toString());
            error.printStackTrace();
            Toast.makeText(context, "Failed to log in", Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    public void addUser(AccountListener listener, String username, String password, String confirm, Context context, String email){
        this.listener = listener;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Constants.API_ADD_USER + "?username=" + username + "&password=" + password + "&confirm="+confirm+"&email="+email;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            regularLogin(username, password, context);
        }, error -> {

        });
        queue.add(request);

    }


    public void setListener(AccountListener listener){
        this.listener = listener;
    }
    private void setUsername(String username){
        this.username = username;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean checkLogin(){
        if(!preferences.getAll().containsKey("key_token")){
            return false;
        }

        return true;
    }
    public void logout(Context context){
        String temp = this.username;
        this.username = "";
        this.loggedIn = false;
        preferences.edit().putString("Token", "").apply();
        updateFBToken("notoken", context, temp);

    }

    public void updateFBToken(String token, Context context, String user){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Constants.API_UPDATE_FIREBASE_TOKEN+"?username="+user+"&firebase_token="+token;
        Log.d("UpdateFB", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("UpdateFBToken", "Successfully updated firebase token association");
        }, error -> {
            Log.d("UpdateFBToken", "Failed to update firebase token association");
        });
        queue.add(stringRequest);
    }

    public interface AccountListener{
        public void onLoginResponse(boolean loggedIn);
    }

}
