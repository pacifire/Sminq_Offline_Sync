package offline_test.sminq.com.sminq_offlinelisting_test.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import offline_test.sminq.com.sminq_offlinelisting_test.R;


/**
 * Created by Pawan on 01/12/16.
 *
 * This class is responsible to connect to the RequestQueue & send back the response to the requested Class.
 */

public class NetworkResponseHandler {

    //High priority UI variables goes below.....
//    private TextSwitcher mProgressTextSwitcher;
    private ProgressDialog progressDialog;



    //Medium priority NON-Ui variables goes below.....
    private NetworkResponseListener networkResponseListener;
    private final Context mContext;
    private final Object instanceOfClass;
    private final String url;
    private Map<String, String> paramsMap;
    private JSONObject paramsJSONObject;
    private String keyForParam;
    private View mProgressView;
    private final Map<String, String> headers;
    private Thread progressThread;//When user calls some API, use shud be able to see Custom Progress, so this Thread helps to switch the Progress Messages.....
    private final Boolean mDisplayProgress;//I



    //Least priority variables goes below....
    private final String TAG = "NetworkResponseHandler";
    private final int REQUEST_TIMEOUT = 20000;


    /**
     * This constructor should be used only if your parameters are in Map.
     *
     * @param context : Context of the Activity shud be passed here....@NonNull variable, i.e. compulsory field.
     * @param instanceOfClass : The instanceof the calling JAVA class is sent in this parameter. This is a Nullable, therefore optional.
     * @param displayProgress : If you want to display progress then send this flag as true, else send false. **/
    public NetworkResponseHandler(@NonNull Context context, @Nullable Object instanceOfClass, @NonNull String url,
                                  @Nullable Map<String, String> params, @NonNull View mProgressView,
                                  @Nullable Map<String, String> headers, @Nullable Boolean displayProgress) {
        this.mContext = context;
        this.instanceOfClass = instanceOfClass;
        this.url = url;
        this.paramsMap = params;
        this.mProgressView = mProgressView;
        this.headers = headers;
        this.mDisplayProgress = displayProgress;


        /////////............SETTING THE PROGRESS UI..............\\\\\\\\\\\\\\\
        setupProgressUI();
    }//NetworkResponseHandler constructor closes here....




    /**
     * This constructor should be used only if your parameters are in ArrayList.
     * **/
    public NetworkResponseHandler(@NonNull Context context, @Nullable Object instanceOfClass, @NonNull String url,
                                  @Nullable JSONObject params, @NonNull View mProgressView,
                                  @Nullable Map<String, String> headers) {
        this.mContext = context;
        this.instanceOfClass = instanceOfClass;
        this.url = url;
        this.paramsJSONObject = params;
        this.mProgressView = mProgressView;
        this.headers = headers;
//        this.mDisplayProgress = false;

        if(mProgressView == null)
            mDisplayProgress = false;
        else
            mDisplayProgress = true;


        /////////............SETTING THE PROGRESS UI..............\\\\\\\\\\\\\\\
        setupProgressUI();
    }//NetworkResponseHandler constructor closes here....



