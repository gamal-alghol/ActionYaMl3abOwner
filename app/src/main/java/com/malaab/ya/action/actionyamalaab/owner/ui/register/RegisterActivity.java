package com.malaab.ya.action.actionyamalaab.owner.ui.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.MultiTextWatcher;
import com.malaab.ya.action.actionyamalaab.owner.utils.PasswordStrength;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterActivity extends BaseActivity implements RegisterMvpView, MultiTextWatcher.OnTextWatcher {

    @BindView(R.id.img_background)
    public ImageView img_background;

    @BindView(R.id.txt_hint)
    public TextView txt_hint;

    @BindView(R.id.edt_fName)
    public EditText edt_fName;
    @BindView(R.id.edt_lName)
    public EditText edt_lName;
    @BindView(R.id.edt_email)
    public EditText edt_email;
    @BindView(R.id.edt_phone)
    public EditText edt_phone;
    @BindView(R.id.edt_password)
    public EditText edt_password;

    @BindView(R.id.ln_strength)
    public LinearLayout ln_strength;
    @BindView(R.id.pBar_strength)
    public ProgressBar pBar_strength;
    @BindView(R.id.txt_strength)
    public TextView txt_strength;

    @BindView(R.id.txt_message)
    public TextView txt_message;

    @BindView(R.id.btn_register)
    public Button btn_register;

    @Inject
    RegisterMvpPresenter<RegisterMvpView> mPresenter;

    private MultiTextWatcher mMultiTextWatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(this);

        Glide.with(getApplicationContext())
                .load(R.drawable.img_background_new_blurred)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(img_background);
    }

    @Override
    protected void initUI() {
//        BlurImage.with(getApplicationContext())
//                .load(R.drawable.img_background_new)
//                .intensity(5)
//                .Async(false)
//                .into(img_background);

//        Glide.with(this)
//                .load(R.drawable.img_background_new)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .dontAnimate()
//                .into(img_background);

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
                .addField(edt_fName)
                .addField(edt_lName)
                .addField(edt_email)
                .addField(edt_phone)
                .addField(edt_password);

        mMultiTextWatcher = new MultiTextWatcher()
                .setCallback(this)
                .registerEditText(edt_password);
    }


    @OnClick(R.id.btn_register)
    public void registerServer() {
        hideKeyboard();

        txt_hint.setVisibility(View.GONE);
        txt_message.setVisibility(View.GONE);

        if (mValidator.isValid()) {
            mPresenter.doServerRegister(edt_fName.getText().toString().trim(), edt_lName.getText().toString().trim(),
                    edt_email.getText().toString().trim(), edt_phone.getText().toString().trim(),
                    edt_password.getText().toString().trim(), "");
        }
    }


    @Override
    public void beforeTextChanged(EditText editText, CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(EditText editText, CharSequence s, int start, int before, int count) {
        if (editText.getId() == R.id.mEditText) {
            mPresenter.getPasswordStrength(s.toString());
        }
    }

    @Override
    public void afterTextChanged(EditText editText, Editable editable) {

    }

    @Override
    public void onEditorAction(EditText editText, int action, Editable editable) {

    }


    @Override
    public void onShowPasswordWeak(PasswordStrength str) {
        if (ln_strength.getVisibility() == View.GONE) {
            ln_strength.setVisibility(View.VISIBLE);
        }

        txt_strength.setText(str.getText(this));
        txt_strength.setTextColor(str.getColor());

        pBar_strength.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        pBar_strength.setProgress(25);
    }

    @Override
    public void onShowPasswordMedium(PasswordStrength str) {
        if (ln_strength.getVisibility() == View.GONE) {
            ln_strength.setVisibility(View.VISIBLE);
        }

        txt_strength.setText(str.getText(this));
        txt_strength.setTextColor(str.getColor());

        pBar_strength.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        pBar_strength.setProgress(50);
    }

    @Override
    public void onShowPasswordStrong(PasswordStrength str) {
        if (ln_strength.getVisibility() == View.GONE) {
            ln_strength.setVisibility(View.VISIBLE);
        }

        txt_strength.setText(str.getText(this));
        txt_strength.setTextColor(str.getColor());

        pBar_strength.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        pBar_strength.setProgress(75);
    }

    @Override
    public void onShowPasswordVeryStrong(PasswordStrength str) {
        if (ln_strength.getVisibility() == View.GONE) {
            ln_strength.setVisibility(View.VISIBLE);
        }

        txt_strength.setText(str.getText(this));
        txt_strength.setTextColor(str.getColor());

        pBar_strength.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        pBar_strength.setProgress(100);
    }

    @Override
    public void onHidePasswordStrength() {
        ln_strength.setVisibility(View.GONE);
        txt_strength.setText("");
        pBar_strength.setProgress(0);
    }

    @Override
    public void onServerRegisteredSuccess(User user) {
        mPresenter.generateUserUniqueId(user);
    }

    @Override
    public void onUserUniqueIdGenerated(User user) {
        mPresenter.addUserToFirebaseDatabase(user);
    }

    @Override
    public void onAddedToFirebaseDatabase() {
        txt_message.setVisibility(View.GONE);

        edt_fName.setVisibility(View.GONE);
        edt_lName.setVisibility(View.GONE);
        edt_email.setVisibility(View.GONE);
        edt_phone.setVisibility(View.GONE);
        edt_password.setVisibility(View.GONE);
        btn_register.setVisibility(View.GONE);

        txt_hint.setVisibility(View.VISIBLE);
        txt_hint.setTextSize(25);
        txt_hint.setText(getString(R.string.msg_owner_create_success));
    }


    @Override
    public void onError(String message) {
        txt_message.setVisibility(View.VISIBLE);
        txt_message.setText(message);

        btn_register.setEnabled(true);
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
        mMultiTextWatcher.setCallback(null);
        mPresenter.onDetach();
        super.onDestroy();
    }

}
