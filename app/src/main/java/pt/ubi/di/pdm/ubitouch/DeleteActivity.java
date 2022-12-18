package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DeleteActivity extends AppCompatActivity {
    Button buttonDeleteAcc;
    TextInputEditText currentPassword;

    private String userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivate);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");

        // log user id and token
        Log.d("Diogo", userId);
        Log.d("Diogo", token);

        buttonDeleteAcc = findViewById(R.id.buttonDeleteAcc);
        currentPassword = findViewById(R.id.currentPassword);

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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeactivateActivity.this);
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
                                            Intent intent = new Intent(DeactivateActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    builder.create().show();

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeactivateActivity.this);
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