    private void setupProgressUI() {


        if(this.mProgressView != null) {
            this.mProgressView.setVisibility(View.VISIBLE);
            this.mProgressView.bringToFront();



//            ImageView mProgressGIF = (ImageView) mProgressView.findViewById(R.id.progressGIF_ImgView);
//            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mProgressGIF);
//            Glide.with(mContext).load(R.raw.progress_loader).into(imageViewTarget);
        }
//            mProgressTextSwitcher = (TextSwitcher) this.mProgressView.findViewById(R.id.progressTextSwitcher);
//            mProgressTextSwitcher.removeAllViews();
//            mProgressTextSwitcher.setFactory(new ProgressViewSwitcher(this.mContext, instanceOfClass));
//
//            if (AppConstants.PROGRESS_MESSAGES == null)
//                AppConstants.PROGRESS_MESSAGES = AppUtils.generateProgressMessages(this.mContext, instanceOfClass);
//
//            mProgressTextSwitcher.setText(AppConstants.PROGRESS_MESSAGES[1]);

//            progressThread = new Thread() {
//                int progressCount = 2;//Since in the progress we are displaying the initial 1st position of Array....
//
//                @Override
//                public void run() {
//                    try {
//                        while (!isInterrupted()) {
//                            Thread.sleep(mContext.getResources().getInteger(R.integer.progress_message_duration));
//                            ((Activity) mContext).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    if (progressCount == AppConstants.PROGRESS_MESSAGES.length)//Bcoz it shud not got ArrayIndex Out Of Bound Exception.
//                                        progressCount = 1;
//
//                                    mProgressTextSwitcher.setText(AppConstants.PROGRESS_MESSAGES[progressCount]);
//
//                                    progressCount++;
//                                }//run closes here.....
//                            });//runOnUiThread(new Runnable() closes here.....
//                        }//while (!isInterrupted()) closes here.....
//                    }//try closes here.....
//                    catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Log.e(TAG, "run: exception = " + e);
//
//                        stopProgressThread();
//                    }//catch closes here.....
//                }//run closes here....
//            };//progressThread = new Thread() closes here.....
//        }//if(displayProgress) closes here.....
//        else
//            Log.d(TAG, "NetworkResponseHandler: Skipping the Thread, bcoz developer does not wants to display the Progress....");

    }//setupProgressUI closes here.....

    public void setNetworkResponseListener(NetworkResponseListener networkResponseListener) {
        this.networkResponseListener = networkResponseListener;
    }//setNetworkResponseListener closes here.....





    public void executeJSONObjectPost(String requestTAG){

            Log.d(TAG, "url: " + url);
            Log.d(TAG, "headers: " + headers);

            if (paramsMap != null)
                Log.d(TAG, "paramsMap: " + paramsMap);
            else
                Log.d(TAG, "paramsJSONObject: " + paramsJSONObject);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, paramsJSONObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    successResponseHandler(response);

                    stopProgressThread();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "Pawan chk error: "+error);
                    errorResponseHandler(error);

