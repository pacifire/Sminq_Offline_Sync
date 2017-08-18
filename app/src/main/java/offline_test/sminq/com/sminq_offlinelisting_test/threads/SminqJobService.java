package offline_test.sminq.com.sminq_offlinelisting_test.threads;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Pawan on 18/08/17.
 */

public class SminqJobService extends JobService {

    //High priority UI variables goes below....



    //Least priority variables goes below....
    private final String TAG = "SminqJobService";



    @Override
    public boolean onStartJob(JobParameters job) {
        //Step1: Fetch all the unsynced data from the DB...
        //Lets Upload all these values one-by-one via a Service.....
        Intent syncIntent = new Intent(this, SyncIntentService.class);
        getApplicationContext().startService(syncIntent);

        return false;
    }//onStartJob closes here....



    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "Pawan onStopJob");
        return false;
    }//onStopJob closes here....
}//SminqJobService class closes here....
