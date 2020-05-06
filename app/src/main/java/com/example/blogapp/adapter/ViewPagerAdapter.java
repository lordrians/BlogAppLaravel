package com.example.blogapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.blogapp.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    private int images[] = {
            R.drawable.ic_onboard,
            R.drawable.ic_onboard,
            R.drawable.ic_onboard
    };

    private String titles[] = {
            "Learn",
            "Create",
            "Enjoy"
    };

    private String desc[] = {
            "lorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsum",
            "lorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsum",
            "lorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsum"
    };


    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.vp_onboard, container, false);

        //insisasi
        ImageView ivOnBoard = v.findViewById(R.id.iv_onboard);
        TextView tvOnBoardTitle = v.findViewById(R.id.tv_onboard_title);
        TextView tvOnBoardDesc = v.findViewById(R.id.tv_onboard_desc);

        ivOnBoard.setImageResource(images[position]);
        tvOnBoardTitle.setText(titles[position]);
        tvOnBoardDesc.setText(desc[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
