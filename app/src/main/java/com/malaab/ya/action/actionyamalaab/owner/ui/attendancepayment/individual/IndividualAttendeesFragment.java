package com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.individual;

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
import com.malaab.ya.action.actionyamalaab.owner.custom.DialogList;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingAgeCategory;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingPlayer;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.di.component.ActivityComponent;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.AttendeesActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.full.FullAttendeesAdapter;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseFragment;
import com.malaab.ya.action.actionyamalaab.owner.utils.ListUtils;
import com.yayandroid.theactivitymanager.TheActivityManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IndividualAttendeesFragment extends BaseFragment implements IndividualAttendeesMvpView {

    @BindView(R.id.pBar_loading)
    public ProgressBar pBar_loading;

    @BindView(R.id.rv_items)
    public RecyclerView rv_items;

    @Inject
    IndividualAttendeesMvpPresenter<IndividualAttendeesMvpView> mPresenter;
    @Inject
    DialogList mDialogList;

    private IndividualAttendeesAdapter adapter;
    private boolean mUserVisibleHint = true;

    private Booking booking;
    private User mUser;


    public IndividualAttendeesFragment() {
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

        mDialogList.build();
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
        mPresenter.getIndividualBookings(mUser.uId);
    }

    @Override
    public void onGetIndividualBookings(List<Booking> bookings) {
        adapter = new IndividualAttendeesAdapter(bookings);
        rv_items.setAdapter(adapter);

        rv_items.setVisibility(View.VISIBLE);

    }


    @Override
    public void onTakeAttendanceBookingSuccess(Booking booking) {
        adapter.updateItem(booking, true);
    }

    @Override
    public void onTakeAttendanceBookingSuccess(BookingPlayer player) {
        mDialogList.updateAttendeesBookingPlayer(player);
    }

    @Override
    public void onPayBookingSuccess(Booking booking) {
        adapter.updateItem(booking, true);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventItemClicked(OnEventItemClicked event) {
        if (TheActivityManager.getInstance().getCurrentActivity() instanceof AttendeesActivity && mUserVisibleHint) {

            if (event.getItem() instanceof Booking) {
                booking = (Booking) event.getItem();

                if (event.getAction() == ItemAction.TAKE_ATTENDANCE) {
                    mPresenter.takeAttendanceBooking(booking, mUser.uId);

                } else if (event.getAction() == ItemAction.VIEW_LIST) {
                    if (!ListUtils.isEmpty(booking.ageCategories)) {
                        for (BookingAgeCategory category : booking.ageCategories) {
                            if (category.isConfirmed) {
                                for (BookingPlayer player : category.players) {
                                    player.price = (booking.priceIndividual / booking.size) * (1 + player.invitees);
                                }

                                mDialogList.addAttendeesBookingPlayers(category.players);
                                mDialogList.showAttendeesBookingPlayers();
                                return;
                            }
                        }
                    }
//                else if (event.getAction() == ItemAction.PAY_BOOKING) {
//                    mPresenter.payBooking((Booking) event.getItem(), mUser.uId);
//                }
                }

            } else if (event.getItem() instanceof BookingPlayer) {
                if (event.getAction() == ItemAction.TAKE_ATTENDANCE) {
                    mPresenter.takeAttendanceBooking(booking, (BookingPlayer) event.getItem(), mUser.uId);
//                    mDialogList.dismiss();
                }
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