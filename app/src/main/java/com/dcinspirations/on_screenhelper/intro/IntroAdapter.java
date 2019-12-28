package com.dcinspirations.on_screenhelper.intro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dcinspirations.on_screenhelper.R;
import com.github.islamkhsh.CardSliderAdapter;

import java.util.ArrayList;

public class IntroAdapter extends CardSliderAdapter<IntroModel> {


    public IntroAdapter(ArrayList<IntroModel> im){
        super(im);
    }

    @Override
    public void bindView(int position, View itemContentView, IntroModel item) {
       ImageView img =  (ImageView)itemContentView.findViewById(R.id.img);
        TextView text = itemContentView.findViewById(R.id.des);
        img.setImageResource(item.getImg_id());
        text.setText(item.getDescription());
    }

    @Override
    public int getItemContentLayout(int position) {
        return R.layout.introlayout;
    }
}
