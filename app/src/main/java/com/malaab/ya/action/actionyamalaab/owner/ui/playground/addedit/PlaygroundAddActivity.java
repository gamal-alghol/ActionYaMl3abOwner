package com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.annotations.PermissionsCodes;
import com.malaab.ya.action.actionyamalaab.owner.custom.BSheetMap;
import com.malaab.ya.action.actionyamalaab.owner.custom.DialogConfirmation;
import com.malaab.ya.action.actionyamalaab.owner.custom.DialogList;
import com.malaab.ya.action.actionyamalaab.owner.custom.DialogSchedule;
import com.malaab.ya.action.actionyamalaab.owner.custom.OnBottomSheetListener;
import com.malaab.ya.action.actionyamalaab.owner.custom.OnDialogScheduleListener;
import com.malaab.ya.action.actionyamalaab.owner.data.model.DirectionListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.model.PlaygroundImage;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.City;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedule;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedules;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSearch;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Region;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventRefresh;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.PlaygroundsActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.ListUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.PermissionsUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.StringUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.ViewUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yayandroid.theactivitymanager.TheActivityManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class PlaygroundAddActivity extends BaseActivity implements PlaygroundAddMvpView, OnBottomSheetListener, OnDialogScheduleListener {

    @BindView(R.id.header_txt_title)
    TextView header_txt_title;
    @BindView(R.id.header_btn_back)
    AppCompatImageButton header_btn_back;
    @BindView(R.id.header_btn_edit)
    AppCompatImageButton header_btn_edit;

    @BindView(R.id.sc_view)
    public ScrollView sc_view;

    @BindView(R.id.edt_playgroundName)
    public EditText edt_playgroundName;


    @BindView(R.id.edt_playgroundName_owner)
    public EditText edt_playgroundName_owner;
    @BindView(R.id.edt_playgroundMobile_owner)
    public EditText edt_playgroundMobile_owner;
    @BindView(R.id.edt_playgroundName_guard)
    public EditText edt_playgroundName_guard;
    @BindView(R.id.edt_playgroundMobile_guard)
    public EditText edt_playgroundMobile_guard;


    @BindView(R.id.sw_note)
    public Switch sw_note;
    @BindView(R.id.edt_playgroundNote)
    public EditText edt_playgroundNote;

    @BindView(R.id.txt_region)
    TextView txt_region;
    @BindView(R.id.txt_city)
    TextView txt_city;
    @BindView(R.id.txt_direction)
    TextView txt_direction;

    @BindView(R.id.ln_images)
    public LinearLayout ln_images;
    @BindView(R.id.txt_images)
    TextView txt_images;
    @BindView(R.id.rv_images)
    RecyclerView rv_images;

    @BindView(R.id.ln_location)
    public LinearLayout ln_location;

    @BindView(R.id.chk_amenitiesBall)
    public CheckBox chk_amenitiesBall;
    @BindView(R.id.chk_amenitiesWater)
    public CheckBox chk_amenitiesWater;
    @BindView(R.id.chk_amenitiesGrass)
    public CheckBox chk_amenitiesGrass;
    @BindView(R.id.chk_amenitiesWC)
    public CheckBox chk_amenitiesWC;
    @BindView(R.id.chk_amenitiesShower)
    public CheckBox chk_amenitiesShower;

    @BindView(R.id.pBar_schedules)
    public ProgressBar pBar_schedules;
    @BindView(R.id.rv_schedule)
    public RecyclerView rv_schedule;
    @BindView(R.id.btn_addSchedule)
    public AppCompatImageButton btn_addSchedule;

    @BindView(R.id.btn_save)
    public Button btn_save;


private boolean isHide=false;
    public Button btn_delet;
    @Inject
    PlaygroundAddMvpPresenter<PlaygroundAddMvpView> mPresenter;

    @Inject
    DialogList mDialogList;
    @Inject
    DialogConfirmation mDialogConfirmation;
private RelativeLayout rl_cancal_playground;
    private DialogSchedule dialogSchedule;

    private LinearLayoutManager layoutManager;
private  Switch sw_cancal_playground;
    private BSheetMap mBSheetMap;
//    private BSheetPlaygroundSchedule mBSheetPlaygroundSchedule;

    private Playground mPlayground;
//    private PlaygroundSchedules schedules;

    private String region, city, direction;
    private UploadListAdapter uploadListAdapter;
    private PlaygroundSchedulesAdapter schedulesAdapter;

    private LocationGooglePlayServicesProvider provider;
    private static final LocationAccuracy ACCURACY_HIGH = LocationAccuracy.HIGH;    //HIGH
    private static final long INTERVAL_HIGH = 1000;                               //1s
    private static final float DISTANCE_HIGH = 1f;

    private double lat, lng;
    private boolean isRefreshLocation;
    private boolean isLoaded;
    private boolean isSaving;
    //    private boolean isAddingMore, isActionTaken;
    private boolean isEditMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground_add);
