package com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingPlayer;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.di.component.ActivityComponent;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseFragment;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsActivity;
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


public class FullBookingsFragment extends BaseFragment implements FullBookingsMvpView {

    @BindView(R.id.pBar_loading)
    public ProgressBar pBar_loading;
    @BindView(R.id.rv_items)
    public RecyclerView rv_items;
    @Inject
    FullBookingsMvpPresenter<FullBookingsMvpView> mPresenter;

    private FullBookingsAdapter adapter;
    private boolean mUserVisibleHint = true;
    private User mUser;
    public FullBookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        mUserVisibleHint = visible;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bookings, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, rootView));
            mPresenter.onAttach(this);
        }

        return rootView;
    }


    @Override
    protected void initUI() {
        rv_items.setHasFixedSize(true);
        rv_items.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventItemClicked(OnEventItemClicked event) {
        if (TheActivityManager.getInstance().getCurrentActivity() instanceof BookingsActivity && mUserVisibleHint) {

            if (event.getItem() instanceof Booking && event.getAction() == ItemAction.RECEIVE) {
                mPresenter.receiveBooking((Booking) event.getItem(), mUser.uId);

            } else if (event.getItem() instanceof BookingPlayer && event.getAction() == ItemAction.DETAILS) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.INTENT_KEY_USER_UID, ((BookingPlayer) event.getItem()).uId);

                ActivityUtils.goTo(getActivity(), UsersDetailsActivity.class, false, bundle);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.getCurrentUser();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mPresenter.onDetach();
        super.onDetach();
    }
}