package com.ciee.cau.recorder;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * 仅有单个Fragment的抽象类
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-04-22-16:27
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * 创建一个Fragment
     */
    protected abstract Fragment createFragment();

    /**
     * 获取布局id
     */
    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager(); // activity被销毁时，它的FragmentManager会将fragment队列保存下来
        Fragment fragment = fm.findFragmentById(R.id.fragment_container); //从FragmentManager的队列中获取fragment
        if (fragment == null) { //若队列中没有fragment
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit(); //造一个fragment，然后添加到队列中
        }
    }
}
