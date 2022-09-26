package com.rizexor.cmrl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";

    private TextView txt;

    private String token;

    private final OkHttpClient client = new OkHttpClient();

    private WebView webView;

    private TextView usernameInpt;
    private TextView passwordInpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = findViewById(R.id.result);
        usernameInpt = findViewById(R.id.uname);
        passwordInpt = findViewById(R.id.pwd);
        webView = findViewById(R.id.paymentsite);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://google.com");
        webView.getSettings().setJavaScriptEnabled(true);
    }

    public void login(View view) throws Exception {
        Log.e(TAG, "Hello");

        MediaType mediaType = MediaType.parse("application/json");
        String username = usernameInpt.getText().toString();
        String password = passwordInpt.getText().toString();
        String userpass = Encryption.encryptData(password, "44 02 d7 16 87 b6 bc 2c 10 89 c3 34 9f dc 19 fb 3d fb ba 88 24 af fb 76 76 e1 33 79 26 cd d6 02");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"username\": \"" + username + "\",\n\t\"userpassword\": \"" + userpass + "\",\n\t\"secretcode\": \"$3cr3t\",\n\t\"appv\": \"ANDROID|2.4.4\"\n}");
        Request request = new Request.Builder()
                .url("https://apiprod.chennaimetrorail.org/v4/api/Passenger/Login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        txt.setText("LOADING...");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                MainActivity.this.runOnUiThread(new Runnable() {
                    final String res = response.body().string();

                    JSONObject obj;
                    String authedURL;

                    {
                        try {
                            obj = new JSONObject(res);
                            MainActivity.this.token = obj.getString("QRjwttoken");
                            authedURL = obj.getString("QRurl") + obj.getString("QRjwttoken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void run() {
                        txt.setText("Success");
                        webView.loadUrl(authedURL);
                    }
                });
            }
        });
    }
}