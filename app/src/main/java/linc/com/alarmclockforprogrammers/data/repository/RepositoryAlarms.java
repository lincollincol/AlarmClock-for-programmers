package linc.com.alarmclockforprogrammers.data.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import linc.com.alarmclockforprogrammers.data.entity.AlarmEntity;
import linc.com.alarmclockforprogrammers.data.entity.QuestionEntity;
import linc.com.alarmclockforprogrammers.data.database.alarms.AlarmDao;
import linc.com.alarmclockforprogrammers.data.database.questions.QuestionsDao;
import linc.com.alarmclockforprogrammers.data.mapper.AlarmEntityMapper;
import linc.com.alarmclockforprogrammers.data.preferences.PreferencesAlarm;
import linc.com.alarmclockforprogrammers.domain.model.Alarm;
import linc.com.alarmclockforprogrammers.utils.JsonUtil;
import linc.com.alarmclockforprogrammers.utils.callbacks.VersionUpdateCallback;

public class RepositoryAlarms {

    private AlarmDao alarmDao;
    private QuestionsDao questionsDao;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private PreferencesAlarm preferences;

    private JsonUtil<String> jsonUtil;
    private AlarmEntityMapper mapper;

    private Map<Integer, Alarm> alarms;

    public RepositoryAlarms(AlarmDao alarmDao,
                            QuestionsDao questionsDao,
                            PreferencesAlarm preferences,
                            JsonUtil<String> jsonUtil,
                            AlarmEntityMapper mapper) {
        this.alarmDao = alarmDao;
        this.questionsDao = questionsDao;
        this.preferences = preferences;
        this.jsonUtil = jsonUtil;
        this.mapper = mapper;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.alarms = new HashMap<>();
    }

    /** Alarms*/
    public Single<Map<Integer, Alarm>> getAlarms() {
        return Single.create((SingleOnSubscribe<Map<Integer, Alarm>>) emitter -> {
            List<AlarmEntity> alarmEntities = alarmDao.getAll();
            alarms.clear();
            for(AlarmEntity alarmEntity : alarmEntities) {
                this.alarms.put(alarmEntity.getId(), mapper.toAlarm(alarmEntity));
            }
            emitter.onSuccess(alarms);
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Alarm> getAlarm(int id) {
        return Single.fromCallable(() -> alarms.get(id));
    }

    public Completable deleteAlarm(Alarm alarm) {
        return Completable.fromAction(() -> {
                alarmDao.deleteAlarm(mapper.toAlarmEntity(alarm));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Completable updateAlarm(Alarm alarm) {
        return Completable.fromAction(() ->
            alarmDao.updateAlarm(mapper.toAlarmEntity(alarm))
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /** Questions*/
    public void updateLocalQuestionsVersion() {
        this.databaseReference = this.firebaseDatabase.getReference("questions_version");
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String remoteVersion = ((String)dataSnapshot.getValue());
                if(remoteVersion.equals(preferences.getLocalQuestionsVersion())) {
                    updateLocalQuestions();
                    preferences.saveLocalQuestionsVersion(remoteVersion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void updateLocalQuestions() {
        List<QuestionEntity> questions = new ArrayList<>();
        this.databaseReference = this.firebaseDatabase.getReference("questions");
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    questions.add(new QuestionEntity(
                                    ((Long) (ds.child("id").getValue())).intValue(),
                                    ((Long) (ds.child("difficult").getValue())).intValue(),
                                    ((Long) (ds.child("trueAnswerPosition").getValue())).intValue(),
                                    (String) (ds.child("programmingLanguage").getValue()),
                                    (String) (ds.child("preQuestion").getValue()),
                                    (String) (ds.child("postQuestion").getValue()),
                                    jsonUtil.listToJson((ArrayList<String>) (ds.child("answers").getValue())),
                                    (String) (ds.child("htmlCodeSnippet").getValue()),
                                    ((Boolean) (ds.child("completed").getValue()))));
                }
                Disposable d = updateQuestions(questions)
                        .subscribe(() -> {}, e -> e.printStackTrace());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {/** Not implemented*/}
        });
    }

    private Completable updateQuestions(List<QuestionEntity> questions) {
        Log.d("SIZE", "updateQuestions: " + questions.size());
        return Completable.fromAction(() -> {
            for(QuestionEntity q : questions) {
                try {
                    questionsDao.insert(q);
                }catch (Exception e) {
                    Log.d("ELEMENT EXIST", ""+q.getId() );
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> getBalance() {
        return Single.fromCallable(preferences::getBalance);
    }

}
