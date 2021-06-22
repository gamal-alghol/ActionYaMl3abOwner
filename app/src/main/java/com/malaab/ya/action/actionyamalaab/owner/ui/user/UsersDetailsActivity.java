package com.malaab.ya.action.actionyamalaab.owner.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Fine;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersDetailsActivity extends BaseActivity implements UsersDetailsMvpView, Serializable{

    @BindView(R.id.header_txt_title)
    TextView header_txt_title;
    @BindView(R.id.header_btn_back)
    AppCompatImageButton header_btn_back;
    @BindView(R.id.header_btn_notifications)
    AppCompatImageButton header_btn_notifications;

    @BindView(R.id.img_profile)
    public CircleImageView img_profile;
    @BindView(R.id.txt_username)
    public TextView txt_username;
    @BindView(R.id.txt_userid)
    public TextView txt_userid;
    @BindView(R.id.txt_age)
    public TextView txt_age;
    @BindView(R.id.ln_address)
    public LinearLayout ln_address;
    @BindView(R.id.txt_address)
    public TextView txt_address;

    @BindView(R.id.txt_fines)
    public TextView txt_fines;

    @BindView(R.id.txt_fines_show)
    public TextView txt_fines_show;


    @BindView(R.id.ln_phone)
    public LinearLayout ln_phone;
    @BindView(R.id.txt_phone)
    public TextView txt_phone;

    @BindView(R.id.ln_email)
    public LinearLayout ln_email;
    @BindView(R.id.txt_email)
    public TextView txt_email;


    ArrayList<Fine> fineList;
    Bundle b;
    @Inject
    UsersDetailsMvpPresenter<UsersDetailsMvpView> mPresenter;
    private String userUid;
    private boolean isLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(this);


        fineList=new ArrayList<>();
        b = new Bundle();
        if (getIntent() != null && getIntent().hasExtra(Constants.INTENT_KEY_USER_UID)) {
            userUid = getIntent().getStringExtra(Constants.INTENT_KEY_USER_UID);
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance()
                .getReference(Constants.FIREBASE_DB_FINES_TABLE).child(userUid);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                Fine fine;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        fine = child.getValue(Fine.class);
                        if (fine != null) {
                            fineList.add(fine);
                        }
                        txt_fines.setText(getString(R.string.has_fine));

                    }
                }
                else {
                    txt_fines_show.setVisibility(View.GONE);
                    txt_fines.setText(getString(R.string.dont_has_fine));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                txt_fines_show.setVisibility(View.GONE);
                txt_fines.setText(getString(R.string.dont_has_fine));

            }
        });

        txt_fines_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putSerializable("list", fineList);
                startActivity(new Intent(getApplicationContext(),FinesActivity.class).putExtras(b));
            }
        });


    }



    @Override
    protected void initUI() {
        header_btn_back.setVisibility(View.VISIBLE);
        header_btn_notifications.setVisibility(View.INVISIBLE); /* Just To fix UI (to center Title) */
        header_txt_title.setText(R.string.title_users);
    }


    @OnClick(R.id.header_btn_back)
    public void goBack() {
        onBackPressed();
    }


    @Override
    public void onGetUserDetails(User user) {
        if (user == null) {
            onError(R.string.error_no_data);
            return;
        }

        Glide.with(this).load(user.profileImageUrl)
                .placeholder(R.drawable.img_profile_player_default)
                .error(R.drawable.img_profile_player_default)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(img_profile);

        txt_username.setText(user.getUserFullName());
        txt_userid.setText(String.valueOf(user.appUserId));
        txt_address.setText(user.address_city);
        txt_age.setText(getString(R.string.age)+": "+user.age);
        txt_phone.setText(user.mobileNo);
        txt_email.setText(user.email);
    }


    @Override
    public void onNoInternetConnection() {
        onError(R.string.error_no_connection);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isLoaded) {
            isLoaded = true;
            mPresenter.getUserDetails(userUid);
        }
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
        mPresenter.onDetach();
        super.onDestroy();
    }
}
