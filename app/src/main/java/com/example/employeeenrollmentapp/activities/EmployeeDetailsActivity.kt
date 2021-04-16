package com.example.employeeenrollmentapp.activities

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.employeeenrollmentapp.databinding.ActivityEmployeeDetailsBinding
import com.example.employeeenrollmentapp.models.EmployeeModel
import kotlinx.android.synthetic.main.activity_employee_details.*


class EmployeeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_employee_details)
        binding = ActivityEmployeeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var employeeDetailModel: EmployeeModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_EMPLOYEE_DETAILS)) {
            // get the Serializable data model class with the details in it
            employeeDetailModel =
                intent.getSerializableExtra(MainActivity.EXTRA_EMPLOYEE_DETAILS) as EmployeeModel
        }

        if (employeeDetailModel != null) {

            setSupportActionBar(toolbar_employee_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = employeeDetailModel.name

            toolbar_employee_detail.setNavigationOnClickListener {
                onBackPressed()
            }

            binding.ivPlaceImage.setImageURI(Uri.parse(employeeDetailModel.image))
            binding.tvEmployeeIDDetails.text = "Employee ID: ${employeeDetailModel.employeeID}"
            binding.tvNameDetails.text = "Employee Name: ${employeeDetailModel.name}"
            binding.tvDobDetails.text = "Date of Birth: ${employeeDetailModel.dateOfBirth}"
            binding.tvDoeDetails.text = "Date of employment: ${employeeDetailModel.dateOfEmployment}"
        }
    }
}