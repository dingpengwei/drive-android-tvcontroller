package com.goodow.drive.android.tvcontroller.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.goodow.drive.android.tvcontroller.TVControllerActivity;

/**
 * @Author:DingPengwei
 * @Email:dingpengwei@goodow.com
 * @DateCrate:May 28, 2014 5:55:23 PM
 * @DateUpdate:May 28, 2014 5:55:23 PM
 * @Des:description
 */
public class TVControllerSampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, TVControllerActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}
