package com.funk.jajo.Messaging;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.funk.jajo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This {@link MessageSender} will create a notification with the specified title and message
 * on the remote device that has subscribed to the Firebase Topic.
 */
public class MessageSender {
    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private final String serverKey = "key=" + "AAAASLAX29A:APA91bFpsSR4JJn_TWxvvTONUdlLix0O1B3I-KuoNid59mFT2GkyRrlrKSwOlujzFZJI9jWfAP0bwyvhETdVhhwr8BDzKuMY9Ajc18EONnPI1opK1zLc24DjJ4cxteEqOxuKNNdBl6dP";
    private final String contentType = "application/json";
    private final String TAG = "NOTIFICATION_TAG";

    private Context context;
    private String topic;
    private String title;
    private String message;

    /**
     * Create a new {@link MessageSender} and send a notification to the remote device
     * @param context The {@link Context} being associated with this {@link MessageSender}
     * @param msgTitle The title of the message that is to be sent
     * @param message The message text that is to be sent to the remote device
     */
    public MessageSender (Context context, String msgTitle, String message ) {
        this.context = context;
        this.title = msgTitle;
        this.message = message;

        this.topic = context.getString(R.string.FIRE_BASE_TOPIC);

        /* Create the Notification and send it to the remote device afterwards. */
        this.sendNotification(this.createNotification());
    }

    private JSONObject createNotification ( )  {
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();

        try {
            notificationBody.put ( "title", this.title );
            notificationBody.put ( "message", this.message );

            notification.put ( "to", this.topic );
            notification.put ( "data", notificationBody );
        } catch ( JSONException e ) {
            Log.e( TAG, "createNotification:  " + e.getMessage() );
        }

        return notification;
    }

    private void sendNotification ( JSONObject notification ) {
        if ( notification == null ) return;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: +" + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Request Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onErrorResponse: Didn't work");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
    }
}
