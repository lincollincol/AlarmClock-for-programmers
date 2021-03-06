package linc.com.alarmclockforprogrammers.ui.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import linc.com.alarmclockforprogrammers.AlarmApp;
import linc.com.alarmclockforprogrammers.R;
import linc.com.alarmclockforprogrammers.data.database.LocalDatabase;
import linc.com.alarmclockforprogrammers.data.mapper.AchievementEntityMapper;
import linc.com.alarmclockforprogrammers.data.mapper.AlarmEntityMapper;
import linc.com.alarmclockforprogrammers.data.mapper.QuestionEntityMapper;
import linc.com.alarmclockforprogrammers.data.preferences.LocalPreferencesManager;
import linc.com.alarmclockforprogrammers.domain.interactor.implementation.InteractorTestImpl;
import linc.com.alarmclockforprogrammers.data.repository.RepositoryTestImpl;
import linc.com.alarmclockforprogrammers.infrastructure.PlayerManager;
import linc.com.alarmclockforprogrammers.infrastructure.VibrationManagerImpl;
import linc.com.alarmclockforprogrammers.ui.activities.WakeActivity;
import linc.com.alarmclockforprogrammers.ui.views.ViewTest;
import linc.com.alarmclockforprogrammers.ui.mapper.QuestionViewModelMapper;
import linc.com.alarmclockforprogrammers.ui.presenters.PresenterTest;
import linc.com.alarmclockforprogrammers.ui.uimodels.QuestionUiModel;
import linc.com.alarmclockforprogrammers.utils.Consts;
import linc.com.alarmclockforprogrammers.utils.JsonUtil;

import static linc.com.alarmclockforprogrammers.utils.Consts.ANIMATION_END;
import static linc.com.alarmclockforprogrammers.utils.Consts.ANIMATION_START;
import static linc.com.alarmclockforprogrammers.utils.Consts.TWO_MINUTES;

