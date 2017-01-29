package com.example.arushi.login;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity {
    EditText email, pass;
    String emailid, Pass;
    public static final String MyPREFERENCES = "MyPref" ;
    public static final String Name = "nameKey";
    public static final String Userid = "idKey";
    SharedPreferences sharedpreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        email=(EditText)findViewById(R.id.username);
        pass=(EditText)findViewById(R.id.pw);

        //Register button
            final TextView signup =(TextView)findViewById(R.id.reg);
        signup.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Register.class);
                                   startActivity(i);

            }
        });

        //Submit Button for login
        Button saveme=(Button)findViewById(R.id.save);
        saveme.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {



                //to check if any one of them is NULL
                if(email.length()==0 || pass.length()==0){

                    Toast.makeText(getApplicationContext(), "Invalid Input!", Toast.LENGTH_LONG).show();

                }
                //to check if pw is less than 8 characters
                else if(pass.length() < 8){

                    Toast.makeText(getApplicationContext(), "Password must be more than 8 characters!", Toast.LENGTH_LONG).show();

                }
                //to check if email contains @
                else if(!email.getText().toString().contains("@")) {
                    Toast.makeText(getApplicationContext(), "Invalid Input!", Toast.LENGTH_LONG).show();
                }

                else {

                emailid = email.getText().toString();
                Pass = pass.getText().toString();

                RequestQueue MyRequestQueue = Volley.newRequestQueue(getBaseContext());
                String url= "http://192.168.1.9:8000/checkmatch/";
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Context context = getApplicationContext();
                        SharedPreferences.Editor editor = sharedpreferences.edit();


                        if(response.equals("\"nomatch\""))
                        {
                           Toast.makeText(context, R.string.popup, LENGTH_LONG)
                                   .show();
                        }

                       else if(response.equals("\"nouser\""))
                        {
                            Toast.makeText(context, "The user is not registered!", LENGTH_LONG)
                                    .show();
                        }
                        else
                        {  //retrieve the userid and name from server and save it in shared preferences

                            String[] parts = response.split(",");
                            String[] t = parts[0].split(":");
                            String id=t[1];

                            String[] t1 = parts[1].split(":");
                            String name=t1[1];
                            name = name.replace("}","");
                            name = name.substring(1);
                            name= name.substring(0,name.length() - 1);


                            editor.putString(Userid, id);
                            editor.putString(Name, name);
                            editor.commit();

                            //for retrieving data from shared preferences
                            String value = sharedpreferences.getString(Userid, "");
                            System.out.println(value);

                            Intent i = new Intent(MainActivity.this, afterlogin.class);
                            startActivity(i);


                        }
                        }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Context context = getApplicationContext();
                       Toast.makeText(context, R.string.fail, LENGTH_LONG)
                                .show();
                        error.printStackTrace();
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("email", emailid);
                        MyData.put("pass", Pass);
                        return MyData;
                    };

                };

                MyRequestQueue.add(MyStringRequest);
                }


            }});



    }

}
