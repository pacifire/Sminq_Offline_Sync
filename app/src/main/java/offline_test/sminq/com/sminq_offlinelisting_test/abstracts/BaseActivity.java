package offline_test.sminq.com.sminq_offlinelisting_test.abstracts;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Pawan on 17/08/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onStop() {
        super.onStop();
    }//onStop closes  here....

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }//onDestroy closes here....

}//BaseActivity closes here....