btn_delet=findViewById(R.id.btn_delet);
sw_cancal_playground=findViewById(R.id.sw_cancal_playground);
        rl_cancal_playground=findViewById(R.id.rl_cancal_playground);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(this);


        sw_cancal_playground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){

    isHide=b;
                }else{
    isHide=false;
                }
            }
        });

    btn_delet.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialogConfirmation
                    .withTitle("هل أنتا متأكد من حذف الملعب ؟")
                    .withMessage("سيتم حذف الملعب والتمارين المرتبطة فيه.")
                    .withPositiveButton(getString(R.string.yes))
                    .withNegativeButton(getString(R.string.no))
                    .setOnDialogConfirmationListener(new DialogConfirmation.OnDialogConfirmationListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            showLoading();
                            DeletPlayGround();
                        }

                        @Override
                        public void onNegativeButtonClick() {
                        }
                    })
                    .build()
                    .show();

        }
    });






        if (getIntent() != null && getIntent().hasExtra(Constants.INTENT_KEY_PLAYGROUND)) {
            mPlayground = getIntent().getParcelableExtra(Constants.INTENT_KEY_PLAYGROUND);
            isEditMode = true;
        }
    }

    private void DeletPlayGround() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE)
                .child(mPlayground.playgroundId);
        mDatabase.removeValue();
        DatabaseReference mDatabaseINDIVIDUALS = FirebaseDatabase.getInstance()
                .getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SCHEDULES_BOOKING_INDIVIDUALS_TABLE);
        mDatabaseINDIVIDUALS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Booking booking=dataSnapshot1.getValue(Booking.class);
                     if(booking.playgroundId.equalsIgnoreCase(mPlayground.playgroundId)){
                     DatabaseReference mDatabase = FirebaseDatabase.getInstance()
                     .getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SCHEDULES_BOOKING_INDIVIDUALS_TABLE)
                     .child(booking.bookingUId);
                         mDatabase.removeValue();

                     }
                     }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DeletPlayGroundFromSearch();


    }
