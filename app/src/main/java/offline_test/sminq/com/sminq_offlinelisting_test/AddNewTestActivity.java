package offline_test.sminq.com.sminq_offlinelisting_test;

import android.graphics.Matrix;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import offline_test.sminq.com.sminq_offlinelisting_test.abstracts.BaseActivity;
import offline_test.sminq.com.sminq_offlinelisting_test.pojo.TestDataPOJO;

/**
 * Created by Pawan on 17/08/17.
 */

@EActivity(R.layout.activity_add_new_test)
public class AddNewTestActivity extends BaseActivity {

    //High priority UI variables goes below.....
    @ViewById(R.id.addNewDataFAB) FloatingActionButton addNewDataFAB;
    @ViewById(R.id.taskNameTIEdtTxt) TextInputEditText mTaskNameEdtTxt;
    @ViewById(R.id.tasDescTIEdtTxt) TextInputEditText mTaskDescEdtTxt;
    @ViewById(R.id.taskNameTIL) TextInputLayout mTaskNameInputLayout;
    @ViewById(R.id.taskDescTIL) TextInputLayout mDescInputLayout;




    //Medium priority NON-UI variables goes below....




    //Least priority variables goes below.....
    private final String TAG = "AddNewTestActivity";





    @AfterViews
    void initializations(){


    }//initializations closes here.....




    @Click(R.id.addNewDataFAB)
    void addData(){

        Boolean flag = chkValidations();

        if(flag){
            addNewDataFAB.startAnimation(AnimationUtils.loadAnimation(AddNewTestActivity.this, R.anim.fab_roate_fwd));
            addNewDataFAB.setClickable(false);


            //Lets add data in Realm....
            Realm addRealmData = Realm.getDefaultInstance();
            addRealmData.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TestDataPOJO newTestObject = realm.createObject(TestDataPOJO.class);
                    newTestObject.setTaskTitle(mTaskNameEdtTxt.getText().toString().trim());
                    newTestObject.setTaskDescription(mTaskDescEdtTxt.getText().toString().trim());
                    newTestObject.setUploaded(false);
                }
            });


        }//if(flag) closes here....


    }//addData closes here....


    /**
     * Method to handle the validations goes below..
     * **/
    private Boolean chkValidations() {

        mTaskNameInputLayout.setError(null);
        mTaskNameInputLayout.setError(null);

        if(mTaskNameEdtTxt.getText().toString().trim().isEmpty() || mTaskNameEdtTxt.getText().toString().trim().equals("")){
            //TaskName is empty...
            mTaskNameInputLayout.setErrorEnabled(true);
            mDescInputLayout.setErrorEnabled(false);
            mTaskNameInputLayout.setError("Task Name cannot be empty !");
            return false;
        }//if taskname is empty closes her.....
        else if(mTaskDescEdtTxt.getText().toString().trim().isEmpty() || mTaskDescEdtTxt.getText().toString().trim().equals("")){
            //Task Details are empty....
            mDescInputLayout.setErrorEnabled(true);
            mTaskNameInputLayout.setErrorEnabled(false);
            mDescInputLayout.setError("Task Name cannot be empty !");
            return false;
        }//else desciption is empty closes here....
        else
            return true;

    }//chkValidations closes here....


}//AddNewTestActivity closes here....
