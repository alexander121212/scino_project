package com.example.scino_book_project.helper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper  {

    // путь к базе данных вашего приложения
    private static String DB_PATH = "/data/data/com.example.scino_book_project/databases/";

    private static String DB_NAME = "database.db";
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    private static final String TABLE_LIST = "lists";
    private static final String TABLE_LIST_BOOK_AUTHOR = "list_book_author";

    public static final String COLUMN_LISTNAME = "listname";
    public static final String COLUMN_AUTHOR = "nameauthor";
    public static final String COLUMN_READED = "readed";
    public static final String COLUMN_BOOK = "namebook";


    /**
     * Конструктор
     * Принимает и сохраняет ссылку на переданный контекст для доступа к ресурсам приложения
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }
    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();

        if(dbExist){
            //ничего не делать - база уже есть
        }else{
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }



    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //база еще не существует
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException{
        //Открываем локальную БД как входящий поток
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    public void openDataBase() throws SQLException {
        //открываем БД
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

        @Override
        public synchronized void close(){
            if(myDataBase != null)
                myDataBase.close();
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    
    
    public int addList(String nameList)
    {
        if(!nameList.isEmpty() && nameList.charAt(0)!=' '){
            ContentValues values = new ContentValues();
            String columnListname = COLUMN_LISTNAME;
            values.put(columnListname, nameList);
            SQLiteDatabase db;



            try {
                db = this.getWritableDatabase();
                Cursor cursor = db.query(TABLE_LIST, null, null, null, null, null, null);

                boolean flag = true;
                for(int i=0;i<cursor.getCount() && cursor!=null ;i++) {
                    if (cursor.moveToFirst()) {
                        cursor.move(i);
                        if(cursor.getString(0).equals(nameList))
                        {
                            flag=false;
                            break;
                        }
                    }
                }

                if(flag) {
                db.insert(TABLE_LIST, null, values);
                    cursor.close();
                    db.close();
                    return 1;
                }
                else return 0;

            }
            catch (SQLiteException ex)
            {
                throw new Error("Unable to add database");
            }

        }
        else
            return -1;

    }


    public int addBookAuthorList(String nameBook, String nameAuthor, String nameList)
    {
        if(nameList.isEmpty() || nameList.charAt(0)==' ' || nameBook.isEmpty() || nameBook.charAt(0)==' ' || nameAuthor.isEmpty() || nameAuthor.charAt(0)==' '){
            return -1;
        }


        ContentValues values = new ContentValues();
        values.put(COLUMN_LISTNAME, nameList);
        values.put(COLUMN_AUTHOR, nameAuthor);
        values.put(COLUMN_READED, "no");
        values.put(COLUMN_BOOK, nameBook);

        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();

            Cursor cursor = db.query(TABLE_LIST_BOOK_AUTHOR, null, null, null, null, null, null);

            boolean flag = true;
            for(int i=0;i<cursor.getCount() && cursor!=null ;i++) {
                if (cursor.moveToFirst()) {
                    cursor.move(i);
                    if(cursor.getString(0).equals(nameList) && cursor.getString(1).equals(nameAuthor) && cursor.getString(3).equals(nameBook))
                    {
                        flag=false;
                        break;
                    }
                }
            }

            if(flag) {
             db.insert(TABLE_LIST_BOOK_AUTHOR, null, values);
                cursor.close();
                db.close();
                return 1;
            }
            else{
                cursor.close();
                db.close();
                return 0;
            }


        }
        catch (SQLiteException ex)
        {
            throw new Error("Unable to add database");
        }

    }

    public ArrayList<String> getLists(){
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_LIST, null, null, null, null, null, null);

            ArrayList<String> buffer = new ArrayList<>();
            cursor.getCount();

            for(int i=0;i<cursor.getCount() && cursor!=null ;i++) {
                if (cursor.moveToFirst()) {
                    cursor.move(i);
                    buffer.add(cursor.getString(cursor.getColumnIndex(COLUMN_LISTNAME)));
                }
            }


            cursor.close();
            db.close();
            return buffer;
        }
        catch (SQLiteException ex)
        {
            throw new Error("Unable to get names of lists");
        }


    }



    public int switchReadable(String nameBook, String nameAuthor, String nameList, boolean swch)
    {

            if (nameBook.isEmpty() || nameBook.charAt(0) == ' '||
                    nameAuthor.isEmpty() || nameAuthor.charAt(0) == ' '||
                    nameList.isEmpty() || nameList.charAt(0) == ' ') return -1;

            SQLiteDatabase db;
            try {
                db = this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                if(swch)
                contentValues.put(COLUMN_READED, "yes");
                else
                    contentValues.put(COLUMN_READED, "no");

                db.update(TABLE_LIST_BOOK_AUTHOR, contentValues, " namebook='" + nameBook + "'", null);
                db.close();
                if (swch) return 1;
                else return 0;

            } catch (SQLiteException ex) {
                throw new Error("Unable to add database");
            }


    }

    public  ArrayList<ArrayList<String>>  getBookList(String list){
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            ArrayList<ArrayList<String>> buffer = new  ArrayList<>();

            Cursor cursor = db.query(TABLE_LIST_BOOK_AUTHOR, null, null, null, null, null, null);


            for(int i=0;i<cursor.getCount() && cursor!=null ;i++)
                if (cursor.moveToFirst()) {
                    cursor.move(i);

                    if (cursor.getString(0).equals(list)) {
                        ArrayList<String> data = new ArrayList<>();
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_READED)));
                        buffer.add(data);
                    }
                }
            cursor.close();
            db.close();
            return buffer;
        }
        catch (SQLiteException ex) {
            throw new Error("Unable to create list database");
        }
    }


    public  ArrayList<ArrayList<String>>  getBookByName(String bookName){
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            ArrayList<ArrayList<String>> buffer = new  ArrayList<>();

            Cursor cursor = db.query(TABLE_LIST_BOOK_AUTHOR, null, null, null, null, null, null);


            for(int i=0;i<cursor.getCount() && cursor!=null ;i++)
                if (cursor.moveToFirst()) {
                    cursor.move(i);

                    if (cursor.getString(3).equals(bookName) || cursor.getString(3).contains(bookName)) {
                        ArrayList<String> data = new ArrayList<>();
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_READED)));
                        buffer.add(data);
                    }
                }
            cursor.close();
            db.close();
            return buffer;
        }
        catch (SQLiteException ex) {
            throw new Error("Unable to create list database");
        }
    }


    public  ArrayList<ArrayList<String>>  getReadedBookList(boolean swch){
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            ArrayList<ArrayList<String>> buffer = new  ArrayList<>();

            Cursor cursor = db.query(TABLE_LIST_BOOK_AUTHOR, null, null, null, null, null, null);


            for(int i=0;i<cursor.getCount() && cursor!=null ;i++)
                if (cursor.moveToFirst()) {
                    cursor.move(i);

                    if (cursor.getString(2).equals("yes") && swch) {
                        ArrayList<String> data = new ArrayList<>();
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_READED)));
                        buffer.add(data);
                    }
                    else if (cursor.getString(2).equals("no") && !swch) {
                        ArrayList<String> data = new ArrayList<>();
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_READED)));
                        buffer.add(data);


                    }
                }
            cursor.close();
            db.close();
            return buffer;
        }
        catch (SQLiteException ex) {
            throw new Error("Unable to create list database");
        }
    }


    public  ArrayList<ArrayList<String>>  getByBookAndListAndCategory(String bookName, String listName, String category){
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            ArrayList<ArrayList<String>> buffer = new  ArrayList<>();

            Cursor cursor = db.query(TABLE_LIST_BOOK_AUTHOR, null, null, null, null, null, null);


            for(int i=0;i<cursor.getCount() && cursor!=null ;i++)
                if (cursor.moveToFirst()) {
                    cursor.move(i);

                    if (cursor.getString(0).equals(listName) && cursor.getString(2).equals(category) && cursor.getString(3).equals(bookName)) {
                        ArrayList<String> data = new ArrayList<>();
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
                        data.add(cursor.getString(cursor.getColumnIndex(COLUMN_READED)));
                        buffer.add(data);
                    }
                }
            cursor.close();
            db.close();
            return buffer;
        }
        catch (SQLiteException ex) {
            throw new Error("Unable to create list database");
        }
    }

    public boolean deleteList(String listName) {
        SQLiteDatabase db;

        try {
            db = this.getWritableDatabase();

            db.delete(TABLE_LIST, "listname = ?", new String[]{listName});
            db.close();
            return  true;
        }
        catch (SQLiteException ex)
        {
            return false;
        }

    }

    public boolean deleteBook(String bookName){
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_LIST_BOOK_AUTHOR, COLUMN_BOOK + " = ?", new String[]{bookName});
            db.close();


            return  true;
        }
        catch (SQLiteException ex)
        {
            return false;
        }
    }

    public int getCount()
    {
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LIST, null);
            int i =0;
            while (cursor.moveToNext())
            {
                i++;
            }
            cursor.close();
            db.close();
            return i;
        }
        catch (SQLiteException ex)
        {
            return -1;
        }
    }

    public int getCountList(String nameList)
    {
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LIST_BOOK_AUTHOR, null);
            int i =0;
            while (cursor.moveToNext())
            {
                if (cursor.getString(0).equals(nameList)) i++;
            }
            cursor.close();
            db.close();
            return i;
        }
        catch (SQLiteException ex)
        {
            return -1;
        }
    }


    public boolean replaceBook(String oldName, String newName)
    {
        if (oldName.isEmpty() || oldName.charAt(0) == ' ') return false;
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("namebook",newName);
            db.update(TABLE_LIST_BOOK_AUTHOR, contentValues, "namebook= '" + oldName + "'", null);
            db.close();


            return true;
        } catch (SQLiteException ex) {
            throw new Error("Unable to add database");
        }
    }

    public boolean replaceAuthor(String oldName, String newName)
    {
        if (oldName.isEmpty() || oldName.charAt(0) == ' ') return false;
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("nameauthor", newName);
            db.update(TABLE_LIST_BOOK_AUTHOR, contentValues, "nameauthor= '" + oldName + "'", null);
            db.close();


            return true;
        } catch (SQLiteException ex) {
            throw new Error("Unable to add database");
        }
    }




    public boolean replaceList(String nameListToReplace, String listName) {
        if (nameListToReplace.isEmpty() || nameListToReplace.charAt(0) == ' ') return false;
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("listname", listName);
            db.update(TABLE_LIST, contentValues, "listname= '" + nameListToReplace + "'", null);
            db.close();

            return true;
        } catch (SQLiteException ex) {
            throw new Error("Unable to add database");
        }
    }

    public boolean replaceListBook(String nameListToReplace, String listName) {
        if (nameListToReplace.isEmpty() || nameListToReplace.charAt(0) == ' ') return false;
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("listname",listName);
            db.update(TABLE_LIST_BOOK_AUTHOR, contentValues, "listname= '" + nameListToReplace + "'", null);
            db.close();


            return true;
        } catch (SQLiteException ex) {
            throw new Error("Unable to add database");
        }
    }

}
