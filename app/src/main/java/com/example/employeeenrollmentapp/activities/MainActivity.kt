package com.example.employeeenrollmentapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.employeeenrollmentapp.adapters.EmployeeAdapter
import com.example.employeeenrollmentapp.database.DatabaseHandler
import com.example.employeeenrollmentapp.databinding.ActivityMainBinding
import com.example.employeeenrollmentapp.models.EmployeeModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var employeeAdapter: EmployeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAddEmployee.setOnClickListener {
            val intent = Intent(this, AddEmployeeActivity::class.java)
            startActivityForResult(intent, ADD_EMPLOYEE_ACTIVITY_REQUEST_CODE)
        }

        binding.employeeSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                employeeAdapter.filter.filter(newText)
                return false
            }

        })

        getEmployeesListFromLocalDB()
    }

    private fun getEmployeesListFromLocalDB() {

        val dbHandler = DatabaseHandler(this)

        val getEmployeeList = dbHandler.getEmployeeList()

        if (getEmployeeList.size > 0) {
            binding.rvEmployeeList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE
            setupEmployeesRecyclerView(getEmployeeList)
        } else {
            binding.rvEmployeeList.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    private fun setupEmployeesRecyclerView(employeeList: ArrayList<EmployeeModel>) {

        binding.rvEmployeeList.layoutManager = LinearLayoutManager(this)
        binding.rvEmployeeList.setHasFixedSize(true)

        employeeAdapter = EmployeeAdapter(this, employeeList)
        binding.rvEmployeeList.adapter = employeeAdapter

        employeeAdapter.setOnClickListener(object :
            EmployeeAdapter.OnClickListener {
            override fun onClick(position: Int, model: EmployeeModel) {
                val intent = Intent(this@MainActivity, EmployeeDetailsActivity::class.java)
                intent.putExtra(EXTRA_EMPLOYEE_DETAILS, model)
                startActivity(intent)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // check if the request code is same as what is passed  here it is 'ADD_PLACE_ACTIVITY_REQUEST_CODE'
        if (requestCode == ADD_EMPLOYEE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getEmployeesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }

    companion object {
        private const val ADD_EMPLOYEE_ACTIVITY_REQUEST_CODE = 1
        internal const val EXTRA_EMPLOYEE_DETAILS = "extra_employee_details"
    }
}