                    stopProgressThread();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if(headers != null) {
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                    else {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return super.getHeaders();
                    }
                }//getHeaders closes here....

            };


            request.setTag(requestTAG);//Such that when Fragment/Activity closes the Requests with the same TAG are deleted/removed.....
            request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = RequestQueueSingleton.getInstance(mContext).getRequestQueue();
            RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

    }//executeJSONArrayPost closes here.....






    public void executePOST(String requestTAG) {
        Log.d(TAG, "url: "+url);
        Log.d(TAG, "headers: "+headers);

        if(paramsMap != null)
            Log.d(TAG, "paramsMap: "+paramsMap);
        else
            Log.d(TAG, "paramsJSONObject: "+paramsJSONObject);




        if(mDisplayProgress)
            startProgressThread();



        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
                successResponseHandler(response);

                stopProgressThread();
            }//onResponse closes here......
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                errorResponseHandler(volleyError);


                stopProgressThread();
            }//onErrorResponse closes here.....
        }){//new Response.ErrorListener() closes here....

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(paramsMap != null)
                    return paramsMap;
                else
                    return super.getParams();
            }//getParams closes here.....

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(headers != null) {
                    headers.put("Content-Type", "application/json"); /**....Added for Logout....**/
                    return headers;
                }
                else {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json"); /**....Added for Logout....**/
                    return headers;
                }
            }//getHeaders closes here....


            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8"; /**....Added for Logout....**/
            }
        };


        request.setTag(requestTAG);//Such that when Fragment/Activity closes the Requests with the same TAG are deleted/removed.....
        request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = RequestQueueSingleton.getInstance(mContext).getRequestQueue();
        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

    }//executePOST closes here....




    private void errorResponseHandler(VolleyError volleyError) {

//        if(volleyError == null){
////            stopProgressThread();
//            networkResponseListener.networkResponseFailure(mContext.getResources().getString(R.string.noInternetErrorMessage).toString().trim());
//        }//if(volleyError == null) closes here....
//        else {
            if (volleyError instanceof NoConnectionError) {
                if (mProgressView != null)
                    mProgressView.setVisibility(View.GONE);

                stopProgressThread();
                networkResponseListener.networkResponseFailure(mContext.getResources().getString(R.string.noInternetErrorString).toString().trim());
//            networkResponseListener.networkResponseFailure(new String(volleyError.networkResponse.data).toString().trim());
            }//if(volleyError instanceof NoConnectionError) closes here.....
            else if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {

                try {
                    JSONObject errorResponseObject = new JSONObject(new String(volleyError.networkResponse.data).toString().trim());
                    Log.d(TAG, "errorResponseObject: "+errorResponseObject);

                    String errorResponse = mContext.getResources().getString(R.string.noInternetErrorString);
                    if(errorResponseObject.has("error"))
                        errorResponse = errorResponseObject.getJSONObject("error").getString("message");
                    else if(errorResponseObject.has("message")) {
                        errorResponse = errorResponseObject.getString("message");
                    }








                    ///////////////...................TOKEN GETS EXPIRED, AFTER SOME TIME SO WE HAVE TO CALL THE LOGIN API & UPDATE THE TOKEN...............\\\\\\\\\\\\\\\\\\\\\

                    if(errorResponse.toString().trim().equalsIgnoreCase("Token has expired") || errorResponse.toString().trim().equalsIgnoreCase("token mismatch")){

//                        networkResponseListener.networkResponseFailure(errorResponse.toString().trim());


                        //Tell user that he has been Logged out.
//                        AlertDialog.Builder sessionExpiredBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
//                        sessionExpiredBuilder.setCancelable(false);
//                        sessionExpiredBuilder.setMessage(mContext.getResources().getString(R.string.sessionExpiredValidation));
//                        sessionExpiredBuilder.setPositiveButton(mContext.getResources().getString(R.string.okSpelling), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//
////                                new LogoutHandler(mContext).sessionExpired();
//                            }//onClick closes here.....
//
//                        });//ogoutBuilder.setPositiveButton closes here....
//                        sessionExpiredBuilder.show();


                    }//if Token is expired closes here.....
                    else
                        networkResponseListener.networkResponseFailure(errorResponse.toString().trim());


                    ///////////////...................TOKEN GETS EXPIRED, AFTER SOME TIME SO WE HAVE TO CALL THE LOGIN API & UPDATE THE TOKEN...............\\\\\\\\\\\\\\\\\\\\\





                }//try closes here....
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onErrorResponse: " + e);
                }//catch closes here.....
                finally {
                    volleyError = new VolleyError(new String(volleyError.networkResponse.data));

                    if (mProgressView != null)
                        mProgressView.setVisibility(View.GONE);

                    stopProgressThread();
                }//finally closes here....


            }//if(volleyError.networkResponse != null && volleyError.networkResponse.data != null) closes here....
            else
                Log.e(TAG, "Unhandled error response where volleyError = " + volleyError);
//                new UnhandledException("Unhandled error response where volleyError = " + volleyError);
//        }//else volleyerror is not null closes here.....

        stopProgressThread();
    }//errorResponseHandler closes here.....





    public void executeGET(String requestTAG){
        Log.d(TAG, "url: "+url);


        if(mDisplayProgress)
            startProgressThread();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                successResponseHandler(response);

                stopProgressThread();
            }//onResponse closes here.....
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


//                if(!(instanceOfClass instanceof DashboardActivity))
                errorResponseHandler(error);
