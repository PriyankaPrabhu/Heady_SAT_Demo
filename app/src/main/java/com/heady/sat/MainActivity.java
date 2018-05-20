package com.heady.sat;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.heady.sat.api.DataRequester;
import com.heady.sat.db.DBHandler;
import com.heady.sat.model.Category;
import com.heady.sat.model.Product;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Product>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        new DataRequester().requestData(this);
//
//        backupDatabase(this);
        init();


    }

    private void init() {
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });



        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub



                Intent intent = new Intent(MainActivity.this,ProductCatlogueView.class);

                startActivity(intent);
                return false;
            }
        });
    }



    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Product>>();

        DBHandler dbHandler= DBHandler.getInstance(MainActivity.this);
        ArrayList<Category> categories= dbHandler.getCategory();

        // Adding child data
        for (int i = 0; i <categories.size() ; i++) {
            Category category= categories.get(i);
            listDataHeader.add(category.getName());
            List<Product> products = category.getProducts();
            listDataChild.put(listDataHeader.get(i), products);
        }



    }
    public boolean backupDatabase(Context context) {
        File from = context.getDatabasePath("EDATA");
        File to = getBackupDatabaseFile();
        try {
            FileUtils.copyFile(from, to);
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("Oho", "Error backuping up database: " + e.getMessage(), e);
        }
        return false;
    }


    public File getBackupDatabaseFile() {
        File dir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/backupCW");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "THE" + "_" + "EDATA");
    }
}
