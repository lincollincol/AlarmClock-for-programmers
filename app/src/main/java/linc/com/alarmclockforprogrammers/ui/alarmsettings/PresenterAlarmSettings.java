package linc.com.alarmclockforprogrammers.ui.alarmsettings;

import android.util.Log;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import linc.com.alarmclockforprogrammers.domain.interactor.alarmsettings.InteractorAlarmSettings;
import linc.com.alarmclockforprogrammers.domain.model.Alarm;
import linc.com.alarmclockforprogrammers.ui.mapper.AlarmViewModelMapper;
import linc.com.alarmclockforprogrammers.ui.viewmodel.AlarmViewModel;
import linc.com.alarmclockforprogrammers.utils.PathUtil;
import linc.com.alarmclockforprogrammers.utils.ResUtil;

public class PresenterAlarmSettings {

    private ViewAlarmSettings view;
    private InteractorAlarmSettings interactor;
    private CompositeDisposable disposables;

    private AlarmViewModel alarmViewModel;
    private AlarmViewModelMapper mapper;
    private PathUtil pathUtil;

    public PresenterAlarmSettings(InteractorAlarmSettings interactor,
                                  AlarmViewModelMapper mapper,
                                  PathUtil pathUtil) {
        this.interactor = interactor;
        this.mapper = mapper;
        this.pathUtil = pathUtil;
        this.disposables = new CompositeDisposable();
    }

    void bind(ViewAlarmSettings view, int alarmId) {
        this.view = view;
        Disposable d = this.interactor.execute(alarmId)
                .subscribe(this::setAlarmData);
    }

    void unbind() {
        view = null;
        //todo interactor.dispose
    }

    void saveAlarm() {
        Disposable d = interactor.saveAlarm(mapper.toAlarm(alarmViewModel))
            .subscribe(view::openAlarmsFragment);
    }

    void cancelChanges() {
        view.openAlarmsFragment();
    }

    /**
     * Time pickers
     */
    void hourSelected(int hour) {
        this.alarmViewModel.setHour(hour);
    }

    void minuteSelected(int minute) {
        this.alarmViewModel.setMinute(minute);
    }

    /**
     * Other settings
     */
    void labelEntered(String label) {
        this.alarmViewModel.setLabel(label);
    }

    void selectDays() {
        view.showDaysSelectionDialog(ResUtil.Array.WEEKDAYS.getArray(), alarmViewModel.getSelectedDays());
    }

    void selecdSong() {
        view.openFileManager();
    }

    void daysSelected(boolean[] days) {
        this.alarmViewModel.setSelectedDays(days);
        view.showWeekdays(alarmViewModel.getWeekdayMarks(ResUtil.Array.WEEKDAYS_MARKS.getArray()));
    }

    void songSelected(String path) {
        File song = new File(path);
        Log.d("PATH", "songSelected: " + song.getAbsolutePath());
        pathUtil.getPath(song);
        //todo permission
        view.showPermissionRequest();
        alarmViewModel.setSongPath(path);
        view.showAlarmSong(song.getName());
    }

    void alarmEnable(boolean enable) {
        this.alarmViewModel.setEnable(enable);
    }

    /**
     * Task settings
     */
    void taskEnable(boolean enable) {
        this.alarmViewModel.setContainsTask(enable);
        view.showTaskSettings(enable);
    }

    void selectDifficult() {
        view.showDifficultSelectionDialog(ResUtil.Array.DIFFICULT.getArray(),
                alarmViewModel.getDifficultPosition());
    }

    void selectLanguage() {
        view.showLanguageSelectionDialog(ResUtil.Array.LANGUAGES.getArray(),
                alarmViewModel.getLanguagePosition());
    }

    void difficultSelected(int difficultPosition) {
        this.alarmViewModel.setDifficultPosition(difficultPosition);
        view.showDifficult(ResUtil.Array.DIFFICULT.getItem(difficultPosition));
    }

    void languageSelected(int languagePosition) {
        this.alarmViewModel.setLanguagePosition(languagePosition);
        view.showLanguage(ResUtil.Array.LANGUAGES.getItem(languagePosition));
    }

    private void setAlarmData(Alarm alarm) {
        this.alarmViewModel = mapper.toAlarmViewModel(alarm);
        this.view.showTime(alarmViewModel.getHour(), alarmViewModel.getMinute());
        this.view.showAlarmSong(alarmViewModel.getSongPath());
        this.view.showTaskState(alarmViewModel.isContainsTask());
        this.view.showEnableState(alarmViewModel.isEnable());
        this.view.showWeekdays(alarmViewModel.getWeekdayMarks(ResUtil.Array.WEEKDAYS_MARKS.getArray()));
        this.view.showDifficult(ResUtil.Array.DIFFICULT.getItem(alarmViewModel.getDifficultPosition()));
        this.view.showLanguage(ResUtil.Array.LANGUAGES.getItem(alarmViewModel.getLanguagePosition()));
    }

    void backToAlarms() {
        view.openAlarmsFragment();
    }

}