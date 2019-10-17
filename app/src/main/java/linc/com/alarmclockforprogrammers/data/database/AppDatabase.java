package linc.com.alarmclockforprogrammers.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import linc.com.alarmclockforprogrammers.data.entity.AchievementEntity;
import linc.com.alarmclockforprogrammers.data.entity.AlarmEntity;
import linc.com.alarmclockforprogrammers.data.entity.QuestionEntity;
import linc.com.alarmclockforprogrammers.data.database.achievements.AchievementsDao;
import linc.com.alarmclockforprogrammers.data.database.alarms.AlarmDao;
import linc.com.alarmclockforprogrammers.data.database.questions.QuestionsDao;

@Database(entities = {AlarmEntity.class, QuestionEntity.class, AchievementEntity.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AlarmDao alarmDao();
    public abstract QuestionsDao questionsDao();
    public abstract AchievementsDao achievementsDao();
    private static AppDatabase database;

    public static AppDatabase getDatabase(Context mContext){
        if(database == null){
            synchronized (AppDatabase.class) {
                if(database == null) {
                    database = Room.databaseBuilder(mContext, AppDatabase.class, "alarms_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return database;
    }
}
