package offline_test.sminq.com.sminq_offlinelisting_test.threads;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import offline_test.sminq.com.sminq_offlinelisting_test.network.RequestQueueSingleton;
import offline_test.sminq.com.sminq_offlinelisting_test.pojo.TestDataPOJO;
import offline_test.sminq.com.sminq_offlinelisting_test.utils.AppConstants;

/**
 * Created by Pawan on 18/08/17.
 */

public class SminqJobService extends JobService {

    //High priority UI variables goes below....



    //Least priority variables goes below....
    private final String TAG = "SminqJobService";
    public static final int REQUEST_TIMEOUT = 200000;


    @Override
    public boolean onStartJob(JobParameters job) {
        //Step1: Fetch all the unsynced data from the DB...
        Realm getRealmData = Realm.getDefaultInstance();
        RealmResults<TestDataPOJO> unSyncedTestResults = getRealmData.where(TestDataPOJO.class).equalTo("isUploaded", false).findAll();

        if(unSyncedTestResults != null) {
            if (unSyncedTestResults.size() > 0) {
                //Lets sync the Results in the Webservice...
                Log.d(TAG, "Pawan unsynced Results: "+unSyncedTestResults.size());
//                TestDataPOJO[] unsyncedArray = unSyncedTestResults.toArray(TestDataPOJO[]);
                TestDataPOJO[] unsyncedArray = unSyncedTestResults.toArray(new TestDataPOJO[unSyncedTestResults.size()]);
                Log.d(TAG, "Pawan unsyncedArray: "+unsyncedArray);

                //Storing the data in a Map, bcoz backend developer cannot handle the JSON Obect, so let's try to pass Key value pair in a Map.
                Map<String, String> unsyncedTestMap = new HashMap<String, String>();
                for(TestDataPOJO pojo : unsyncedArray)
                    unsyncedTestMap.put(pojo.getTaskTitle(), pojo.getTaskDescription());

                Log.d(TAG, "Pawan unsyncedTestMap: "+unsyncedTestMap);

                //Lets upload this data to the Server....
                syncDataToServer(unsyncedTestMap);




                return false;//Yes work is done, now please close the JobSceduler....
            }//if(unSyncedTestResults.size() > 0) closes here...
            else{
                //No data found....
                Log.i(TAG, "No unsynced results found.");
                return false;
            }//else closes here....
        }//if(unSyncedTestResults != null) closes here....
        else{
            //No data found....
            Log.i(TAG, "No unsynced results found, bcoz testResult object is null !");
            return false;
        }//else closes here....

    }//onStartJob closes here....


    private void syncDataToServer(@NonNull Map<String, String> unsyncedTestMap) {

        final HashMap<String, String> headers = new HashMap<>();


        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String uploadUnSyncUrl = AppConstants.APP_URL + AppConstants.UPLOAD_TEST_RESULTS;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uploadUnSyncUrl, new JSONObject(unsyncedTestMap), future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        request.setTag(AppConstants.UPLOAD_UNSYNC_API_TAG);//Such that when Fragment/Activity closes the Requests with the same TAG are deleted/removed.....
        request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().add(request);


        try {
            JSONObject response = future.get(); // this will block

            Log.d(TAG, "syncDataToServer: " + request.getHeaders());
        }//try closes here....
        catch (Exception e) {
            e.printStackTrace();
        }//catch closes here.....


    }//syncDataToServer closes here.....




    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "Pawan onStopJob");
        return false;
    }//onStopJob closes here....

}//SminqJobService class closes here....
