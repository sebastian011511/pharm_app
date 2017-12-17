package com.bhcc.app.pharmtech.view.quiz;

import android.support.v4.app.FragmentActivity;
import com.bhcc.app.pharmtech.view.navigation.ReplaceFragmentCommand;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Nils
 *
 * This class holds the parameters of a quiz, and the methods to take it again.
 * It can be written to a file, as an object, to save and reload it.
 */

public class QuizTracker implements Serializable
{
    // to show specific information about this quiz
    private ArrayList<Double> scoreList = new ArrayList<>(); // holds a list of scores for each time take

    // to take quiz again
    private String[] topicList;
    private String[] fieldList;
    private int numOfQuestions;
    private String title;

    /**
     * @param topicList List of topics covered in quiz.
     * @param fieldList list of fields covered in quiz.
     * @param numOfQuestions number of questions  in quiz.
     * @param title title to identify the quiz so user can retake it.
     */
    public QuizTracker(String[] topicList, String[] fieldList, int numOfQuestions, String title)
    {
        this.topicList = topicList;
        this. fieldList = fieldList;
        this.numOfQuestions = numOfQuestions;
        this.title = title;
    }

    public void startQuiz(FragmentActivity currentActivity)
    {
        QuizMultipleChoiceFragment fragment = QuizMultipleChoiceFragment.newInstance(topicList, fieldList, numOfQuestions, this, true);
        ReplaceFragmentCommand.startNewFragment(currentActivity, fragment, true);
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

    public int getNumTimesTaken()
    {
        return scoreList.size();
    }

    public void addScore(double score)
    {
        scoreList.add(score);
    }

    public void addScoreToLastIndex(double score)
    {
        scoreList.set(scoreList.size() - 1, score);
    }

    public String getTitle()
    {
        return this.title;
    }
}