//                else{
//                    Log.d(TAG, "error.networkResponse.statusCode: "+error.networkResponse.statusCode);
//                }

                stopProgressThread();
            }//onErrorResponse closes here.....
        }){//new Response.ErrorListener() closes here.....
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(headers != null) {
                    headers.put("Content-Type", "application/json");

                    Log.d(TAG, "getHeaders: "+headers);
                    return headers;
                }
                else {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");

                    Log.d(TAG, "getHeaders: "+headers);
                    return headers;
                }
            }//getHeaders closes here....

        };//new StringRequest closes here.....


        request.setTag(requestTAG);//Such that when Fragment/Activity closes the Requests with the same TAG are deleted/removed.....
        request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = RequestQueueSingleton.getInstance(mContext).getRequestQueue();
        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);
    }//executeGET closes here....





    public void executeJSONObjectPATCH(String requestTAG){

        Log.d(TAG, "url: " + url);
        Log.d(TAG, "headers: " + headers);

        if (paramsMap != null)
            Log.d(TAG, "paramsMap: " + paramsMap);
        else
            Log.d(TAG, "paramsJSONObject: " + paramsJSONObject);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, paramsJSONObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                successResponseHandler(response);

                stopProgressThread();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "Pawan chk error: "+error);
                errorResponseHandler(error);

                stopProgressThread();
            }
        }){//new Response.ErrorListener() closes here.....
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(headers != null) {
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
                else {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            }//getHeaders closes here....

        };


        request.setTag(requestTAG);//Such that when Fragment/Activity closes the Requests with the same TAG are deleted/removed.....
        request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = RequestQueueSingleton.getInstance(mContext).getRequestQueue();
        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

    }//executePATCH closes here.....




    public void executeDELETE(String requestTAG){

        Log.d(TAG, "url: " + url);
        Log.d(TAG, "headers: " + headers);

        if (paramsMap != null)
            Log.d(TAG, "paramsMap: " + paramsMap);
        else
            Log.d(TAG, "paramsJSONObject: " + paramsJSONObject);


        if(mDisplayProgress)
            startProgressThread();

        StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                successResponseHandler(response);

                stopProgressThread();
            }//onResponse closes here.....
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorResponseHandler(error);

                stopProgressThread();
            }//onErrorResponse closes here.....
        }){//new Response.ErrorListener() closes here....
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(headers != null) {
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
                else {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            }//getHeaders closes here....


            /**
             * We dont consider the params part in the Network Handler bcoz, delete method ignores the params part anyways.
             * **/
        };

        request.setTag(requestTAG);//Such that when Fragment/Activity closes the Requests with the same TAG are deleted/removed.....
        request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = RequestQueueSingleton.getInstance(mContext).getRequestQueue();
        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

    }//executeDelete closes here....





    private void successResponseHandler(String response) {
        Log.d(TAG, "onResponse: "+response);
        networkResponseListener.networkResponseSuccess(response);

        if(mProgressView != null)
            mProgressView.setVisibility(View.GONE);

        stopProgressThread();
    }//successResponseHandler closes here.....



    private void successResponseHandler(JSONObject responseJSONObject) {
        Log.d(TAG, "onResponse: "+responseJSONObject);
        networkResponseListener.networkResponseSuccess(responseJSONObject.toString().trim());

        if(mProgressView != null)
            mProgressView.setVisibility(View.GONE);

        stopProgressThread();
    }//successResponseHandler closes here.....


    public static void cancelRequests(@NonNull String tagName, @NonNull Context context){
        RequestQueueSingleton.getInstance(context).getRequestQueue().cancelAll(tagName);
    }//cancelRequests closes here.....


    /**
     * When we call API, we display a Progress screen & Progress Screen has a TextSwitcher in a Thread, so after the request-response is completed, We shud stop that Thread.
     * So this method is responsible to stop that particular Thread.**/
    private void stopProgressThread() {

        if (progressThread != null)
            if (progressThread.isAlive()) {
                progressThread.interrupt();

                if(mProgressView != null)
                    mProgressView.setVisibility(View.GONE);
            }
            else
                Log.w(TAG, "progressThread is not Alive: ");
        else
            Log.w(TAG, "progressThread is null: ");
    }//stopProgressThread closes here....


    private void startProgressThread() {
        if(mProgressView != null)
            mProgressView.setVisibility(View.VISIBLE);
    }//startProgressThread closes here.....


}//NetworkResponseHandler closes here.....
