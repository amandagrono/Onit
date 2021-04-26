package com.temple.onit.userreminder;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.temple.onit.Constants;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CreateProximityReminderPopup {
    int width = LinearLayout.LayoutParams.MATCH_PARENT;
    int height = LinearLayout.LayoutParams.MATCH_PARENT;

    EditText createTitle,createBody,createTarget,createDistance;
    Button submit,cancel;
    PopupWindow createUserProximityWindow;
    ConstraintLayout createBackground;
    Context context;
    TextInputLayout distanceCheck, targetCheck;
    int maxTriggerDistance = 300, minTriggerDistance = 50; // 300 meter is still going out of your way
    boolean targetIssue = false, distanceIssue = false;
    CreateDone listener;


    public  void showUserProximityCreatePopUp(View v, Context con, CreateDone listener){
        context = con;
        this.listener = listener;

        v.getContext();
        LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); // get layout inflater

        View createProximityPopup = inflater.inflate(R.layout.proximity_create_popup,null);

        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;

        createUserProximityWindow = new PopupWindow(createProximityPopup,width,height, true); // true makes items outside popup inactive
        createUserProximityWindow.showAtLocation(v, Gravity.CENTER,0,0);

        createBackground = createProximityPopup.findViewById(R.id.createProxBackground);
        createBackground.setBackgroundColor(Color.parseColor("#BFCCCCCC")); //Snow white, 75% transparency

        createTitle = createProximityPopup.findViewById(R.id.createTitle);
        createBody = createProximityPopup.findViewById(R.id.createBody);

        createTarget = createProximityPopup.findViewById(R.id.createTarget);
        targetCheck = createProximityPopup.findViewById(R.id.geoDistanceCheck);
        createTarget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!createTarget.getText().toString().contains("@") || !createTarget.getText().toString().endsWith(".com")){
                        targetCheck.setError("Please enter target user E-mail");
                        targetIssue= true;
                    }
                    else if(createTarget.getText().toString().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())){
                        targetCheck.setError("Cannot send a reminder to yourself");
                        targetIssue = true;
                    }
                    else {
                        targetCheck.setError(null);
                        targetIssue = false;
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        createDistance = createProximityPopup.findViewById(R.id.createDistance);
        distanceCheck = createProximityPopup.findViewById(R.id.distanceCheck);
        createDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(!TextUtils.isDigitsOnly(createDistance.getText().toString()) || createDistance.getText().toString().length() ==0 ){ // if distance entered isn't a number
                            distanceCheck.setError("Distance must be a number");
                            distanceIssue = true;

                        } else if (Integer.valueOf(createDistance.getText().toString()) > maxTriggerDistance ||Integer.valueOf(createDistance.getText().toString()) < minTriggerDistance  ){
                            distanceCheck.setError("Trigger Distance: Max = 300 meters, Min = 50 meters");
                            distanceIssue = true;
                        } else {
                            distanceCheck.setError(null);
                            distanceIssue = false;
                        }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        submit = createProximityPopup.findViewById(R.id.createProxSubmit);
        submit.setOnClickListener(view -> {
            if(!targetIssue && !distanceIssue){
                addUserReminder(createTitle.getText().toString(),createBody.getText().toString(),createDistance.getText().toString(),createTarget.getText().toString());
            }
            else{
                Toast.makeText(context, "Please fix input issues", Toast.LENGTH_SHORT).show();
            }

        });

        cancel = createProximityPopup.findViewById(R.id.createProxCancel);
        cancel.setOnClickListener(view -> {
            createUserProximityWindow.dismiss();

        });

    }
    private void addUserReminder( String title, String body , String distance, String target){
        final String postTitle= title,
                postBody= body,
                postDist = distance,
                postTarget = target,
                issuer = OnitApplication.instance.getAccountManager().username;

        RequestQueue queue = Volley.newRequestQueue(context);
        String server = Constants.API_ADD_USER_REMINDER;

        StringRequest makeRequest = new StringRequest(Request.Method.POST,server,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                if(response.compareTo("error")==0){
                    Toast.makeText(context,"Target User does not exist",Toast.LENGTH_LONG).show();
                } else {
                    createUserProximityWindow.dismiss();
                    String toast = "Added" + postTitle;
                    Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
                    listener.createDone();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context,"Target User does not exist",Toast.LENGTH_LONG).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> saveUserProximityReminder = new HashMap();
                saveUserProximityReminder.put("title",postTitle);
                saveUserProximityReminder.put("body",postBody);
                saveUserProximityReminder.put("target",postTarget);
                saveUserProximityReminder.put("distance",postDist);
                saveUserProximityReminder.put("issuer",issuer);
                return saveUserProximityReminder;
            }
        };
        queue.add(makeRequest);

    }

    public interface CreateDone{
        public void createDone();
    }
}
