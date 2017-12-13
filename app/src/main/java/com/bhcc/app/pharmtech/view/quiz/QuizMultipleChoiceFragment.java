package com.bhcc.app.pharmtech.view.quiz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.MedicineSchema;
import com.bhcc.app.pharmtech.data.model.Medicine;
import com.bhcc.app.pharmtech.view.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizMultipleChoiceFragment extends Fragment
{
    private static final String TAG = "QuizMultipleChoiceFrag";

    // Bundle argument id
    private static final String EXTRA_TOPIC_LIST = "extra: topic list";
    private static final String EXTRA_FIELD_LIST = "extra: field list";
    private static final String EXTRA_NUM_QUIZ = "extra: num quiz";
    private static final String EXTRA_QUIZ_TRACKER = "extra: quiz tracker";
    private static final String EXTRA_IS_RETAKE = "extra: is retake";

    // static usage
    private final static int NUM_CHOICE = 4;
    // for each instance
    private int numQuiz;
    private int index = 0;
    private int done = 0;
    private int correct = 0;

    // Lists
    private String[] topicList;
    private String[] fieldList;
    private List<Medicine> medicines;
    private List<Medicine> allMedicines;

    // Views & Widgets
    private LinearLayout mLinearLayout;
    private LinearLayout mSubmitButtonLayout;
    private TextView mScoreTextView;
    private TextView mQuestion;
    private Button[] mSubmitButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView[] tvQuestion = null;
    private RadioGroup[] rgChoices = null;
    private RadioButton[][] rbChoice1 = null;

    // Keeping track variables
    private int[] correctChoice;
    private String[] strQuestion = null;
    private String[] strAnswer = null;
    private ArrayList<Integer> indexOfSubmittedQuestionList;
    boolean[] isViewCreated = null;

    // File name
    private String outPutFileName;
    private String trackerFileName;

    // to be saved and hold things about each quiz
    private QuizTracker tracker;

    // flags if operations to create new file need to happen or not
    private boolean isRetake;

    /**
     * To create a fragment w/ bundle arguments
     *
     * @param topicList
     * @param fieldList
     * @param numQuiz
     * @return QuizMultipleChoiceFragment
     */
    public static QuizMultipleChoiceFragment newInstance(String[] topicList, String[] fieldList, int numQuiz, QuizTracker tracker, boolean isRetake)
    {
        Bundle bundle = new Bundle();

        bundle.putStringArray(EXTRA_TOPIC_LIST, topicList);
        bundle.putStringArray(EXTRA_FIELD_LIST, fieldList);
        bundle.putInt(EXTRA_NUM_QUIZ, numQuiz);
        bundle.putSerializable(EXTRA_QUIZ_TRACKER, tracker);
        bundle.putBoolean(EXTRA_IS_RETAKE, isRetake);

        QuizMultipleChoiceFragment quizFragment = new QuizMultipleChoiceFragment();
        quizFragment.setArguments(bundle);

        return quizFragment;
    }

    /**
     * To set up views & widgets
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_quiz, container, false);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.quiz_activity_linear_layout);
        mSubmitButtonLayout = (LinearLayout) view.findViewById(
                R.id.quiz_activity_linear_layout_submit_button);
        mScoreTextView = (TextView) view.findViewById(R.id.score_quiz);
        mQuestion = (TextView) view.findViewById(R.id.question_quiz);
        mNextButton = (ImageButton) view.findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) view.findViewById(R.id.previous_button);
        return view;

    }

    /**
     * To create a review file for the quiz
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        topicList = getArguments().getStringArray(EXTRA_TOPIC_LIST);
        fieldList = getArguments().getStringArray(EXTRA_FIELD_LIST);
        numQuiz = getArguments().getInt(EXTRA_NUM_QUIZ, 0);
        tracker = (QuizTracker) getArguments().getSerializable(EXTRA_QUIZ_TRACKER);
        isRetake = getArguments().getBoolean(EXTRA_IS_RETAKE);

        // if its the first time user is taking quiz, and a quizName.txt and quizName.dat file to the file 'REVIEW_FILE'
        if(!isRetake)
        {
            // get the file that holds the index for files corresponding to quizzes taken
            File reviewInfo = new File(getActivity().getFilesDir(), MainActivity.REVIEW_FILE);

            // create entries in REVIEW_FILE.txt that reference this tracker, and this .txt output
            try
            {
                PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(reviewInfo, true)));
                outPutFileName = tracker.getTitle() + ".txt";
                trackerFileName = tracker.getTitle() + ".dat";
                printWriter.append(outPutFileName + "\n");
                printWriter.append(trackerFileName + "\n");
                printWriter.close();
                Log.d(TAG, "wrote file names to REVIEW_FILE");
            }
            catch (Exception ex)
            {
                Log.e(TAG, "REVIEW_FILE not updated", ex);
            }
        }
        // otherwise, REVIEW_FILE knows about the .txt and the .dat files. Get them from Tracker object
        else
        {
            outPutFileName = tracker.getTitle() + ".txt";
            trackerFileName = tracker.getTitle() + ".dat";

            Log.d(TAG, "User is retaking " + tracker.getTitle());
            Log.d(TAG, "User has taken this quiz " + tracker.getNumTimesTaken() + " and is about to take it again");
            Log.d(TAG, "Currently, their average is " + tracker.getAverageScore());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    /**
     * To set up variables & views
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setUpView();
    }

    /**
     * To lock the orientation
     */
    @Override
    public void onResume()
    {
        super.onResume();
    }

    /**
     * To unlock the orientation
     */
    @Override
    public void onPause()
    {
        super.onPause();
        saveToFile();
    }

    /**
     * To set up views & widgets
     */
    private void setUpView()
    {
        // initialize variables for this quiz
        index = 0;
        done = 0;
        correct = 0;

        // get all medicines
        allMedicines = MedicineLab.get(getActivity()).getSpecificMedicines(null, null, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);

        // set up lists
        correctChoice = new int[numQuiz];

        mSubmitButton = new Button[numQuiz];

        tvQuestion = new TextView[numQuiz];
        strQuestion = new String[numQuiz];
        strAnswer = new String[numQuiz];
        rgChoices = new RadioGroup[numQuiz];
        rbChoice1 = new RadioButton[NUM_CHOICE][numQuiz];

        indexOfSubmittedQuestionList = new ArrayList<>();

        // set up boolean flags to false
        isViewCreated = new boolean[numQuiz];
        for (int i = 0; i < isViewCreated.length; i++)
        {
            isViewCreated[i] = false;
        }

        // set up widgets
        mScoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        medicines = findMedicinesQuiz(topicList);
        Collections.shuffle(medicines);
        medicines = medicines.subList(0, numQuiz);
        Log.i("test1", String.valueOf(medicines.size()));

        for (Medicine medicine : medicines)
        {
            Log.i("test1", medicine.getGenericName());
        }
        for (int i = 0; i < fieldList.length; i++)
        {
            Log.i("test1", fieldList[i]);
        }

        ///////// Next button //////////
        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                index = (index + 1) % numQuiz;
                updateUI();
            }
        });

        ///////// Previous //////////
        mPreviousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (index == 0)
                    index = numQuiz;
                index = (index - 1) % numQuiz;
                updateUI();
            }
        });

        // Update UI
        updateUI();
    }

    /**
     * To update UI
     */
    private void updateUI()
    {
        // update scores
        //mDrugNameTextView.setText(medicines.get(index).getGenericName());

        String results="Done: " + done + "/" + numQuiz
                + "\t\t\t" + "Correct: " + correct;
        mScoreTextView.setText(results);

        // Clear old views
        mSubmitButtonLayout.removeAllViews();

        mLinearLayout.removeAllViews();

        // Add a question to the view
        mLinearLayout.addView(mQuestion);

        // if a view is not yet created, create a new view
        if (!isViewCreated[index])
        {

            // To random number
            Random randomNumber = new Random();

            /////////////////// QUESTION /////////////////////////
            // random the question
            int randomField = Math.abs(randomNumber.nextInt() % fieldList.length);
            Log.i("test1", String.valueOf(randomField));


            /**Using string format to set up question on quiz
             *3 parameters for string:1st get string ID, 2nd pass the study topic, 3rd get brand name
             *
             * 2nd parameter.  If the study topic is "Special" category, then add "instructions" to
             * to the string so that it says "what is special instructions for DRUGNAME?
             * else leave regular string.*/
            strQuestion[index] = getString(R.string.quiz_question,
                    fieldList[randomField] == MedicineSchema.MedicineTable.Cols.SPECIAL ?
                            fieldList[randomField] + " instructions" : fieldList[randomField],
                    medicines.get(index).getGenericName(), medicines.get(index).getBrandName());


            /**2nd attempt at formating question if 1st doesnt work*/
            /*strQuestion[index]=getString(R.string.quiz_question,fieldList[randomField],
                    medicines.get(index).getGenericName(),medicines.get(index).getBrandName());
            */
            mQuestion.setText(strQuestion[index]);


            /////////////////// CHOICES /////////////////////////
            // random correct choice
            int randomCorrectChoice = Math.abs(randomNumber.nextInt() % NUM_CHOICE);
            correctChoice[index] = randomCorrectChoice;

            // radio group
            rgChoices[index] = new RadioGroup(getActivity());

            // radio button w/ correct answer
            rbChoice1[randomCorrectChoice][index] = new RadioButton(getActivity());

            // add correct choice
            ArrayList<String> choices = new ArrayList<>();
            String temp1 = "";
            switch (fieldList[randomField])
            {
                case MedicineSchema.MedicineTable.Cols.PURPOSE:
                    temp1 = medicines.get(index).getPurpose();
                    break;
                case MedicineSchema.MedicineTable.Cols.DEASCH:
                    temp1 = medicines.get(index).getDeaSch();
                    break;
                case MedicineSchema.MedicineTable.Cols.SPECIAL:
                    temp1 = medicines.get(index).getSpecial();
                    break;
                case MedicineSchema.MedicineTable.Cols.CATEGORY:
                    temp1 = medicines.get(index).getCategory();
                    break;
            }
            if (temp1.equals("") || temp1.equals("-"))
            {
                temp1 = "N/A";
            }
            rbChoice1[randomCorrectChoice][index].setText(temp1);
            rbChoice1[randomCorrectChoice][index].setId(randomCorrectChoice);
            rbChoice1[randomCorrectChoice][index].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            choices.add(temp1);

            // add random choices
            for (int i = 0; i < NUM_CHOICE; i++)
            {
                String temp2 = "";
                if (i != randomCorrectChoice)
                {
                    rbChoice1[i][index] = new RadioButton(getActivity());
                    do
                    {
                        int randomIndex = Math.abs(randomNumber.nextInt() % allMedicines.size());
                        Medicine medicine = allMedicines.get(randomIndex);
                        switch (fieldList[randomField])
                        {
                            case MedicineSchema.MedicineTable.Cols.PURPOSE:
                                temp2 = medicine.getPurpose();
                                break;
                            case MedicineSchema.MedicineTable.Cols.DEASCH:
                                temp2 = medicine.getDeaSch();
                                break;
                            case MedicineSchema.MedicineTable.Cols.SPECIAL:
                                temp2 = medicine.getSpecial();
                                break;
                            case MedicineSchema.MedicineTable.Cols.CATEGORY:
                                temp2 = medicine.getCategory();
                                break;
                        }
                        if (temp2.equals("") || temp2.equals("-"))
                        {
                            temp2 = "N/A";
                        }
                        rbChoice1[i][index].setText(temp2);
                    }
                    while (choices.contains(temp2));
                    rbChoice1[i][index].setId(i);

                    rbChoice1[i][index].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                    rgChoices[index].addView(rbChoice1[i][index]);
                    choices.add(temp2);
                } else
                {
                    rgChoices[index].addView(rbChoice1[randomCorrectChoice][index]);
                }
            }


            for (int i = 0; i < NUM_CHOICE; i++)
            {
                int position = Math.abs(randomNumber.nextInt() % NUM_CHOICE);
            }


            // add radio group
            mLinearLayout.addView(rgChoices[index]);


            ///////////// Submit Button ///////////////
            Button button = new Button(getActivity());
            mSubmitButton[index] = button;
            mSubmitButton[index].setText("Submit");
            mSubmitButton[index].setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    try
                    {
                        // add the index to the submit list
                        indexOfSubmittedQuestionList.add(index);

                        // find the checked radio button
                        int checkedId = rgChoices[index].getCheckedRadioButtonId();

                        // check if the answer is correct
                        // change the color
                        // update the score / done
                        if (checkedId == correctChoice[index])
                        {
                            mSubmitButton[index].setText("Correct");
                            strAnswer[index] = ((RadioButton) getView().findViewById(checkedId)).getText().toString();
                            correct++;
                        } else
                        {
                            mSubmitButton[index].setText("Incorrect");
                            ((RadioButton) getView().findViewById(checkedId)).setTextColor(Color.parseColor("#E22170"));
                            strAnswer[index] = ((RadioButton) getView().findViewById(checkedId)).getText().toString();
                        }
                        done++;
                        mSubmitButton[index].setEnabled(false);
                        rbChoice1[correctChoice[index]][index].setTextColor(Color.parseColor("#4CAF50"));

                        // disable all radio buttons in this question
                        for (int i = 0; i < NUM_CHOICE; i++)
                        {
                            rbChoice1[i][index].setEnabled(false);
                        }

                        String results="Done: " + done + "/" + numQuiz
                                + "\t\t\t" + "Correct: " + correct;
                        rgChoices[index].setEnabled(false);
                        mScoreTextView.setText(results);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getContext(), "Please choose one of the choices", Toast.LENGTH_SHORT).show();
                    }

                    if (done == numQuiz)
                    {
                        showSummaryDialog();
                    }
                }
            });

            mSubmitButtonLayout.addView(mSubmitButton[index]);

            Log.i("test1", "ViewCreated");
            isViewCreated[index] = true;
        } else
        {
            //mLinearLayout.addView(tvQuestion[index]);
            mQuestion.setText(strQuestion[index]);
            mLinearLayout.addView(rgChoices[index]);

            mSubmitButtonLayout.addView(mSubmitButton[index]);

        }

    }

    /**
     * to find medicines from the database
     *
     * @param topicList
     * @return List of medicines
     */
    private List<Medicine> findMedicinesQuiz(String[] topicList)
    {

        String whereArgs = "(";
        for (int i = 0; i < topicList.length; i++)
        {
            whereArgs += "?";
            if (i != topicList.length - 1)
                whereArgs += ",";
        }
        whereArgs += ")";

        List<Medicine> medicinesQuiz = MedicineLab.get(getActivity())
                .getSpecificMedicines("StudyTopic IN " + whereArgs, topicList, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);
        return medicinesQuiz;
    }

    /**
     * To convert from px to dp
     *
     * @param x
     * @return int dp
     */
    private int toDP(double x)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) x, getResources().getDisplayMetrics());
    }

    /**
     * To save the quiz to a review file
     */
    private void saveToFile()
    {
        // create file, or overwrite it
        File file = new File(getActivity().getFilesDir(), outPutFileName);
        try
        {
            PrintWriter printWriter = new PrintWriter(file);
            for (int i : indexOfSubmittedQuestionList)
            {
                printWriter.write(strQuestion[i] + "\n");
                printWriter.write("Your Answer: " + strAnswer[i] + "\n");
                printWriter.write("Correct Answer: " + (rbChoice1[correctChoice[i]][i]).getText() + "\n\n");
            }
            Log.d(TAG, "output of quiz written to " + outPutFileName);
            printWriter.close();
        }
        catch (Exception ex)
        {
        }

        try
        {
            FileOutputStream fileOutputStream = this.getActivity().openFileOutput(trackerFileName, Context.MODE_PRIVATE);
            ObjectOutputStream output = new ObjectOutputStream(fileOutputStream);
            output.writeObject(tracker);
            output.close();

            Log.d(TAG, "tracker object written to " + trackerFileName);
        }
        catch (Exception e)
        {

        }
    }

    /**
     * To Show dialog for the summary
     */
    private void showSummaryDialog()
    {
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.score_summary_dialog);

        double percentage = (correct * 100.0 / numQuiz);
        Log.i("test", String.valueOf(percentage));

        // now that we calculated the percentage, give that to the tracker.
        tracker.addScore(percentage);

        TextView tvScorePercentage = (TextView) dialog.findViewById(R.id.score_percentage);
        tvScorePercentage.setText(String.valueOf((int) percentage) + "%");

        TextView tvCorrectPoints = (TextView) dialog.findViewById(R.id.correct_score);
        tvCorrectPoints.setText(String.valueOf(correct));

        TextView tvWrongPoints = (TextView) dialog.findViewById(R.id.wrong_score);
        tvWrongPoints.setText(String.valueOf(numQuiz - correct));

        TextView tvOK = (TextView) dialog.findViewById(R.id.score_summary_ok_button);
        tvOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}