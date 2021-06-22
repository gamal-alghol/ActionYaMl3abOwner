package com.malaab.ya.action.actionyamalaab.owner.custom;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.model.CalendarTime;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Day;
import com.malaab.ya.action.actionyamalaab.owner.data.model.DurationListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.model.SizeListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedule;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.adapter.TimesAdapter;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit.DaysAdapter;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.ListUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.ScreenUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.StringUtils;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DialogSchedule implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.dialog_btn_close)
    ImageButton btn_close;
    @BindView(R.id.dialog_txt_title)
    TextView txt_title;

    @BindView(R.id.ln_days)
    LinearLayout ln_days;
    @BindView(R.id.sp_days)
    CustomSpinner sp_days;

    @BindView(R.id.ln_size)
    LinearLayout ln_size;
    @BindView(R.id.txt_size)
    TextView txt_size;

    @BindView(R.id.ln_duration)
    LinearLayout ln_duration;
    @BindView(R.id.txt_duration)
    TextView txt_duration;

    @BindView(R.id.edt_from)
    EditText edt_from;
    @BindView(R.id.edt_to)
    EditText edt_to;

    @BindView(R.id.edt_price)
    EditText edt_price;

    @BindView(R.id.lbl_disableTimes)
    TextView lbl_disableTimes;
    @BindView(R.id.rv_times)
    RecyclerView rv_times;

    private Activity mActivity;
    private Dialog mDialog;
    private OnDialogScheduleListener mListener;

    private DialogList mDialogList;
    private TimePickerDialog mTimePickerDialog;

