package com.example.employeeenrollmentapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.employeeenrollmentapp.models.EmployeeModel

class DatabaseHandler (context: Context):
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EmployeeDatabase"
        private const val TABLE_EMPLOYEE = "EmployeeTable"

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_EMPLOYEE_ID = "employeeID"
        private const val KEY_NAME = "name"
        private const val KEY_DATE_OF_BIRTH = "dateOfBirth"
        private const val KEY_DATE_OF_EMPLOYMENT = "dateOfEmployment"
        private const val KEY_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_EMPLOYEE_TABLE = ("CREATE TABLE " + TABLE_EMPLOYEE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EMPLOYEE_ID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_DATE_OF_BIRTH + " TEXT,"
                + KEY_DATE_OF_EMPLOYMENT + " TEXT,"
                + KEY_IMAGE + " TEXT)")
        db?.execSQL(CREATE_EMPLOYEE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_EMPLOYEE")
        onCreate(db)
    }

    fun addEmployee(employee: EmployeeModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_EMPLOYEE_ID, employee.employeeID)
        contentValues.put(KEY_NAME, employee.name)
        contentValues.put(KEY_DATE_OF_BIRTH, employee.dateOfBirth)
        contentValues.put(KEY_DATE_OF_EMPLOYMENT, employee.dateOfEmployment)
        contentValues.put(KEY_IMAGE, employee.image)

        // Inserting Row
        val result = db.insert(TABLE_EMPLOYEE, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return result
    }

    fun getEmployeeList(): ArrayList<EmployeeModel> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val employeeList: ArrayList<EmployeeModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_EMPLOYEE" // Database select query

        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val employee = EmployeeModel(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_EMPLOYEE_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE_OF_BIRTH)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE_OF_EMPLOYMENT)),
                            cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                    )
                    employeeList.add(employee)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return employeeList
    }

}