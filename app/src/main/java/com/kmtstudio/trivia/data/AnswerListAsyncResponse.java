package com.kmtstudio.trivia.data;

import com.kmtstudio.trivia.model.Question;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {

    void processFinished(ArrayList<Question> questionArrayList);
}
