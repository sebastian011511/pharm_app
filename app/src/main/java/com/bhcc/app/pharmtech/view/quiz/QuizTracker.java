package com.bhcc.app.pharmtech.view.quiz;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.bhcc.app.pharmtech.view.navigation.ReplaceFragmentCommand;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nils on 11/28/2017.
 */

public class QuizTracker implements Serializable
{
    // to show specific information about this quiz
    private ArrayList<Double> scoreList;

    // to take quiz again
    private String[] topicList;
    private String[] fieldList;
    private int numOfQuestions;

    public QuizTracker(String[] topicList, String[] fieldList, int numOfQuestions)
    {
        this.topicList = topicList;
        this. fieldList = fieldList;
        this.numOfQuestions = numOfQuestions;
    }
    public void startQuiz(FragmentActivity currentActivity)
    {
        QuizMultipleChoiceFragment fragment = QuizMultipleChoiceFragment
                .newInstance(topicList, fieldList, numOfQuestions);
        ReplaceFragmentCommand
                .startNewFragment(currentActivity, fragment, true);
    }

    public Double getAverageScore()
    {
        double total = 0;

        for(int i = 0; i < scoreList.size(); i++)
        {
            total += scoreList.get(i);
        }

        return total/scoreList.size();
    }

    public int getNum
}
