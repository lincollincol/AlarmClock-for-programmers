package linc.com.alarmclockforprogrammers.data.mapper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import linc.com.alarmclockforprogrammers.data.entity.AchievementEntity;
import linc.com.alarmclockforprogrammers.domain.models.Achievement;

public class AchievementEntityMapper {

    public AchievementEntity toAchievementEntity(@NotNull Achievement achievement) {
        return new AchievementEntity(
                achievement.getId(),
                achievement.getAward(),
                achievement.getTasksToComplete(),
                achievement.getCompletedTasks(),
                achievement.getAchievementCondition(),
                achievement.getProgrammingLanguage(),
                achievement.isCompleted(),
                achievement.isAwardReceived()
        );
    }

    public AchievementEntity toAchievementEntity(@NotNull DataSnapshot dataSnapshot) {
        return new AchievementEntity(
                ((Long) (dataSnapshot.child("id").getValue())).intValue(),
                ((Long) (dataSnapshot.child("award").getValue())).intValue(),
                ((Long) (dataSnapshot.child("tasksToComplete").getValue())).intValue(),
                ((Long) (dataSnapshot.child("completedTasks").getValue())).intValue(),
                (String) (dataSnapshot.child("achievementTask").getValue()),
                (String) (dataSnapshot.child("language").getValue()),
                ((Boolean) (dataSnapshot.child("completed").getValue())),
                ((Boolean) (dataSnapshot.child("awardReceived").getValue())));
    }

    public Achievement toAchievement(@NotNull AchievementEntity achievementEntity) {
        final Achievement achievement = new Achievement();
        achievement.setId(achievementEntity.getId());
        achievement.setProgrammingLanguage(achievementEntity.getLanguage());
        achievement.setAward(achievementEntity.getAward());
        achievement.setCompletedTasks(achievementEntity.getCompletedTasks());
        achievement.setTasksToComplete(achievementEntity.getTasksToComplete());
        achievement.setAchievementCondition(achievementEntity.getAchievementCondition());
        achievement.setCompleted(achievementEntity.isCompleted());
        achievement.setAwardReceived(achievementEntity.isAwardReceived());
        return achievement;
    }

    public List<AchievementEntity> toAchievementEntitiesList(List<Achievement> achievements) {
        List<AchievementEntity> achievementEntities = new ArrayList<>();
        for(Achievement achievement : achievements) {
            achievementEntities.add(toAchievementEntity(achievement));
        }
        return achievementEntities;
    }

    public List<Achievement> toAchievementsList(List<AchievementEntity> achievementEntities) {
        List<Achievement> achievements = new ArrayList<>();
        for(AchievementEntity achievementEntity : achievementEntities) {
            achievements.add(toAchievement(achievementEntity));
        }
        return achievements;
    }

}


