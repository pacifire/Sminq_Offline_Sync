package offline_test.sminq.com.sminq_offlinelisting_test.pojo;

import junit.framework.Test;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Pawan on 18/08/17.
 */

public class MainResponsePOJO extends RealmObject{




    private String status;


    private RealmList<TestDataPOJO> data;
}
