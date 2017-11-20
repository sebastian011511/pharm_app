package com.bhcc.app.pharmtech.view;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.model.DrugOfDayManager;
import com.bhcc.app.pharmtech.data.model.Medicine;
import com.bhcc.app.pharmtech.view.filter.FilterFragment;
import com.bhcc.app.pharmtech.view.navigation.ReplaceFragmentCommand;
import com.bhcc.app.pharmtech.view.quiz.SelectQuizFragment;
import com.bhcc.app.pharmtech.view.review.ReviewFragment;
import com.bhcc.app.pharmtech.view.study.DrugOfDayFragment;
import com.bhcc.app.pharmtech.view.study.MedicineListFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private static final String TAG = "MainActivity";

    public static final String REVIEW_FILE = "ReviewInfo.txt";
    public static final String DRUG_OF_DAY_FILE = "DrugOfDay.dat";

    private static final int ASCENDING_ID = 0;
    private static final int DESCENDING_ID = 1;
    private static final int RANDOM_ID = 2;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        loadDefaultFragment();
        createReviewFile();
        showDrugOfDay();
    }

    /**
     * Shows drug of the day if hasn't been displayed yet.
     * @author Nils
     */
    private void showDrugOfDay()
    {
        // object that controls what drugs have been shown as drug of day
        DrugOfDayManager drugOfDayManager;
        // file that DrugOfDayManager is serialized to
        File file = new File(getApplicationContext().getFilesDir(), DRUG_OF_DAY_FILE);
        // String to identify drug of day
        String drugOfDay = null;

        try
        {
            // if the file has been made(ie, its not the first time the program is run), open and read from it
            if (file.exists())
            {
                FileInputStream fileInputStream = this.openFileInput(DRUG_OF_DAY_FILE);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                drugOfDayManager = (DrugOfDayManager) objectInputStream.readObject();
                drugOfDay = drugOfDayManager.getDrugOfDay();
                objectInputStream.close();
                fileInputStream.close();
            }
            // otherwise make a new one, using the list of drugs from MedicineLab
            else
            {
                MedicineLab lab = MedicineLab.get(this);
                drugOfDayManager = new DrugOfDayManager(lab.getMedicines());
            }

            // if drug of the day has not been shown today, show it, and resave the file so next time the program knows it displayed it.
            if (!drugOfDayManager.drugShownToday())
            {
                // show dialog with drug of day
                drugOfDay = drugOfDayManager.getDrugOfDay();
                FragmentManager manager = getSupportFragmentManager();
                DrugOfDayFragment dialog = DrugOfDayFragment.newInstance(drugOfDay);
                dialog.show(manager, TAG);

                // write the updated manager back to a file
                FileOutputStream fileOutputStream = this.openFileOutput(DRUG_OF_DAY_FILE, Context.MODE_PRIVATE);
                ObjectOutputStream output = new ObjectOutputStream(fileOutputStream);
                output.writeObject(drugOfDayManager);
                output.close();
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * If there's no fragment in container, display study fragment
     */
    private void loadDefaultFragment()
    {
        ReplaceFragmentCommand.startNewFragment(this, new MedicineListFragment(), false);
    }

    private void setUpToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        /**@svasco
         * Menu updated in main menu, if statements was not very organized*/
        switch (item.getItemId())
        {
            case R.id.study:
                ReplaceFragmentCommand.startNewFragment(this, new MedicineListFragment(), false);
                break;
            case R.id.quiz:
                ReplaceFragmentCommand.startNewFragment(this, new SelectQuizFragment(), false);
                break;
            case R.id.review:
                ReplaceFragmentCommand.startNewFragment(this, new ReviewFragment(), false);
                break;
            case R.id.filter:
                ReplaceFragmentCommand.startNewFragment(this, new FilterFragment(), false);
                break;
            case R.id.sort:
                showRadioButtonDialog();
                break;
            case R.id.about:
                ReplaceFragmentCommand.startNewFragment(this, new AboutFragment(), false);
                break;
            case R.id.legal:
                ReplaceFragmentCommand.startNewFragment(this, new LegalFragment(), false);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        closeDrawer();

        return false;
    }

    private boolean closeDrawer()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (!closeDrawer())
        {
            super.onBackPressed();
        }
    }


    /**
     * To Show dialog for the sorting selection
     */
    private void showRadioButtonDialog()
    {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_sorting_dialog);

        List<String> stringList = new ArrayList<>();  // list to hold choices
        // add choices
        stringList.add("Ascending");
        stringList.add("Descending");
        stringList.add("Random");

        // Radio group to hold radio buttons
        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        // add radio buttons to radio group
        RadioButton rbAscending = new RadioButton(this);
        rbAscending.setText(stringList.get(ASCENDING_ID));
        rbAscending.setId(ASCENDING_ID);
        rg.addView(rbAscending);

        RadioButton rbDescending = new RadioButton(this);
        rbDescending.setText(stringList.get(DESCENDING_ID));
        rbDescending.setId(DESCENDING_ID);
        rg.addView(rbDescending);

        RadioButton rdRandom = new RadioButton(this);
        rdRandom.setText(stringList.get(RANDOM_ID));
        rdRandom.setId(RANDOM_ID);
        rg.addView(rdRandom);

        TextView tvOK = (TextView) dialog.findViewById(R.id.choose_sorting_ok_button);
        tvOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int id = rg.getCheckedRadioButtonId();
                switch (id)
                {
                    case ASCENDING_ID:
                        MedicineLab.get(getApplication()).sortAscending();
                        Log.i("test3", MedicineLab.get(getApplication()).getMedicines().get(0).getGenericName());
                        break;
                    case DESCENDING_ID:
                        MedicineLab.get(getApplication()).sortDescending();
                        Log.i("test3", MedicineLab.get(getApplication()).getMedicines().get(0).getGenericName());
                        break;
                    case RANDOM_ID:
                        MedicineLab.get(getApplication()).sortRandom();
                        break;
                }
                dialog.dismiss();
                loadDefaultFragment();
            }
        });

        dialog.show();
    }

    /**
     * To Create a file to hold review files information
     */
    private void createReviewFile()
    {
        File file = new File(getApplicationContext().getFilesDir(), REVIEW_FILE);

        if (!file.exists())
        {
            try
            {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

}
