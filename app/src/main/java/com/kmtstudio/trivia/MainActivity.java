package com.kmtstudio.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kmtstudio.trivia.data.AnswerListAsyncResponse;
import com.kmtstudio.trivia.data.QuestionBank;
import com.kmtstudio.trivia.model.Question;
import com.kmtstudio.trivia.model.Score;
import com.kmtstudio.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView, questionCounterTextView, scoreTxtView, highestScore;
    private Button trueBtn, falseBtn;
    private ImageButton prvBtn, nxtBtn;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        score = new Score(); //Score object

        prefs = new Prefs(this); //Shared preference


        questionTextView = findViewById(R.id.question_textview);
        questionCounterTextView = findViewById(R.id.counter_text);
        scoreTxtView = findViewById(R.id.score_text_view);
        highestScore = findViewById(R.id.highest_score);

        prvBtn = findViewById(R.id.prev_btn);
        nxtBtn = findViewById(R.id.next_btn);
        trueBtn = findViewById(R.id.true_btn);
        falseBtn = findViewById(R.id.false_btn);


        prvBtn.setOnClickListener(this);
        nxtBtn.setOnClickListener(this);
        trueBtn.setOnClickListener(this);
        falseBtn.setOnClickListener(this);


        scoreTxtView.setText(MessageFormat.format("Current Score: {0} ", String.valueOf(score.getScore())));

        //get previous state
        currentQuestionIndex = prefs.getState();
        Log.d("State", "onCreate: "+prefs.getState());

        highestScore.setText(MessageFormat.format("Highest Score : {0}", String.valueOf(prefs.getHighScore())));


        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionArrayList.size()));
                Log.d("inside", "processFinished: " + questionArrayList);
            }
        });


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.prev_btn) {

            if (currentQuestionIndex > 0) {
                currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                updateQuestion();
            }

        } else if (v.getId() == R.id.next_btn) {

            goNext();

        } else if (v.getId() == R.id.true_btn) {

            checkAnswer(true);
            Log.d("eer", "onClick:  ee");
            updateQuestion();

        } else if (v.getId() == R.id.false_btn) {

            checkAnswer(false);
            Log.d("eer", "onClick:  ee");
            updateQuestion();
        }

    }

    private void checkAnswer(boolean userChooseCorrect) {

        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageID = 0;

        if (userChooseCorrect == answerIsTrue) {
            fadeView();
            addPoints();
            toastMessageID = R.string.correct_answer;


        } else {
            shakeAnimation();
            deductPoints();
            toastMessageID = R.string.wrong_answer;
        }

        Toast.makeText(getApplicationContext(), toastMessageID, Toast.LENGTH_SHORT).show();
    }


    private void updateQuestion() {

        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size()));
    }


    private void addPoints() {

        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreTxtView.setText(MessageFormat.format("Current Score: {0} ", String.valueOf(score.getScore())));
        //Log.d("Score", "addPoints:  " + score.getScore());
    }


    private void deductPoints() {

        scoreCounter -= 100;

        if (scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreTxtView.setText(MessageFormat.format("Current Score: {0} ", String.valueOf(score.getScore())));
        } else {

            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTxtView.setText(MessageFormat.format("Current Score: {0} ", String.valueOf(score.getScore())));
            //Log.d("Score Decrease", "deductPoints:  " + score.getScore());
        }

    }


    private void fadeView() {

        CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {

        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake_animation);

        final CardView cardView = findViewById(R.id.cardView);

        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void goNext() {

        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }


    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}