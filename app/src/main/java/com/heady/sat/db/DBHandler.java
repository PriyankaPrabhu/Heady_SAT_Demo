package com.heady.sat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.heady.sat.model.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class DBHandler extends SQLiteOpenHelper {

    Context context;
    private static DBHandler DBHnadlerInstance = null;
    private static final String DATABASE_NAME = "EDATA";
    private static final int DATABASE_VERSION = 1;
    static SQLiteDatabase db;
    public static String TBCATEGORY = "TBCAT";
    public static String TBPRODUCT = "TBPRO";
    public static String TBVARIANT = "TBVAR";
    public static String TBTAX = "TBTAX";
    public static String TBRANKING = "TBRANK";


    private DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = this.getWritableDatabase();

    }


    public static DBHandler getInstance(Context ctx) {

        if (DBHnadlerInstance == null) {
            DBHnadlerInstance = new DBHandler(ctx);
        }
        return DBHnadlerInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < newVersion) {

        }
    }


    // create all required tables

    public void createTables() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.execSQL("CREATE TABLE " + TBCATEGORY
                    + "( id TEXT, name TEXT, child_categories TEXT)");

            db.execSQL("CREATE TABLE " + TBPRODUCT
                    + "( CAT_ID TEXT, id TEXT, name TEXT, date_added TEXT)");

            db.execSQL("CREATE TABLE " + TBVARIANT
                    + "( PROD_ID TEXT, id TEXT, color TEXT, size TEXT, price TEXT)");

            db.execSQL("CREATE TABLE " + TBTAX
                    + "( PROD_ID TEXT, name TEXT, value TEXT)");

            db.execSQL("CREATE TABLE " + TBRANKING
                    + "( ranking TEXT, id TEXT, view_count TEXT, order_count TEXT, shares TEXT)");


        } catch (Exception exp) {

            exp.printStackTrace();
        } finally {
            // db.close();
            close();
        }
    }


    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = context.getDatabasePath(DATABASE_NAME) + "";

            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            // database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public void deletealltable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = null;
        try {
            cur = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table'", null);

            if (cur.moveToFirst()) {
                while (!cur.isAfterLast()) {
                    String tablename = cur
                            .getString(cur.getColumnIndex("name"));
                    if (!tablename.equals("sqlite_sequence"))
                        db.execSQL("DROP TABLE IF EXISTS " + tablename);

                    cur.moveToNext();
                }
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
            close();
        }
    }

    public void insertCategoryData(String jsonData)
    {
        try{
            JSONObject eObject = new JSONObject(jsonData);
            if(eObject != null)
            {
                String categoryString = eObject.getString("categories");
                JSONArray categoryArray = new JSONArray(categoryString);

                for(int k = 0; k < categoryArray.length (); ++k )
                {
                    JSONObject categoryObj = new JSONObject(categoryArray.getString(k));
                    JSONArray keys = categoryObj.names ();
                    ContentValues values = new ContentValues();
                    SQLiteDatabase db = this.getWritableDatabase();
                    String catId = null;
                    String prodData = null;

                    for (int i = 0; i < keys.length (); ++i) {

                        String key = keys.getString (i);
                        String value = categoryObj.getString (key);


                        if(key.equalsIgnoreCase("id"))
                            catId = value;
                        else if(key.equalsIgnoreCase("products"))
                            prodData = value;

                        if(catId!=null && prodData!=null)
                            insertProductData(catId, prodData);

                        if(!key.equalsIgnoreCase("products"))
                            values.put(key, value);
                    }


                    db.insert(TBCATEGORY, null, values);
                    close();
                }



            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void insertProductData(String catId, String prodData)
    {
        try{
            JSONArray productArray = new JSONArray(prodData);
            for(int k = 0; k < productArray.length (); ++k )
            {
                JSONObject productObject = new JSONObject(productArray.getString(k));
                if(productObject != null)
                {
                    ContentValues values = new ContentValues();
                    SQLiteDatabase db = this.getWritableDatabase();
                    String productId = null;
                    String variantData = null;
                    String taxData = null;

                    values.put("CAT_ID", catId);

                    JSONArray keys = productObject.names ();
                    for (int i = 0; i < keys.length (); ++i) {
                        String key = keys.getString (i);
                        String value = productObject.getString (key);


                        if(key.equalsIgnoreCase("id"))
                            productId = value;
                        else if(key.equalsIgnoreCase("variants"))
                            variantData = value;
                        else if(key.equalsIgnoreCase("tax"))
                            taxData = value;

                        if(productId!=null && variantData!=null)
                            insertVariantData(productId, variantData);

                        if(productId!=null && taxData!=null)
                            insertTaxData(productId, taxData);

                        if(!key.equalsIgnoreCase("variants") && !key.equalsIgnoreCase("tax"))
                            values.put(key, value);
                    }

                    db.insert(TBPRODUCT, null, values);
                }
            }


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void insertVariantData(String productId, String variantData)
    {
        try{
            JSONArray variantArray = new JSONArray(variantData);

            for(int k = 0; k < variantArray.length (); ++k )
            {
                JSONObject variantObject = new JSONObject(variantArray.getString(k));
                if(variantObject != null)
                {
                    ContentValues values = new ContentValues();
                    SQLiteDatabase db = this.getWritableDatabase();

                    values.put("PROD_ID", productId);

                    JSONArray keys = variantObject.names ();
                    for (int i = 0; i < keys.length (); ++i) {
                        String key = keys.getString (i);
                        String value = variantObject.getString (key);
                        values.put(key, value);
                    }

                    db.insert(TBVARIANT, null, values);
                }
            }



        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void insertTaxData(String productId, String taxData)
    {
        try{
            JSONObject taxObject = new JSONObject(taxData);
            if(taxObject != null)
            {
                ContentValues values = new ContentValues();
                SQLiteDatabase db = this.getWritableDatabase();

                values.put("PROD_ID", productId);

                JSONArray keys = taxObject.names ();
                for (int i = 0; i < keys.length (); ++i) {
                    String key = keys.getString (i);
                    String value = taxObject.getString (key);
                    values.put(key, value);
                }

                db.insert(TBTAX, null, values);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void insertRankingData(String jsonData)
    {
        try{
            JSONObject eObject = new JSONObject(jsonData);
            if(eObject != null) {

                String rankingString = eObject.getString("rankings");
                JSONArray rankingArray = new JSONArray(rankingString);

                for(int k = 0; k < rankingArray.length (); ++k )
                {
                    JSONObject rankingObj = new JSONObject(rankingArray.getString(k));
                    JSONArray keys = rankingObj.names();
                    ContentValues values = new ContentValues();
                    SQLiteDatabase db = this.getWritableDatabase();

                    for (int i = 0; i < keys.length (); ++i) {

                        String key = keys.getString(i);
                        String value = rankingObj.getString(key);
                        if(key.equalsIgnoreCase("products"))
                        {
                            JSONArray proArray = new JSONArray(value);

                            for (int l = 0; l < keys.length (); ++l) {
                                JSONObject proObj = new JSONObject(proArray.getString(l));
                                JSONArray proObjkey = proObj.names();
                                if(proObj != null)
                                {
                                    for (int j = 0; j < proObjkey.length (); ++j) {
                                        String key1 = proObjkey.getString(j);
                                        String value1 = proObj.getString(key1);

                                        values.put(key1, value1);
                                    }
                                }
                            }
                        }
                        else {
                            values.put(key, value);
                        }
                    }

                    db.insert(TBRANKING, null, values);
                    close();
                }


            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public Cursor getAllBooks(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT *  FROM " + tableName;

        Cursor cur = null;
        try {
            cur = db.rawQuery(query, new String[] {});
            int noOfRows = cur.getCount();
            if (noOfRows > 0) {
            }
            cur.moveToFirst();
        } catch (Exception e) {
            System.out.println("In getallData block------->" + e);
        } finally {
            // if (cur != null && !cur.isClosed())
            // cur.close();
            // db.close();
            // close();
        }
        return cur;

    }
    public Cursor getCusrsor(String SQl) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(SQl, new String[]{});

    }

    public ArrayList<Category> getCategory() {
        ArrayList<Category> list = new ArrayList<>();

        Cursor cursor = null;
        try {
            String query = "Select id,name,child_categories from TBCAT";
            cursor = getCusrsor(query);
            cursor.moveToFirst();

            Category catDao;

            if (cursor.getCount() != 0) {
                do {
                    catDao = new Category();

                    catDao.setId(cursor.getString(cursor.getColumnIndex("id")));
                    catDao.setName(cursor.getString(cursor.getColumnIndex("name")));
//                    catDao.setChildCategories(cursor.getString(cursor.getColumnIndex("ROLECODE")));


                    list.add(catDao);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
