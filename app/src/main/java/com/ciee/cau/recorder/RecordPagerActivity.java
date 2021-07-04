package com.ciee.cau.recorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class RecordPagerActivity extends AppCompatActivity implements RecordFragment.Callbacks{

    private static final String EXTRA_RECORD_ID = "com.ciee.cau.recorder.record_id";

    private ViewPager mViewPager;
    private List<Record> mRecords;

    private Button mJumpToFirstButton;
    private Button mJumpToLastButton;

    public static Intent newIntent(Context packageContext, UUID recordId) {
        Intent intent = new Intent(packageContext, RecordPagerActivity.class);
        intent.putExtra(EXTRA_RECORD_ID, recordId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_pager);

        UUID recordId = (UUID) getIntent().getSerializableExtra(EXTRA_RECORD_ID);

        mViewPager = findViewById(R.id.record_view_pager);
        mJumpToFirstButton = findViewById(R.id.jump_to_first);
        mJumpToLastButton = findViewById(R.id.jump_to_last);

        mJumpToFirstButton.setOnClickListener(v -> mViewPager.setCurrentItem(0));
        mJumpToLastButton.setOnClickListener(v -> mViewPager.setCurrentItem(mRecords.size() - 1));

        mRecords = RecordLab.get(this).getRecords();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager,0) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Record record = mRecords.get(position);
                return RecordFragment.newInstance(record.getId());
            }

            @Override
            public int getCount() {
                return mRecords.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mJumpToFirstButton.setVisibility(View.VISIBLE);
                mJumpToLastButton.setVisibility(View.VISIBLE);

                if (position == 0) {
                    mJumpToFirstButton.setVisibility(View.INVISIBLE);
                }
                if (position == mRecords.size() - 1) {
                    mJumpToLastButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < mRecords.size(); i++) {
            if (mRecords.get(i).getId().equals(recordId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onRecordUpdated(Record record) {

    }
}