package com.luisrubenrodriguez.getyourguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luisrubenrodriguez.getyourguide.adapter.ReviewRecyclerViewAdapter;
import com.luisrubenrodriguez.getyourguide.model.Review;
import com.luisrubenrodriguez.getyourguide.model.ReviewInfo;
import com.luisrubenrodriguez.getyourguide.service.GYGService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseApp implements MainView, ReviewRecyclerViewAdapter.OnLoadMoreListener {

    public static final int ADDREVIEW_REQUEST_CODE = 1;
    public static final int FIRST_PAGE = 0;
    //TODO move to another place, i.e enum
    private static final String PREFERENCES = "preferences";
    private static final String CACHED_LIST = "cached_list";
    private static final String CACHED_NEXTPAGE = "cached_nextpage";
    private static final String CACHED_SELECTEDFILTER = "cached_selectedfilter";


    private static final String TAG = "MainActivity";
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.review_list)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.add_review)
    FloatingActionButton mAddReview;
    @BindView(R.id.filterSpinner)
    Spinner mFilterSpinner;

    private List<Review> mReviewList;
    private MainPresenter mMainPresenter;
    private boolean isLoadingCachedData = false;

    @Inject
    public GYGService mGYGService;

    private LinearLayoutManager mRecyclerViewLayoutManager;
    private ReviewRecyclerViewAdapter mReviewRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDependencies().inject(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mReviewList = new ArrayList<>();
        initSwipeRefreshLayout();
        initRecyclerView();
        initSpinner();

        mMainPresenter = new MainPresenter(mGYGService, this);
        mMainPresenter.getReviews(FIRST_PAGE, mFilterSpinner.getSelectedItemPosition());
        mAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddReviewActivity();
            }
        });
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMainPresenter.getReviews(FIRST_PAGE, mFilterSpinner.getSelectedItemPosition());
            }
        });
    }

    /**
     * Setup of the recyclerView, layoutManager, itemDecoration and the Adapter.
     */
    private void initRecyclerView() {
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mReviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(this);
        mReviewRecyclerViewAdapter.setLoadMoreListener(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                mRecyclerViewLayoutManager.getOrientation());
        mReviewRecyclerView.addItemDecoration(dividerItemDecoration);
        mReviewRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mReviewRecyclerView.setAdapter(mReviewRecyclerViewAdapter);
    }

    /**
     * Initialization of the spinner, setup of the ItemSelectedListener.
     */
    private void initSpinner() {
        mFilterSpinner.setSelection(0, false);
        mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Flag added to avoid calling again the API when restoring from cache.
                if (!isLoadingCachedData) {
                    mMainPresenter.getReviews(FIRST_PAGE, position);
                } else {
                    isLoadingCachedData = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * It will start the add review activity.
     */
    private void startAddReviewActivity() {
        Intent intent = new Intent(this, AddReviewActivity.class);
        startActivityForResult(intent, ADDREVIEW_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            showLoading();
            mMainPresenter.getReviews(FIRST_PAGE, mFilterSpinner.getSelectedItemPosition());
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows loading animation
     */
    @Override
    public void showLoading() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    /**
     * Hides the loading animation
     */
    @Override
    public void hideLoading() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(this, R.string.error_retrieving_data, Toast.LENGTH_SHORT).show();
    }

    /**
     * Takes data received from the API and informs the recyclerview.adapter of the change
     *
     * @param reviewInfo Object created from the API response
     */
    @Override
    public void loadReviewList(ReviewInfo reviewInfo) {

        if (reviewInfo.getReviews() != null) {
            List<Review> reviews = reviewInfo.getReviews();

            if (reviews.size() > 0) {

                //TODO move this logic.
                boolean scrollToTop = false;
                //Case loading first page.
                if (reviewInfo.getCurrentPage() == 0) {
                    mReviewList = reviews;
                    scrollToTop = true;
                } else {
                    //Remove the loading item
                    mReviewList.remove(mReviewList.size() - 1);
                    mReviewList.addAll(reviews);
                }
                //Check if there are more items to retrieve.
                Integer nextPage = reviewInfo.getCurrentPage() + 1;
                if (nextPage * GYGService.REVIEW_COUNT < reviewInfo.getTotalReviews()) {
                    mReviewList.add(new Review(Review.TYPE_REVIEW));
                    mReviewRecyclerViewAdapter.setNextPage(nextPage);
                } else {
                    mReviewRecyclerViewAdapter.setNextPage(ReviewRecyclerViewAdapter.NOMOREPAGES);
                }
                mReviewRecyclerViewAdapter.loadNewData(mReviewList);
                if (scrollToTop) {
                    mRecyclerViewLayoutManager.scrollToPosition(0);
                }
            }
        }
    }

    /**
     * Add a review object to the list.
     * I decided to add the review at the first position and scroll to the top, adding it to the beginning of the list is inefficient and should be refactor.
     *
     * @param review Object received from the MockAPI
     */
    @Override
    public void addLocalReview(Review review) {
        mReviewList.add(0, review);
        mReviewRecyclerViewAdapter.loadNewData(mReviewList);
        mRecyclerViewLayoutManager.scrollToPosition(0);
    }

    /**
     * Tries to load the next page.
     *
     * @param page Page to be loade
     */
    @Override
    public void onLoadMore(Integer page) {
        mMainPresenter.getReviews(page, mFilterSpinner.getSelectedItemPosition());
    }

    /**
     * Process the result of the addReviewActivity
     *
     * @param requestCode Payload/Request code used for the activity.
     * @param resultCode  result code
     * @param data        Intent Data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case ADDREVIEW_REQUEST_CODE:
                    String title = data.getStringExtra(AddReviewActivity.ADDREVIEW_TITLE);
                    String message = data.getStringExtra(AddReviewActivity.ADDREVIEW_MESSAGE);
                    String rating = data.getStringExtra(AddReviewActivity.ADDREVIEW_RATING);
                    //Log.d(TAG, "onActivityResult: now process this data: " + title + " message: " + message + "rating: " + rating);
                    mMainPresenter.addReview(new Review(rating, title, message));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCachedData();
    }

    /**
     * Saves the current List<Review> to SharedPreferences.
     */
    private void saveCachedData() {
        if (mReviewList != null && mReviewList.size() > 0) {

            String reviewListString = new Gson().toJson(mReviewList);

            SharedPreferences pref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString(CACHED_LIST, reviewListString);
            editor.putInt(CACHED_NEXTPAGE, mReviewRecyclerViewAdapter.getNextPage());
            editor.putInt(CACHED_SELECTEDFILTER, mFilterSpinner.getSelectedItemPosition());
            editor.commit();

        }
    }

    /**
     * Check if there's cached data and loads it.
     */
    @Override
    public void tryLoadingCachedData() {
        SharedPreferences pref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String reviewListString = pref.getString(CACHED_LIST, null);

        if (null != reviewListString) {

            int filterPosition = pref.getInt(CACHED_SELECTEDFILTER, 0);
            int nextpage = pref.getInt(CACHED_NEXTPAGE, -1);
            try {
                Type listType = new TypeToken<ArrayList<Review>>() {
                }.getType();

                mReviewList = new Gson().fromJson(reviewListString, listType);
                mReviewRecyclerViewAdapter.loadNewData(mReviewList);
                mReviewRecyclerViewAdapter.setNextPage(nextpage);
                isLoadingCachedData = true;
                mFilterSpinner.setSelection(filterPosition);
                Toast.makeText(this, R.string.cached_data_loaded, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d(TAG, "loadCachedData: error while loading cached data");
            }
        }
    }

}
