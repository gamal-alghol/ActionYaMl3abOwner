package com.malaab.ya.action.actionyamalaab.owner.ui.playground;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventRefresh;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit.PlaygroundAddActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.ActivityUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaygroundsActivity extends BaseActivity implements PlaygroundsMvpView {

    @BindView(R.id.pBar_loading)
    public ProgressBar pBar_loading;

    @BindView(R.id.rv_individual)
    public RecyclerView rv_individual;

    @Inject
    PlaygroundsMvpPresenter<PlaygroundsMvpView> mPresenter;

    private boolean isLoaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playgrounds);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
               mPresenter.onAttach(this);
    }

    @Override
    protected void initUI() {
        rv_individual.setHasFixedSize(true);
        rv_individual.setLayoutManager(new LinearLayoutManager(this));
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
    public void onGetPlayground(List<Playground> playgrounds) {
//        mPresenter.listenToFutsalCourtsUpdates(); because it will get the same data again

        PlaygroundsAdapter adapter = new PlaygroundsAdapter(playgrounds);
        adapter.registerListener(new PlaygroundsAdapter.OnPlaygroundClicked() {
            @Override
            public void setOnPlaygroundClicked(Playground playground, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.INTENT_KEY_PLAYGROUND, playground);
                ActivityUtils.goTo(PlaygroundsActivity.this, PlaygroundAddActivity.class, false, bundle);
                finish();
            }
        });

        rv_individual.setAdapter(adapter);

        rv_individual.setVisibility(View.VISIBLE);
    }


    @Override
    public void onNoInternetConnection() {
        onError(R.string.error_no_connection);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventRefresh(OnEventRefresh event) {
        mPresenter.getFutsalCourts();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        if (!isLoaded) {
            isLoaded = true;
            mPresenter.getFutsalCourts();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mPresenter.onDetach();
        super.onDestroy();
    }
}
