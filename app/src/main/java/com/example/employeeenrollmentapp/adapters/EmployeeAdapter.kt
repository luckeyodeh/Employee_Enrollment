package com.example.employeeenrollmentapp.adapters


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.employeeenrollmentapp.R
import com.example.employeeenrollmentapp.models.EmployeeModel
import kotlinx.android.synthetic.main.item_employee.view.*
import java.util.*
import kotlin.collections.ArrayList


open class EmployeeAdapter(
    private val context: Context,
    private var list: ArrayList<EmployeeModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

     var employeeFilterList: ArrayList<EmployeeModel>

    private var onClickListener: OnClickListener? = null

    init {
       employeeFilterList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_employee,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = employeeFilterList[position]

        if (holder is MyViewHolder) {

            holder.itemView.iv_place_image.setImageURI(Uri.parse(model.image))
            holder.itemView.tvEmployeeName.text = model.name
            holder.itemView.tvEmployeeID.text = model.employeeID

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {

//        return list.size
        return employeeFilterList.size
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: EmployeeModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    employeeFilterList = list
                } else {
                    val resultList = ArrayList<EmployeeModel>()
                    for (row in list) {
                        if (row.toString().toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    employeeFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = employeeFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                employeeFilterList = results?.values as ArrayList<EmployeeModel>
                notifyDataSetChanged()
            }

        }

    }
}

