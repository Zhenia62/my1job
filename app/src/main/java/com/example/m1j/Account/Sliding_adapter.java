package com.example.m1j.Account;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.example.m1j.R;

import java.util.ArrayList;

public class Sliding_adapter extends PagerAdapter {
    private ArrayList<Login_screens_Get_Set> data_list;

    private LayoutInflater inflater;
    private Context context;


    public Sliding_adapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        data_list = new ArrayList<>();
        data_list.add(new Login_screens_Get_Set("Откройте для себя разнообразие профессий\n в видео формате", R.drawable.time_pic));
        data_list.add(new Login_screens_Get_Set("Делайте свайп вправо чтобы лайкнуть видео\n или влево чтобы пропустить", R.drawable.time_pic));
        data_list.add(new Login_screens_Get_Set("Алгоритм будет подбирать подходящие\n для вас вакансии", R.drawable.time_pic));
        data_list.add(new Login_screens_Get_Set("Так же, вы в любой момент можете сменить роль\n и разместить уже свою вакансию", R.drawable.time_pic));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_slider_layout, view, false);

        if (imageLayout != null) {

            final ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.image);
            imageView.setImageResource(data_list.get(position).image);
            TextView title_txt = imageLayout.findViewById(R.id.title);
            title_txt.setText(data_list.get(position).name);
            view.addView(imageLayout, 0);
        }

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public class Login_screens_Get_Set {
        String name;
        Integer image;

        public Login_screens_Get_Set(String name, Integer image) {
            this.name = name;
            this.image = image;
        }
    }
}
