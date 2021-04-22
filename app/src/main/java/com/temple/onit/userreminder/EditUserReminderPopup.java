package com.temple.onit.userreminder;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.temple.onit.Constants;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;

import java.util.HashMap;
import java.util.Map;

public class EditUserReminderPopup {
    int width = LinearLayout.LayoutParams.MATCH_PARENT;
    int height = LinearLayout.LayoutParams.MATCH_PARENT;
    int id;
    EditText editTitle,editBody,editDistance;
    TextView NonEditableTarget;
    Button cancel,submit;
    TextInputLayout distanceCheck;
    ConstraintLayout editBackground;
    PopupWindow userReminderWindow;
    Context context;
    int maxTriggerDistance = 300, minTriggerDistance = 50; // 300 meter is still going out of your way

    public  void showUserReminderEditPopUp (View v, String title, String body , String distance, String target, int id, Context context){
        this.id = id;
        this.context = context;

        v.getContext();
        LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View userReminderPopup = inflater.inflate(R.layout.proximity_reminder_edit_popup,null);

        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;

        userReminderWindow = new PopupWindow(userReminderPopup,width,height, true); // true makes items outside popup inactive
        userReminderWindow.showAtLocation(v, Gravity.CENTER,0,0);

        editTitle = userReminderPopup.findViewById(R.id.editeUserTitle);
        editTitle.setText(title);
        editBody = userReminderPopup.findViewById(R.id.editUserBody);
        editBody.setText(body);
        editDistance = userReminderPopup.findViewById(R.id.editUserDistance);
        editDistance.setText(distance);
        distanceCheck = userReminderPopup.findViewById(R.id.distanceCheck);
        editDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isDigitsOnly(editDistance.getText().toString()) || editDistance.getText().toString().length() ==0 ){ // if distance entered isn't a number or blank
                    distanceCheck.setError("Distance must be a number");

                } else if (Integer.valueOf(editDistance.getText().toString()) > maxTriggerDistance ||Integer.valueOf(editDistance.getText().toString()) < minTriggerDistance  ){
                    distanceCheck.setError("Trigger Distance: Max = 300 meters, Min = 50 meters");
                } else {
                    distanceCheck.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        NonEditableTarget = userReminderPopup.findViewById(R.id.editUserTarget);
        NonEditableTarget.setText(target);
        editBackground = userReminderPopup.findViewById(R.id.userReminderBG);
        editBackground.setBackgroundColor(Color.parseColor("#BFCCCCCC")); //Snow white, 75% transparency


        cancel = userReminderPopup.findViewById(R.id.editUserCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userReminderWindow.dismiss();
            }
        });

        submit = userReminderPopup.findViewById(R.id.editUserSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               editUserReminder(editTitle.getText().toString(),editBody.getText().toString(),editDistance.getText().toString(),target);
            }
        });

        // dismiss if touch outside of edit card
        userReminderPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userReminderWindow.dismiss();
                return true;
            }
        });

    }

    private void editUserReminder( String title, String body , String distance, String target){
        final String postTitle= title,
                postBody= body,
                postDist = distance,
                postTarget = target,
                postUser = OnitApplication.instance.getAccountManager().username;
        Log.d("target",postTarget);

        RequestQueue queue = Volley.newRequestQueue(context);
        String server = Constants.API_UPDATE_USER_PROXIMITY_REMINDER;

        StringRequest makeRequest = new StringRequest(Request.Method.POST,server,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                if(response.compareTo("error")==0){
                    Toast.makeText(context,"Target User does not exist",Toast.LENGTH_LONG).show();
                } else {
                    userReminderWindow.dismiss();
                    String toast = "Updated " + postTitle;
                    Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
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
                Map<String, String> editUserReminder = new HashMap();
                editUserReminder.put("newTitle",postTitle);
                editUserReminder.put("newBody",postBody);
                editUserReminder.put("newDistance",postDist);
                editUserReminder.put("target",postTarget);
                editUserReminder.put("username",postUser);
                editUserReminder.put("id",String.valueOf(id));
                return editUserReminder;
            }
        };
        queue.add(makeRequest);

    }

}

