package linc.com.alarmclockforprogrammers.data.database.questions;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import linc.com.alarmclockforprogrammers.data.entity.QuestionEntity;

@Dao
public interface QuestionsDao {

    @Query("SELECT * FROM questions")
    List<QuestionEntity> getAll();

    @Query("SELECT * FROM questions WHERE language = :language AND difficult = :difficult")
    List<QuestionEntity> getByParams(String language, int difficult);

    @Query("SELECT COUNT(*) FROM questions")
    int getItemCount();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(QuestionEntity question);

    @Update
    void update(QuestionEntity question);

}
