package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blogapp.adapter.ViewPagerAdapter;

public class OnBoardActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager vpOnBoard;
    private Button btnLeft, btnRight;
    private ViewPagerAdapter vpAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        init();
    }

    private void init() {
        vpOnBoard = findViewById(R.id.vp_onboard);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        dotsLayout = findViewById(R.id.dotsLayout);
        vpAdapter = new ViewPagerAdapter(this);
        addDots(0);
        vpOnBoard.addOnPageChangeListener(listener);
        vpOnBoard.setAdapter(vpAdapter);

        btnRight.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
    }

    private void addDots(int position){
        dotsLayout.removeAllViews();
        dots = new TextView[3];
        for (int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            //html code for dots
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorLightGrey));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }


    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            if (position == 0){
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            } else if (position == 1){
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            } else {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnRight:
                if (btnRight.getText().toString().equals("Next")){
                    vpOnBoard.setCurrentItem(vpOnBoard.getCurrentItem()+1);
                } else {
                    startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                    finish();
                }
               break;

            case R.id.btnLeft:
                vpOnBoard.setCurrentItem(vpOnBoard.getCurrentItem()+2);

        }
    }
}
