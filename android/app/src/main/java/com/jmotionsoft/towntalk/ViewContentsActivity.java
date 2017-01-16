package com.jmotionsoft.towntalk;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jmotionsoft.towntalk.dialog.CommentDialog;
import com.jmotionsoft.towntalk.dialog.CommentDialogCallback;
import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomAlertListener;
import com.jmotionsoft.towntalk.message.CustomConfirm;
import com.jmotionsoft.towntalk.message.CustomConfirmListener;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.model.Comment;
import com.jmotionsoft.towntalk.model.Contents;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.util.AppPreferences;
import com.jmotionsoft.towntalk.util.CLog;
import com.jmotionsoft.towntalk.util.DateUtil;

import java.util.LinkedList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ViewContentsActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private final int REQUEST_EDIT = 1;

    private ScrollView scr_view;
    private TextView txt_title;
    private TextView txt_nickname;
    private TextView txt_reg_date;
    private TextView txt_body;
    private LinearLayout lay_images;
    private ListView lst_comment;

    private WePlaceService mWePlaceService;
    private CommentAdapter mCommentAdapter;
    private AppPreferences mAppPreferences;

    private int mBoardNo = -1;
    private int mContentsNo = -1;
    private Comment mComment;
    private String mBoardName = "";
    private Member mMember;
    private Contents mContents;
    private Menu mMenu;

    private List<Comment> mCommentList;

    int mCommentPosition = -1;
    private boolean mIsCommentScrollLast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Contents.ID_BOARD_NAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_contents);

        scr_view = (ScrollView)findViewById(R.id.scr_view);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_nickname = (TextView)findViewById(R.id.txt_nickname);
        txt_reg_date = (TextView)findViewById(R.id.txt_reg_date);
        lay_images = (LinearLayout)findViewById(R.id.lay_images);
        txt_body = (TextView)findViewById(R.id.txt_body);
        lst_comment = (ListView)findViewById(R.id.lst_comment);

        mBoardName = getIntent().getStringExtra(Contents.ID_BOARD_NAME);
        mBoardNo = getIntent().getIntExtra(Contents.ID_BOARD_NO, -1);
        mContentsNo = getIntent().getIntExtra(Contents.ID_CONTENTS_NO, -1);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());
        mAppPreferences = new AppPreferences(getApplicationContext());

        mCommentAdapter = new CommentAdapter();
        mCommentList = new LinkedList<>();
        lst_comment.setAdapter(mCommentAdapter);
        lst_comment.setOnItemLongClickListener(mOnItemLongClickListener);

        mMember = mAppPreferences.getMember();

        getContents();
    }

    private void getContents(){
        Call<Contents> call = mWePlaceService.getContents(mBoardNo, mContentsNo);
        call.enqueue(new CustomCallback<Contents>(this) {
            @Override
            public void onSuccess(Call<Contents> call, Response<Contents> response) {
                if(!response.isSuccessful()){
                    CustomAlert.newInstance(getString(R.string.msg_server_error), new CustomAlertListener() {
                        @Override
                        public void onClickPositiveButton() {
                            finish();
                        }
                    }).show(getFragmentManager(), TAG);
                    return;
                }

                mContents = response.body();
                txt_title.setText(mContents.getTitle());
                txt_nickname.setText(mContents.getNickname());
                txt_reg_date.setText(DateUtil.getDateByTimeZone(mContents.getReg_date()));

                txt_body.setText(mContents.getBody());

                if(mContents.getUser_no().equals(mMember.getUser_no())){
                    mMenu.findItem(R.id.action_edit_contents).setVisible(true);
                    mMenu.findItem(R.id.action_delete_contents).setVisible(true);
                }

                if(mContents.getImages_no() != null && !mContents.getImages_no().trim().equals("")){
                    getImages(mContents.getImages_no());
                }

                getCommentList();
            }
        });
    }

    private void getImages(String images_no){
        lay_images.removeAllViews();

        String[] imageArray = images_no.split(",");
        int image_size = imageArray.length;

        for(int i = 0; image_size > i; i++){
            ImageView imageView = new ImageView(this);

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 36, 0, 0);
            imageView.setLayoutParams(layoutParams);

            HttpApi.loadImage(this, imageArray[i]).into(imageView);

            lay_images.addView(imageView);
        }
    }

    private void getCommentList(){
        Call<List<Comment>> call = mWePlaceService.getCommentList(mContentsNo);
        call.enqueue(new CustomCallback<List<Comment>>(this) {
            @Override
            public void onSuccess(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(!response.isSuccessful()){
                    CustomToast.showMessage(ViewContentsActivity.this, R.string.msg_http_server_error);
                    return;
                }

                mCommentList = response.body();
                updateCommentList();
            }
        });
    }

    private void updateCommentList(){
        mCommentAdapter.notifyDataSetChanged();

        if(mIsCommentScrollLast){
            scr_view.post(new Runnable() {
                @Override
                public void run() {
                    scr_view.fullScroll(View.FOCUS_DOWN);
                }
            });
            mIsCommentScrollLast = false;
        }

        setListViewHeightBasedOnChildren();
    }

    private void confirmDeleteContents(){
        CustomConfirm.newInstance(getString(R.string.msg_confirm_delete_contents),
                new CustomConfirmListener() {
            @Override
            public void onClickNegativeButton() {

            }

            @Override
            public void onClickPositiveButton() {
                deleteContents();
            }
        }).show(getFragmentManager(), TAG);
    }

    private void deleteContents(){
        Call<ResponseBody> call = mWePlaceService.deleteContents(mBoardNo, mContentsNo);
        call.enqueue(new CustomCallback<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    CustomToast.showMessage(ViewContentsActivity.this, R.string.msg_http_server_error);
                    return;
                }

                CustomAlert.newInstance(getString(R.string.msg_success_delete), new CustomAlertListener() {
                    @Override
                    public void onClickPositiveButton() {
                        Intent intent = new Intent();
                        intent.putExtra(ContentsListActivity.EXTRA_VIEW_COMMAND,
                                ContentsListActivity.REQUEST_COMMAND_DELETE);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).show(getFragmentManager(), TAG);
            }
        });
    }

    private void addComment(){
        CommentDialog.newInstance(new CommentDialogCallback() {
            @Override
            public void onClickDone(final DialogFragment dialog, String addComment) {
                if(addComment == null || addComment.trim().equals("")){
                    CustomToast.showMessage(ViewContentsActivity.this, R.string.msg_input_comment);
                    return;
                }

                Comment comment = new Comment();
                comment.setContents_no(mContentsNo);
                comment.setComment(addComment);
                Call<ResponseBody> call = mWePlaceService.addComment(mContentsNo, comment);
                call.enqueue(new CustomCallback<ResponseBody>(ViewContentsActivity.this) {
                    @Override
                    public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            dialog.dismiss();

                            mIsCommentScrollLast = true;
                            getCommentList();

                            setResultListUpdate();
                        }else{
                            CustomToast.showMessage(
                                    ViewContentsActivity.this, R.string.msg_http_server_error);
                        }
                    }
                });
            }
        }).show(getFragmentManager(), TAG);
    }

    private void editComment(){
        CommentDialog.newInstance(mComment.getComment(), new CommentDialogCallback() {
            @Override
            public void onClickDone(final DialogFragment dialog, String editedComment) {
                if(editedComment == null || editedComment.trim().equals("") || editedComment.equals(mComment.getComment())){
                    CustomToast.showMessage(ViewContentsActivity.this, R.string.msg_input_comment);
                    return;
                }

                mComment.setComment(editedComment);
                Call<ResponseBody> call = mWePlaceService.editComment(mContentsNo, mComment);
                call.enqueue(new CustomCallback<ResponseBody>(ViewContentsActivity.this) {
                    @Override
                    public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            dialog.dismiss();
                            updateCommentAfterEdited();
                        }else{
                            CustomToast.showMessage(
                                    ViewContentsActivity.this, R.string.msg_http_server_error);
                        }
                    }
                });
            }
        }).show(getFragmentManager(), TAG);
    }

    private void updateCommentAfterEdited(){
        Call<Comment> call = mWePlaceService.getComment(mContentsNo, mComment.getComment_no());
        call.enqueue(new CustomCallback<Comment>(this) {
            @Override
            public void onSuccess(Call<Comment> call, Response<Comment> response) {
                if(response.isSuccessful()){
                    mCommentList.set(mCommentPosition, response.body());
                    updateCommentList();
                }else{
                    getCommentList();
                }
            }
        });
    }

    private void deleteComment(){
        Call<ResponseBody> call = mWePlaceService.deleteComment(mContentsNo, mComment.getComment_no());
        call.enqueue(new CustomCallback<ResponseBody>(ViewContentsActivity.this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())  {
                    mCommentList.remove(mComment);
                    updateCommentList();
                    setResultListUpdate();
                }else{
                    CustomToast.showMessage(
                            ViewContentsActivity.this, R.string.msg_http_server_error);
                }
            }
        });
    }

    private void showCommentMoreMenu(View v, Comment comment, int position){
        mComment = comment;
        mCommentPosition = position;

        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.comment_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(mOnMenuItemClickListener);
        popup.show();
    }

    private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener =
            new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_edit_comment:
                    editComment();
                    return true;

                case R.id.action_delete_comment:
                    deleteComment();
                    return true;

                default:
                    return false;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contents_view_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_add_comment:
                addComment();
                return true;

            case R.id.action_edit_contents:
                Intent editIntent = new Intent(ViewContentsActivity.this, ContentsEditActivity.class);
                editIntent.putExtra(Contents.ID_BOARD_NO, mBoardNo);
                editIntent.putExtra(Contents.ID_CONTENTS_NO, mContentsNo);
                editIntent.putExtra(Contents.ID_BOARD_NAME, mBoardName);
                startActivityForResult(editIntent, REQUEST_EDIT);
                return true;

            case R.id.action_delete_contents:
                confirmDeleteContents();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class CommentAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mCommentList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCommentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Comment comment = mCommentList.get(position);
            ViewHolder viewHolder = new ViewHolder();

            if(convertView == null){
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_comment, parent, false);

                viewHolder.txt_nickname = (TextView)convertView.findViewById(R.id.txt_nickname);
                viewHolder.txt_reg_date = (TextView)convertView.findViewById(R.id.txt_reg_date);
                viewHolder.txt_comment = (TextView)convertView.findViewById(R.id.txt_comment);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.txt_nickname.setText(comment.getNickname());
            viewHolder.txt_reg_date.setText(DateUtil.getDateByTimeZone(comment.getReg_date()));
            viewHolder.txt_comment.setText(comment.getComment());

            return convertView;
        }

        public class ViewHolder{
            TextView txt_nickname;
            TextView txt_reg_date;
            TextView txt_comment;
        }
    }

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener =
            new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Comment comment = mCommentList.get(position);

            CLog.d(TAG, comment.getUser_no()+"/"+mMember.getUser_no());

            if(comment.getUser_no().equals(mMember.getUser_no())){
                showCommentMoreMenu(view, comment, position);
                return true;
            }else{
                return false;
            }
        }
    };

    public void setListViewHeightBasedOnChildren() {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(lst_comment.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < mCommentAdapter.getCount(); i++) {
            View listItem = mCommentAdapter.getView(i, null, lst_comment);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lst_comment.getLayoutParams();
        params.height = totalHeight + (lst_comment.getDividerHeight() * (mCommentAdapter.getCount() - 1));
        lst_comment.setLayoutParams(params);
        lst_comment.requestLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_EDIT){
            getContents();

            setResultListUpdate();
        }
    }

    private void setResultListUpdate(){
        Intent intent = new Intent();
        intent.putExtra(ContentsListActivity.EXTRA_VIEW_COMMAND,
                ContentsListActivity.REQUEST_COMMAND_UPDATE);
        setResult(RESULT_OK, intent);
    }
}
