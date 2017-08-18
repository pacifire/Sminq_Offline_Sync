package offline_test.sminq.com.sminq_offlinelisting_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;
import offline_test.sminq.com.sminq_offlinelisting_test.abstracts.BaseActivity;
import offline_test.sminq.com.sminq_offlinelisting_test.adapters.TasksListAdapter;
import offline_test.sminq.com.sminq_offlinelisting_test.network.NetworkResponseHandler;
import offline_test.sminq.com.sminq_offlinelisting_test.network.NetworkResponseListener;
import offline_test.sminq.com.sminq_offlinelisting_test.pojo.MainResponsePOJO;
import offline_test.sminq.com.sminq_offlinelisting_test.pojo.TestDataPOJO;
import offline_test.sminq.com.sminq_offlinelisting_test.threads.SminqJobService;
import offline_test.sminq.com.sminq_offlinelisting_test.utils.AppConstants;

/**
 * Created by Pawan on 17/08/17.
 */

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements NetworkResponseListener {

    //High priority UI variables goes below.....
    @ViewById(R.id.errorContainerRl) RelativeLayout mErrorContainer;
    @ViewById(R.id.testRcyclerV) RecyclerView mTasksRecyclerV;
    @ViewById(R.id.errorTxtV) TextView mErrorTxtV;
    @ViewById(R.id.addNewDataBtn) Button mAddNewTestBtn;
    @ViewById(R.id.toolbar) Toolbar mToolbar;


    //Medium priority NON-UI variables goes below....
    private ArrayList<TestDataPOJO> testDataAl;
    private NewTaskAddedReceiver newTaskAddedReceiver;
    private TasksListAdapter tasksListAdapter;
    private FirebaseJobDispatcher mDispatcher;


    //Least priority variables goes below....
    private final String TAG = "MainActivity";





    @AfterViews
    void initializations(){

        //Registering the Broadcast receiever....
        newTaskAddedReceiver = new NewTaskAddedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.NEW_TASK_ADDED_BROADCAST);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(newTaskAddedReceiver, intentFilter);


        /**
         * Lets check if Table has entries or not !
         * If yes, then fetch the data & store in ArrayList
         * **/
        setTestDataFromTable();



        mToolbar.setTitle(getString(R.string.app_name));



        //////////////////..............CREATING JOB DISPATCHER....................\\\\\\\\\\\\\\\\\\
        scheduleTheJob();


    }//initializations closes here....





    /**
     * This method will fetch data from the table & add in the ArrayList.
     * such that data can be displayed in the RecyclerView.
     * **/
    private void setTestDataFromTable() {

        Realm getRealmData = Realm.getDefaultInstance();
//        RealmResults<TestDataPOJO> unSyncedTestResults = getRealmData.where(TestDataPOJO.class).equalTo("isUploaded", false).findAll();
        RealmResults<TestDataPOJO> allResults = getRealmData.where(TestDataPOJO.class).findAll();

        if(allResults != null) {
            if (allResults.size() > 0) {
                //Lets store the Results...

                //Convert Array to ArrayList...
                testDataAl = new ArrayList<TestDataPOJO>(allResults);

                handleCorrectView();

                setAdapter();

            }//if(unSyncedTestResults.size() > 0) closes here...
            else{
                //No data found....
                testDataAl = null;
                handleErrorView(null);

                //Lets call API & get data from API....
                callAPiAndGetTestData();
            }//else closes here....
        }//if(unSyncedTestResults != null) closes here....
        else{
            //No data found....
            testDataAl = null;
            handleErrorView(null);

            //Lets call API & get data from API....
            callAPiAndGetTestData();
        }//else closes here....

    }//setTestDataFromTable closes here....



    private void callAPiAndGetTestData() {
        String url = AppConstants.APP_URL+AppConstants.GET_TEST_RESULTS;
        NetworkResponseHandler getTestDataHandler = new NetworkResponseHandler(MainActivity.this, MainActivity.this,
                url, null, null, null, false);
        getTestDataHandler.setNetworkResponseListener(MainActivity.this);
        getTestDataHandler.executeGET("");
    }//callAPiAndGetTestData closes here....


    /**
     * This method sets the Adapter on the Recyclerview.
     * **/
    private void setAdapter() {
        if(testDataAl != null){

            tasksListAdapter = new TasksListAdapter();
            tasksListAdapter.setTestsAl(testDataAl);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
            mTasksRecyclerV.setLayoutManager(mLayoutManager);
            mTasksRecyclerV.setItemAnimator(new DefaultItemAnimator());
            mTasksRecyclerV.setAdapter(tasksListAdapter);
        }//if(testDataAl != null) closes here....
        else
            Log.e(TAG, "Unhandled condition: testDataAl is null.");
    }//setAdapter closes here....


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
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(newTaskAddedReceiver);
    }//onDestroy closes here.....

    @Override
    public void networkResponseSuccess(final String response) {
        try {

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.createObjectFromJson(MainResponsePOJO.class, response.toString().trim());


                    //Set adapter now....
                    setTestDataFromTable();
                }//execute closes here....
            });
        }//try closes here....
        catch(Exception e){
            e.printStackTrace();
        }
    }//networkResponseSuccess closes here....

    @Override
    public void networkResponseFailure(String errorMessage) {
        Snackbar.make(mErrorContainer, errorMessage.toString().trim(), Snackbar.LENGTH_SHORT).show();
    }//networkResponseFailure closes here....


    /////////////////..............BROADCAST RECEIVER FOR NEWLY ADDED TASK............\\\\\\\\\\\\\\\\\\
    private class NewTaskAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(!isDestroyed()) {//Not condition....
//                Log.d(TAG, "onReceive: "+intent.getStringExtra(AppConstants.NEW_TASK_NAME_EXTRA));

                if(testDataAl == null)
                    testDataAl = new ArrayList<TestDataPOJO>();

                TestDataPOJO newDataPOJO = new TestDataPOJO();
                newDataPOJO.setTaskTitle(intent.getStringExtra(AppConstants.NEW_TASK_NAME_EXTRA));
                newDataPOJO.setTaskDescription(intent.getStringExtra(AppConstants.NEW_TASK_DESC_EXTRA));
                newDataPOJO.setUploaded(false);

                testDataAl.add(0, newDataPOJO);




                //Now lets notify the adapter for position 1....
                if(tasksListAdapter == null){
                    handleCorrectView();

                    setAdapter();
                }//if(tasksListAdapter == null) closes here....
                else {
                    //else notify the adaptre for additiona t 0th position...
                    tasksListAdapter.setTestsAl(testDataAl);
                    tasksListAdapter.notifyItemInserted(0);
                }//else closes here.....

            }//if(!isDestroyed()) closes here.....
        }//onReceive closes here.....
    }//NewTaskAddedReceiver closes here....



    /**
     * This method is responsible for Job Scheduling.
     * **/
    private void scheduleTheJob() {

        if(mDispatcher == null)
            mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MainActivity.this));

        /**
         * We may need to cancel the old Jobs, so lets debug & come here again.
         * **/
//        mDispatcher.cancel(AppConstants.TASK_SYNC_SERVICE);


        Job syncJob = mDispatcher.newJobBuilder()
                .setService(SminqJobService.class)
                .setTag(AppConstants.TASK_SYNC_SERVICE)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0,20))//both in seconds....
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        mDispatcher.mustSchedule(syncJob);

    }//scheduleTheJob closes here....
}//MainActivity closes here....
