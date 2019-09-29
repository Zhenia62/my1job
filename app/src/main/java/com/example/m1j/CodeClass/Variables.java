package com.example.m1j.CodeClass;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {
    public static String pref_name = "pref_name";
    public static String f_name = "f_name";
    public static String l_name = "l_name";
    public static String birth_day = "birth_day";
    public static String gender = "gender";
    public static String uid = "uid";
    public static String u_pic = "u_pic";
    public static String islogin = "islogin";

    public static String current_Lat = "current_Lat";
    public static String current_Lon = "current_Lon";

    public static String seleted_Lat = "seleted_Lat";
    public static String selected_Lon = "selected_Lon";
    public static String is_seleted_location_selected = "is_seleted_location_selected";

    public static String selected_location_string = "selected_location_string";


    public static String device_token = "device_token";
    public static String ispuduct_puchase = "ispuduct_puchase";

    public static String versionname = "1.0";

    public static boolean is_reload_users = false;
    public static String show_me = "show_me";
    public static String max_distance = "max_distance";
    public static String max_age = "max_age";
    public static String show_me_on_binder = "show_me_on_tinder";


    public static int default_distance = 800;
    public static int default_age = 60;


    public static int permission_camera_code = 786;
    public static int permission_write_data = 788;
    public static int permission_Read_data = 789;
    public static int permission_Recording_audio = 790;


    public static String Pic_firstpart = "https://graph.facebook.com/";
    public static String Pic_secondpart = "/picture?width=500&width=500";

    public static String Pic_firstpart_200 = "https://graph.facebook.com/";
    public static String Pic_secondpart_200 = "/picture?width=200&width=200";

    public static String gif_firstpart = "https://media.giphy.com/media/";
    public static String gif_secondpart = "/100w.gif";

    public static String gif_firstpart_chat = "https://media.giphy.com/media/";
    public static String gif_secondpart_chat = "/200w.gif";


    public static String gif_api_key1 = "gif api key which you will get from Giphy developer site";

    // Bottom two variable Related with in App Subscription
    //First step get licencekey
    public static String licencekey = "Licence key which you will get from playstore";

    //create the Product id or in app subcription id
    public static String product_ID = "product subscrition id which you will make from playstore";


    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);


    public static int Select_image_from_gallry_code = 3;




    public static String domain = "ServerAPI";




    public static String SignUp = domain + "signup";

    public static String Edit_profile = domain + "edit_profile";

    public static String getUserInfo = domain + "getUserInfo";

    public static String uploadImages = domain + "uploadImages";

    public static String deleteImages = domain + "deleteImages";

    public static String userNearByMe = domain + "userNearByMe";

    public static String flat_user = domain + "flat_user";

    public static String myMatch = domain + "myMatch";

    public static String firstchat = domain + "firstchat";

    public static String unMatch = domain + "unMatch";

    public static String show_or_hide_profile = domain + "show_or_hide_profile";

    public static String update_purchase_Status = domain + "update_purchase_Status";

    public static String deleteAccount = domain + "deleteAccount";

}
