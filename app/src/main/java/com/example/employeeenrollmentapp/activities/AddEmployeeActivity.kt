package com.example.employeeenrollmentapp.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.employeeenrollmentapp.R
import com.example.employeeenrollmentapp.databinding.ActivityAddEmployeeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import com.example.employeeenrollmentapp.database.DatabaseHandler
import com.example.employeeenrollmentapp.models.EmployeeModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class AddEmployeeActivity : AppCompatActivity(), View.OnClickListener {

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListenerDob: DatePickerDialog.OnDateSetListener
    private lateinit var dateSetListenerDoe: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mEmployeeDetails: EmployeeModel? = null

    private lateinit var binding: ActivityAddEmployeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_employee)
        binding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddEmployee)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarAddEmployee.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListenerDob =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(binding.etDob)
            }
        dateSetListenerDoe =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(binding.etDoe)
            }
        binding.etDob.setOnClickListener(this)
        binding.etDoe.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.et_dob -> {
                DatePickerDialog(this@AddEmployeeActivity,
                    dateSetListenerDob,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.et_doe -> {
                DatePickerDialog(this@AddEmployeeActivity,
                    dateSetListenerDoe,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery")
                pictureDialog.setItems(
                    pictureDialogItems
                ) { dialog, which ->
                    when (which) {
                        // Here we have create the methods for image selection from GALLERY
                        0 -> choosePhotoFromGallery()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {

                when {
                    binding.etEmployeeID.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter employee ID", Toast.LENGTH_SHORT).show()
                    }
                    binding.etName.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter employee name", Toast.LENGTH_SHORT)
                                .show()
                    }
                    binding.etDob.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please select date of birth", Toast.LENGTH_SHORT)
                                .show()
                    }
                    binding.etDoe.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please select date of employment", Toast.LENGTH_SHORT)
                                .show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                        // Assigning all the values to data model class.
                        val employeeModel = EmployeeModel(
                                if (mEmployeeDetails == null) 0 else mEmployeeDetails!!.id,
                                binding.etEmployeeID.text.toString(),
                                binding.etName.text.toString(),
                                binding.etDob.text.toString(),
                                binding.etDoe.text.toString(),
                                saveImageToInternalStorage.toString()
                        )

                        // Here we initialize the database handler class.
                        val dbHandler = DatabaseHandler(this)

                        if (mEmployeeDetails == null) {
                            val addEmployee = dbHandler.addEmployee(employeeModel)

                            if (addEmployee > 0) {
                                setResult(Activity.RESULT_OK);
                                Toast.makeText(this, "Employee added successfully", Toast.LENGTH_LONG).show()
                                finish()//finishing activity
                            }
                        }
//                        else {
//                            val updateHappyPlace = dbHandler.updateHappyPlace(employeeModel)
//
//                            if (updateHappyPlace > 0) {
//                                setResult(Activity.RESULT_OK);
//                                finish()//finishing activity
//                            }
//                        }
                    }
                }
            }
        }
    }



    private fun updateDateInView(textView: TextView){
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        textView.text = sdf.format(cal.time).toString()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null){
                    val contentURI = data.data
                    try {
                        // Here this is used to get an bitmap from URI
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                        binding.ivPlaceImage!!.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddEmployeeActivity, "Failed to load image!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                // Here after all the permission are granted launch the gallery to select and image.
                if (report!!.areAllPermissionsGranted()) {

                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )

                    startActivityForResult(galleryIntent, GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val IMAGE_DIRECTORY = "EmployeeImages"
    }
}