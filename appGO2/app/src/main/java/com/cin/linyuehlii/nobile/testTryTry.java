package com.cin.linyuehlii.nobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class testTryTry extends AppCompatActivity {
    private EditText etName;
    private EditText etTime;

    private MySQLiteOpenHelper helper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_activity);
        findViews();
        if (helper == null) {
            helper = new MySQLiteOpenHelper(this);
        }
    }

    private void findViews() {

        etName = (EditText) findViewById(R.id.etName);
        etTime = (EditText) findViewById(R.id.etTime);
    }


    public void onFinishInsertClick(View view) {
        String Name = etName.getText().toString().trim();
     String Time = etTime.getText().toString().trim();

    if (Name.length() <= 0) {
            Toast.makeText(this,"nameFail",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (Time == null) {
            Toast.makeText(this, "TimeFail",
                    Toast.LENGTH_SHORT).show();
            return;
        }

      //  ArrayList<Spot> arrayList=new ArrayList<>();
        //arrayList.add();
        Log.d("1","1");
        Spot spot1 = new Spot("6/18", 99, 99, 99);
        long rowId = helper.insert(spot1);
        if (rowId != -1) {
            Toast.makeText(this, "InsertSuccess",
                    Toast.LENGTH_SHORT).show();
            Log.d("2","2");
        } else {
            Toast.makeText(this, "InsertFail",
                    Toast.LENGTH_SHORT).show();
            Log.d("3","3");
        }
        finish();
    }

    public void onCancelClick(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.close();
        }
    }
}