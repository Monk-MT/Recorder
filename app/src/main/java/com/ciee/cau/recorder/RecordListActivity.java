package com.ciee.cau.recorder;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public class RecordListActivity extends SingleFragmentActivity
        implements RecordListFragment.Callbacks, RecordFragment.Callbacks{

    @Override
    protected Fragment createFragment() {
        return new RecordListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onRecordSelected(Record record) {
        if (findViewById(R.id.detail_fragement_container) == null) {
            Intent intent = RecordPagerActivity.newIntent(this, record.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = RecordFragment.newInstance(record.getId());

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragement_container, newDetail).commit();
        }
    }

    @Override
    public void onRecordUpdated(Record record) {
        RecordListFragment listFragment = (RecordListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}