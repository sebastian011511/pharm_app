package com.bhcc.app.pharmtech.view.quiz;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.MedicineSchema;
import com.bhcc.app.pharmtech.data.model.Medicine;
import com.bhcc.app.pharmtech.view.MainActivity;
import com.bhcc.app.pharmtech.view.navigation.ReplaceFragmentCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SelectQuizFragment extends Fragment
{
    public static final String TAG = "SelectQuizFragment";

    //Static variables
    private static final int STUDY_TOPIC_CHECKED_LIST = 0;
    private static final int STUDY_FIELD_CHECKED_LIST = 1;

    // Views & Widgets
    private TextInputLayout amountOfQuestionTextInput;
    private EditText quizTitleEditText;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_select_quiz, container, false);
        return rootView;
    }

    /**
     * To set up views
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
    }

    /**
     * To set up views & widgets
     */
    protected void setUpView()
    {
        // Create a new linear layout
        LinearLayout studyTopicSection = (LinearLayout) rootView
                .findViewById(R.id.study_topics_layout);

        LinearLayout studyFieldsLayout = (LinearLayout) rootView
                .findViewById(R.id.study_fields_layout);

        amountOfQuestionTextInput = (TextInputLayout) rootView.findViewById(R.id.question_qty_text);
        final EditText amountOfQuestionEditText = (EditText) rootView.findViewById(R.id.quiz_question_qty);


        quizTitleEditText = (EditText) rootView.findViewById(R.id.quiz_title_input);

        /**
         * Configure checkboxes for study Topics
         */

        final ArrayList<AppCompatCheckBox> studyTopicCheckBox = new ArrayList<>();

        final MedicineLab medicineLab = MedicineLab.get(getContext());
        List<String> studyTopic = medicineLab.getStudyTopics();
        Log.i("test", studyTopic.toString());

        for (int i = 0; i < studyTopic.size(); i++)
        {
            String topic = studyTopic.get(i);
            AppCompatCheckBox checkBox = new AppCompatCheckBox(getContext());
            checkBox.setText(topic);
            studyTopicCheckBox.add(checkBox);
        }

        // to hold checked study topics
        final ArrayList<String> studyTopicCheckedList = new ArrayList<>();

        /**
         * Set click listeners
         */
        for (int i = 0; i < studyTopicCheckBox.size(); i++)
        {
            studyTopicCheckBox.get(i).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onCheckboxClicked((AppCompatCheckBox) v, studyTopicCheckedList,
                            STUDY_TOPIC_CHECKED_LIST);
                }
            });
            studyTopicSection.addView(studyTopicCheckBox.get(i));
        }


        /**
         * Configure checkboxes for study Fields
         */

        // to hold checked study fields
        final ArrayList<String> studyFieldCheckedList = new ArrayList<>();
        // new checkboxes for study fields
        final ArrayList<AppCompatCheckBox> studyFieldCheckBox = new ArrayList<>();
        {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(getContext());
            checkBox.setText(MedicineSchema.MedicineTable.Cols.DEASCH);
            checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onCheckboxClicked((AppCompatCheckBox) v, studyFieldCheckedList,
                            STUDY_FIELD_CHECKED_LIST);
                }
            });
            studyFieldCheckBox.add(checkBox);
        }
        {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(getContext());
            checkBox.setText(MedicineSchema.MedicineTable.Cols.PURPOSE);
            checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onCheckboxClicked((AppCompatCheckBox) v, studyFieldCheckedList,
                            STUDY_FIELD_CHECKED_LIST);
                }
            });
            studyFieldCheckBox.add(checkBox);
        }
        {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(getContext());
            checkBox.setText(MedicineSchema.MedicineTable.Cols.SPECIAL);
            checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onCheckboxClicked((AppCompatCheckBox) v, studyFieldCheckedList,
                            STUDY_FIELD_CHECKED_LIST);
                }
            });
            studyFieldCheckBox.add(checkBox);
        }
        {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(getContext());
            checkBox.setText(MedicineSchema.MedicineTable.Cols.CATEGORY);
            checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onCheckboxClicked((AppCompatCheckBox) v, studyFieldCheckedList,
                            STUDY_FIELD_CHECKED_LIST);
                }
            });
            studyFieldCheckBox.add(checkBox);
        }

        for (int i = 0; i < studyFieldCheckBox.size(); i++)
        {
            studyFieldsLayout.addView(studyFieldCheckBox.get(i));
        }


        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Multiple choice");

        class StartQuizHandler implements View.OnClickListener
        {
            @Override
            public void onClick(View v)
            {
                // List of chosen medicines
                List<Medicine> medicinesQuiz = findMedicinesQuiz(studyTopicCheckedList);
                // Number of questions
                int numOfQuestions;
                // tracker
                QuizTracker tracker = null;

                // title
                String title = quizTitleEditText.getText().toString();

                int i = 1;
                while(!isNewTitle(title))
                {
                    Log.d(TAG, "Title is not new, making new title");
                    title = title + i;
                    i++;
                }


                // Try to get number of questions from input
                try
                {
                    numOfQuestions = Integer.parseInt(amountOfQuestionEditText.getText().toString());
                }
                catch (Exception ex)
                {
                    numOfQuestions = 0;
                }

                // Validate all inputs
                if (studyTopicCheckedList.isEmpty() || studyFieldCheckedList.isEmpty())
                {
                    Toast.makeText(getActivity(),
                            "Please select at least one STUDY TOPIC and one STUDY FIELD",
                            Toast.LENGTH_SHORT).show();
                } else if (numOfQuestions > medicinesQuiz.size()
                        || numOfQuestions <= 0)
                {
                    Toast.makeText(getActivity(),
                            "Please enter a VALID AMOUNT OF QUESTIONS", Toast.LENGTH_SHORT).show();
                } else
                {

                    // Start the quiz activity
                    for (Medicine medicine : medicinesQuiz)
                    {
                        Log.i("test", medicine.getGenericName());
                    }
                    Log.i("test", String.valueOf(medicinesQuiz.size()));

                    String[] topicList = toStringArray(studyTopicCheckedList);
                    String[] fieldList = toStringArray(studyFieldCheckedList);
                    // create a tracker if user wishes to track progress
                    if(!title.equals(""))
                    {
                        tracker = new QuizTracker(topicList, fieldList, numOfQuestions, title);
                        Toast.makeText(getContext(), "Quiz is being tracked: " + title, Toast.LENGTH_LONG).show();
                    }
                    // start quiz
                    Log.d(TAG, "Starting Quiz, title: " + title);
                    QuizMultipleChoiceFragment fragment = QuizMultipleChoiceFragment.newInstance(topicList, fieldList, numOfQuestions, tracker, false);
                    ReplaceFragmentCommand.startNewFragment(getActivity(), fragment, true);
                }
            }
        }

        ///////////// START THE QUIZ ///////////////
        Button startQuizButton = (Button) rootView.findViewById(R.id.start_button);
        startQuizButton.setOnClickListener(new StartQuizHandler());
    }

    /**
     * to display a line
     *
     * @return TextView
     */
    private TextView getSeparateLine()
    {
        LinearLayoutCompat.LayoutParams layoutParams =
                new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(Color.BLACK);
        return textView;
    }


    /**
     * Convert dp to px
     *
     * @param dp device pixel
     * @return int pixel
     */
    public int dpToPx(int dp)
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    /**
     * OnClickListener for checkboxes
     *
     * @param checkBox
     * @param checkedList
     * @param arrayListCode
     */
    public void onCheckboxClicked(AppCompatCheckBox checkBox, ArrayList<String> checkedList,
                                  int arrayListCode)
    {

        // Is the view now checked?
        boolean checked = checkBox.isChecked();

        if (checked)
        {
            checkedList.add(checkBox.getText().toString());
        } else
        {
            checkedList.remove(checkBox.getText().toString());
        }

        List<Medicine> tempMedicines = findMedicinesQuiz(checkedList);

        if (arrayListCode == STUDY_TOPIC_CHECKED_LIST)
        {
            if (tempMedicines != null)
            {
                String maxQuestion = String.valueOf(tempMedicines.size());
                amountOfQuestionTextInput
                        .setHint("Enter amount of questions (up to " + maxQuestion + ")");
                Log.i("test", String.valueOf(findMedicinesQuiz(checkedList).size()));
            } else
            {
                amountOfQuestionTextInput.setHint("Enter amount of Questions");
                Log.i("test", String.valueOf(0));
            }
        }

    }

    /**
     * To find Medicines for the quiz
     *
     * @param checkedList
     * @return
     */
    private List<Medicine> findMedicinesQuiz(ArrayList<String> checkedList)
    {
        String[] tempStrings = new String[checkedList.size()];
        tempStrings = checkedList.toArray(tempStrings);
        Log.i("test", Arrays.asList(tempStrings).toString());

        String whereArgs = "(";
        for (int i = 0; i < tempStrings.length; i++)
        {
            whereArgs += "?";
            if (i != tempStrings.length - 1)
            {
                whereArgs += ",";
            }
        }
        whereArgs += ")";
        Log.i("test", whereArgs);
        List<Medicine> medicinesQuiz = MedicineLab.get(getContext())
                .getSpecificMedicines("StudyTopic IN " + whereArgs, tempStrings,
                        MedicineSchema.MedicineTable.Cols.GENERIC_NAME);
        return medicinesQuiz;
    }


    /**
     * To convert ArrayList to String[]
     *
     * @param list
     * @return String[]
     */
    private String[] toStringArray(ArrayList<String> list)
    {
        String[] tempStrings = new String[list.size()];
        tempStrings = list.toArray(tempStrings);

        return tempStrings;
    }

    private boolean isNewTitle(String title)
    {
        boolean isNew = true;

        File reviewInfo = new File(getActivity().getFilesDir(), MainActivity.REVIEW_FILE);
        try
        {
            Scanner fileInput = new Scanner(reviewInfo);
            while (fileInput.hasNextLine())
            {
                // add to file name list
                String tempTextFile = fileInput.nextLine();
                tempTextFile = tempTextFile.replace(".txt", "");

                if(tempTextFile.equals(title))
                {
                    return false;
                }

                // skip the next line, which contains the tracker title
                fileInput.nextLine();
            }

            fileInput.close();

        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "problem reading names of existing quiz names", e);
        }

        return isNew;
    }
}
