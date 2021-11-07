package com.kirito.basic.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.kirito.basic.App;
import com.kirito.basic.R;
import com.kirito.basic.base.BaseActivity;
import com.kirito.basic.databinding.ActivitySettingBinding;
import com.kirito.basic.net.NetInterface;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.tencent.mmkv.MMKV;

import java.util.LinkedHashMap;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(v.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}