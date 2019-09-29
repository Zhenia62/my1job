package com.example.m1j.MainMenuActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.m1j.CodeClass.Functions;
import com.example.m1j.Inbox.Inbox_F;
import com.example.m1j.MainMenuActivity.relateToFragment_OnBack.OnBackPressListener;
import com.example.m1j.MainMenuActivity.relateToFragment_OnBack.RootFragment;
import com.example.m1j.Profile.Profile_F;
import com.example.m1j.R;
import com.example.m1j.User.Users_F;
import com.google.android.material.tabs.TabLayout;

public class MainMenuFragment  extends RootFragment implements View.OnClickListener {

    protected TabLayout tabLayout;

    protected Custom_ViewPager pager;

    private ViewPagerAdapter adapter;
    Context context;

    public MainMenuFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        context=getContext();
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        pager = view.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(3);
        pager.setPagingEnabled(false);
        view.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
        }

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Note that we are passing childFragmentManager, not FragmentManager
        adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        setupTabIcons();

    }



    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(pager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }


    private void setupTabIcons() {

        View view1 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView1= view1.findViewById(R.id.image);
        imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_gray));
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView2= view2.findViewById(R.id.image);
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ico_like_dis));
        tabLayout.getTabAt(1).setCustomView(view2);


        View view3 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView3= view3.findViewById(R.id.image);
        imageView3.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_gray));
        tabLayout.getTabAt(2).setCustomView(view3);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){


            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v=tab.getCustomView();
                Functions.hideSoftKeyboard(getActivity());
                ImageView image=v.findViewById(R.id.image);
                switch (tab.getPosition()){
                    case 0:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_color));
                        break;
                    case 1:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ico_like_en));
                        break;

                    case 2:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_color));
                        break;
                }
                tab.setCustomView(v);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v=tab.getCustomView();
                ImageView image=v.findViewById(R.id.image);

                switch (tab.getPosition()){
                    case 0:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_gray));
                        break;
                    case 1:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ico_like_dis));
                        break;
                    case 2:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_gray));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });



        if(MainMenuActivity.action_type.equals("message")){
            TabLayout.Tab tab=tabLayout.getTabAt(2);
            tab.select();
            //chatFragment();
        }
        else if(MainMenuActivity.action_type.equals("match")){
            TabLayout.Tab tab=tabLayout.getTabAt(2);
            tab.select();
        }
        else {
            TabLayout.Tab tab=tabLayout.getTabAt(1);
            tab.select();
        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final Resources resources;

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {
            super(fm);
            this.resources = resources;
        }


        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new Profile_F();
                    break;
                case 1:
                    result = new Users_F();
                    break;
                case 2:
                    result = new Inbox_F();
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 3;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }



    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
//    public void chatFragment(){
//        Chat_Activity chat_activity = new Chat_Activity();
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
//        Bundle args = new Bundle();
//        args.putString("Sender_Id",MainMenuActivity.user_id);
//        args.putString("Receiver_Id",MainMenuActivity.receiverid);
//        args.putString("name",MainMenuActivity.title);
//        args.putString("picture",MainMenuActivity.Receiver_pic);
//        args.putBoolean("is_match_exits",false);
//        chat_activity.setArguments(args);
//        transaction.addToBackStack(null);
//        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
//    }

}
