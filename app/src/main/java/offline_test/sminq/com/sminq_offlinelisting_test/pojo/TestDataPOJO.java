package offline_test.sminq.com.sminq_offlinelisting_test.pojo;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pawan on 17/08/17.
 */

public class TestDataPOJO extends RealmObject{

    private String title;

    private String description;


    private Boolean isUploaded = true;


    public String getTaskTitle() {
        return title;
    }

    public void setTaskTitle(String taskTitle) {
        this.title = taskTitle;
    }

    public String getTaskDescription() {
        return description;
    }

    public void setTaskDescription(String taskDescription) {
        this.description = taskDescription;
    }

    public Boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }

}//TestDataPOJO closes here...