private void DeletPlayGroundFromSearch(){
    Log.d("ddd","mPlayground.playgroundId : "+mPlayground.playgroundId);

    DatabaseReference mDatabaseINDIVIDUALS = FirebaseDatabase.getInstance()
            .getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SEARCH_TABLE);
    mDatabaseINDIVIDUALS.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                PlaygroundSearch  playgroundSearch=dataSnapshot1.getValue(PlaygroundSearch.class);
                Log.d("ddd","id : "+playgroundSearch.playgroundId);
                if(playgroundSearch.playgroundId.equalsIgnoreCase(mPlayground.playgroundId)){
                   DatabaseReference mDatabase = FirebaseDatabase.getInstance()
                   .getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SEARCH_TABLE)
                   .child(dataSnapshot1.getKey());
                   mDatabase.removeValue();
                }
            }
            hideLoading();
            onBackPressed();
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    });

}

    @Override
    protected void initUI() {
        header_btn_back.setVisibility(View.VISIBLE);
        header_txt_title.setText(R.string.title_playground_add);
        header_btn_edit.setVisibility(View.INVISIBLE);

        mValidator.addField(edt_playgroundName);
        mValidator.addField(edt_playgroundName_guard);
        mValidator.addField(edt_playgroundName_owner);
        mValidator.addField(edt_playgroundMobile_guard);
        mValidator.addField(edt_playgroundMobile_owner);

        uploadListAdapter = new UploadListAdapter(new ArrayList<PlaygroundImage>());

        rv_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_images.setHasFixedSize(true);
        rv_images.setAdapter(uploadListAdapter);

        schedulesAdapter = new PlaygroundSchedulesAdapter(new ArrayList<PlaygroundSchedule>());
        schedulesAdapter.setOnItemClickedListener(new PlaygroundSchedulesAdapter.OnPlaygroundScheduleClicked() {
            @Override
            public void setOnPlaygroundScheduleClicked(PlaygroundSchedule schedule, int position) {
                dialogSchedule.addSchedule(schedule);
                dialogSchedule.show();
            }

            @Override
            public void setOnPlaygroundScheduleLongClicked(final PlaygroundSchedule schedule, int position) {
                mDialogConfirmation
                        .withTitle(String.format(getString(R.string.dialog_confirmation_delete), DateTimeUtils.getArabicDayName(schedule.day)))
                        .withMessage(String.format(getString(R.string.dialog_confirmation_delete_message), DateTimeUtils.getArabicDayName(schedule.day)))
                        .withPositiveButton(getString(R.string.yes))
                        .withNegativeButton(getString(R.string.no))
                        .setOnDialogConfirmationListener(new DialogConfirmation.OnDialogConfirmationListener() {
                            @Override
                            public void onPositiveButtonClick() {
                                schedulesAdapter.removeItem(schedule, true);
                            }

                            @Override
                            public void onNegativeButtonClick() {
                            }
                        })
                        .build()
                        .show();
            }

            @Override
            public void setOnPlaygroundScheduleEnable(PlaygroundSchedule schedule, int position) {
                Log.d("fff","addPlaygroundSchedules:"+mPlayground.name);
                mPresenter.addPlaygroundSchedules(mPlayground, schedulesAdapter.getItems());
            }
        });

        layoutManager = new LinearLayoutManager(this);
        rv_schedule.setLayoutManager(layoutManager);
        rv_schedule.setHasFixedSize(true);
        rv_schedule.setAdapter(schedulesAdapter);

        mDialogList.build();

        mBSheetMap = new BSheetMap();
        mBSheetMap.attachAndInit(this);
        mBSheetMap.setOnBottomSheetListener(this);

//        mBSheetPlaygroundSchedule = new BSheetPlaygroundSchedule();
//        mBSheetPlaygroundSchedule.attachAndInit(this);
//        mBSheetPlaygroundSchedule.setOnBottomSheetListener(this);

        dialogSchedule = new DialogSchedule();
        dialogSchedule.with(this)
                .withTitle("اضافة يوم عمل")
                .withListener(this)
                .build();

        if (isEditMode && mPlayground != null) {
            btn_delet.setVisibility(View.VISIBLE);
            edt_playgroundName.setText(mPlayground.name);
            edt_playgroundName_owner.setText(mPlayground.nameOwner);
            edt_playgroundMobile_owner.setText(mPlayground.mobileOwner);
            edt_playgroundName_guard.setText(mPlayground.nameguard);
            edt_playgroundMobile_guard.setText(mPlayground.mobileguard);
            sw_note.setChecked(mPlayground.isPromotion);
            sw_cancal_playground.setChecked(mPlayground.isHide);
            edt_playgroundNote.setText(mPlayground.note);
            region = mPlayground.address_region;
            city = mPlayground.address_city;
            direction = mPlayground.address_direction;

            txt_region.setText(mPlayground.address_region);
            txt_city.setText(mPlayground.address_city);
            txt_direction.setText(mPlayground.address_direction);

            if (mPlayground.latitude != 0 && mPlayground.longitude != 0) {
                if (ListUtils.isEmpty(mPlayground.images)) {
                    mBSheetMap.addMarker(mPlayground.name, "", mPlayground.latitude, mPlayground.longitude, "");
                } else {
                    mBSheetMap.addMarker(mPlayground.name, "", mPlayground.latitude, mPlayground.longitude, mPlayground.images.get(0));
                }
            }

            if (!ListUtils.isEmpty(mPlayground.images)) {
                for (String url : mPlayground.images) {
                    uploadListAdapter.addItem(new PlaygroundImage(url, url, true), false);
                }
            }

            if (mPlayground.amenity != null) {
                chk_amenitiesShower.setChecked(mPlayground.amenity.hasShower);
                chk_amenitiesWC.setChecked(mPlayground.amenity.hasWC);
                chk_amenitiesGrass.setChecked(mPlayground.amenity.hasGrass);
                chk_amenitiesWater.setChecked(mPlayground.amenity.hasWater);
                chk_amenitiesBall.setChecked(mPlayground.amenity.hasBall);
            }

            mPresenter.getPlaygroundSchedules(mPlayground.playgroundId);

            ViewUtils.scrollToBottom(sc_view);
        }
    }


    @OnCheckedChanged(R.id.sw_note)
    public void onRadioButtonClicked(CompoundButton aSwitch, boolean checked) {
        switch (aSwitch.getId()) {
            case R.id.sw_note:
                if (checked) {
                    edt_playgroundNote.setVisibility(View.VISIBLE);
                } else {
                    edt_playgroundNote.setText("");
                    edt_playgroundNote.setVisibility(View.GONE);
                }
                break;
        }
    }


    @OnClick(R.id.header_btn_back)
    public void goBack() {
        onBackPressed();
    }


    @OnClick(R.id.ln_region)
    public void selectRegion() {
        mDialogList
                .withTitle(getString(R.string.title_region))
                .showRegions();
    }

    @OnClick(R.id.ln_city)
    public void selectCity() {
        mDialogList
                .withTitle(getString(R.string.city))
                .showCities();
    }

    @OnClick(R.id.ln_direction)
    public void selectDirection() {
        mDialogList
                .withTitle(getString(R.string.direction))
                .showDirections();
    }


    @OnClick(R.id.ln_images)
    public void selectImages() {
        PermissionsUtils.requestPermission(PlaygroundAddActivity.this, PermissionsCodes.WRITE_EXTERNAL_STORAGE, Permission.STORAGE);
    }

    @OnClick(R.id.ln_location)
    public void selectLocation() {
        PermissionsUtils.requestPermission(PlaygroundAddActivity.this, PermissionsCodes.LOCATION, Permission.LOCATION);
    }

    @OnClick(R.id.btn_addSchedule)
    public void addSchedule() {
        dialogSchedule.show();
    }


    @OnClick(R.id.btn_save)
    public void save() {
        if (!mValidator.isValid()) {
            return;
        }

        if (StringUtils.isEmpty(region)) {
            onError(R.string.error_no_region_selected);
            return;
        }

        if (StringUtils.isEmpty(city)) {
            onError(R.string.error_no_city_selected);
            return;
        }

        if (StringUtils.isEmpty(direction)) {
            onError(R.string.error_no_direction_selected);
            return;
        }

        if (mBSheetMap.getSelectedLocation() == null || mBSheetMap.getSelectedLocation().latitude == 0 || mBSheetMap.getSelectedLocation().longitude == 0) {
            onError(R.string.error_no_location_selected);
            return;
        }

        if (schedulesAdapter.getItemCount() == 0) {
            onError(R.string.error_no_schedule_selected);
            return;
        }

//        mBSheetPlaygroundSchedule.show();

        if (isEditMode) {
            isSaving = true;

            mPresenter.updatePlayground(mPlayground.playgroundId, edt_playgroundName.getText().toString().trim(),
                    edt_playgroundName_owner.getText().toString().trim(),
                    edt_playgroundMobile_owner.getText().toString().trim(),
                    edt_playgroundName_guard.getText().toString().trim(),
                    edt_playgroundMobile_guard.getText().toString().trim(),
                    sw_note.isChecked(), edt_playgroundNote.getText().toString().trim(),
                    region, city, direction, mBSheetMap.getSelectedLocation(),
                    uploadListAdapter.getItems(),
                    chk_amenitiesShower.isChecked(), chk_amenitiesWC.isChecked(), chk_amenitiesGrass.isChecked(), chk_amenitiesWater.isChecked(), chk_amenitiesBall.isChecked(),
                    mPlayground.isActive,isHide);
            Log.d("ttt",isHide+"2");

        } else {
            Log.d("fff","else:"+edt_playgroundMobile_owner.getText().toString().trim());

            isSaving = true;

            mPresenter.addPlayground(edt_playgroundName.getText().toString().trim(),
                    edt_playgroundName_owner.getText().toString().trim(),
                    edt_playgroundMobile_owner.getText().toString().trim(),
                    edt_playgroundName_guard.getText().toString().trim(),
                    edt_playgroundMobile_guard.getText().toString().trim(),
                    sw_note.isChecked(), edt_playgroundNote.getText().toString().trim(),
                    region, city, direction, mBSheetMap.getSelectedLocation(),
                    uploadListAdapter.getItems(),
                    chk_amenitiesShower.isChecked(), chk_amenitiesWC.isChecked(), chk_amenitiesGrass.isChecked(), chk_amenitiesWater.isChecked()
                    , chk_amenitiesBall.isChecked(),isHide);
        }
    }


    @PermissionYes(PermissionsCodes.LOCATION)
    private void gotGPSPermission(@NonNull List<String> grantedPermissions) {
        mBSheetMap.show();
    }

    @PermissionNo(PermissionsCodes.LOCATION)
    private void noGPSPermission(@NonNull List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            PermissionsUtils.showAlwaysDeniedCustomDialog(this);
        }
    }


    @PermissionYes(PermissionsCodes.WRITE_EXTERNAL_STORAGE)
    private void gotStoragePermission(@NonNull List<String> grantedPermissions) {
        EasyImage.openChooserWithGallery(this, "اختر الصور", 0);
    }

    @PermissionNo(PermissionsCodes.WRITE_EXTERNAL_STORAGE)
    private void noStoragePermission(@NonNull List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            PermissionsUtils.showAlwaysDeniedCustomDialog(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                showMessage(e.getMessage());
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                for (File file : imageFiles) {
                    uploadListAdapter.addItem(new PlaygroundImage(file.getPath(), "", false), true);
                }

                mPresenter.uploadPlaygroundImages(uploadListAdapter.getItems());
            }

            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                // Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(PlaygroundAddActivity.this);
                    if (photoFile != null)
                        photoFile.delete();
                }
            }
        });
    }


    @Override
    public void onScheduleDayCreated(PlaygroundSchedule playgroundSchedule) {
        ViewUtils.scrollToBottom(sc_view);

        schedulesAdapter.addItem(playgroundSchedule, true);
        layoutManager.scrollToPosition(schedulesAdapter.getItemPosition(playgroundSchedule));
    }


    @Override
    public void onPreClose() {

    }

    @Override
    public void onSlide(float slideOffset) {

    }

    @Override
    public void onBottomSheetExpanded(@IdRes int bsheetId) {
        if (bsheetId == R.id.bSheet_map) {

            if (!SmartLocation.with(this).location().state().locationServicesEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.msg_gps_enable))
                        .setCancelable(false)
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                isRefreshLocation = true;
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("تجاهل", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                return;
            }

            LocationParams locationParams = new LocationParams.Builder()
                    .setAccuracy(ACCURACY_HIGH)
                    .setInterval(INTERVAL_HIGH)
                    .setDistance(DISTANCE_HIGH)
                    .build();

            SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();
            smartLocation.location(provider)
                    .config(locationParams)
//                    .config(LocationParams.NAVIGATION)
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(android.location.Location location) {
                            isRefreshLocation = false;

                            if (location == null) {
                                showMessage(R.string.error_gps);
                                return;
                            }

                            lat = location.getLatitude();
                            lng = location.getLongitude();

                            if (lat != 0 && lng != 0) {
                                mBSheetMap.addMarker("موقعك الحالي", "", lat, lng, "");

                            } else {
                                showMessage(R.string.error_gps);
                            }
                        }
                    });
        }
    }

    @Override
    public void onBottomSheetCollapsed(@IdRes int bsheetId, Object... args) {
//        if (bsheetId == R.id.bSheet_playground_schedule) {
//            if (args != null) {
//                schedules = (PlaygroundSchedules) args[0];
//                isAddingMore = (Boolean) args[1];
//                isActionTaken = (Boolean) args[2];
//
//                if (isEditMode) {
//                    isSaving = true;
//                    mPresenter.updatePlayground(mPlayground.playgroundId, edt_playgroundName.getText().toString().trim(), region, city, direction, mBSheetMap.getSelectedLocation(),
//                            uploadListAdapter.getItems(),
//                            chk_amenitiesShower.isChecked(), chk_amenitiesWC.isChecked(), chk_amenitiesGrass.isChecked(), chk_amenitiesWater.isChecked(), chk_amenitiesBall.isChecked());
//
//                } else {
//                    isSaving = true;
//                    mPresenter.addPlayground(edt_playgroundName.getText().toString().trim(), region, city, direction, mBSheetMap.getSelectedLocation(),
//                            uploadListAdapter.getItems(),
//                            chk_amenitiesShower.isChecked(), chk_amenitiesWC.isChecked(), chk_amenitiesGrass.isChecked(), chk_amenitiesWater.isChecked(), chk_amenitiesBall.isChecked());
//                }
//            }
//        }
    }


    @Override
    public void showLoading() {
        super.showLoading();

        pBar_schedules.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        Log.d("ttt","hside");

        pBar_schedules.setVisibility(View.GONE);
    }


    @Override
    public void onGetCurrentUser(User user) {
    }

    @Override
    public void onGetRegionsSuccess(List<Region> regions) {
        mDialogList.addRegions(regions);

        List<DirectionListItem> list = new ArrayList<>();
        list.add(new DirectionListItem("جنوب", "جنوب"));
        list.add(new DirectionListItem("غرب", "غرب"));
        list.add(new DirectionListItem("شمال", "شمال"));
        list.add(new DirectionListItem("شرق", "شرق"));
        mDialogList.addDirections(list);

//        if (isEditMode && mPlayground != null) {
//            if (!StringUtils.isEmpty(mPlayground.address_region)) {
//                mDialogList.setRegionSelected(mPlayground.address_region);
//            }
//        }
    }

    @Override
    public void onGetRegionsFailed() {
    }


    @Override
    public void onGetPlaygroundSchedules(PlaygroundSchedules playgroundSchedules) {
        schedulesAdapter.setItems(playgroundSchedules.playgroundSchedules);
//        mBSheetPlaygroundSchedule.setPlaygroundSchedules(playgroundSchedules.playgroundSchedules);
    }


    @Override
    public void onPlaygroundImageUploaded(PlaygroundImage playgroundImage) {
        uploadListAdapter.updateItem(playgroundImage, false);

        if (isSaving && uploadListAdapter.isPhotosUploaded()) {
            isSaving = false;

            if (isEditMode) {
//                mPresenter.addPlaygroundSchedules(mPlayground, schedules.playgroundSchedules);

                mPresenter.updatePlayground(mPlayground.playgroundId, edt_playgroundName.getText().toString().trim(),
                        edt_playgroundName_owner.getText().toString().trim(),
                        edt_playgroundMobile_owner.getText().toString().trim(),
                        edt_playgroundName_guard.getText().toString().trim(),
                        edt_playgroundMobile_guard.getText().toString().trim(),
                        sw_note.isChecked(), edt_playgroundNote.getText().toString().trim(),
                        region, city, direction, mBSheetMap.getSelectedLocation(),
                        uploadListAdapter.getItems(),
                        chk_amenitiesShower.isChecked(), chk_amenitiesWC.isChecked(), chk_amenitiesGrass.isChecked(), chk_amenitiesWater.isChecked(), chk_amenitiesBall.isChecked(),
                        mPlayground.isActive,isHide);



            } else {
                mPresenter.addPlayground(edt_playgroundName.getText().toString().trim(),
                        edt_playgroundName_owner.getText().toString().trim(),
                        edt_playgroundMobile_owner.getText().toString().trim(),
                        edt_playgroundName_guard.getText().toString().trim(),
                        edt_playgroundMobile_guard.getText().toString().trim(),
                        sw_note.isChecked(), edt_playgroundNote.getText().toString().trim(),
                        region, city, direction, mBSheetMap.getSelectedLocation(),
                        uploadListAdapter.getItems(),
                        chk_amenitiesShower.isChecked(), chk_amenitiesWC.isChecked()
                        , chk_amenitiesGrass.isChecked(), chk_amenitiesWater.isChecked()
                        , chk_amenitiesBall.isChecked(),isHide);
            }
        }
    }

    @Override
    public void onPlaygroundImageDeleted(int position) {
        uploadListAdapter.removeItem(position);
        EventBus.getDefault().post(new OnEventRefresh());
    }

    @Override
    public void onPlaygroundAdded(Playground playground) {
        isEditMode = true;
        mPlayground = playground;
        mPresenter.addPlaygroundSchedules(mPlayground, schedulesAdapter.getItems());

//        if (isContinue) {
//            isEditMode = true;
//            mPresenter.addPlaygroundSchedules(mPlayground, schedules.playgroundSchedules);
//        } else {
//            showMessage(R.string.msg_success);
//            onBackPressed();
//        }

        EventBus.getDefault().post(new OnEventRefresh());
    }

    @Override
    public void onPlaygroundUpdated(Playground playground) {
        mPlayground = playground;
        mPresenter.addPlaygroundSchedules(mPlayground, schedulesAdapter.getItems());

        EventBus.getDefault().post(new OnEventRefresh());
    }

    @Override
    public void onPlaygroundSchedulesAdded(Playground playground, List<PlaygroundSchedule> playgroundSchedules) {
        if (playgroundSchedules.size() > 0) {
            mPresenter.addPlaygroundSearch(playground, playgroundSchedules.get(0));
        }
    }

    @Override
    public void onPlaygroundSearchAdded() {
        showMessage(R.string.msg_success);

//        if (isAddingMore) {
//        mBSheetPlaygroundSchedule.reset();
//        mBSheetPlaygroundSchedule.setPlaygroundSchedules(schedules.playgroundSchedules);
//        mBSheetPlaygroundSchedule.show();

//        } else {
//            onBackPressed();
//        }
    }


    @Override
    public void onNoInternetConnection() {
        onError(R.string.error_no_connection);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventItemClicked(OnEventItemClicked event) {
        if (TheActivityManager.getInstance().getCurrentActivity() instanceof PlaygroundAddActivity) {

            if (event.getItem() instanceof Region && event.getAction() == ItemAction.PICK) {
                mDialogList.addCities(((Region) event.getItem()).cities);

                region = ((Region) event.getItem()).uid;
                txt_region.setText(region);

                city = "";
                txt_city.setText(getString(R.string.city));

                direction = "";
                txt_direction.setText(getString(R.string.direction));

            } else if (event.getItem() instanceof City && event.getAction() == ItemAction.PICK) {
                city = ((City) event.getItem()).name;
                txt_city.setText(city);

                direction = "";
                txt_direction.setText(getString(R.string.direction));

            } else if (event.getItem() instanceof DirectionListItem && event.getAction() == ItemAction.PICK) {
                direction = ((DirectionListItem) event.getItem()).name;
                txt_direction.setText(direction);

            } else if (event.getItem() instanceof PlaygroundImage && event.getAction() == ItemAction.REMOVE) {
                mPresenter.deletePlaygroundImages(mPlayground, ((PlaygroundImage) event.getItem()).onlinePath, event.getPosition());
            }

            mDialogList.dismiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        if (!isLoaded) {
            isLoaded = true;
            mPresenter.getCurrentUser();
            mPresenter.getRegions();
        }

        if (isRefreshLocation) {
            onBottomSheetExpanded(R.id.bSheet_map);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
//        if (!mBSheetMap.onBackPressed() && !mBSheetPlaygroundSchedule.onBackPressed()) {
        if (!mBSheetMap.onBackPressed()) {

            super.onBackPressed();
            startActivity(new Intent(getApplicationContext(), PlaygroundsActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        mDialogList.onDestroy();
        mBSheetMap.onDetach();
//        mBSheetPlaygroundSchedule.onDetach();
        mPresenter.onDetach();

        super.onDestroy();
    }
}
