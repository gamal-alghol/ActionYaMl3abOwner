package com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.NotificationType;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Location;
import com.malaab.ya.action.actionyamalaab.owner.data.model.PlaygroundImage;
import com.malaab.ya.action.actionyamalaab.owner.data.model.SizeListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Amenity;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.City;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundOwner;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedule;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedules;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSearch;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Region;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BasePresenter;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.FileUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.FirebaseUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.ListUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.StringUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.analytics.IAnalyticsTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.firebase.IFirebaseTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.SchedulerProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class PlaygroundAddPresenter<V extends PlaygroundAddMvpView>
        extends BasePresenter<V>
        implements PlaygroundAddMvpPresenter<V> {

    @Inject
    public AppCompatActivity mActivity;

    private DatabaseReference mDatabasePlaygrounds;
    private ValueEventListener mValueEventListener;
    private StorageReference mStorage;


    @Inject
    public PlaygroundAddPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable, IAnalyticsTracking iAnalyticsTracking, IFirebaseTracking iFirebaseTracking) {
        super(dataManager, schedulerProvider, compositeDisposable, iAnalyticsTracking, iFirebaseTracking);

        iAnalyticsTracking.LogEventScreen("Android - Owner - Add Playground Screen");
        iFirebaseTracking.LogEventScreen("Android - Owner - Add Playground Screen");

        mDatabasePlaygrounds = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_REGIONS_TABLE);
        mStorage = FirebaseStorage.getInstance().getReference();
    }


    @Override
    public void getCurrentUser() {
        if (!isViewAttached()) {
            return;
        }

        getMvpView().onGetCurrentUser(getDataManager().getCurrentUser());
    }


    @Override
    public void getRegions() {
        /* To load the list once only*/
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()) {
                    Region region;
                    City city;
                    List<Region> regions = new ArrayList<>();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        region = new Region();
                        region.uid = child.getKey();
                        region.cities = new ArrayList<>();

                        for (DataSnapshot snap : child.getChildren()) {
                            city = snap.getValue(City.class);
                            region.cities.add(city);
                        }

                        regions.add(region);
                    }

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().onGetRegionsSuccess(regions);

                } else {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().onError(R.string.error);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                AppLogger.e(" Error -> " + error.toException());

                if (!isViewAttached()) {
                    return;
                }

                getMvpView().onError(R.string.error);
            }
        };

        mDatabasePlaygrounds.addListenerForSingleValueEvent(mValueEventListener);
    }

    @Override
    public void getPlaygroundSchedules(String playgroundUid) {
        getMvpView().showLoading();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SCHEDULES_TABLE);
        mDatabase.child(playgroundUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (dataSnapshot != null && dataSnapshot.exists()) {

                            PlaygroundSchedule playgroundSchedule;

                            PlaygroundSchedules playgroundSchedules = new PlaygroundSchedules();
                            playgroundSchedules.playgroundSchedules = new ArrayList<>();

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                playgroundSchedule = child.getValue(PlaygroundSchedule.class);
                                if (playgroundSchedule != null) {
                                    playgroundSchedules.playgroundSchedules.add(playgroundSchedule);
                                }
                            }

                            getMvpView().onGetPlaygroundSchedules(playgroundSchedules);

                        } else {
//                            getMvpView().onError(R.string.error_no_data);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        AppLogger.d("isUserExistInDB onCancelled = " + databaseError.getMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(R.string.error);
                    }
                });
    }


    @Override
    public void uploadPlaygroundImages(List<PlaygroundImage> playgroundImages) {

        for (final PlaygroundImage image : playgroundImages) {
            String name = String.valueOf(System.currentTimeMillis()) + "." + FileUtils.getFileExtension(mActivity, new File(image.localPath));

            StorageReference fileToUpload = mStorage.child(Constants.FIREBASE_DB_PLAYGROUND_IMAGE_FOLDER).child(name);
            fileToUpload.putFile(Uri.fromFile(new File(image.localPath))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    if (!isViewAttached()) {
                        return;
                    }

                        image.onlinePath = Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString();
                    image.isUploaded = true;

                    getMvpView().onPlaygroundImageUploaded(image);
                }
            });
        }
    }

    @Override
    public void deletePlaygroundImages(final Playground playground, final String imageUrl, final int position) {
        getMvpView().showLoading();

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        //        StorageReference imageRef = storageRef.child(imageUrl);

        storageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getMvpView().onPlaygroundImageDeleted(position);

                        if (playground != null) {

                            if (playground.images.contains(imageUrl)) {
                                playground.images.remove(imageUrl);
                            }

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE);
                            mDatabase.child(playground.playgroundId)
                                    .child("images")
                                    .setValue(playground.images)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (!isViewAttached()) {
                                                return;
                                            }

                                            getMvpView().hideLoading();


                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            AppLogger.w(" onComplete");

                                            if (!isViewAttached()) {
                                                return;
                                            }

                                            getMvpView().hideLoading();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            AppLogger.e(" Error -> " + e.getLocalizedMessage());

                                            if (!isViewAttached()) {
                                                return;
                                            }

                                            getMvpView().hideLoading();
                                            getMvpView().onError(e.getLocalizedMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        AppLogger.e("deletePlaygroundImages -> " + exception.getLocalizedMessage());
                    }
                });


