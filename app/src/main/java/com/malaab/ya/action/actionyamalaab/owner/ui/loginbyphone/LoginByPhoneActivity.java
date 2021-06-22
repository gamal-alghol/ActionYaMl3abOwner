package com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.PhoneAuthProvider;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.custom.EditTextWithEyeToggle;
import com.malaab.ya.action.actionyamalaab.owner.custom.PhoneNumberEdit;
import com.malaab.ya.action.actionyamalaab.owner.custom.PrefixEditText;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.home.HomeActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.login.LoginActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.ActivityUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginByPhoneActivity extends BaseActivity implements LoginByPhoneMvpView {

    @BindView(R.id.img_background)
    public ImageView img_background;

    @BindView(R.id.txt_message)
    public TextView txt_message;

    @BindView(R.id.edt_phone)
    public PhoneNumberEdit edt_phone;
    @BindView(R.id.edt_verificationCode)
    public EditTextWithEyeToggle edt_verificationCode;
    @BindView(R.id.btn_requestVerificationCode)
    public Button btn_requestVerificationCode;
    @BindView(R.id.btn_verify)
    public Button btn_verify;

    @BindView(R.id.edt_fName)
    public EditText edt_fName;
    @BindView(R.id.edt_lName)
    public EditText edt_lName;
    @BindView(R.id.edt_email)
    public EditText edt_email;
    @BindView(R.id.btn_register)
    public Button btn_register;

    @Inject
    LoginByPhoneMvpPresenter<LoginByPhoneMvpView> mPresenter;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mPhoneVerificationId;
    private String mUserUId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_phone);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(this);

        Glide.with(getApplicationContext())
                .load(R.drawable.img_background_new_blurred)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_background);
    }

    @Override
    protected void initUI() {
//        BlurImage.with(getApplicationContext())
//                .intensity(5)
//                .Async(true)
//                .into();

//        Glide.with(getApplicationContext())
//                .load(R.drawable.img_background_new)
//                .asBitmap()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                        if (bitmap != null) {
//                            Blurry.with(getApplicationContext())
//                                    .from(bitmap)
//                                    .into(img_background);
//                        }
//                    }
//
//                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                        super.onLoadFailed(e, errorDrawable);
//                    }
//                });

        edt_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        mValidator
                .addField(edt_phone);
    }


    @OnClick(R.id.btn_requestVerificationCode)
    public void requestVerificationCode() {
        hideKeyboard();
        txt_message.setVisibility(View.GONE);
        if (mValidator.isValid()) {
            if (edt_phone.getText().toString().startsWith("0")) {
                edt_phone.setText(edt_phone.getText().toString().replaceFirst("0", ""));
                Log.d("ttt","startsWith");
            }
            Log.d("ttt","isValid");

            btn_requestVerificationCode.setEnabled(false);
            mPresenter.getVerificationCode(edt_phone.getPrefix(), edt_phone.getText().toString());

//            new CountDownTimer(45000, 1000) {
//                @Override
//                public void onTick(long l) {
//                    timer.setText("0:" + l / 1000 + " s");
//                    resendCode.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onFinish() {
//                    timer.setText(0 + " s");
//                    resendCode.startAnimation(AnimationUtils.loadAnimation(PhoneActivity.this, R.anim.slide_from_right));
//                    resendCode.setVisibility(View.VISIBLE);
//                }
//            }.start();
        }
    }

    @OnClick(R.id.btn_verify)
    public void verifyCode() {
        txt_message.setVisibility(View.GONE);
        btn_verify.setEnabled(false);

        mValidator.addField(edt_verificationCode.getEditText());

        if (mValidator.isValid()) {
            mPresenter.verifyPhoneNumberWithCode(mPhoneVerificationId, edt_verificationCode.getText().trim());
        }
    }

    @OnClick(R.id.btn_register)
    public void registerServer() {
        hideKeyboard();

        txt_message.setVisibility(View.GONE);

        mValidator
                .addField(edt_fName)
                .addField(edt_lName);

        if (mValidator.isValid()) {
            mPresenter.generateUserUniqueId(mUserUId);
        }
    }


    @Override
    public void showMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            txt_message.setText(message);
            txt_message.setVisibility(View.VISIBLE);
        }

        btn_requestVerificationCode.setEnabled(true);
    }

    @Override
    public void onGetVerificationCodeSuccess(String phoneVerificationId, PhoneAuthProvider.ForceResendingToken resendToken) {
        mPhoneVerificationId = phoneVerificationId;
        mResendToken = resendToken;

        btn_requestVerificationCode.setVisibility(View.GONE);
        edt_verificationCode.setVisibility(View.VISIBLE);
        btn_verify.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetVerificationCodeFailed() {
        btn_requestVerificationCode.setEnabled(true);
    }


    @Override
    public void onUserVerifiedSuccess(String userUId) {
        mPresenter.isUserExistInDB(userUId);
    }

    @Override
    public void onUserVerifiedFailed() {
        btn_verify.setEnabled(true);
    }


    @Override
    public void onUserIsActive(User user) {
        ActivityUtils.finishInstance(LoginActivity.class);
        ActivityUtils.goTo(LoginByPhoneActivity.this, HomeActivity.class, true);
    }

    @Override
    public void onUserNotActive(String message) {
        edt_fName.setVisibility(View.GONE);
        edt_lName.setVisibility(View.GONE);
        edt_email.setVisibility(View.GONE);
        edt_phone.setVisibility(View.GONE);

        btn_requestVerificationCode.setVisibility(View.GONE);
        btn_verify.setVisibility(View.GONE);
        btn_register.setVisibility(View.GONE);

        txt_message.setVisibility(View.VISIBLE);
        txt_message.setTextSize(25);
        txt_message.setText(message);
    }

    @Override
    public void onCreateNewUser(String userUId) {
        mUserUId = userUId;

        edt_phone.setEnabled(false);
        edt_verificationCode.setVisibility(View.GONE);
        btn_requestVerificationCode.setVisibility(View.GONE);
        btn_verify.setVisibility(View.GONE);

        edt_fName.setVisibility(View.VISIBLE);
        edt_lName.setVisibility(View.VISIBLE);
        edt_email.setVisibility(View.VISIBLE);
        btn_register.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserUniqueIdGenerated(String userUId, long appUserId) {
        mPresenter.addUserToFirebaseDatabase(userUId, appUserId,
                edt_fName.getText().toString().trim(), edt_lName.getText().toString().trim(),
                edt_email.getText().toString().trim(),
                edt_phone.getPrefix(), edt_phone.getText().toString(),
                "", "");
    }


    @Override
    public void onNoInternetConnection() {
        txt_message.setText(R.string.error_no_connection);
    }


    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

}