//    private PlaygroundSchedule playgroundSchedule;

    private String title, timeStart, timeEnd;
    private List<SizeListItem> sizes;
    private List<DurationListItem> durations;
    private boolean isFrom;

    private DaysAdapter daysAdapter;
    private TimesAdapter timesAdapter;
    private Day day;
    private CalendarTime mCalendarTime;


    public DialogSchedule with(Activity activity) {
        if (mDialog == null) {
            mActivity = activity;
            mDialog = new Dialog(mActivity);
        }

        return this;
    }

    public DialogSchedule withTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogSchedule withListener(OnDialogScheduleListener listener) {
        mListener = listener;
        return this;
    }

    public DialogSchedule build() {
        if (mDialog.getWindow() != null) {
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            mDialog.setContentView(R.layout.custom_dialog_schedule);

            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mDialog.setCanceledOnTouchOutside(true);

            ButterKnife.bind(this, mDialog);
        }

        txt_title.setText(title);

        int i = 1;
        List<Day> days = new ArrayList<>();
        days.add(new Day(i++, "السبت"));
        days.add(new Day(i++, "الأحد"));
        days.add(new Day(i++, "الاثنين"));
        days.add(new Day(i++, "الثلاثاء"));
        days.add(new Day(i++, "الأربعاء"));
        days.add(new Day(i++, "الخميس"));
        days.add(new Day(i, "الجمعة"));

        daysAdapter = new DaysAdapter(mActivity, days);
        sp_days.setAdapter(daysAdapter);

//        for (int position = 0; position < categoriesBarAdapter.getCount(); position++) {
//            if (categoriesBarAdapter.getItem(position).name == ) {
//                sp_days.setSelection(position, false, true);
//
//                if ((sp_days.getSelectedView()) != null) {
//                    ((TextView) sp_days.getSelectedView()).setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark));
//                }
//
////                header_txt_title.setText(categoriesBarAdapter.getItem(position).name);
//                break;
//            }
//        }

        sp_days.setOnItemSelectedListener(this);


        sizes = new ArrayList<>();
        durations = new ArrayList<>();

        mTimePickerDialog = TimePickerDialog.newInstance(this, false);

        mDialogList = new DialogList().with(mActivity);
        mDialogList.build();

        initPlaygroundSizes();
        initDurations();

        rv_times.setHasFixedSize(true);
        rv_times.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        return this;
    }


    public void onConfigurationChanged() {
        if (mDialog != null && mDialog.isShowing()) {
            if (mDialog.getWindow() != null)
                mDialog.getWindow().setLayout((int) (ScreenUtils.getScreenWidth(mActivity) * 0.9f), (int) (ScreenUtils.getScreenHeight(mActivity) * 0.9f));
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
        list.add(new SizeListItem("11 x 11", Constants.SIZE_22, false));
        mDialogList.addSizes(list);
    }

    private void initDurations() {
        List<DurationListItem> list = new ArrayList<>();
        list.add(new DurationListItem("60 min", Constants.DURATION_60, false));
        list.add(new DurationListItem("90 min", Constants.DURATION_90, false));
        list.add(new DurationListItem("120 min", Constants.DURATION_120, false));
        mDialogList.addDurations(list);
    }


    @OnClick(R.id.dialog_btn_close)
    void back() {
        dismiss();
    }


    @OnClick(R.id.ln_days)
    void selectDay() {
        sp_days.performClick();
    }

    @OnClick(R.id.ln_size)
    void selectSize() {
        mDialogList.showSizes();
    }

    @OnClick(R.id.ln_duration)
    void selectDuration() {
        mDialogList.showDurations();
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

    @OnClick(R.id.btn_saveSchedule)
    void saveSchedule() {
        if (day == null) {
            showMessage(mActivity.getString(R.string.error_no_day_selected));
            return;
        }

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

        PlaygroundSchedule playgroundSchedule = new PlaygroundSchedule();
        playgroundSchedule.isActive = true;

        playgroundSchedule.day = day.name;
        playgroundSchedule.size = sizes;
        playgroundSchedule.duration = durations;
        playgroundSchedule.timeStart = timeStart;
        playgroundSchedule.timeEnd = timeEnd;
        playgroundSchedule.price = Float.valueOf(edt_price.getText().toString());

        playgroundSchedule.disabledTimes = new ArrayList<>();
        if (timesAdapter != null && !ListUtils.isEmpty(timesAdapter.getItems())) {
            playgroundSchedule.disabledTimes = new ArrayList<>(timesAdapter.getItems());
        }

        if (mListener != null) {
            mListener.onScheduleDayCreated(playgroundSchedule);
        }

        dismiss();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_days && sp_days.getCount() > 0) {
            if (parent.getSelectedItem() != null) {
                day = new Day((Day) parent.getSelectedItem());
                day.name = DateTimeUtils.getEnglishDayName(day.name);
            }

            if ((sp_days.getSelectedView()) != null) {
                ((TextView) sp_days.getSelectedView()).setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                ((TextView) sp_days.getSelectedView()).setTextSize(20);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            mDialogList.dismiss();

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
            mDialogList.dismiss();

            StringBuilder size = new StringBuilder();
            for (DurationListItem s : mDialogList.getDurations()) {
                if (s.isSelected) {
                    size.append(s.name).append("\n");
                }
            }
            size.deleteCharAt(size.lastIndexOf("\n"));
            txt_duration.setText(size.toString());

        } else if (event.getItem() instanceof CalendarTime && event.getAction() == ItemAction.PICK) {
            setTimeSelected((CalendarTime) event.getItem(), event.getPosition());
        }

//            mDialogList.dismiss();
    }


    private void showMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
        }
    }


    private void setTimeSelected(CalendarTime calendarTime, int pos) {
        timesAdapter.setIsSelectedByUSer(pos, !calendarTime.isAvailable);
    }

    private void populateTimes(String timeStart, String timeEnd, int duration, List<CalendarTime> disabledTimes) {
        String[] startTime = timeStart.split(":");
        int startHour = Integer.parseInt(startTime[0]);
        int startMin = Integer.parseInt(startTime[1]);

        Calendar startTimeCal = Calendar.getInstance();
//        startTimeCal.setTime(mCalendarDay.date.getTime());
        startTimeCal.set(Calendar.HOUR_OF_DAY, startHour);
        startTimeCal.set(Calendar.MINUTE, startMin);

        String[] endTime = timeEnd.split(":");
        int endHour = Integer.parseInt(endTime[0]);
        int endMin = Integer.parseInt(endTime[1]);

        Calendar endTimeCal = Calendar.getInstance();
//        endTimeCal.setTime(mCalendarDay.date.getTime());
        endTimeCal.set(Calendar.HOUR_OF_DAY, endHour);
        endTimeCal.set(Calendar.MINUTE, endMin);

        if (endTimeCal.before(startTimeCal)) {
            endTimeCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        timesAdapter = new TimesAdapter(getTimes(startTimeCal.getTime(), endTimeCal.getTime(), duration, disabledTimes));
        rv_times.setAdapter(timesAdapter);
    }

    private static List<CalendarTime> getTimes(Date fromDate, Date toDate, int duration, List<CalendarTime> disabledTimes) {

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(fromDate);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(toDate);

        CalendarTime calendarTime;
        List<CalendarTime> times = new ArrayList<>();

        while (startDate.before(endDate)) {

            Calendar tempTime = Calendar.getInstance();
            tempTime.setTime(startDate.getTime());

            Calendar endDateTemp = Calendar.getInstance();
            endDateTemp.setTime(tempTime.getTime());
            endDateTemp.add(Calendar.MINUTE, duration);

            calendarTime = new CalendarTime(tempTime.getTimeInMillis(), endDateTemp.getTimeInMillis(), duration, R.drawable.icon_football_field_new, false, true);

            if (!ListUtils.isEmpty(disabledTimes)) {
                for (CalendarTime time : disabledTimes) {
                    if (DateTimeUtils.getHour(time.timeStart) == DateTimeUtils.getHour(calendarTime.timeStart)) {
                        calendarTime.isAvailable = time.isAvailable;
                    }
                }
            }

            times.add(calendarTime);

            startDate.add(Calendar.MINUTE, duration);
        }

        return times;
    }


    public void addSchedule(PlaygroundSchedule schedule) {
        reset();

        if (schedule != null) {
//            playgroundSchedule = schedule;

            for (int position = 0; position < daysAdapter.getCount(); position++) {
                if (daysAdapter.getItem(position).name.equalsIgnoreCase(DateTimeUtils.getArabicDayName(schedule.day))) {
                    sp_days.setSelection(position);
                    break;
                }
            }

            for (SizeListItem sizeListItem : schedule.size) {
                sizeListItem.isSelected = true;
                mDialogList.setSizeSelected(sizeListItem);

                sizes.add(sizeListItem);

                StringBuilder size = new StringBuilder();
                for (SizeListItem s : mDialogList.getSizes()) {
                    if (s.isSelected) {
                        size.append(s.name).append("\n");
                    }
                }
                size.deleteCharAt(size.lastIndexOf("\n"));
                txt_size.setText(size.toString());
            }

            for (DurationListItem durationListItemItem : schedule.duration) {
                durationListItemItem.isSelected = true;
                mDialogList.setDurationSelected(durationListItemItem);

                durations.add(durationListItemItem);

                StringBuilder size = new StringBuilder();
                for (DurationListItem s : mDialogList.getDurations()) {
                    if (s.isSelected) {
                        size.append(s.name).append("\n");
                    }
                }
                size.deleteCharAt(size.lastIndexOf("\n"));
                txt_duration.setText(size.toString());
            }

            timeStart = schedule.timeStart;
            timeEnd = schedule.timeEnd;

            edt_from.setText(timeStart);
            edt_to.setText(timeEnd);
            edt_price.setText(String.format(Locale.ENGLISH, "%.2f", schedule.price));

            if (schedule.isActive && !ListUtils.isEmpty(durations)) {
                lbl_disableTimes.setVisibility(View.VISIBLE);
                rv_times.setVisibility(View.VISIBLE);

                populateTimes(timeStart, timeEnd, durations.get(0).value, schedule.disabledTimes);
            }
        }
    }

    public void reset() {
//        playgroundSchedule = null;

        day = null;

        sizes = new ArrayList<>();
        durations = new ArrayList<>();

        isFrom = false;
        timeStart = "";
        timeEnd = "";

        txt_size.setText(mActivity.getString(R.string.title_playground_size));
        txt_duration.setText(mActivity.getString(R.string.duration));

        mDialogList.resetSizes();
        mDialogList.resetDurations();

        edt_from.setText("");
        edt_to.setText("");
        edt_price.setText("");
    }


    private boolean isDialogShown() {
        return (mDialog != null && mDialog.isShowing());
    }

    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            if (mDialog.getWindow() != null) {
                mDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenHeight(mActivity) * 0.9f));
//                mDialog.getWindow().setLayout((int) (ScreenUtils.getScreenWidth(mActivity) * 0.9f), (int) (ScreenUtils.getScreenHeight(mActivity) * 0.6f));
            }

            mDialog.show();
        }
    }

    public void dismiss() {
        if (isDialogShown()) {
            mDialog.dismiss();
        }
    }


    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        if (mDialog != null) {
            dismiss();

            mDialog = null;
            mActivity = null;
        }
    }

}