//        if (playground != null) {
//            if (ListUtils.isEmpty(playground.images)) {
//                if (!isViewAttached()) {
//                    return;
//                }
//
//                getMvpView().hideLoading();
//
//                return;
//            }
//
//            if (playground.images.contains(imageUrl)) {
//                playground.images.remove(imageUrl);
//            }
//
//            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE);
//            mDatabase.child(playground.playgroundId)
//                    .child("images")
//                    .setValue(playground.images)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            if (!isViewAttached()) {
//                                return;
//                            }
//
//                            getMvpView().hideLoading();
//
//
//                        }
//                    })
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            AppLogger.w(" onComplete");
//
//                            if (!isViewAttached()) {
//                                return;
//                            }
//
//                            getMvpView().hideLoading();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            AppLogger.e(" Error -> " + e.getLocalizedMessage());
//
//                            if (!isViewAttached()) {
//                                return;
//                            }
//
//                            getMvpView().hideLoading();
//                            getMvpView().onError(e.getLocalizedMessage());
//                        }
//                    });
//
//        }

//        // Delete the file
//        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                getMvpView().onPlaygroundImageDeleted(position);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                AppLogger.e("deletePlaygroundImages -> " + exception.getLocalizedMessage());
//            }
//        });
    }

    @Override
    public void addPlayground(String name,String nameOwner, String mobileOwner, String nameGuard, String mobileGuard, boolean isPromotion, String note, String region, String city, String direction, Location location, List<PlaygroundImage> images,
                              boolean hasShower, boolean hasWC, boolean hasGrass, boolean hasWater, boolean hasBall,boolean isHide) {

        if (getDataManager().getCurrentUser() == null) {
            getMvpView().onError(R.string.msg_user_not_signed_in);
            return;
        }

        if (StringUtils.isEmpty(name)) {
            getMvpView().onError(R.string.error_no_playground_name);
            return;
        }

        if (StringUtils.isEmpty(region)) {
            getMvpView().onError(R.string.error_no_region_selected);
            return;
        }

        if (StringUtils.isEmpty(city)) {
            getMvpView().onError(R.string.error_no_city_selected);
            return;
        }

        if (StringUtils.isEmpty(direction)) {
            getMvpView().onError(R.string.error_no_direction_selected);
            return;
        }

        if (location == null || location.latitude == 0 || location.longitude == 0) {
            getMvpView().onError(R.string.error_no_location_selected);
            return;
        }

        getMvpView().showLoading();

        for (PlaygroundImage image : images) {
            if (!image.isUploaded) {
                return;
            }
        }

        final Playground playground = new Playground();
        playground.name = name;
        playground.nameOwner = nameOwner;
        playground.mobileOwner = mobileOwner;
        playground.nameguard = nameGuard;
        playground.mobileguard = mobileGuard;
        playground.ownerId = getDataManager().getCurrentUser().uId;
        playground.address_region = region;
        playground.address_city = city;
        playground.address_direction = direction;
        playground.latitude = location.latitude;
        playground.longitude = location.longitude;
        playground.owner = new PlaygroundOwner();
        playground.owner.uId = getDataManager().getCurrentUser().uId;
        playground.owner.appUserId = getDataManager().getCurrentUser().appUserId;
        playground.owner.name = getDataManager().getCurrentUser().getUserFullName();
        playground.owner.email = getDataManager().getCurrentUser().email;
        playground.owner.mobileNo = getDataManager().getCurrentUser().mobileNo;
        playground.owner.profileImageUrl = getDataManager().getCurrentUser().profileImageUrl;
playground.isHide=isHide;
        playground.images = new ArrayList<>();
        for (PlaygroundImage image : images) {
            playground.images.add(image.onlinePath);
        }

        playground.amenity = new Amenity(hasShower, hasWC, hasGrass, hasWater, hasBall);

        playground.isActive = true;

        playground.isPromotion = isPromotion;
        if (isPromotion) {
            playground.note = note;
        } else {
            playground.note = "";
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE);
        playground.playgroundId = mDatabase.child(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE).push().getKey();

        mDatabase.child(playground.playgroundId)
                .setValue(playground)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onPlaygroundAdded(playground);
                        getMvpView().showMessage(R.string.msg_success);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AppLogger.w(" onComplete");

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        FirebaseUtils.sendNotificationToAdmin(NotificationType.OWNER_PLAYGROUND_NEW, playground.owner.uId, playground.owner.name, playground.owner.profileImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AppLogger.e(" Error -> " + e.getLocalizedMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void updatePlayground(String playgroundId, String name,String nameOwner, String mobileOwner, String nameGuard, String mobileGuard, boolean isPromotion, String note, String region, String city, String direction, Location location,
                                 List<PlaygroundImage> images,
                                 boolean hasShower, boolean hasWC, boolean hasGrass, boolean hasWater, boolean hasBall,
                                 boolean isActive,boolean isHide) {

        if (getDataManager().getCurrentUser() == null) {
            getMvpView().onError(R.string.msg_user_not_signed_in);
            return;
        }

        if (StringUtils.isEmpty(name)) {
            getMvpView().onError(R.string.error_no_playground_name);
            return;
        }

        if (StringUtils.isEmpty(region)) {
            getMvpView().onError(R.string.error_no_region_selected);
            return;
        }

        if (StringUtils.isEmpty(city)) {
            getMvpView().onError(R.string.error_no_city_selected);
            return;
        }

        if (StringUtils.isEmpty(direction)) {
            getMvpView().onError(R.string.error_no_direction_selected);
            return;
        }

        if (location == null || location.latitude == 0 || location.longitude == 0) {
            getMvpView().onError(R.string.error_no_location_selected);
            return;
        }

        getMvpView().showLoading();

        for (PlaygroundImage image : images) {
            if (!image.isUploaded) {
                return;
            }
        }

        final Playground playground = new Playground();
        playground.playgroundId = playgroundId;
        playground.name = name;
        playground.nameOwner = nameOwner;
        playground.mobileOwner = mobileOwner;
        playground.nameguard = nameGuard;
        playground.mobileguard = mobileGuard;
        playground.note = note;
        playground.ownerId = getDataManager().getCurrentUser().uId;
        playground.address_region = region;
        playground.address_city = city;
        playground.address_direction = direction;
        playground.latitude = location.latitude;
        playground.longitude = location.longitude;

        playground.isPromotion = isPromotion;
        if (isPromotion) {
            playground.note = note;
        } else {
            playground.note = "";
        }

        playground.owner = new PlaygroundOwner();
        playground.owner.uId = getDataManager().getCurrentUser().uId;
        playground.owner.appUserId = getDataManager().getCurrentUser().appUserId;
        playground.owner.name = getDataManager().getCurrentUser().getUserFullName();
        playground.owner.email = getDataManager().getCurrentUser().email;
        playground.owner.mobileNo = getDataManager().getCurrentUser().mobileNo;
        playground.owner.profileImageUrl = getDataManager().getCurrentUser().profileImageUrl;

        playground.images = new ArrayList<>();
        for (PlaygroundImage image : images) {
            playground.images.add(image.onlinePath);
        }

        playground.amenity = new Amenity(hasShower, hasWC, hasGrass, hasWater, hasBall);
playground.isHide=isHide;
        playground.isActive= isActive;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE);

//        if (StringUtils.isEmpty(playground.playgroundId)) {
//            playground.playgroundId = mDatabase.child(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE).push().getKey();
//        }

        mDatabase.child(playgroundId)
                .setValue(playground)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onPlaygroundUpdated(playground);
                        getMvpView().showMessage(R.string.msg_success);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AppLogger.w(" onComplete");

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

//                        FirebaseUtils.sendNotificationToAdmin(NotificationType.OWNER_PLAYGROUND_NEW, playground.owner.uId, playground.owner.name, playground.owner.profileImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AppLogger.e(" Error -> " + e.getLocalizedMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void addPlaygroundSchedules(final Playground playground, final List<PlaygroundSchedule> playgroundSchedules) {
        if (playground == null) {
            getMvpView().showMessage("يجب إضافة الملعب أولا");
            return;
        }


        getMvpView().showLoading();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SCHEDULES_TABLE);

        mDatabase.child(playground.playgroundId)
//                .child(schedule.day)
                .setValue(playgroundSchedules)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

//                        if (finalI == playgroundSchedules.size()) {
                        getMvpView().onPlaygroundSchedulesAdded(playground, playgroundSchedules);
                        getMvpView().showMessage(R.string.msg_success);
//                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AppLogger.w(" onComplete");

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AppLogger.e(" Error -> " + e.getLocalizedMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(e.getLocalizedMessage());
                    }
                });


