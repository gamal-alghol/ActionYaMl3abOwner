package com.malaab.ya.action.actionyamalaab.owner.custom;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.AppCompatImageButton;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.model.DurationListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.model.SizeListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedule;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedules;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.ListUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.StringUtils;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BSheetPlaygroundSchedule extends BottomSheetBehavior implements TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.header_txt_title)
    TextView header_txt_title;
    @BindView(R.id.header_btn_back)
    AppCompatImageButton header_btn_back;
    @BindView(R.id.header_btn_notifications)
    AppCompatImageButton header_btn_notifications;

    @BindView(R.id.txt_size)
    TextView txt_size;
    @BindView(R.id.txt_duration)
    TextView txt_duration;

    @BindView(R.id.txt_sat)
    TextView txt_sat;
    @BindView(R.id.txt_sun)
    TextView txt_sun;
    @BindView(R.id.txt_mon)
    TextView txt_mon;
    @BindView(R.id.txt_tue)
    TextView txt_tue;
    @BindView(R.id.txt_wen)
    TextView txt_wen;
    @BindView(R.id.txt_thu)
    TextView txt_thu;
    @BindView(R.id.txt_fri)
    TextView txt_fri;

    @BindView(R.id.edt_from)
    EditText edt_from;
    @BindView(R.id.edt_to)
    EditText edt_to;

    @BindView(R.id.edt_price)
    EditText edt_price;

    @BindView(R.id.txt_daysBalance)
    TextView txt_daysBalance;

    private Activity mActivity;
    private BottomSheetBehavior mBottomSheetBehavior;
    private OnBottomSheetListener mBottomSheetListener;

    private DialogList mDialogList;
    private TimePickerDialog mTimePickerDialog;

    private PlaygroundSchedules mPlaygroundSchedules;

    private String timeStart, timeEnd;
    public List<SizeListItem> sizes;
    public List<DurationListItem> durations;
    private int daysSelected = 0;

    private boolean isSaturday, isSunday, isMonday, isTuesday, isWednesday, isThursday, isFriday;
    private boolean isFrom;

    private boolean isAddingMore;
    private boolean isActionTaken;
    private boolean isEditMode;


    public void attachAndInit(final Activity activity) {
        if (activity != null) {
            mActivity = activity;

            View bottomSheet = mActivity.findViewById(R.id.bSheet_playground_schedule);
            ButterKnife.bind(this, bottomSheet);

            header_btn_back.setVisibility(View.VISIBLE);
            header_txt_title.setText("إضافة جدول الحجوزات");
            header_btn_notifications.setVisibility(View.INVISIBLE);

            mPlaygroundSchedules = new PlaygroundSchedules();
            mPlaygroundSchedules.playgroundSchedules = new ArrayList<>();

            sizes = new ArrayList<>();
            durations = new ArrayList<>();

            mTimePickerDialog = TimePickerDialog.newInstance(this, false);

            mDialogList = new DialogList().with(mActivity);
            mDialogList.build();

            initPlaygroundSizes();
            initDurations();

            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);

            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            mBottomSheetBehavior.setPeekHeight(0);

            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (mBottomSheetListener == null)
                        return;

                    if (newState == STATE_EXPANDED) {
                        if (mBottomSheetBehavior instanceof LockableBottomSheetBehavior) {
                            ((LockableBottomSheetBehavior) mBottomSheetBehavior).setLocked(true);
                        }

                        isActionTaken = false;

                        mBottomSheetListener.onBottomSheetExpanded(R.id.bSheet_playground_schedule);

                    } else if (newState == STATE_COLLAPSED) {
                        if (isActionTaken) {
                            mBottomSheetListener.onBottomSheetCollapsed(R.id.bSheet_playground_schedule, mPlaygroundSchedules, isAddingMore, isActionTaken);
                        }
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    if (mBottomSheetListener == null)
                        return;

                    mBottomSheetListener.onSlide(slideOffset);
                }
            });
        }
    }


    @OnClick(R.id.header_btn_back)
    void close() {
        hide();
    }


    @OnClick(R.id.ln_size)
    void selectSize() {
        mDialogList.showSizes();
    }

    @OnClick(R.id.ln_duration)
    void selectDuration() {
        mDialogList.showDurations();
    }


    @OnClick(R.id.txt_sat)
    void selectSat() {
        isSaturday = !isSaturday;
        selectDay(txt_sat, isSaturday);
    }

    @OnClick(R.id.txt_sun)
    void selectSun() {
        isSunday = !isSunday;
        selectDay(txt_sun, isSunday);
    }

    @OnClick(R.id.txt_mon)
    void selectMon() {
        isMonday = !isMonday;
        selectDay(txt_mon, isMonday);
    }

    @OnClick(R.id.txt_tue)
    void selectTue() {
        isTuesday = !isTuesday;
        selectDay(txt_tue, isTuesday);
    }

    @OnClick(R.id.txt_wen)
    void selectWedn() {
        isWednesday = !isWednesday;
        selectDay(txt_wen, isWednesday);
    }

    @OnClick(R.id.txt_thu)
    void selectThu() {
        isThursday = !isThursday;
        selectDay(txt_thu, isThursday);
    }

    @OnClick(R.id.txt_fri)
    void selectFri() {
        isFriday = !isFriday;
        selectDay(txt_fri, isFriday);
    }


    @OnClick(R.id.edt_from)
    void selectTimeFrom() {
        isFrom = true;

        mTimePickerDialog.show(mActivity.getFragmentManager(), Constants.TIMEPICKER_TAG);

        edt_from.setInputType(InputType.TYPE_NULL);
        edt_from.requestFocus();
    }

    @OnClick(R.id.edt_to)
    void selectTimeTo() {
        isFrom = false;

        mTimePickerDialog.show(mActivity.getFragmentManager(), Constants.TIMEPICKER_TAG);

        edt_to.setInputType(InputType.TYPE_NULL);
        edt_to.requestFocus();
    }


    @OnClick(R.id.btn_saveAdd)
    void saveAdd() {
        if (ListUtils.isEmpty(sizes)) {
            showMessage(mActivity.getString(R.string.error_no_size_selected));
            return;
        }

        if (ListUtils.isEmpty(durations)) {
            showMessage(mActivity.getString(R.string.error_no_duration_selected));
            return;
        }

        if (StringUtils.isEmpty(timeStart) || StringUtils.isEmpty(timeEnd)) {
            showMessage(mActivity.getString(R.string.error_no_operation_hour_selected));
            return;
        }

        if (StringUtils.isEmpty(edt_price.getText().toString())) {
            showMessage(mActivity.getString(R.string.error_no_price_selected));
            return;
        }

        if (!isSaturday && !isSunday && !isMonday && !isTuesday && !isWednesday && !isThursday && !isFriday) {
            showMessage(mActivity.getString(R.string.error_no_day_selected));
            return;
        }

        makeSchedule(isFriday, "Friday");
        makeSchedule(isThursday, "Thursday");
        makeSchedule(isWednesday, "Wednesday");
        makeSchedule(isTuesday, "Tuesday");
        makeSchedule(isMonday, "Monday");
        makeSchedule(isSunday, "Sunday");
        makeSchedule(isSaturday, "Saturday");

        isAddingMore = true;
        isActionTaken = true;
        hide();
    }

    @OnClick(R.id.btn_uploadPlayground)
    void save() {
        if (!isEditMode) {
            if (ListUtils.isEmpty(sizes)) {
                showMessage(mActivity.getString(R.string.error_no_size_selected));
                return;
            }

            if (ListUtils.isEmpty(durations)) {
                showMessage(mActivity.getString(R.string.error_no_duration_selected));
                return;
            }

            if (StringUtils.isEmpty(timeStart) || StringUtils.isEmpty(timeEnd)) {
                showMessage(mActivity.getString(R.string.error_no_operation_hour_selected));
                return;
            }

            if (StringUtils.isEmpty(edt_price.getText().toString())) {
                showMessage(mActivity.getString(R.string.error_no_price_selected));
                return;
            }

            if (!isSaturday && !isSunday && !isMonday && !isTuesday && !isWednesday && !isThursday && !isFriday) {
                showMessage(mActivity.getString(R.string.error_no_day_selected));
                return;
            }

            makeSchedule(isFriday, "Friday");
            makeSchedule(isThursday, "Thursday");
            makeSchedule(isWednesday, "Wednesday");
            makeSchedule(isTuesday, "Tuesday");
            makeSchedule(isMonday, "Monday");
            makeSchedule(isSunday, "Sunday");
            makeSchedule(isSaturday, "Saturday");
        }

        isAddingMore = false;
        isActionTaken = true;
        hide();
    }

    private void makeSchedule(boolean isSelected, String dayName) {
        PlaygroundSchedule playgroundSchedule = new PlaygroundSchedule();
        playgroundSchedule.isActive = true;
        playgroundSchedule.day = dayName;
        playgroundSchedule.size = sizes;
        playgroundSchedule.duration = durations;
        playgroundSchedule.timeStart = timeStart;
        playgroundSchedule.timeEnd = timeEnd;
        playgroundSchedule.price = Float.valueOf(edt_price.getText().toString());

        if (mPlaygroundSchedules.playgroundSchedules.contains(playgroundSchedule)) {
            mPlaygroundSchedules.playgroundSchedules.remove(playgroundSchedule);
        }

        if (isSelected) {
            mPlaygroundSchedules.playgroundSchedules.add(0, playgroundSchedule);
        }
    }


    public void setOnBottomSheetListener(OnBottomSheetListener listener) {
        mBottomSheetListener = listener;
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourStr = String.valueOf(hourOfDay);
        String minStr = String.valueOf(minute);
        String secStr = String.valueOf(second);

        if (hourOfDay < 10) {
            hourStr = "0" + hourOfDay;
        }
        if (minute < 10) {
            minStr = "0" + minute;
        }
        if (second < 10) {
            secStr = "0" + second;
        }

        if (isFrom) {
            timeStart = hourStr + ":" + minStr + ":" + secStr;
            edt_from.setText(timeStart);

        } else {
            timeEnd = hourStr + ":" + minStr + ":" + secStr;
            edt_to.setText(timeEnd);
        }
    }


    private void initPlaygroundSizes() {
        List<SizeListItem> list = new ArrayList<>();
        list.add(new SizeListItem("5 x 5", Constants.SIZE_10, false));
        list.add(new SizeListItem("6 x 6", Constants.SIZE_12, false));
        list.add(new SizeListItem("7 x 7", Constants.SIZE_14, false));
        list.add(new SizeListItem("8 x 8", Constants.SIZE_16, false));
        list.add(new SizeListItem("9 x 9", Constants.SIZE_18, false));
        list.add(new SizeListItem("10 x 10", Constants.SIZE_20, false));
        mDialogList.addSizes(list);
    }

    private void initDurations() {
        List<DurationListItem> list = new ArrayList<>();
        list.add(new DurationListItem("60 min", Constants.DURATION_60, false));
        list.add(new DurationListItem("90 min", Constants.DURATION_90, false));
        list.add(new DurationListItem("120 min", Constants.DURATION_120, false));
        mDialogList.addDurations(list);
    }


    private void selectDay(TextView view, boolean isSelected) {
        if (isSelected) {
            daysSelected++;
            view.setBackgroundResource(R.drawable.layout_rounded_border_green);
        } else {
            daysSelected--;
            view.setBackgroundResource(R.drawable.layout_rounded_border_gray);
        }

        getDaysBalance();
    }

    private void getDaysBalance() {
        txt_daysBalance.setText("باقي " + (7 - daysSelected) + " أيام");
    }


    private void showMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
        }
    }


    public void setPlaygroundSchedules(List<PlaygroundSchedule> playgroundSchedules) {
        isEditMode = true;

        mPlaygroundSchedules = new PlaygroundSchedules();
        mPlaygroundSchedules.playgroundSchedules = new ArrayList<>(playgroundSchedules);

        for (PlaygroundSchedule schedule : playgroundSchedules) {
            switch (schedule.day) {
                case "Friday":
                    selectFri();
                    break;

                case "Thursday":
                    selectThu();
                    break;

                case "Wednesday":
                    selectWedn();
                    break;

                case "Tuesday":
                    selectTue();
                    break;

                case "Monday":
                    selectMon();
                    break;

                case "Sunday":
                    selectSun();
                    break;

                case "Saturday":
                    selectSat();
                    break;
            }
        }
    }

    public void reset() {
        mPlaygroundSchedules = new PlaygroundSchedules();
        mPlaygroundSchedules.playgroundSchedules = new ArrayList<>();

        timeStart = "";
        timeEnd = "";

        sizes = new ArrayList<>();
        durations = new ArrayList<>();

        isSaturday = false;
        isSunday = false;
        isMonday = false;
        isTuesday = false;
        isWednesday = false;
        isThursday = false;
        isFriday = false;

        isFrom = false;

        isAddingMore = false;

        txt_size.setText(mActivity.getString(R.string.title_playground_size));
        txt_duration.setText(mActivity.getString(R.string.duration));

        selectDay(txt_sat, false);
        selectDay(txt_sun, false);
        selectDay(txt_mon, false);
        selectDay(txt_tue, false);
        selectDay(txt_wen, false);
        selectDay(txt_thu, false);
        selectDay(txt_fri, false);

        daysSelected = 0;
        getDaysBalance();

        edt_from.setText("");
        edt_to.setText("");
        edt_price.setText("");

        mDialogList.resetSizes();
        mDialogList.resetDurations();
    }


