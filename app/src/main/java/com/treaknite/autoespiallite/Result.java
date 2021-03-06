package com.treaknite.autoespiallite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Result extends AppCompatActivity {

    static {
        System.loadLibrary("keys");
    }

    public static native String getTokenKey();
    public static String token = new String(Base64.decode(getTokenKey(), Base64.DEFAULT));

    TextView txtString;
    Button goBack;
    public String url = "https://a.azure-eu-west.platform.peltarion.com/deployment/3262d868-dcd5-4a2e-9213-7ca9985561a5/forward";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public String postBody;
    public String dataBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        // Hiding action bar
        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        dataBase64 = bundle.getString("imageTextString");
        txtString = (TextView) findViewById(R.id.result_txtView_result);
        goBack = (Button) findViewById(R.id.result_btn_startAgain);

        postBody = "{\"rows\": [{\"image\": \"{" + dataBase64 + "}\" }]}";

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Result.this, Capture.class);
                finish();
                startActivity(intent);
            }
        });
    }

    void run() throws IOException {
        String apiToken = "Bearer " + token;

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, postBody);
        Log.d("Error", "Error chech here");
        Request request = new Request.Builder()
                .header("Authorization", apiToken)
                .header("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                Result.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Jason Parsing Error", myResponse);
                        txtString.setText(myResponse);
                    }
                });

            }
        });
    }
}