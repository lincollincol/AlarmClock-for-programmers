package linc.com.alarmclockforprogrammers.ui.fragments.alarms.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import linc.com.alarmclockforprogrammers.model.data.Alarm;
import linc.com.alarmclockforprogrammers.R;

public class AdapterAlarms extends RecyclerView.Adapter<AdapterAlarms.AlarmsHolder> {

    private List<Alarm> alarms;
    private Context context;
    private OnAlarmSelected onAlarmSelected;

    public AdapterAlarms(OnAlarmSelected onAlarmSelected, Context context) {
        this.onAlarmSelected = onAlarmSelected;
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
        return new AlarmsHolder(view, onAlarmSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmsHolder alarmsHolder, int i) {
        alarmsHolder.programmingLanguage.setText(alarms.get(i).getLanguage());
        alarmsHolder.time.setText(alarms.get(i).getTime());
        alarmsHolder.days.setText(alarms.get(i).getDay());

        if(alarms.get(i).isEnable()) {
            alarmsHolder.layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.NONAME_3));
        }
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
        private OnAlarmSelected alarmSelected;

        public AlarmsHolder(@NonNull View itemView, OnAlarmSelected alarmSelected) {
            super(itemView);
            this.programmingLanguage = itemView.findViewById(R.id.item_alarm__programming_language);
            this.time = itemView.findViewById(R.id.item_alarm__time);
            this.days = itemView.findViewById(R.id.item_alarm__days);
            this.layout = itemView.findViewById(R.id.layout_alarm);
            this.alarmSelected = alarmSelected;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            alarmSelected.onSelected(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            alarmSelected.onHold(getAdapterPosition());
            return false;
        }
    }

    public interface OnAlarmSelected {
        void onSelected(int position);
        void onHold(int position);
    }
}