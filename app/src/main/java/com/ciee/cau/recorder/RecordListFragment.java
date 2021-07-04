package com.ciee.cau.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-04-22-16:27
 */
public class RecordListFragment extends Fragment {
    private RecyclerView mRecordRecyclerView;
    private RecordAdapter mAdapter;
    private LinearLayout mNoRecordLinearLayout;
    private ImageView mAddFirstRecordImageView;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onRecordSelected(Record record);
    }

    @Override //在fragment附加给activity时调用
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        mRecordRecyclerView = view.findViewById(R.id.record_recycler_view);
        mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNoRecordLinearLayout = view.findViewById(R.id.no_record_view);

        updateUI();

        mAddFirstRecordImageView = view.findViewById(R.id.add_first_record);
        mAddFirstRecordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Record record = new Record();
                RecordLab.get(getActivity()).addRecord(record);
                updateUI();
                mCallbacks.onRecordSelected(record);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_record_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_record:
                Record record = new Record();
                RecordLab.get(getActivity()).addRecord(record);
                updateUI();
                mCallbacks.onRecordSelected(record);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * 更新ToolBar的副标题
     */
    private void updateSubtitle() {
        if (mSubtitleVisible) {
            RecordLab recordLab = RecordLab.get(getActivity());
            int recordCount = recordLab.getRecords().size();
            String subtitle = getString(R.string.subtitle_format, recordCount);

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setSubtitle(subtitle);
        } else {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setSubtitle(null);
        }
    }

    /**
     * 更新主界面UI
     */
    public void updateUI() {
        RecordLab recordLab = RecordLab.get(getActivity());
        List<Record> records = recordLab.getRecords();
        if (records.size() == 0) {
            mRecordRecyclerView.setVisibility(View.GONE);
            mNoRecordLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mRecordRecyclerView.setVisibility(View.VISIBLE);
            mNoRecordLinearLayout.setVisibility(View.GONE);
        }

        if (mAdapter == null) {
            mAdapter = new RecordAdapter(records);
            mRecordRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecords(records);
            mAdapter.notifyDataSetChanged();
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(mRecordRecyclerView);
        updateSubtitle();
    }

    private class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Record mRecord;

        public RecordHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_record, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.record_title);
            mDateTextView = itemView.findViewById(R.id.record_date);
            mSolvedImageView = itemView.findViewById(R.id.record_solved);
        }

        /**
         * 当ViewHolder显示时，更新ViewHolder
         * @param record
         */
        public void bind(Record record) {
            mRecord = record;
            mTitleTextView.setText(mRecord.getTitle());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat mformat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
            mDateTextView.setText(mformat.format(mRecord.getDate()));
            mSolvedImageView.setVisibility(record.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onRecordSelected(mRecord);
        }
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        private List<Record> mRecords;

        public RecordAdapter(List<Record> records){
            this.mRecords = records;
        }

        @NonNull
        @Override
        public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new RecordHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
            Record record = mRecords.get(position);
                holder.bind(record);
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }

        public void setRecords(List<Record> records) {
            mRecords = records;
        }
    }

    /**
     * 处理ViewHolder滑动逻辑
     */
    private class ItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            // 上下拖动
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            // 向左滑动
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            int fromPosition = viewHolder.getAdapterPosition();
//            int toPosition = viewHolder.getAdapterPosition();
//            if (fromPosition < toPosition) {
//                //分别把中间所有的 item 的位置重新交换
//                for (int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(mUserBookShelfResponses, i, i + 1);
//                }
//            } else {
//                for (int i = fromPosition; i > toPosition; i--) {
//                    Collections.swap(mUserBookShelfResponses, i, i - 1);
//                }
//            }
//            mAdapter.notifyItemMoved(fromPosition, toPosition);
//            return true;
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            //记录将要删除 item 的位置
//            int position = viewHolder.getAdapterPosition();
//            final UserBookShelfResponse bookShelfResponse = mUserBookShelfResponses.get(position);
//            bookShelfResponse.setIndex(position);
//            //将位置放到数据中，再保存到队列中方便操作
//            queue.add(bookShelfResponse);
//            //滑动删除，将该 item 数据从集合中移除，
//            //被移除的数据可能还需要被撤销，已经被保存到队列中了
//            mUserBookShelfResponses.remove(position);
//            mBookShelfAdapter.notifyItemRemoved(position);
        }

        //处理动画
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //滑动时改变 Item 的透明度，以实现滑动过程中实现渐变效果
                final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

//        //滑动事件完成时回调
//        //在这里可以实现撤销操作
//        @Override
//        public void clearView(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
//            super.clearView(recyclerView, viewHolder);
//            if (!queue.isEmpty()) {
//                //如果队列中有数据，说明刚才有删掉一些 item
//                Snackbar.make(((BaseActivity) getActivity()).getToolbar(), R.string.delete_bookshelf_success, Snackbar.LENGTH_LONG).setAction(R.string.repeal, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //SnackBar 的撤销按钮被点击，队列中取出刚被删掉的数据，然后再添加到数据集合，实现数据被撤回的动作
//                        final UserBookShelfResponse bookShelfResponse = (UserBookShelfResponse) queue.remove();
//                        //通知 Adapter                        mBookShelfAdapter.notifyItemInserted(bookShelfResponse.getIndex());
//                        mUserBookShelfResponses.add(bookShelfResponse.getIndex(), bookShelfResponse);
//                        //实际开发中遇到一个 bug：删除第一个item再撤销出现的试图延迟
//                        //手动将 recyclerView 滑到顶部可以解决这个bug
//                        if (bookShelfResponse.getIndex() == 0) {
//                            mRecyclerView.smoothScrollToPosition(0);
//                        }
//                    }
//                }).setCallback(new Snackbar.Callback() {
//                    //不撤销将做正在的删除操作，监听 SnackBar 消失事件，
//                    //SnackBar 消失（非排挤式消失）出队、访问服务器删除数据。
//                    @Override
//                    public void onDismissed(Snackbar snackbar, int event) {
//                        super.onDismissed(snackbar, event);
//                        //event 为消失原因，详细介绍在下文【附3】
//                        //排除一种情况就是联系删除多个 item SnackBar 挤掉前一个 SnackBar 导致的
//                        //消失
//                        if (event != DISMISS_EVENT_ACTION) {
//                            final UserBookShelfResponse bookShelfResponse = (UserBookShelfResponse) queue.remove();
//                            mLibraryPresenter.setBookShelf(BaseApplication.getUserId(), BaseApplication.getUserPassword(),
//                                    bookShelfResponse.getName(), bookShelfResponse.getRemark(), bookShelfResponse.getID(), 1);
//                        }
//                    }
//                }).show();
//            }
//        }

//        //是否长按进行拖拽
//        @Override
//        public boolean isLongPressDragEnabled() {
//            return true;
//        }
    }

}