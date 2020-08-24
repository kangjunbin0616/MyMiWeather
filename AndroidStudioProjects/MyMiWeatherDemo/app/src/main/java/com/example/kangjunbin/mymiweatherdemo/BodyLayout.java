package com.example.kangjunbin.mymiweatherdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import java.util.jar.Attributes;

/**
 * Created by kangjunbin on 2020/7/20.
 */

public class BodyLayout extends LinearLayout {
    public BodyLayout(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        LayoutInflater.from(context).inflate(R.layout.body_layout,this);
    }
}
