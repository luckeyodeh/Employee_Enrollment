package com.example.employeeenrollmentapp.models

import java.io.Serializable
import java.util.*

data class EmployeeModel (
        val id: Int,
        val employeeID: String,
        val name: String,
        val dateOfBirth: String,
        val dateOfEmployment: String,
        val image: String
) : Serializable