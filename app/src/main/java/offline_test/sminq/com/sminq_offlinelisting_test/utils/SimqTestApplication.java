package offline_test.sminq.com.sminq_offlinelisting_test.utils;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Pawan on 17/08/17.
 */

public class SimqTestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
    }//onCreate closes here.....

}//SimqTestApplication closes here....
