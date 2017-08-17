package offline_test.sminq.com.sminq_offlinelisting_test.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import offline_test.sminq.com.sminq_offlinelisting_test.R;
import offline_test.sminq.com.sminq_offlinelisting_test.pojo.TestDataPOJO;
import offline_test.sminq.com.sminq_offlinelisting_test.utils.AppUtils;

/**
 * Created by Pawan on 17/08/17.
 */

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.Viewholder>{

    //Medium priority NON-Ui variables goes below....
    private ArrayList<TestDataPOJO> testsAl;




    //Least priority variables goes below....
    private final String TAG = "TasksListAdapter";



    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_test, null);

        return new Viewholder(view);
    }//onCreateViewHolder closes here....

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {

        holder.mTitleTxtV.setText(AppUtils.capitalizeSentence(testsAl.get(position).getTaskTitle()));
        holder.mTestDescRxrV.setText(testsAl.get(position).getTaskDescription());

    }//onBindViewHolder closes here....

    @Override
    public int getItemCount() {
        if(testsAl != null)
            return testsAl.size();
        else
            return 0;
    }//getItemCount closes here....


    public void setTestsAl(ArrayList<TestDataPOJO> testsAl) {
        this.testsAl = testsAl;
    }//setTestsAl closes here....


    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView mTitleTxtV;
        private TextView mTestDescRxrV;

        public Viewholder(View itemView) {
            super(itemView);

            mTitleTxtV = itemView.findViewById(R.id.taskTitleTxtV);
            mTestDescRxrV = itemView.findViewById(R.id.taskDescTxtV);
        }//Viewholder method closes here....
    }//Viewholder class closes here.....
}//TasksListAdapter closes here...
