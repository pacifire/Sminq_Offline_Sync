package offline_test.sminq.com.sminq_offlinelisting_test.network;

/**
 * Created by Pawan on 01/12/16.
 */

public interface NetworkResponseListener {

    void networkResponseSuccess(String response);

    void networkResponseFailure(String errorMessage);
}//NetworkResponseListener closes here.....
