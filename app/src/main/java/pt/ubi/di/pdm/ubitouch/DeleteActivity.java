package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeleteActivity extends AppCompatActivity {

    Button buttonDeleteAcc;
    TextInputEditText currentPassword;

    private String userId;
    private String token;

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");

        // log user id and token
        Log.d("Diogo", userId);
        Log.d("Diogo", token);

        buttonDeleteAcc = findViewById(R.id.buttonDeleteAcc);
        currentPassword = findViewById(R.id.currentPassword);
        back = findViewById(R.id.btnNotif);

        back.setOnClickListener(v -> {
            //Intent intent = new Intent(this, SecurityActivity.class);
            finish(); // Delete activity from stack
        });

        buttonDeleteAcc.setOnClickListener(v -> {
            deleteAcc();
        });
    }

    private void deleteAcc() {
        // check if the password is correct
        // if it is, delete the account
        String URL = "https://server-ubi-touch.herokuapp.com/users/delete/" + userId;

        String password = currentPassword.getText().toString();

        if (password.isEmpty()) {
            currentPassword.setError("Password is required");
            currentPassword.requestFocus();
            return;
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // log the json body
            Log.d("Diogo", jsonBody.toString());
            Log.d("Diogo", URL);
            Log.d("Diogo", token);
            Log.d("Diogo", userId);
            Log.d("Diogo", password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Diogo", response.toString());
                            try {
                                String msg = response.getString("message");
                                Log.d("Diogo", msg);
                                if (msg.equals("User deleted successfully")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this);
                                    builder.setTitle("Account deleted");
                                    builder.setMessage("Your account has been deleted");
                                    // on okay, go to the login activity
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // delete the user from the shared preferences
                                            SharedPreferences sharedPref = getSharedPreferences("user",
                                                    Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.clear();
                                            editor.apply();

                                            // go to the login activity
                                            Intent intent = new Intent(DeleteActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    builder.create().show();

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this);
                                    builder.setMessage("Wrong password")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                    // clear the password field
                                    currentPassword.setText("");
                                    // focus on the password field
                                    currentPassword.requestFocus();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Diogo", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        }
    }
}