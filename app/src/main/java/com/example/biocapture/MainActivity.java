package com.example.biocapture;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.annotation.SuppressLint;
import java.sql.Connection;
import java.sql.DriverManager;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    @SuppressLint("NewApi")
    public static Connection connectionclass(){
        Connection con=null;
        String ip="172.1.1.0",port="54574",databasename="NKABUNEDB";
        StrictMode.ThreadPolicy tp= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl="jdbc:jtds:sqlserver://"+ip+":"+port+";databasename="+databasename+";integratedSecurity=true";
            con= DriverManager.getConnection(connectionUrl);
        }
        catch (Exception exception){
            Log.e("Error",exception.getMessage());
        }
        return con;
    }
   }