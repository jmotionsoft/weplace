package com.jmotionsoft.towntalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.model.Board;
import com.jmotionsoft.towntalk.model.BoardList;
import com.jmotionsoft.towntalk.model.Contents;
import com.jmotionsoft.towntalk.model.Group;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.model.UserLocation;
import com.jmotionsoft.towntalk.util.AppPreferences;
import com.jmotionsoft.towntalk.util.BoardUtil;
import com.jmotionsoft.towntalk.util.CLog;
import com.jmotionsoft.towntalk.util.DateUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ContentsListActivity extends AppCompatActivity{
    private final String TAG = getClass().getSimpleName();

    private final int REQUEST_CONTENTS_ADD = 1;
    private final int REQUEST_CONTENTS_VIEW = 2;
    private final int REQUEST_SETTING = 3;
    public static final String REQUEST_COMMAND_UPDATE = "UPDATE";
    public static final String REQUEST_COMMAND_DELETE = "DELETE";
    public static final String EXTRA_VIEW_COMMAND = "VIEW_COMMAND";

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Menu mMenu;

    private ListView lst_contents;
    private TextView txt_empty;
    private View headerView;
    private TextView txt_nickname;
    private TextView txt_email;
    private Spinner spn_location;

    private WePlaceService mWePlaceService;
    private AppPreferences mAppPreferences;
    private ContentsAdapter mContentsAdapter;

    private List<UserLocation> mLocationList;
    private BoardList mBoardList;
    private List<Contents> mContentsList;
    private String mCurrentBoardName;
    private int mCurrentBoardNo = -1;
    private int mViewContentsNo = -1;
    private int mViewContentsPosition = -1;
    private Integer mLastContentsNo;
    private boolean mListViewLock = false;
    private Member mMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.i(TAG, "onCreate()=> ");

        setContentView(R.layout.activity_contents_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        mContentsAdapter = new ContentsAdapter();

        txt_empty = (TextView)findViewById(R.id.txt_empty);
        lst_contents = (ListView)findViewById(R.id.lst_contents);
        lst_contents.setOnItemClickListener(onItemClickListener);
        lst_contents.setOnScrollListener(mOnScrollListener);

        headerView = LayoutInflater.from(this)
                .inflate(R.layout.nav_header_contents_list, mNavigationView);
        spn_location = (Spinner)headerView.findViewById(R.id.spn_location);
        txt_nickname = (TextView)headerView.findViewById(R.id.txt_nickname);
        txt_email = (TextView)headerView.findViewById(R.id.txt_email);
        headerView.findViewById(R.id.btn_setting).setOnClickListener(mOnClickListener);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());
        mAppPreferences = new AppPreferences(getApplicationContext());

        mContentsList = new LinkedList<>();
        lst_contents.setAdapter(mContentsAdapter);

        mMember = mAppPreferences.getMember();

        fillViewContents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CLog.i(TAG, "onResume()=> ");
    }

    private void fillViewContents(){
        CLog.i(TAG, "fillViewContents()=> ");

        getLocationList();
        getMenu();
    }

    private void getLocationList(){
        Call<List<UserLocation>> call = mWePlaceService.getLocationList();
        call.enqueue(new CustomCallback<List<UserLocation>>(this) {
            @Override
            public void onSuccess(Call<List<UserLocation>> call, Response<List<UserLocation>> response) {
                if(!response.isSuccessful()){
                    CustomToast.showMessage(getApplicationContext(), R.string.msg_false_get_location_list);
                }else{
                    mLocationList = response.body();
                }

                fillHeader();
            }
        });
    }

    private void fillHeader(){
        txt_nickname.setText(mMember.getNickname());
        txt_email.setText(mMember.getEmail());

        if(mLocationList == null || mLocationList.size() == 0)
            return;

        ArrayList<String> locationNameList = new ArrayList<>();
        int selectIndex = 0;
        for(int i = 0; mLocationList.size() > i; i++){
            locationNameList.add(mLocationList.get(i).getLocation_name());
            if(mLocationList.get(i).getDefault_yn().equals("Y")){
                selectIndex = i;
            }
        }

        ArrayAdapter<String> locationAdapter =
                new ArrayAdapter<>(this, R.layout.item_location_spinner, locationNameList);
        locationAdapter.setDropDownViewResource(R.layout.item_location_spinner_dropdown);
        spn_location.setAdapter(locationAdapter);
        spn_location.setSelection(selectIndex);
        spn_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CLog.i(TAG, "onItemSelected: "+mLocationList.get(i).getLocation_name());
                if(!mLocationList.get(i).getDefault_yn().equals("Y"))
                    selectLocation(mLocationList.get(i).getLocation_no());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getMenu(){
        Call<BoardList> call = mWePlaceService.getBoardList();
        call.enqueue(new CustomCallback<BoardList>(this) {
            @Override
            public void onSuccess(Call<BoardList> call, Response<BoardList> response) {
                if(!response.isSuccessful()){
                    CustomToast.showMessage(getApplicationContext(), R.string.msg_http_error);
                }else{
                    mBoardList = response.body();
                    createMenu();
                }
            }
        });
    }

    private void createMenu(){
        MenuBuilder menu = (MenuBuilder) mNavigationView.getMenu();

        LinkedHashSet<Integer> boardFavorites = mAppPreferences.getBoardFavorites();
        if(boardFavorites.size() > 0){
            SubMenu subMenu = menu.addSubMenu(getString(R.string.txt_favorites));
            for(int aBoardNo : boardFavorites){
                Board favoritesBoard = null;
                for(Board aBoard : mBoardList.getBoardList()){
                    if(aBoard.getBoard_no().equals(aBoardNo))
                        favoritesBoard = aBoard;
                }

                if(favoritesBoard == null)
                    continue;

                subMenu.add(0, aBoardNo, favoritesBoard.getOrder_no(),
                        favoritesBoard.getName())
                        .setIcon(BoardUtil.getBoardIcon(favoritesBoard.getIcon()));
            }
        }

        for(Group aGroup : mBoardList.getGroupList()){
            SubMenu subMenu = menu.addSubMenu(aGroup.getName());
            for(Board aBoard : mBoardList.getBoardList()){
                if(!aBoard.getGroup_no().equals(aGroup.getGroup_no()))
                    continue;

                subMenu.add(aGroup.getGroup_no(), aBoard.getBoard_no(), aBoard.getOrder_no(),
                        aBoard.getName())
                        .setIcon(BoardUtil.getBoardIcon(aBoard.getIcon()));
            }
        }

        MenuItem item = menu.findItem(1);
        mCurrentBoardName = item.getTitle().toString();
        getSupportActionBar().setTitle(mCurrentBoardName);
        mCurrentBoardNo = item.getItemId();
        mLastContentsNo = null;
        getContentsList(true);
    }

    private void changeMenu(){
        MenuBuilder menu = (MenuBuilder) mNavigationView.getMenu();
        menu.clearAll();

        LinkedHashSet<Integer> boardFavorites = mAppPreferences.getBoardFavorites();
        if(boardFavorites.size() > 0){
            SubMenu subMenu = menu.addSubMenu(getString(R.string.txt_favorites));
            for(int aBoardNo : boardFavorites){
                Board favoritesBoard = null;
                for(Board aBoard : mBoardList.getBoardList()){
                    if(aBoard.getBoard_no().equals(aBoardNo))
                        favoritesBoard = aBoard;
                }

                if(favoritesBoard == null)
                    continue;

                subMenu.add(0, aBoardNo, favoritesBoard.getOrder_no(),
                        favoritesBoard.getName())
                        .setIcon(BoardUtil.getBoardIcon(favoritesBoard.getIcon()));
            }
        }

        for(Group aGroup : mBoardList.getGroupList()){
            SubMenu subMenu = menu.addSubMenu(aGroup.getName());
            for(Board aBoard : mBoardList.getBoardList()){
                if(!aBoard.getGroup_no().equals(aGroup.getGroup_no()))
                    continue;

                subMenu.add(aGroup.getGroup_no(), aBoard.getBoard_no(), aBoard.getOrder_no(),
                        aBoard.getName())
                        .setIcon(BoardUtil.getBoardIcon(aBoard.getIcon()));
            }
        }
    }

    private void changeIconFavoritesMenu(){
        if(mMenu == null)
            return;

        MenuItem item = mMenu.findItem(R.id.action_favorites);
        if(item == null)
            return;

        if(mAppPreferences.isFavoritesBoard(mCurrentBoardNo))
            item.setIcon(R.drawable.ic_star);
        else
            item.setIcon(R.drawable.ic_star_border);
    }


    private void getContentsList(final boolean isClearList){
        if(isClearList)
            mLastContentsNo = null;

        Call<List<Contents>> call = mWePlaceService.getContentsList(
                mCurrentBoardNo, mLastContentsNo);
        call.enqueue(new CustomCallback<List<Contents>>(this) {
            @Override
            public void onSuccess(Call<List<Contents>> call, Response<List<Contents>> response) {
                if(!response.isSuccessful()) {
                    CustomToast.showMessage(getApplicationContext(), R.string.msg_http_error);
                    return;
                }

                if(mContentsList == null){
                    mContentsList = new LinkedList<>();
                }else if(isClearList){
                        mContentsList.clear();
                }

                List<Contents> contentsList = response.body();

                if(contentsList.size() > 0){
                    mLastContentsNo = contentsList.get(contentsList.size() -1).getContents_no();
                    mContentsList.addAll(response.body());

                    updateContentsList();

                    if(isClearList){
                        lst_contents.setSelectionAfterHeaderView();
                    }
                }
                mListViewLock = false;

                if(mContentsList.size() == 0){
                    lst_contents.setVisibility(View.GONE);
                    txt_empty.setVisibility(View.VISIBLE);
                }else{
                    lst_contents.setVisibility(View.VISIBLE);
                    txt_empty.setVisibility(View.GONE);
                }
            }
        });

        changeIconFavoritesMenu();
    }

    private void updateContentsList(){
        mContentsAdapter.notifyDataSetChanged();
    }

    class ContentsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mContentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mContentsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Contents contents = mContentsList.get(position);
            ViewHolder viewHolder = new ViewHolder();

            if(convertView == null){
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_contents, parent, false);

                viewHolder.txt_title = (TextView)convertView.findViewById(R.id.txt_title);
                viewHolder.txt_comment_count = (TextView)convertView.findViewById(R.id.txt_comment_count);
                viewHolder.txt_nickname = (TextView)convertView.findViewById(R.id.txt_nickname);
                viewHolder.txt_reg_date = (TextView)convertView.findViewById(R.id.txt_reg_date);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.txt_title.setText(contents.getTitle());
            if(contents.getComment_count() > 0){
                viewHolder.txt_comment_count.setText(String.valueOf(contents.getComment_count()));
            }else{
                viewHolder.txt_comment_count.setVisibility(View.GONE);
            }

            viewHolder.txt_nickname.setText(contents.getNickname());
            viewHolder.txt_reg_date.setText(DateUtil.getDateByTimeZone(contents.getReg_date()));

            return convertView;
        }

        class ViewHolder{
            TextView txt_title;
            TextView txt_comment_count;
            TextView txt_nickname;
            TextView txt_reg_date;
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Contents contents = mContentsList.get(position);
            CLog.df(TAG, "board_no: %s, contents_no: %s", mCurrentBoardNo, contents.getContents_no());

            mViewContentsNo = contents.getContents_no();
            mViewContentsPosition = position;

            Intent viewIntent = new Intent(ContentsListActivity.this, ViewContentsActivity.class);
            viewIntent.putExtra(Contents.ID_BOARD_NAME, mCurrentBoardName);
            viewIntent.putExtra(Contents.ID_BOARD_NO, mCurrentBoardNo);
            viewIntent.putExtra(Contents.ID_CONTENTS_NO, contents.getContents_no());
            startActivityForResult(viewIntent, REQUEST_CONTENTS_VIEW);
        }
    };

    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    int id = item.getItemId();
                    CLog.df(TAG, "id: %s, title: %s", id, item.getTitle());

                    mCurrentBoardName = item.getTitle().toString();
                    getSupportActionBar().setTitle(mCurrentBoardName);
                    mCurrentBoardNo = id;
                    mLastContentsNo = null;
                    getContentsList(true);

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_setting:
                    startActivityForResult(
                            new Intent(ContentsListActivity.this, SettingActivity.class),
                            REQUEST_SETTING);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == REQUEST_CONTENTS_ADD){
            mLastContentsNo = null;
            getContentsList(true);
        }else if(requestCode == REQUEST_CONTENTS_VIEW){
            String command = null;
            if(data != null){
                command = data.getStringExtra(EXTRA_VIEW_COMMAND);
            }

            CLog.i(TAG, "onActivityResult()=> command: "+command);
            if(command == null) return;

            if(command.equals(REQUEST_COMMAND_DELETE)){
                mContentsList.remove(mViewContentsPosition);
                updateContentsList();
                return;
            }

            if(command.equals(REQUEST_COMMAND_UPDATE)){
                updateContentsAfterEdited();
            }
        }else if(requestCode == REQUEST_SETTING){
            mMember = mAppPreferences.getMember();
            fillViewContents();
        }
    }

    private void updateContentsAfterEdited(){
        Call<Contents> call = mWePlaceService.getContents(mCurrentBoardNo, mViewContentsNo);
        call.enqueue(new CustomCallback<Contents>(this) {
            @Override
            public void onSuccess(Call<Contents> call, Response<Contents> response) {
                if(response.isSuccessful()){
                    mContentsList.set(mViewContentsPosition, response.body());
                    updateContentsList();
                }else{
                    getContentsList(true);
                }
            }
        });
    }

    private void selectLocation(int location_no){
        Call<ResponseBody> call = mWePlaceService.selectLocation(location_no);
        call.enqueue(new CustomCallback<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    mDrawerLayout.closeDrawers();
                    getContentsList(true);
                }else{
                    CustomAlert.newInstance(getString(R.string.msg_false_select_location))
                            .show(getFragmentManager(), TAG);
                    for(int i = 0; mLocationList.size() > i; i++){
                        if(mLocationList.get(i).getDefault_yn().equals("Y")){
                            spn_location.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contents_list, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent addIntent = new Intent(ContentsListActivity.this, ContentsAddActivity.class);
            addIntent.putExtra(Contents.ID_BOARD_NAME, mCurrentBoardName);
            addIntent.putExtra(Contents.ID_BOARD_NO, mCurrentBoardNo);
            startActivityForResult(addIntent, REQUEST_CONTENTS_ADD);
            return true;
        }else if(id == R.id.action_refresh){
            mLastContentsNo = null;
            getContentsList(true);
            return true;
        }else if(id == R.id.action_favorites){
            mAppPreferences.putAndRemoveBoardFavorites(mCurrentBoardNo);
            changeIconFavoritesMenu();
            changeMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(totalItemCount == 0 || totalItemCount < BoardList.PAGE_COUNT || mListViewLock) return;

            if(firstVisibleItem + visibleItemCount == totalItemCount){
                mListViewLock = true;
                getContentsList(false);
            }
        }
    };
}
