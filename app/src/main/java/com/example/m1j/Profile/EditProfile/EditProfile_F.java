package com.example.m1j.Profile.EditProfile;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.m1j.CodeClass.Functions;
import com.example.m1j.CodeClass.Variables;
import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.example.m1j.Profile.Profile_F;
import com.example.m1j.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;
import com.wonshinhyo.dragrecyclerview.SimpleDragListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.m1j.CodeClass.Variables.Select_image_from_gallry_code;


public class EditProfile_F extends Fragment {

    View view;
    Context context;

    //DragRecyclerView profile_photo_list;


    ImageButton back_btn;

    IOSDialog iosDialog;

    EditText about_edit, job_title_edit, company_edit, school_edit, dateofbrith_edit;
    RadioButton male_btn, female_btn;

    byte[] image_byteArray;

    Profile_photos_Adapter profile_photos_adapter;

    ArrayList<String> images_list;


    TextView done_txt, profile_name_txt;

    public EditProfile_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        context = getContext();


        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();


        profile_name_txt = view.findViewById(R.id.profile_name_txt);
        profile_name_txt.setText("О " + MainMenuActivity.user_name);


        images_list = new ArrayList<>();


//        profile_photo_list = view.findViewById(R.id.Profile_photos_list);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
//        profile_photo_list.setLayoutManager(layoutManager);
//        profile_photo_list.setHasFixedSize(false);

