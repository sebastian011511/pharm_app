package com.bhcc.app.pharmtech.view.review;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.view.MainActivity;
import com.bhcc.app.pharmtech.view.navigation.ReplaceFragmentCommand;
import com.bhcc.app.pharmtech.view.quiz.QuizTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment
{

    public static final String TAG = "ReviewFragment";

    // Lists
    private List<String> nameList;
    private List<String> textFileNameList;
    private List<String> trackerFileNameList;

    private String trackerFile = "temp.dat";

    // views
    private RecyclerView quizListRecyclerView;

    // Adapter
    private QuizListAdapter quizListAdapter;

    public ReviewFragment()
    {
        // Required empty public constructor
    }

    /**
     * To set up lists & get data from the file
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // set up lists
        nameList = new ArrayList<>();
        textFileNameList = new ArrayList<>();
        trackerFileNameList = new ArrayList<>();

        // get data from the file
        // each review file's name
        File reviewInfo = new File(getActivity().getFilesDir(), MainActivity.REVIEW_FILE);
        try
        {
            Scanner fileInput = new Scanner(reviewInfo);
            while (fileInput.hasNextLine())
            {
                // add to file name list
                String tempTextFile = fileInput.nextLine();
                textFileNameList.add(tempTextFile);

                // add to tracker file name list
                String tempTrackerFile = fileInput.nextLine();
                trackerFileNameList.add(tempTrackerFile);

                // todo drop filename extesnion here
                nameList.add(tempTextFile);
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.i("test5", "Error\n");
        }
    }

    /**
     * To set up UI
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // set up views
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        quizListRecyclerView = (RecyclerView) view;
        quizListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // update UI
        updateUI();

        return view;
    }

    /**
     * To update UI
     */
    private void updateUI()
    {
        // if no review in the file, show a warning toast
        // otherwise, show the review list
        if (nameList.size() > 0)
        {
            quizListAdapter = new QuizListAdapter(nameList);
            quizListRecyclerView.setAdapter(quizListAdapter);
        } else
        {
            Toast.makeText(getContext(), "Take a quiz first!!!", Toast.LENGTH_LONG).show();
        }
    }

    // =====================  ViewHolder =================================//

    private class QuizListHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        // widgets
        private TextView idTextView;
        private TextView nameTextView;
        private ImageButton trash;
        private Button btnTakeAgain;
        private TextView tvNumTimesTaken;

        /**
         * Constructor
         *
         * @param itemView
         */
        public QuizListHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);  //set onclick listener to the override method
            // link variables to widgets
            idTextView = (TextView) itemView.findViewById(R.id.quiz_review_id);
            nameTextView = (TextView) itemView.findViewById(R.id.quiz_review_date);
            trash = (ImageButton) itemView.findViewById(R.id.delete_button);
            btnTakeAgain = (Button) itemView.findViewById(R.id.btn_take_again);
            tvNumTimesTaken = (TextView) itemView.findViewById(R.id.text_times_taken);
        }


        /**
         * To bind data to a holder
         *
         * @param name
         */
        public void bindReview(String name)
        {
            // set text to each widget
            idTextView.setText("Quiz #" + (getPosition() + 1));
            nameTextView.setText(name);

            QuizTracker tracker = getTracker(trackerFileNameList.get(getPosition()));
            tvNumTimesTaken.setText("Times Taken: " + Integer.toString(tracker.getNumTimesTaken()));


            // Delete Review Part
            trash.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        // Delete the review file
                        File fileDeleted = new File(getActivity().getFilesDir(), textFileNameList.get(getPosition()));
                        fileDeleted.delete();

                        // delete the tracker file
                        File trackerFileDeleted = new File(getActivity().getFilesDir(), trackerFileNameList.get(getPosition()));
                        trackerFileDeleted.delete();

                        // Remove file name from the list
                        textFileNameList.remove(getPosition());
                        trackerFileNameList.remove(getPosition());

                        // Write update file name list to the info file
                        File file = new File(getActivity().getFilesDir(), MainActivity.REVIEW_FILE);
                        PrintWriter printWriter = new PrintWriter(file);

                        for (int i = 0; i < textFileNameList.size(); i++)
                        {
                            printWriter.write(textFileNameList.get(i) + "\n");
                            printWriter.write(trackerFileNameList.get(i) + "\n");
                        }

                        printWriter.close();

                    }
                    catch (Exception ex)
                    {
                    }

                    //update UI
                    ReplaceFragmentCommand.startNewFragment(getActivity(), new ReviewFragment(), false);
                }
            });

            btnTakeAgain.setOnClickListener(new View.OnClickListener()
            {
                QuizTracker tracker;

                @Override
                public void onClick(View v)
                {
                    Log.d(TAG, "Take again clicked");

                    try
                    {
                        // CALL METHOD
                        FileInputStream fileInputStream = getActivity().openFileInput(trackerFileNameList.get(getPosition()));
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        tracker = (QuizTracker) objectInputStream.readObject();
                        objectInputStream.close();
                        fileInputStream.close();

                        // Take the quiz again
                        tracker.startQuiz(getActivity());
                    }
                    catch (Exception ieo)
                    {
                        Log.e(TAG, "file problem", ieo);
                    }

                }
            });
        }

        /**
         * Set onClickListener to a holder
         *
         * @param v
         */
        @Override
        public void onClick(View v)
        {
            ReviewDetailFragment fragment = ReviewDetailFragment.newInstance(textFileNameList.get(getPosition()));
            ReplaceFragmentCommand.startNewFragment(getActivity(), fragment, false);
        }
    }


    // =====================  Adapter ================================= //

    private class QuizListAdapter extends RecyclerView.Adapter<QuizListHolder>
    {
        // lists
        private List<String> nameList;

        /**
         * Constructor
         *
         * @param nameList
         */
        public QuizListAdapter(List<String> nameList)
        {
            this.nameList = nameList;
        }

        /**
         * To set up views
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public QuizListHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.fragment_quiz_review, parent, false);
            return new QuizListHolder(view);
        }

        /**
         * To bind adapter to a holder
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(QuizListHolder holder, int position)
        {
            ;
            holder.bindReview(nameList.get(position));
        }

        /**
         * To get the list size
         *
         * @return List Size
         */
        @Override
        public int getItemCount()
        {
            return nameList.size();
        }
    }

    private QuizTracker getTracker(String str)
    {
        QuizTracker tracker = null;
        try
        {
            FileInputStream fileInputStream = getActivity().openFileInput(str);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            tracker = (QuizTracker) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "problem opening quiz tracker", ex);
        }

        return tracker;
    }
}
