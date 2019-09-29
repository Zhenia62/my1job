package com.example.m1j.Firebase_Notification;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;

import com.example.m1j.Chat.Chat_Activity;
import com.example.m1j.CodeClass.Variables;
import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.example.m1j.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.core.Context;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Firebase_Notification_Receive extends FirebaseMessagingService {
    String pic;
    String title;
    String message;
    String senderid;
    String receiverid;
    String group_id;
    SharedPreferences sharedPreferences;

    sendNotification sendNotification = new sendNotification(Firebase_Notification_Receive.this);

    Handler handler = new Handler();
    Runnable runnable;

    Snackbar snackbar;

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
            pic = remoteMessage.getData().get("icon");
            senderid = remoteMessage.getData().get("senderid");
            receiverid = remoteMessage.getData().get("receiverid");
            // it is the notification from user to user chat
            if (!Chat_Activity.senderid_for_check_notification.equals(senderid)) {
                // if the user does not open the chat
                // if all the notification is on
                sendNotification.execute(pic);

            }

        }


    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        if ((s != null || !s.equals("null")) && sharedPreferences.getString(Variables.device_token, "null").equals("null"))
            sharedPreferences.edit().putString(Variables.device_token, s).commit();

    }


    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;

        public sendNotification(Context context) {
            super();
            this.ctx = context;
        }

        public sendNotification(Firebase_Notification_Receive firebase_notification_receive) {
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            // in notification first we will get the image of the user and then we will show the notification to user
            // in onPostExecute
            InputStream in;
            try {

                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);


            if (MainMenuActivity.mainMenuActivity != null) {


                if (snackbar != null) {
                    snackbar.getView().setVisibility(View.INVISIBLE);
                    snackbar.dismiss();
                }

                if (handler != null && runnable != null) {
                    handler.removeCallbacks(runnable);
                }


                View layout = MainMenuActivity.mainMenuActivity.getLayoutInflater().inflate(R.layout.item_layout_custom_notification, null);
                TextView titletxt = layout.findViewById(R.id.username);
                TextView messagetxt = layout.findViewById(R.id.message);
                ImageView imageView = layout.findViewById(R.id.user_image);
                titletxt.setText(title);
                messagetxt.setText(message);
                imageView.setImageBitmap(result);


                snackbar = Snackbar.make(MainMenuActivity.mainMenuActivity.findViewById(R.id.container), "", Snackbar.LENGTH_LONG);

                Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                textView.setVisibility(View.INVISIBLE);

                final ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
                if (params instanceof CoordinatorLayout.LayoutParams) {
                    ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
                } else {
                    ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
                }

                snackbarLayout.setPadding(0, 0, 0, 0);
                snackbarLayout.addView(layout, 0);


                snackbar.getView().setVisibility(View.INVISIBLE);

                snackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onShown(Snackbar sb) {
                        super.onShown(sb);
                        snackbar.getView().setVisibility(View.VISIBLE);
                    }

                });


                runnable = new Runnable() {
                    @Override
                    public void run() {
                        snackbar.getView().setVisibility(View.INVISIBLE);

                    }
                };

                handler.postDelayed(runnable, 2750);


                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                snackbar.show();


                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        snackbar.dismiss();
                        snackbar.getView().setVisibility(View.INVISIBLE);
                        chatFragment(MainMenuActivity.user_id, senderid, title, false);

                    }
                });


            }

        }
    }


    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String senderid, String receiverid, String name, boolean is_match_exits) {
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = MainMenuActivity.mainMenuActivity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id", senderid);
        args.putString("Receiver_Id", receiverid);
        args.putString("picture", pic);
        args.putString("name", name);
        args.putBoolean("is_match_exits", is_match_exits);
        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }


}

