package com.temple.onit;

public class Constants {
    public static final String API_KEY = "AIzaSyAXMc7l_3eY5BY7eWLBUaA4unYnQMz9zsU";


    public static final String RECURRING = "RECURRING";
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";
    public static final String ALARM_TITLE = "ALARM_TITLE";

    public static final String ALARM_CHANNEL = "ALARM_SERVICE_CHANNEL";

    public static final String ARRIVAL_HOUR = "ARRIVAL_HOUR";
    public static final String ARRIVAL_MINUTE = "ARRIVAL_MINUTE";

    public static final String LEAVE_HOUR = "LEAVE_HOUR";
    public static final String LEAVE_MINUTE = "LEAVE_MINUTE";

    public static final String ALARM_ID = "ALARM_ID";

    public static final String DESTINATION_LATITUDE = "DESTINATION_LATITUDE";
    public static final String DESTINATION_LONGITUDE = "DESTINATION_LONGITUDE";


    public static final int HOUR_IN_MILLIS = 3600000;
    public static final int MINUTE_IN_MILLIS = 60000;

    public static final String URL = "http://100.25.118.238:8000";

    /* API ENDPOINTS FOR ACCOUNTS */
    public static final String API_ADD_USER = URL+"/add_user";
    public static final String API_LOGIN = URL+"/login";
    public static final String API_TOKEN_LOGIN = URL+"/token_login";
    public static final String API_UPDATE_FIREBASE_TOKEN = URL+"/update_fb_token";

    /* API ENDPOINTS FOR USER REMINDERS */
    public static final String API_ADD_USER_REMINDER = URL+"/user_reminder";
    public static final String API_GET_UNACCEPTED_USER_REMINDERS = URL+"/get_unaccepted_user_reminders";
    public static final String API_GET_USER_REMINDERS = URL+"/get_user_reminder";
    public static final String API_DELETE_USER_REMINDER = URL+"/delete_user_reminder";
    public static final String API_UPDATE_USER_REMINDER = URL+"/user_reminder_update";
    public static final String API_ACCEPT_USER_REMINDER = URL+"/accept_user_reminder";

    /* API ENDPOINTS FOR GEOFENCED REMINDERS */
    public static final String API_ADD_GEOFENCED_REMINDER = URL+"/geo_reminder";
    public static final String API_GET_GEOFENCED_REMINDERS = URL+"/get_geo_reminder";
    public static final String API_DELETE_GEOFENCED_REMINDER = URL+"/delete_geofence";

    /* API ENDPOINTS FOR SMART ALARMS */
    public static final String API_ADD_SMART_ALARM = URL+"/alarm";
    public static final String API_GET_SMART_ALARMS = URL+"/get_alarm";
    public static final String API_DELETE_SMART_ALARMS = URL+"/delete_alarm";
}