public class FragmentTest extends Fragment implements ViewTest, View.OnClickListener,
        Animator.AnimatorListener, RadioGroup.OnCheckedChangeListener {

    private TextView completedTasks;
    private TextView balance;
    private TextView preQuestion;
    private TextView postQuestion;
    private RadioGroup answersGroup;
    private FloatingActionButton nextQuestion;
    private FloatingActionButton payForQuestion;
    private CodeView codeSnippet;
    private ObjectAnimator progressAnimation;
    private PresenterTest presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalDatabase database = AlarmApp.getInstance().getDatabase();

        if(presenter == null) {
            this.presenter = new PresenterTest(
                    new InteractorTestImpl(new RepositoryTestImpl(
                            database.questionsDao(),
                            database.alarmDao(),
                            database.achievementsDao(),
                            new LocalPreferencesManager(getActivity()),
                            new QuestionEntityMapper(new JsonUtil<>(new Gson())),
                            new AlarmEntityMapper(),
                            new AchievementEntityMapper()
                    ), new PlayerManager(getActivity()), new VibrationManagerImpl(getActivity())),
                    new QuestionViewModelMapper()
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        ProgressBar progressBar = view.findViewById(R.id.wake__time_for_answer);
        this.completedTasks = view.findViewById(R.id.wake__completed_tasks);
        this.balance = view.findViewById(R.id.wake__balance);
        this.preQuestion = view.findViewById(R.id.wake__pre_question);
        this.postQuestion = view.findViewById(R.id.wake__post_question);
        this.codeSnippet = view.findViewById(R.id.wake__code_snippet);
        this.answersGroup = view.findViewById(R.id.wake__answers_group);
        this.nextQuestion = view.findViewById(R.id.wake__next_question);
        this.payForQuestion = view.findViewById(R.id.wake__pay_for_answer);

        this.nextQuestion.setOnClickListener(this);
        this.payForQuestion.setOnClickListener(this);
        this.answersGroup.setOnCheckedChangeListener(this);

        this.progressAnimation = ObjectAnimator.ofInt(progressBar,
                getResources().getString(R.string.animator_property_progress), ANIMATION_END, ANIMATION_START);
        this.progressAnimation.addListener(this);
        this.progressAnimation.setDuration(TWO_MINUTES);
        this.progressAnimation.start();

        this.presenter.bind(this, getArguments().getInt(Consts.ALARM_ID));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.wake__next_question:
                // Get selected
                RadioButton radioButton = getView().findViewById(answersGroup.getCheckedRadioButtonId());
                // Check answer by position
                this.presenter.checkAnswer(answersGroup.indexOfChild(radioButton));
                break;
            case R.id.wake__pay_for_answer:
                this.presenter.showPaymentPrice();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        this.presenter.optionSelected();
    }

    @Override
    public void setCodeTheme(ColorTheme theme, String language) {
        codeSnippet.highlightCode(language)
                .setColorTheme(theme);
    }

    @Override
    public void showQuestion(QuestionUiModel question) {
        this.preQuestion.setText(question.getPreQuestion());
        this.postQuestion.setText(question.getPostQuestion());

        codeSnippet.setCodeContent(question.getHtmlCodeSnippet());

        this.answersGroup.clearCheck();
        // Reset radio buttons
        for(int i = 0; i < answersGroup.getChildCount(); i++) {
            RadioButton answer = (RadioButton)answersGroup.getChildAt(i);
            answer.setText(question.getAnswerOptions().get(i));
        }
    }

    @Override
    public void showBalance(int balance) {
        this.balance.setText(String.valueOf(balance));
    }

    @Override
    public void showCompletedTasks(String completedTasks) {
        this.completedTasks.setText(completedTasks);
    }

    @Override
    public void highlightAnswers(int position, int color) {
        RadioButton answer = (RadioButton) answersGroup.getChildAt(position);
        answer.setTextColor(color);
    }

    @Override
    public void disappearHighlight(int color) {
        for(int i = 0; i < answersGroup.getChildCount(); i++) {
            RadioButton answer = (RadioButton)answersGroup.getChildAt(i);
            answer.setTextColor(color);
        }
    }

    @Override
    public void setOptionsEnable(boolean enable) {
        for(int i = 0; i < answersGroup.getChildCount(); i++) {
            RadioButton answer = (RadioButton)answersGroup.getChildAt(i);
            answer.setEnabled(enable);
        }
    }

    @Override
    public void setNextEnable(boolean enable, @ColorInt int color) {
        this.nextQuestion.setEnabled(enable);
        this.nextQuestion.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void setPayEnable(boolean enable, @ColorInt int color) {
        this.payForQuestion.setEnabled(enable);
        this.payForQuestion.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void showPayDialog(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.AlertDialogStyle));
        dialogBuilder.setCancelable(false)
                .setTitle(R.string.title_dialog_skip_question)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_yes, (dialog, id) -> presenter.makePayment())
                .setNegativeButton(R.string.dialog_no, (dialog, id) -> dialog.cancel())
                .show();
    }

    @Override
    public void showFinishDialog(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.AlertDialogStyle));
        dialogBuilder.setCancelable(false)
                .setTitle(R.string.title_dialog_task)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_got_it, (dialog, id) -> presenter.finishAlarm())
                .show();
    }

    @Override
    public void startProgress() {
        this.progressAnimation.setCurrentPlayTime(ANIMATION_START);
        this.progressAnimation.start();
    }

    @Override
    public void pauseProgress() {
        this.progressAnimation.pause();
    }

    @Override
    public void closeActivity() {
        ((WakeActivity) getActivity()).finishTask();
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        this.presenter.timeOut();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unbind();
    }

    @Override
    public void onAnimationStart(Animator animation) {/** Not implemented*/}

    @Override
    public void onAnimationCancel(Animator animation) {/** Not implemented*/}

    @Override
    public void onAnimationRepeat(Animator animation) {/** Not implemented*/}
}
