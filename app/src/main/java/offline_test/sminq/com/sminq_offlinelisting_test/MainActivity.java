package offline_test.sminq.com.sminq_offlinelisting_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import offline_test.sminq.com.sminq_offlinelisting_test.abstracts.BaseActivity;
import offline_test.sminq.com.sminq_offlinelisting_test.pojo.TestDataPOJO;
import offline_test.sminq.com.sminq_offlinelisting_test.utils.AppConstants;

/**
 * Created by Pawan on 17/08/17.
 */

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    //High priority UI variables goes below.....
    @ViewById(R.id.errorContainerRl) RelativeLayout mErrorContainer;
    @ViewById(R.id.testRcyclerV) RecyclerView mTasksRecyclerV;
    @ViewById(R.id.errorTxtV) TextView mErrorTxtV;
    @ViewById(R.id.addNewDataBtn) Button mAddNewTestBtn;
    @ViewById(R.id.toolbar) Toolbar mToolbar;


    //Medium priority NON-UI variables goes below....
    private ArrayList<TestDataPOJO> testDataAl;
    private NewTaskAddedReceiver newTaskAddedReceiver;



    //Least priority variables goes below....
    private final String TAG = "MainActivity";





    @AfterViews
    void initializations(){

        if(testDataAl == null)
            handleErrorView(null);
        else
            handleCorrectView();



        mToolbar.setTitle(getString(R.string.app_name));
    }//initializations closes here....


    private void handleCorrectView() {
        mErrorContainer.setVisibility(View.GONE);
        mTasksRecyclerV.setVisibility(View.VISIBLE);
    }//handleCorrectView closes here....




    private void handleErrorView(@Nullable String errorMsg) {

        mErrorContainer.setVisibility(View.VISIBLE);
        mTasksRecyclerV.setVisibility(View.GONE);
        mAddNewTestBtn.setVisibility(View.VISIBLE);

        if(errorMsg == null)
            mErrorTxtV.setText(getString(R.string.noTestsFoundString));
        else
            mErrorTxtV.setText(errorMsg);
    }//handleErrorView closes here....




    private void handleNoInternetView(){
        handleErrorView(getString(R.string.noInternetErrorString));

        mAddNewTestBtn.setVisibility(View.GONE);
    }//handleNoInternetView closes here....



    @Click({R.id.addNewDataBtn, R.id.fab})
    void addNewTest(){
        startActivity(new Intent(MainActivity.this, AddNewTestActivity_.class));
    }//addNewTest closes here....


    @Override
    protected void onResume() {
        super.onResume();

        //Registering the Broadcast receiever....
        newTaskAddedReceiver = new NewTaskAddedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.NEW_TASK_ADDED_BROADCAST);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(newTaskAddedReceiver, intentFilter);
    }//onResume closes here....


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(newTaskAddedReceiver);
    }//onDestroy closes here.....

    /////////////////..............BROADCAST RECEIVER FOR NEWLY ADDED TASK............\\\\\\\\\\\\\\\\\\
    private class NewTaskAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(!isDestroyed()) {//Not condition....
                Log.d(TAG, "onReceive: ");
            }//if(!isDestroyed()) closes here.....
        }//onReceive closes here.....
    }//NewTaskAddedReceiver closes here....
}//MainActivity closes here....
