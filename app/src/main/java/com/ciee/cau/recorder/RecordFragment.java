package com.ciee.cau.recorder;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecordFragment extends Fragment {

    private static final String ARG_RECORD_ID = "record_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Record mRecord;
    private File mPhotoFile;
    private ImageView mPhotoView;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onRecordUpdated(Record record);
    }

    /**
     * 创建RecordFragment
     */
    public static RecordFragment newInstance(UUID recordId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECORD_ID, recordId);

        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args); // 每个fragment实例都可附带一个Bundle对象
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID recordId = (UUID) getArguments().getSerializable(ARG_RECORD_ID);
        mRecord = RecordLab.get(getActivity()).getRecord(recordId);
        mPhotoFile = RecordLab.get(getActivity()).getPhotoFile(mRecord);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record,container,false);

        mPhotoView = v.findViewById(R.id.record_photo);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null;
        mPhotoView.setEnabled(canTakePhoto);

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.ciee.cau.recorder.fileProvider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = packageManager.queryIntentActivities(captureImage, packageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView();
            }
        });


        mTitleField = v.findViewById(R.id.record_title);
        mTitleField.setText(mRecord.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecord.setTitle(s.toString());
                updateRecord();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mDateButton = v.findViewById(R.id.record_date);
        updateDate(mDateButton,"yyyy年MM月dd日");
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mRecord.getDate());
                dialog.setTargetFragment(RecordFragment.this, REQUEST_DATE); // 设置目标Fragment，以便接收返回数据
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = v.findViewById(R.id.record_time);
        updateDate(mTimeButton,"HH时mm分");
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mRecord.getDate());
                dialog.setTargetFragment(RecordFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = v.findViewById(R.id.record_solved);
        mSolvedCheckBox.setChecked(mRecord.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRecord.setSolved(isChecked);
                updateRecord();
            }
        });

        final Intent pickContent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = v.findViewById(R.id.record_suspect);
        mSuspectButton.setOnClickListener(v1 -> startActivityForResult(pickContent, REQUEST_CONTACT));

        if (mRecord.getPerson() != null) {
            mSuspectButton.setText(mRecord.getPerson());
        }

        mReportButton = v.findViewById(R.id.record_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder intent = ShareCompat.IntentBuilder.from(getActivity());
                intent.setType("text/plain");
                intent.setText(getRecordReport());
                intent.setSubject(getString(R.string.record_report_subject));
                intent.setChooserTitle(getString(R.string.send_report));
                intent.startChooser();
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        RecordLab.get(getActivity()).updateRecord(mRecord);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mRecord.setDate(date);
            updateDate(mDateButton,"yyyy年MM月dd日");
            updateRecord();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mRecord.setDate(date);
            updateDate(mTimeButton,"HH时mm分");
            updateRecord();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            try (Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null)) {
                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                String suspect = c.getString(0);
                mRecord.setPerson(suspect);
                updateRecord();
                mSuspectButton.setText(suspect);
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.ciee.cau.recorder.fileProvider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // 关闭文件访问
            updateRecord();
            updatePhotoView();
        }
    }

    /**
     * 更新记录
     */
    private void updateRecord() {
        RecordLab.get(getActivity()).updateRecord(mRecord);
        mCallbacks.onRecordUpdated(mRecord);
    }

    /**
     * 更新日期/时间
     */
    private void updateDate(Button button, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormat = new SimpleDateFormat(format);
        button.setText(mFormat.format(mRecord.getDate()));
    }

    /**
     * 获取分享报文
     */
    private String getRecordReport() {
        String solvedString = null;
        if (mRecord.isSolved()) {
            solvedString = getString(R.string.record_report_solved);
        } else {
            solvedString = getString(R.string.record_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mRecord.getDate()).toString();

        String person = mRecord.getPerson();
        if (person == null) {
            person = getString(R.string.record_report_no_suspect);
        } else {
            person = getString(R.string.record_report_suspect, person);
        }

        String report = getString(R.string.record_report, mRecord.getTitle(), dateString, solvedString, person);

        return report;
    }

    /**
     * 更新照片
     */
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
//            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),mPhotoView.getWidth(), mPhotoView.getHeight());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}