//    public void setPlaygroundPlaygroundSchedules(PlaygroundSchedules playgroundSchedules) {
//        mPlaygroundSchedules = playgroundSchedules;
//
//        for (PlaygroundSchedule schedule : mPlaygroundSchedules.playgroundSchedules) {
//            switch (schedule.day) {
//                case "Saturday":
//                    selectSat();
//                    break;
//
//                case "Sunday":
//                    selectSun();
//                    break;
//
//                case "Monday":
//                    selectMon();
//                    break;
//
//                case "Tuesday":
//                    selectTue();
//                    break;
//
//                case "Wednesday":
//                    selectWedn();
//                    break;
//
//                case "Thursday":
//                    selectThu();
//                    break;
//
//                case "Friday":
//                    selectFri();
//                    break;
//            }
//        }
//    }


    private boolean isShown() {
        return mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public void show() {
        if (!isShown()) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void hide() {
//        if (isShown()) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventItemClicked(OnEventItemClicked event) {

        if (event.getItem() instanceof SizeListItem) {

            SizeListItem item = (SizeListItem) event.getItem();

            if (event.getAction() == ItemAction.PICK) {
                item.isSelected = true;
                if (!sizes.contains(item)) {
                    sizes.add(item);
                }

            } else if (event.getAction() == ItemAction.REMOVE) {
                item.isSelected = false;
                if (sizes.contains(item)) {
                    sizes.remove(item);
                }
            }

            mDialogList.setSizeSelected(item);

            StringBuilder size = new StringBuilder();
            for (SizeListItem s : mDialogList.getSizes()) {
                if (s.isSelected) {
                    size.append(s.name).append("\n");
                }
            }
            size.deleteCharAt(size.lastIndexOf("\n"));
            txt_size.setText(size.toString());


        } else if (event.getItem() instanceof DurationListItem) {

            DurationListItem item = (DurationListItem) event.getItem();

            if (event.getAction() == ItemAction.PICK) {
                item.isSelected = true;
                if (!durations.contains(item)) {
                    durations.add(item);
                }

            } else if (event.getAction() == ItemAction.REMOVE) {
                item.isSelected = false;
                if (durations.contains(item)) {
                    durations.remove(item);
                }
            }

            mDialogList.setDurationSelected(item);

            StringBuilder size = new StringBuilder();
            for (DurationListItem s : mDialogList.getDurations()) {
                if (s.isSelected) {
                    size.append(s.name).append("\n");
                }
            }
            size.deleteCharAt(size.lastIndexOf("\n"));
            txt_duration.setText(size.toString());

        }

//            mDialogList.dismiss();
    }


    public boolean onBackPressed() {
        if (isShown()) {
            hide();
            return true;
        }

        return false;
    }

    public void onDetach() {
        EventBus.getDefault().unregister(this);
        mBottomSheetListener = null;
        mActivity = null;
    }

}
