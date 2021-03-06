package linc.com.alarmclockforprogrammers.ui.adapters;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import linc.com.alarmclockforprogrammers.R;
import linc.com.alarmclockforprogrammers.ui.uimodels.AlarmUiModel;
import linc.com.alarmclockforprogrammers.utils.ResUtil;

public class AdapterAlarms extends RecyclerView.Adapter<AdapterAlarms.AlarmsHolder> {

    private Map<Integer, AlarmUiModel> alarms;
    private List<Integer> keys;
    private OnAlarmClicked onAlarmClicked;

    public AdapterAlarms(OnAlarmClicked onAlarmClicked) {
        this.onAlarmClicked = onAlarmClicked;
        this.alarms = new LinkedHashMap<>();
        keys = new ArrayList<>();
    }

    public void setAlarms(Map<Integer, AlarmUiModel> alarms) {
        this.alarms.clear();
        this.keys.clear();
        this.alarms.putAll(alarms);
        this.keys.addAll(alarms.keySet());
        notifyDataSetChanged();
    }

    public void removeAlarm(int id) {
        int keyPosition = keys.indexOf(id);
        alarms.remove(id);
        keys.remove(keyPosition);
        notifyItemRemoved(keyPosition);
    }

    public void updateAlarm(AlarmUiModel alarmUiModel) {
        alarms.put(alarmUiModel.getId(), alarmUiModel);
        notifyItemChanged(keys.indexOf(alarmUiModel.getId()));
    }

    @NonNull
    @Override
    public AlarmsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alarm, viewGroup, false);
        return new AlarmsHolder(view, onAlarmClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmsHolder alarmsHolder, int i) {
        alarmsHolder.setAlarm(alarms.get(keys.get(i)));
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    class AlarmsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView time;
        private TextView days;
        private View enableIndicator;
        private OnAlarmClicked alarmClicked;

        public AlarmsHolder(@NonNull View itemView, OnAlarmClicked alarmClicked) {
            super(itemView);
            this.time = itemView.findViewById(R.id.item_alarm__time);
            this.days = itemView.findViewById(R.id.item_alarm__days);
            this.enableIndicator = itemView.findViewById(R.id.item_alarm__enable_indicator);
            this.alarmClicked = alarmClicked;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            alarmClicked.onAlarmClicked(keys.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            alarmClicked.onHold(keys.get(getAdapterPosition()));
            return false;
        }

        public void setAlarm(AlarmUiModel alarm) {
            GradientDrawable background = (GradientDrawable) enableIndicator.getBackground();
            background.setColor(alarm.isEnable() ?
                    ResUtil.Color.ACTIVE.getColor() : ResUtil.Color.NOT_ACTIVE.getColor());
            this.time.setText(alarm.getTime());
            this.days.setText(alarm.getWeekdayMarks(ResUtil.Array.WEEKDAYS_MARKS.getArray()));
        }

    }

    public interface OnAlarmClicked {
        void onAlarmClicked(int id);
        void onHold(int id);
    }
}
