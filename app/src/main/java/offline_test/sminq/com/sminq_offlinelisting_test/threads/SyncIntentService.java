package offline_test.sminq.com.sminq_offlinelisting_test.threads;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

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

public class SyncIntentService extends IntentService {


    //Medium priority NON-UI variables goes below....
    private final String TITLE_KEY = "title";
    private final String DESCRIPTION_KEY = "description";


    //Least priority variables goes below.....
    private final String TAG = "SyncIntentService";
    public static final int REQUEST_TIMEOUT = 200000;



    public SyncIntentService() {
        super("SyncIntentService");
    }//SyncIntentService constructor closes here....



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Realm getRealmData = Realm.getDefaultInstance();
        RealmResults<TestDataPOJO> unSyncedTestResults = getRealmData.where(TestDataPOJO.class).equalTo("isUploaded", false).findAll();

        if(unSyncedTestResults != null) {
            if (unSyncedTestResults.size() > 0) {
                //Lets sync the Results in the Webservice...

                TestDataPOJO[] unsyncedArray = unSyncedTestResults.toArray(new TestDataPOJO[unSyncedTestResults.size()]);

                for(final TestDataPOJO pojo : unsyncedArray) {
                    Boolean syncSuccess = syncDataToServer(pojo.getTaskTitle(), pojo.getTaskDescription());

                    if(syncSuccess){
                        //Lets update the value in the Local DB....

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction(){

                            @Override
                            public void execute(Realm realm) {
                                TestDataPOJO uploadedRealResults = realm.where(TestDataPOJO.class)
                                            .equalTo("title", pojo.getTaskTitle())
                                            .equalTo("description",pojo.getTaskDescription())
                                            .findFirst();
                                uploadedRealResults.setUploaded(true);
//                                realm.commitTransaction();
                            }//execute closes here....
                        });

                    }//if(syncSuccess) closes here....

                }//for(TestDataPOJO pojo : unsyncedArray) closes here....

            }//if(unSyncedTestResults.size() > 0) closes here...
            else{
                //No data found....
                Log.i(TAG, "No unsynced results found.");
            }//else closes here....
        }//if(unSyncedTestResults != null) closes here....
        else{
            //No data found....
            Log.i(TAG, "No unsynced results found, bcoz testResult object is null !");
        }//else closes here....
    }//onHandleIntent closes here....





    private Boolean syncDataToServer(@NonNull String title, @NonNull String description) {


        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TITLE_KEY, title.toString().trim());
        params.put(DESCRIPTION_KEY, description.toString().trim());


        final HashMap<String, String> headers = new HashMap<>();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String uploadUnSyncUrl = AppConstants.APP_URL + AppConstants.UPLOAD_TEST_RESULTS;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uploadUnSyncUrl, new JSONObject(params), future, future) {
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

            Log.d(TAG, "syncDataToServer: "+response);
            Log.d(TAG, "syncDataToServer: " + request.getHeaders());


            //Lets update the data in the Local DB, i.e. make the flag as true....
            return true;

        }//try closes here....
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }//catch closes here.....


    }//syncDataToServer closes here.....
}//SyncIntentService closes here....
