package linc.com.alarmclockforprogrammers.ui.achievements;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import linc.com.alarmclockforprogrammers.AlarmApp;
import linc.com.alarmclockforprogrammers.R;
import linc.com.alarmclockforprogrammers.data.entity.AchievementEntity;
import linc.com.alarmclockforprogrammers.data.database.AppDatabase;
import linc.com.alarmclockforprogrammers.data.preferences.PreferencesAlarm;
import linc.com.alarmclockforprogrammers.domain.interactor.achievements.InteractorAchievements;
import linc.com.alarmclockforprogrammers.data.repository.RepositoryAchievements;
import linc.com.alarmclockforprogrammers.ui.activities.main.MainActivity;
import linc.com.alarmclockforprogrammers.ui.achievements.adapters.AdapterAchievements;
import linc.com.alarmclockforprogrammers.ui.base.BaseFragment;

import static linc.com.alarmclockforprogrammers.utils.Consts.DISABLE;

public class FragmentAchievements extends BaseFragment implements
        AdapterAchievements.OnReceiveClickListener, ViewAchievements {

    private TextView balance;

    private AdapterAchievements adapter;
    private PresenterAchievements presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase database = AlarmApp.getInstance().getDatabase();

        if(presenter == null) {
            presenter = new PresenterAchievements(this,
                    new InteractorAchievements(new RepositoryAchievements(database.achievementsDao()),
                    new PreferencesAlarm(getActivity()))
            );
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        Toolbar toolbar = view.findViewById(R.id.achievements__toolbar);
        RecyclerView achievementsList = view.findViewById(R.id.achievements__list_of_achievements);
        this.balance = view.findViewById(R.id.achievements__balance);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        SnapHelper snapHelper = new LinearSnapHelper();
        this.adapter = new AdapterAchievements(this);

        snapHelper.attachToRecyclerView(achievementsList);
        achievementsList.setLayoutManager(layoutManager);
        achievementsList.setAdapter(adapter);

        toolbar.setNavigationOnClickListener(v -> presenter.returnToAlarms());

        this.presenter.setData();

        return view;
    }

    @Override
    public void onClick(int position) {
        this.presenter.receiveAward(position);
    }

    @Override
    public void disableDrawerMenu() {
        ((MainActivity) getActivity()).setDrawerEnabled(DISABLE);
    }

    @Override
    public void setAchievements(List<AchievementEntity> achievements) {
        this.adapter.setAchievements(achievements);
    }

    @Override
    public void setBalance(int balance) {
        this.balance.setText(String.valueOf(balance));
    }

    @Override
    public void openAlarmsFragment() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        this.presenter.returnToAlarms();
    }
}
