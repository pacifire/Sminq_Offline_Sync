package offline_test.sminq.com.sminq_offlinelisting_test.pojo;

import io.realm.RealmObject;

/**
 * Created by Pawan on 17/08/17.
 */

public class TestDataPOJO extends RealmObject{


    private String taskTitle;
    private String taskDescription;
    private Boolean isUploaded;


    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }

}//TestDataPOJO closes here...
