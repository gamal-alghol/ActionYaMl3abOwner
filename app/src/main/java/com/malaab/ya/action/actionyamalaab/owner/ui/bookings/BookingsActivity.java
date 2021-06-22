package com.malaab.ya.action.actionyamalaab.owner.ui.bookings;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingPlayer;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full.FullBookingsAdapter;
import com.malaab.ya.action.actionyamalaab.owner.ui.user.UsersDetailsActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.ActivityUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.yayandroid.theactivitymanager.TheActivityManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BookingsActivity extends BaseActivity implements BookingsMvpView {

    @BindView(R.id.mAppBar)
    public AppBarLayout mAppBar;

    @BindView(R.id.header_txt_title)
    TextView header_txt_title;
    @BindView(R.id.header_btn_back)
    AppCompatImageButton header_btn_back;
    @BindView(R.id.header_btn_notifications)
    AppCompatImageButton header_btn_notifications;

//    @BindView(R.id.tl_container)
//    public TabLayout tl_container;
//    @BindView(R.id.vp_content)
//    public RTLViewPager vp_content;

    @BindView(R.id.pBar_loading)
    public ProgressBar pBar_loading;

    @BindView(R.id.rv_items)
    public RecyclerView rv_items;

    @Inject
    BookingsPresenter<BookingsMvpView> mPresenter;

    private FullBookingsAdapter adapter;
    private User mUser;

//    private static final int TAB_INDIVIDUAL = 1;
//    private static final int TAB_Full = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(this);
    }

    @Override
    protected void initUI() {
        header_btn_back.setVisibility(View.VISIBLE);
        header_btn_notifications.setVisibility(View.INVISIBLE); /* Just To fix UI (to center Title) */
        header_txt_title.setText(R.string.title_bookings_manage);

        rv_items.setHasFixedSize(true);
        rv_items.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //region TO ENABLE VIEWPAGER WITH TABS
        //        Tab[] mTabs = new Tab[]{
//                new Tab(TAB_INDIVIDUAL, getString(R.string.individual)) {
//                    @Override
//                    public Fragment getFragment() {
//                        return new IndividualBookingsFragment();
//                    }
//                },
//                new Tab(TAB_Full, getString(R.string.full)) {
//                    @Override
//                    public Fragment getFragment() {
//                        return new FullBookingsFragment();
//                    }
//                }
//        };
//
//        RTLPagerAdapter mTabsAdapter = new RTLPagerAdapter(getSupportFragmentManager(), mTabs, true);
//        vp_content.setAdapter(mTabsAdapter);
//        vp_content.setRtlOriented(true);
//        tl_container.setupWithViewPager(vp_content);
//
//        vp_content.setCurrentItem(0);
        //endregion

//        FragmentsAdapter mAdapter = new FragmentsAdapter(getSupportFragmentManager());
//        mAdapter.addFragment(new Fragment(), getString(R.string.individual));
//        mAdapter.addFragment(new (), getString(R.string.full));

//        vp_content.setOffscreenPageLimit(mAdapter.getCount() - 1);
//        vp_content.setAdapter(mAdapter);

//        vp_content.setRtlOriented(true);
//        tl_container.setupWithViewPager(vp_content);
    }


    @OnClick(R.id.header_btn_back)
    public void goBack() {
        onBackPressed();
    }


    @Override
    public void showLoading() {
        pBar_loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        pBar_loading.setVisibility(View.GONE);
    }


    @Override
    public void onGetCurrentUser(User user) {
        mUser = user;
        mPresenter.getFullBookings(mUser.uId);
    }


    @Override
    public void onGetFullBookings(List<Booking> bookings) {
        adapter = new FullBookingsAdapter(bookings);
        rv_items.setAdapter(adapter);

        rv_items.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceiveBookingSuccess(Booking booking) {
        adapter.updateItem(booking, true);
    }


    @Override
    public void onUserAuthenticationFailed() {
        super.onUserAuthenticationFailed();
    }

    @Override
    public void onNoInternetConnection() {
        onError(R.string.error_no_connection);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventItemClicked(OnEventItemClicked event) {
        if (TheActivityManager.getInstance().getCurrentActivity() instanceof BookingsActivity) {
            if (event.getItem() instanceof Booking && event.getAction() == ItemAction.RECEIVE) {
                mPresenter.receiveBooking((Booking) event.getItem(), mUser.uId);
            } else if (event.getItem() instanceof BookingPlayer && event.getAction() == ItemAction.DETAILS) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.INTENT_KEY_USER_UID, ((BookingPlayer) event.getItem()).uId);

                ActivityUtils.goTo(BookingsActivity.this, UsersDetailsActivity.class, false, bundle);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        mPresenter.getCurrentUser();
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideKeyboard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mPresenter.onDetach();
        super.onDestroy();
    }
}
