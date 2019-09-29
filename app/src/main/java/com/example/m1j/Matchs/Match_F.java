package com.example.m1j.Matchs;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.m1j.Chat.Chat_Activity;
import com.example.m1j.CodeClass.Variables;
import com.example.m1j.Inbox.Match_Get_Set;
import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.example.m1j.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;



// this is the view which is show when both users like each other

public class Match_F extends Fragment {

    View view;
    Context context;

    TextView match_txt;
    ImageView user1_pic,user2_pic;

    LinearLayout send_message_layout;
    Match_Get_Set item;

    DatabaseReference rootref;

    public Match_F() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_match, container, false);
        context=getContext();

        ImageButton cross_btn=view.findViewById(R.id.cross_btn);
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        rootref=FirebaseDatabase.getInstance().getReference();

        match_txt=view.findViewById(R.id.match_txt);
        user1_pic=view.findViewById(R.id.user1_pic);
        user2_pic=view.findViewById(R.id.user2_pic);


        send_message_layout=view.findViewById(R.id.send_message_layout);


        Bundle bundle=getArguments();
        if(bundle!=null){

            // get  user data from privous view and show i that view
            item= (Match_Get_Set) bundle.getSerializable("data");

            SendPushNotification(item.getU_id());

            match_txt.setText(" You and "+item.getUsername()+" Like each other");

            Picasso.with(context)
                    .load(MainMenuActivity.user_pic)
                    .placeholder(R.drawable.image_placeholder).into(user1_pic);

            Picasso.with(context)
                    .load(item.getPicture())
                    .placeholder(R.drawable.image_placeholder).into(user2_pic);

        }



        // click listener of message btn
        send_message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatFragment(MainMenuActivity.user_id,item.getU_id(),item.getUsername(),item.getPicture());
            }
        });


        return view;
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 300);
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 300);
        }
    }



    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them
    public void chatFragment(String senderid,String receiverid,String name,String picture){
        getActivity().onBackPressed();
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        args.putBoolean("is_match_exits",true);
        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }


    // when this screen open it will send the notification to other user that
    // both are like the each other and match will build between the users
    public void SendPushNotification(final String receverid){
        rootref.child("Users").child(receverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                String token=dataSnapshot.child("token").getValue().toString();
                Map<String,String> notimap= new HashMap<>();
                notimap.put("name",MainMenuActivity.user_name);
                notimap.put("message","Congrats! you got a match");
                notimap.put("picture", Variables.Pic_firstpart+MainMenuActivity.user_id+Variables.Pic_secondpart);
                notimap.put("token",token);
                notimap.put("receiverid", receverid);
                notimap.put("action_type", "match");
                rootref.child("notifications").child(MainMenuActivity.user_id).push().setValue(notimap);

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
