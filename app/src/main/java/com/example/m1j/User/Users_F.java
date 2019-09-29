package com.example.m1j.User;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.m1j.Account.LoginActivity;
import com.example.m1j.CodeClass.Variables;
import com.example.m1j.Inbox.Match_Get_Set;
import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.example.m1j.MainMenuActivity.relateToFragment_OnBack.RootFragment;
import com.example.m1j.Matchs.Match_F;
import com.example.m1j.R;;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class Users_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    CardStackView card_viewstack;

    ImageButton detail_btn;

    User_Adapter adapter;

    ImageButton refresh_btn,cross_btn,heart_btn;

    public Users_F() {
        // Required empty public constructor
    }

    RelativeLayout user_list_layout,find_nearby_User;

    DatabaseReference rootref;

    boolean is_Api_running=false;

    boolean is_view_load=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_users, container, false);

        context=getContext();

        adapter=new User_Adapter(context);

        init_bottom_view();

        rootref= FirebaseDatabase.getInstance().getReference();



        ImageView profile_image=view.findViewById(R.id.profileimage);
        Picasso.with(context).
                load(MainMenuActivity.user_pic)
                .placeholder(R.drawable.image_placeholder)
                .into(profile_image);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_Api_running){
                    is_Api_running=true;
                    GetPeople_nearby();
                }
            }
        });




        GetPeople_nearby();


        card_viewstack=view.findViewById(R.id.card_viewstack_us);
        card_viewstack.setAdapter(adapter);
        //card_viewstack.setAdapter(adapter);
        card_viewstack.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {
            }

            @Override
            public void onCardSwiped(SwipeDirection direction) {

                // card swipe get the top user index for get the data in list
                int positon=card_viewstack.getTopIndex()-1;


                if(positon<adapter.getCount()) {
                    final Nearby_User_Get_Set item = adapter.getItem(positon);


                    // if swipe left the we will call a fuction for update the value in firebase
                    if (direction.equals(SwipeDirection.Left)) {

                        updatedata_onLeftSwipe(item);

                    }

                    // if swipe Right the we will call a fuction for update the value in firebase

                    else if (direction.equals(SwipeDirection.Right)) {
                        updatedata_onrightSwipe(item);

                    }

                    // find if the swipes card is last or not
                    if (card_viewstack.getTopIndex() == adapter.getCount()) {
                        // if last then we will replace the view and show the ad
//                        if(mInterstitialAd.isLoaded()){
//                            mInterstitialAd.show();
//                        }

                        ShowfindingView();
                    }
                }
            }

            @Override
            public void onCardReversed() {
                // card swipe get the top user index for get the data in list
                int positon=card_viewstack.getTopIndex();

                if(positon<adapter.getCount()) {
                    final Nearby_User_Get_Set item = adapter.getItem(positon);
                    updatedata_onreverse(item);
                }

                }

            @Override
            public void onCardMovedToOrigin() {

            }

            @Override
            public void onCardClicked(int index) {

            }
        });





        // on press detail btn we will open the detail of user
        detail_btn=view.findViewById(R.id.detail_btn);
        detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open_user_detail();


            }
        });


        user_list_layout=view.findViewById(R.id.user_list_layout);
        find_nearby_User=view.findViewById(R.id.find_nearby_User);


        // intialize the bottom three btns


        is_view_load=true;

        return view;

    }


    public void init_bottom_view(){

        cross_btn=view.findViewById(R.id.cross_btn);
        heart_btn=view.findViewById(R.id.heart_btn);

        cross_btn.setOnClickListener(this);
        heart_btn.setOnClickListener(this);
    }


    // when we swipe left , right or reverse then this method is call and update the value in firebase database
    public void updatedata_onLeftSwipe(final Nearby_User_Get_Set item){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh");
        final String formattedDate = df.format(c);

        rootref.child("Match").child(item.getFb_id()).child(MainMenuActivity.user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map mymap=new HashMap<>();
                    mymap.put("match","false");
                    mymap.put("type","dislike");
                    mymap.put("status","0");
                    mymap.put("time",formattedDate);
                    mymap.put("name",item.getName());

                    Map othermap=new HashMap<>();
                    othermap.put("match","false");
                    othermap.put("type","dislike");
                    othermap.put("status","0");
                    othermap.put("time",formattedDate);
                    othermap.put("name",MainMenuActivity.user_name);

                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).updateChildren(mymap);
                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).updateChildren(othermap);

                }else {
                    Map mymap=new HashMap<>();
                    mymap.put("match","false");
                    mymap.put("type","dislike");
                    mymap.put("status","0");
                    mymap.put("time",formattedDate);
                    mymap.put("name",item.getName());
                    mymap.put("effect","true");

                    Map othermap=new HashMap<>();
                    othermap.put("match","false");
                    othermap.put("type","dislike");
                    othermap.put("status","0");
                    othermap.put("time",formattedDate);
                    othermap.put("name",MainMenuActivity.user_name);
                    othermap.put("effect","false");

                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).setValue(mymap);
                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).setValue(othermap);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public void updatedata_onrightSwipe(final Nearby_User_Get_Set item){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh");
        final String formattedDate = df.format(c);

        Query query=rootref.child("Match").child(item.getFb_id()).child(MainMenuActivity.user_id);
        query.keepSynced(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    Map mymap=new HashMap<>();
//                    mymap.put("match","true");
//                    mymap.put("type","like");
//                    mymap.put("status","0");
//                    mymap.put("time",formattedDate);
//                    mymap.put("name",item.getName());
//
//                    Map othermap=new HashMap<>();
//                    othermap.put("match","true");
//                    othermap.put("type","like");
//                    othermap.put("status","0");
//                    othermap.put("time",formattedDate);
//                    othermap.put("name",MainMenuActivity.user_name);
//
//                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).updateChildren(mymap);
//                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).updateChildren(othermap);
//
//                }else {
//                    Map mymap=new HashMap<>();
//                    mymap.put("match","false");
//                    mymap.put("type","like");
//                    mymap.put("status","0");
//                    mymap.put("time",formattedDate);
//                    mymap.put("name",item.getName());
//                    mymap.put("effect","true");
//
//                    Map othermap=new HashMap<>();
//                    othermap.put("match","false");
//                    othermap.put("type","like");
//                    othermap.put("status","0");
//                    othermap.put("time",formattedDate);
//                    othermap.put("name",MainMenuActivity.user_name);
//                    othermap.put("effect","false");
//
//                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).setValue(mymap);
//                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).setValue(othermap);
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(item.getSwipe().equals("like")){

            Match_Get_Set match_get_set=new Match_Get_Set();
            match_get_set.setU_id(item.getFb_id());
            match_get_set.setUsername(item.getName());
            match_get_set.setPicture(item.getImagesurl().get(0));
            openMatch(match_get_set);

        }

    }


    public void updatedata_onreverse(final Nearby_User_Get_Set item){
//
//        Query query=rootref.child("Match").child(item.getFb_id()).child(MainMenuActivity.user_id);
//        query.keepSynced(true);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child("match").getValue().equals("true")){
//
//                    Map mymap=new HashMap<>();
//                    mymap.put("match","false");
//
//                    Map othermap=new HashMap<>();
//                    othermap.put("match","false");
//
//                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).updateChildren(mymap);
//                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).updateChildren(othermap);
//
//                }
//                else {
//
//                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).removeValue();
//                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).removeValue();
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }




// when ever a user go to the main center view then we call a api of nearby
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if((isVisibleToUser && Variables.is_reload_users) && is_view_load){
            if(!is_Api_running){
                is_Api_running=true;
                Variables.is_reload_users =false;
                GetPeople_nearby();
            }
        }
    }


    // when all the card is swiped then this mehtod will call and replace the view and show the ad
    public void ShowfindingView(){
        Variables.is_reload_users =true;
        user_list_layout.setVisibility(View.GONE);
        find_nearby_User.setVisibility(View.VISIBLE);

        final PulsatorLayout pulsator = (PulsatorLayout) view.findViewById(R.id.pulsator);
        pulsator.start();
    }


    public void ShowUser_ListView(){
        user_list_layout.setVisibility(View.VISIBLE);
        find_nearby_User.setVisibility(View.GONE);
    }




    // this method will intializae the inertial add will will show when user swipe all the users
