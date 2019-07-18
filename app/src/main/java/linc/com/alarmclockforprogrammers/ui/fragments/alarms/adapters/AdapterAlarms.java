package linc.com.alarmclockforprogrammers.ui.fragments.alarms.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import linc.com.alarmclockforprogrammers.model.data.database.alarms.Alarm;
import linc.com.alarmclockforprogrammers.R;

public class AdapterAlarms extends RecyclerView.Adapter<AdapterAlarms.AlarmsHolder> {

    private List<Alarm> alarms;
    private Context context;
    private OnAlarmClicked onAlarmClicked;

    public AdapterAlarms(OnAlarmClicked onAlarmClicked, Context context) {
        this.onAlarmClicked = onAlarmClicked;
        this.context = context;
        this.alarms = new ArrayList<>();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms.clear();
        this.alarms.addAll(alarms);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alarm, viewGroup, false);
        return new AlarmsHolder(view, onAlarmClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmsHolder alarmsHolder, int i) {
        alarmsHolder.setAlarm(alarms.get(i));

//        if(alarms.get(i).isEnable()) {
//            alarmsHolder.layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.NONAME_3));
//        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }



    class AlarmsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView programmingLanguage;
        private TextView time;
        private TextView days;
        private CardView layout;
        private OnAlarmClicked alarmClicked;

        public AlarmsHolder(@NonNull View itemView, OnAlarmClicked alarmClicked) {
            super(itemView);
            this.programmingLanguage = itemView.findViewById(R.id.item_alarm__programming_language);
            this.time = itemView.findViewById(R.id.item_alarm__time);
            this.days = itemView.findViewById(R.id.item_alarm__days);
            this.layout = itemView.findViewById(R.id.layout_alarm);
            this.alarmClicked = alarmClicked;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            alarmClicked.onAlarmClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            alarmClicked.onHold(getAdapterPosition());
            return false;
        }

        public void setAlarm(Alarm alarm) {
            this.programmingLanguage.setText(getProgrammingsLanguage(alarm.getLanguage()));
            this.time.setText((alarm.getHour()+":"+alarm.getMinute()));
            this.days.setText(getDaysMarks(alarm));
        }

        private String getDaysMarks(Alarm alarm) {
            final String[] weekDays = context.getResources().getStringArray(R.array.week_days_marks);
            StringBuilder marks = new StringBuilder();

            for(int i = 0; i < alarm.getDays().length(); i++) {
                int day = Character.getNumericValue(alarm.getDays().charAt(i)) - 1;
                marks.append(weekDays[day]);
                marks.append((i == (alarm.getDays().length()-1) ? "" : ", "));
            }
            return marks.toString();
        }

        private String getDifficultMode(int position) {
            String[] difficultModes = context.getResources().getStringArray(R.array.difficult_modes);
            return difficultModes[position];
        }

        private String getProgrammingsLanguage(int position) {
            String[] language = context.getResources().getStringArray(R.array.programming_languages);
            return language[position];
        }
    }

    public interface OnAlarmClicked {
        void onAlarmClicked(int position);
        void onHold(int position);
    }
}