        profile_photos_adapter = new Profile_photos_Adapter(context, images_list, new Profile_photos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int postion, View view) {
                if (view.getId() == R.id.cross_btn) {
                    if (item.equals("")) {
                        selectImage();
                    } else {
                        Call_Api_For_deletelink(item);
                        profile_photos_adapter.notifyDataSetChanged();
                    }
                }
            }
        });


        profile_photos_adapter.setOnItemDragListener(new SimpleDragListener() {

            @Override
            public void onDrop(int fromPosition, int toPosition) {
                super.onDrop(fromPosition, toPosition);
                Log.d("resp", "" + fromPosition + "--" + toPosition);
                String from_image = images_list.get(fromPosition);
                String to_image = images_list.get(toPosition);
                if (to_image.equals("") || from_image.equals("")) {
                    images_list.remove(toPosition);
                    images_list.add(toPosition, from_image);

                    images_list.remove(from_image);
                    images_list.add(fromPosition, to_image);
                }
                profile_photos_adapter.notifyDataSetChanged();

            }

            @Override
            public void onSwiped(int pos) {
                super.onSwiped(pos);
                Log.d("resp", "" + pos);

            }
        });

        //profile_photo_list.setAdapter(profile_photos_adapter);


        about_edit = view.findViewById(R.id.about_user);
        job_title_edit = view.findViewById(R.id.jobtitle_edit);
        company_edit = view.findViewById(R.id.company_edit);
        school_edit = view.findViewById(R.id.school_edit);
        dateofbrith_edit = view.findViewById(R.id.dateofbirth_edit);

        male_btn = view.findViewById(R.id.male_btn);
        female_btn = view.findViewById(R.id.female_btn);

        back_btn = view.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });


        dateofbrith_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.Opendate_picker(context, dateofbrith_edit);


            }
        });

        done_txt = view.findViewById(R.id.done_txt);
        done_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call_Api_For_edit();
            }
        });

        Get_User_info();

        return view;
    }


    // open the gallery when user press button to upload a picture
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Select_image_from_gallry_code);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Select_image_from_gallry_code) {

                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
            } else if (requestCode == 123) {
                handleCrop(resultCode, data);
            }

        }
    }


    // botoom there function are related to crop the image
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(500, 500).start(context, getCurrentFragment(), 123);

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri userimageuri = Crop.getOutput(result);

            InputStream imageStream = null;
            try {
                imageStream = getActivity().getContentResolver().openInputStream(userimageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

            String path = userimageuri.getPath();
            Matrix matrix = new Matrix();
            android.media.ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                try {
                    exif = new android.media.ExifInterface(path);
                    int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image_byteArray = out.toByteArray();

            SavePicture();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Fragment getCurrentFragment() {
        return getActivity().getSupportFragmentManager().findFragmentById(R.id.MainMenuFragment);

    }


    // after crop this fuction is call and it store the picture in firebase database
    public void SavePicture() {
        iosDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        String id = reference.push().getKey();
        // first we upload image after upload then get the picture url and save the group data in database
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference filelocation = storageReference.child(MainMenuActivity.user_id)
                .child(id + ".jpg");
        filelocation.putBytes(image_byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url =taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                Call_Api_For_uploadLink(url);
                iosDialog.cancel();
            }
        });
    }

    //UploadTask.TaskSnapshot
    // after save the image in firebase we will save the image url in our server

    private void Call_Api_For_uploadLink(String link) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("image_link", link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.uploadImages, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        iosDialog.cancel();

                        try {
                            JSONObject jsonObject = new JSONObject(respo);
                            String code = jsonObject.optString("code");
                            if (code.equals("200")) {
                                Get_User_info();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo", error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    // this method will call when we click for delete the profile images
    private void Call_Api_For_deletelink(String link) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("image_link", link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.deleteImages, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        iosDialog.cancel();
                        try {
                            JSONObject jsonObject = new JSONObject(respo);
                            String code = jsonObject.optString("code");
                            if (code.equals("200")) {
                                Get_User_info();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo", error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    // below two method is used get the user pictures and about text from our server
    private void Get_User_info() {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.getUserInfo, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        Parse_user_info(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo", error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void Parse_user_info(String loginData) {
        iosDialog.cancel();
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                JSONObject userdata = msg.getJSONObject(0);


                images_list.clear();
                images_list.add(userdata.optString("image1"));
                images_list.add(userdata.optString("image2"));
                images_list.add(userdata.optString("image3"));
                images_list.add(userdata.optString("image4"));
                images_list.add(userdata.optString("image5"));
                images_list.add(userdata.optString("image6"));

                about_edit.setText(userdata.optString("about_me"));
                job_title_edit.setText(userdata.optString("job_title"));
                company_edit.setText(userdata.optString("company"));
                school_edit.setText(userdata.optString("school"));
                dateofbrith_edit.setText(userdata.optString("birthday"));

                if (userdata.optString("gender").toLowerCase().equals("male")) {
                    male_btn.setChecked(true);
                } else if (userdata.optString("gender").toLowerCase().equals("female")) {
                    female_btn.setChecked(true);
                }


                profile_photos_adapter.notifyDataSetChanged();


            }
        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }


    // on done btn press this method will call
    // below two mehtod is user for save the change in our profile which we have done
    private void Call_Api_For_edit() {

        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

            List<String> images = new ArrayList<>();

            List<String> adapter_images = profile_photos_adapter.getData();
            for (int i = 0; i < adapter_images.size(); i++) {
                if (!adapter_images.get(i).equals(MainMenuActivity.user_pic)) {
                    images.add(adapter_images.get(i));
                }
            }

            parameters.put("image1", adapter_images.get(0));
            parameters.put("image2", adapter_images.get(1));
            parameters.put("image3", adapter_images.get(2));
            parameters.put("image4", adapter_images.get(3));
            parameters.put("image5", adapter_images.get(4));
            parameters.put("image6", adapter_images.get(5));

            parameters.put("about_me", about_edit.getText().toString());
            parameters.put("job_title", job_title_edit.getText().toString());
            parameters.put("company", company_edit.getText().toString());
            parameters.put("school", school_edit.getText().toString());
            parameters.put("birthday", dateofbrith_edit.getText().toString());


            if (male_btn.isChecked()) {
                parameters.put("gender", "Male");

            } else if (female_btn.isChecked()) {
                parameters.put("gender", "Female");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp", parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.Edit_profile, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        Parse_edit_data(respo);
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo", error.toString());
                    }
                });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);

    }

    public void Parse_edit_data(String loginData) {
        iosDialog.cancel();
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msg = jsonObject.getJSONArray("msg");
                JSONObject userdata = msg.getJSONObject(0);

                MainMenuActivity.sharedPreferences.edit().putString(Variables.birth_day, userdata.optString("age")).commit();
                MainMenuActivity.birthday = userdata.optString("age");


                Profile_F.age.setText(MainMenuActivity.birthday);

                if (!MainMenuActivity.user_pic.equals(userdata.optString("image1"))) {
                    MainMenuActivity.sharedPreferences.edit().putString(Variables.u_pic, userdata.optString("image1")).commit();
                    MainMenuActivity.user_pic = userdata.optString("image1");
                    Picasso.with(context).load(MainMenuActivity.user_pic)
                            .resize(200, 200)
                            .placeholder(R.drawable.profile_image_placeholder)
                            .centerCrop()
                            .into(Profile_F.profile_image);
                }

                // if data is save then we will go back
                getActivity().onBackPressed();

            }
        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }


}