//    private InterstitialAd mInterstitialAd;
//    @Override
//    public void onResume() {
//        super.onResume();
//        MobileAds.initialize(context, "ca-app-pub-3940256099942544~3347511713");
//
//        //code for intertial add
//        mInterstitialAd = new InterstitialAd(context);
//
//        //here we will get the add id keep in mind above id is app id and below Id is add Id
//        mInterstitialAd.setAdUnitId(getResources().getString(R.string.my_Interstitial_Add));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//        });
//    }



    /// / all the action that will performe the bottom 3 buttons
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.cross_btn:
                Clearbackstack();
                swipeLeft();
                break;

            case R.id.heart_btn:
                Clearbackstack();
                swipeRight();
                break;
        }
    }



    // below two method will automatically swipe the card
    public void swipeLeft() {

        View target = card_viewstack.getTopView();
        View targetOverlay = card_viewstack.getTopView().getOverlayContainer();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", -10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, -2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(400);
        translateY.setStartDelay(400);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet cardAnimationSet = new AnimatorSet();
        cardAnimationSet.playTogether(rotation, translateX, translateY);

        ObjectAnimator overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f);
        overlayAnimator.setDuration(200);
        AnimatorSet overlayAnimationSet = new AnimatorSet();
        overlayAnimationSet.playTogether(overlayAnimator);

        card_viewstack.swipe(SwipeDirection.Left, cardAnimationSet, overlayAnimationSet);
    }

    public void swipeRight() {

        View target = card_viewstack.getTopView();
        View targetOverlay = card_viewstack.getTopView().getOverlayContainer();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", 10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, 2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(400);
        translateY.setStartDelay(400);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet cardAnimationSet = new AnimatorSet();
        cardAnimationSet.playTogether(rotation, translateX, translateY);

        ObjectAnimator overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f);
        overlayAnimator.setDuration(200);
        AnimatorSet overlayAnimationSet = new AnimatorSet();
        overlayAnimationSet.playTogether(overlayAnimator);

        card_viewstack.swipe(SwipeDirection.Right, cardAnimationSet, overlayAnimationSet);
    }



    // if any fagment is open then it will closs the fragment
    public void Clearbackstack(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

    }





    // below two method will get all the  new user that is nearby of us  and parse the data in dataset
    // in that case which has a name of Nearby get set
    private void GetPeople_nearby() {

        String latlong="";
        if(MainMenuActivity.sharedPreferences.getBoolean(Variables.is_seleted_location_selected,false)){

            latlong=MainMenuActivity.sharedPreferences.getString(Variables.seleted_Lat,"33.738045")+", "+MainMenuActivity.sharedPreferences.getString(Variables.selected_Lon,"73.084488");
        }else {
            latlong=MainMenuActivity.sharedPreferences.getString(Variables.current_Lat,"33.738045")+", "+MainMenuActivity.sharedPreferences.getString(Variables.current_Lon,"73.084488");
        }



        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("lat_long", latlong);
            parameters.put("gender", MainMenuActivity.sharedPreferences.getString(Variables.show_me,"all"));
            parameters.put("age_range", ""+MainMenuActivity.sharedPreferences.getInt(Variables.max_age,Variables.default_age));
            parameters.put("distance", ""+MainMenuActivity.sharedPreferences.getInt(Variables.max_distance,Variables.default_distance));
            parameters.put("version",Variables.versionname);
            parameters.put("device",context.getResources().getString(R.string.device));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.userNearByMe, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        Parse_user_info(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        is_Api_running=false;
                        Log.d("respoeee",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    String block="0";
    public void Parse_user_info(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                adapter.clear();
                JSONArray msg=jsonObject.getJSONArray("msg");
                for (int i=0; i<msg.length();i++){
                JSONObject userdata=msg.getJSONObject(i);
                Nearby_User_Get_Set item=new Nearby_User_Get_Set();
                item.setFb_id(userdata.optString("fb_id"));
                item.setFirst_name(userdata.optString("first_name"));
                item.setLast_name(userdata.optString("last_name"));
                item.setName(userdata.optString("first_name")+" "+userdata.optString("last_name"));
                item.setJob_title(userdata.optString("job_title"));
                item.setCompany(userdata.optString("company"));
                item.setSchool(userdata.optString("school"));
                item.setBirthday(userdata.optString("birthday"));
                item.setAbout(userdata.optString("about_me"));
                item.setLocation(userdata.optString("distance"));
                item.setGender(userdata.optString("gender"));
                item.setSwipe(userdata.optString("swipe"));

                block=userdata.optString("block");

                ArrayList<String> images=new ArrayList<>();

                  images.add(userdata.optString("image1"));

                  if(!userdata.optString("image2").equals(""))
                    images.add(userdata.optString("image2"));

                    if(!userdata.optString("image3").equals(""))
                        images.add(userdata.optString("image3"));

                    if(!userdata.optString("image4").equals(""))
                        images.add(userdata.optString("image4"));

                    if(!userdata.optString("image5").equals(""))
                        images.add(userdata.optString("image5"));

                    if(!userdata.optString("image6").equals(""))
                        images.add(userdata.optString("image6"));

                    item.setImagesurl(images);

                adapter.add(item);
                }

                if(!(msg.length()>0)){
                    ShowfindingView();
                }else {
                    ShowUser_ListView();
                }

                is_Api_running=false;
                adapter.notifyDataSetChanged();

                if(block.equals("1")){
                    // on press logout btn we will chat the local value and move the user to login screen
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.islogin,false).commit();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    getActivity().finish();

                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }


    }



    // this mehtod will show the view which will show the more detail about the user
    public void open_user_detail(){

        User_detail_F user_detail_f = new User_detail_F();

        Nearby_User_Get_Set item= adapter.getItem(card_viewstack.getTopIndex());
        Bundle args = new Bundle();
        args.putSerializable("data",item);
        user_detail_f.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainMenuFragment, user_detail_f)
                .addToBackStack(null)
                .commit();

    }





    // when a match is build between user then this method is call and open the view if match screen
    public void openMatch(Match_Get_Set item){
        Match_F match_f = new Match_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("data",item);
        match_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, match_f).commit();

    }


}
