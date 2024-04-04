package com.example.farm2door;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PredictActivity extends AppCompatActivity {

    public static final String TAG = "PredictActivity";
    public static final String PREDICTION_SERVER_URL = "https://6e1b-41-209-57-187.ngrok-free.app/predict";
    RequestQueue requestQueue;
    GraphView graphView;
    ProgressBar progressBar;
    View progressBarLayout;
    Button btnRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_predict);

        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBar = progressBarLayout.findViewById(R.id.progressBar);
        btnRefresh = findViewById(R.id.btnRefresh);

        graphView = findViewById(R.id.graphView);
        requestQueue = Volley.newRequestQueue(this);

        // initialize loadin
        progressBar.setVisibility(View.VISIBLE);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{});
        graphView.setTitle("Predicted Demand");
        // give axis titles
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time (Months)");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Demand");
        graphView.setTitleTextSize(20);

        graphView.addSeries(series);

        // data to send to prediction server
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("duration", 7);
        // initiate POST request to prediction server
        JsonObjectRequest predictRequest = new JsonObjectRequest(Request.Method.POST, PREDICTION_SERVER_URL, new JSONObject(requestBody),
                (Response.Listener<JSONObject>) response -> {
                    // parse using JSON
                    try {
                        JSONArray predictions = response.getJSONArray("predictions");
                        double[] predictionValues = new double[predictions.length()];
                        for (int i = 0; i < predictions.length(); i++) {
                            predictionValues[i] = predictions.getDouble(i);
                        }

                        // plot data points for the graph
                        DataPoint[] dataPoints = new DataPoint[predictionValues.length];
                        for (int i = 0; i < predictionValues.length; i++) {
                            dataPoints[i] = new DataPoint(i, predictionValues[i]);
                        }

                        series.resetData(dataPoints);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Prediction success!", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Json Format Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                },
                (Response.ErrorListener) error -> {
                    // handle error
                    Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(predictRequest);

        btnRefresh.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            requestQueue.add(predictRequest);
        });
        //
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}