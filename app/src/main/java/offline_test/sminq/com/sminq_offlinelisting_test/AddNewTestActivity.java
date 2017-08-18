package offline_test.sminq.com.sminq_offlinelisting_test;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.view.animation.AnimationUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import offline_test.sminq.com.sminq_offlinelisting_test.abstracts.BaseActivity;
import offline_test.sminq.com.sminq_offlinelisting_test.pojo.TestDataPOJO;
import offline_test.sminq.com.sminq_offlinelisting_test.utils.AppConstants;

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
    @ViewById(R.id.addNewDataParentContainer) ConstraintLayout mParentContainer;


    //Medium priority NON-UI variables goes below....


    //Least priority variables goes below.....
    private final String TAG = "AddNewTestActivity";



    @Click(R.id.addNewDataFAB)
    void addData() {

        Boolean flag = chkValidations();

        if (flag) {
            addNewDataFAB.startAnimation(AnimationUtils.loadAnimation(AddNewTestActivity.this, R.anim.fab_rotate_fwd));
            addNewDataFAB.setClickable(false);


            //Lets add data in Realm....
            Realm addRealmData = Realm.getDefaultInstance();
            addRealmData.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TestDataPOJO newTestObject = realm.createObject(TestDataPOJO.class);
                    newTestObject.setTaskTitle(mTaskNameEdtTxt.getText().toString().trim());
                    newTestObject.setTaskDescription(mTaskDescEdtTxt.getText().toString().trim());
                    newTestObject.setUploaded(false);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //If entry is added Successfully in the Realm Object then control comes here....
//                    Toast.makeText(AddNewTestActivity.this, getString(R.string.newTaskAddSuccessMsg), Toast.LENGTH_SHORT).show();

                    Snackbar.make(mParentContainer, getString(R.string.newTaskAddSuccessMsg), Snackbar.LENGTH_SHORT).show();


                    //Lets broadcast this new Task, such that it can be displayed in the List....
                    Intent newTaskAddedIntent = new Intent();
                    newTaskAddedIntent.putExtra(AppConstants.NEW_TASK_NAME_EXTRA, mTaskNameEdtTxt.getText().toString().trim());
                    newTaskAddedIntent.putExtra(AppConstants.NEW_TASK_DESC_EXTRA, mTaskDescEdtTxt.getText().toString().trim());
                    newTaskAddedIntent.setAction(AppConstants.NEW_TASK_ADDED_BROADCAST);
                    LocalBroadcastManager.getInstance(AddNewTestActivity.this).sendBroadcast(newTaskAddedIntent);


                    //REset UI for new entry.....
                    mTaskNameEdtTxt.setText("");
                    mTaskDescEdtTxt.setText("");
                    mTaskNameEdtTxt.requestFocus();


                    addNewDataFAB.startAnimation(AnimationUtils.loadAnimation(AddNewTestActivity.this, R.anim.fab_rotate_backward));
                    addNewDataFAB.setClickable(true);
                }//onSuccess closes here.....
            });


        }//if(flag) closes here....


    }//addData closes here....


    /**
     * Method to handle the validations goes below..
     **/
    private Boolean chkValidations() {

        mTaskNameInputLayout.setError(null);
        mTaskNameInputLayout.setError(null);

        if (mTaskNameEdtTxt.getText().toString().trim().isEmpty() || mTaskNameEdtTxt.getText().toString().trim().equals("")) {
            //TaskName is empty...
            mTaskNameInputLayout.setErrorEnabled(true);
            mDescInputLayout.setErrorEnabled(false);
            mTaskNameInputLayout.setError(getString(R.string.taskNameEmptyErrorMsg));
            return false;
        }//if taskname is empty closes her.....
        else if (mTaskDescEdtTxt.getText().toString().trim().isEmpty() || mTaskDescEdtTxt.getText().toString().trim().equals("")) {
            //Task Details are empty....
            mDescInputLayout.setErrorEnabled(true);
            mTaskNameInputLayout.setErrorEnabled(false);
            mDescInputLayout.setError(getString(R.string.taskDescEmptyErrorMsg));
            return false;
        }//else desciption is empty closes here....
        else
            return true;

    }//chkValidations closes here....


}//AddNewTestActivity closes here....