//        for (final PlaygroundSchedule schedule : playgroundSchedules) {
////            final String playgroundUId = mDatabase.child(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE).push().getKey();
//            i++;
//            final int finalI = i;
//
//            mDatabase.child(playground.playgroundId)
//                    .child(schedule.day)
//                    .setValue(schedule)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            if (!isViewAttached()) {
//                                return;
//                            }
//
//                            getMvpView().hideLoading();
//
//                            if (finalI == playgroundSchedules.size()) {
//                                getMvpView().onPlaygroundSchedulesAdded(playground, playgroundSchedules);
//                                getMvpView().showMessage(R.string.msg_success);
//                            }
//                        }
//                    })
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            AppLogger.w(" onComplete");
//
//                            if (!isViewAttached()) {
//                                return;
//                            }
//
//                            getMvpView().hideLoading();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            AppLogger.e(" Error -> " + e.getLocalizedMessage());
//
//                            if (!isViewAttached()) {
//                                return;
//                            }
//
//                            getMvpView().hideLoading();
//                            getMvpView().onError(e.getLocalizedMessage());
//                        }
//                    });
//        }
    }

    @Override
    public void addPlaygroundSearch(final Playground playground, final PlaygroundSchedule playgroundSchedule) {
//        for (SizeListItem size : playgroundSchedule.size) {
//            addPlaygroundSearch(playground.playgroundId, playground.name, playground.address_region, playground.address_city, playground.address_direction,
//                    playground.latitude, playground.longitude,
//                    playground.images,
//                    false, false, false,
//                    playgroundSchedule.price, size.value,
//                    true);
//        }

        getMvpView().showLoading();

        int i = 0;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SEARCH_TABLE);

        for (SizeListItem size : playgroundSchedule.size) {
            i++;
            final int finalI = i;

            final PlaygroundSearch playgroundSearch = new PlaygroundSearch();
            playgroundSearch.playgroundId = playground.playgroundId;
            playgroundSearch.name = playground.name;
            playgroundSearch.address_region = playground.address_region;
            playgroundSearch.address_city = playground.address_city;
            playgroundSearch.address_direction = playground.address_direction;
            playgroundSearch.latitude = playground.latitude;
            playgroundSearch.longitude = playground.longitude;

            if (!ListUtils.isEmpty(playground.images)) {
                playgroundSearch.images = new ArrayList<>(playground.images);
            }

            //        for (PlaygroundImage image : images) {
            //            playground.images.add(image.onlinePath);
            //        }

            playgroundSearch.hasAgeYoung = false;
            playgroundSearch.hasAgeMiddle = false;
            playgroundSearch.hasAgeOld = false;

            playgroundSearch.price = playgroundSchedule.price;
            playgroundSearch.size = size.value;

            playgroundSearch.isActive = playground.isActive;

//          if (StringUtils.isEmpty(playground.playgroundId)) {
//              playground.playgroundId = mDatabase.child(Constants.FIREBASE_DB_PLAYGROUNDS_SEARCH_TABLE).push().getKey();
//          }

            final String key = mDatabase.child(Constants.FIREBASE_DB_PLAYGROUNDS_SEARCH_TABLE).push().getKey();
            mDatabase.child(key)
                    .setValue(playgroundSearch)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            AppLogger.w(" onSuccess");

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            if (finalI == playgroundSchedule.size.size()) {
                                getMvpView().onPlaygroundSearchAdded();
                                getMvpView().showMessage(R.string.msg_success);
                            }

                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            AppLogger.w(" onComplete");

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AppLogger.e(" Error -> " + e.getLocalizedMessage());

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                            getMvpView().onError(e.getLocalizedMessage());
                        }
                    });
        }
    }
}
