package org.kalvari.ignite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.kalvari.ignite.R
import org.kalvari.ignite.model.RenunganModel
import java.util.Locale

class RenunganAdapter(var context: Context, var renunganModels: ArrayList<RenunganModel>) :
    RecyclerView.Adapter<RenunganAdapter.ViewHolder>(), Filterable {
    @JvmField
    var getRenunganModels: ArrayList<RenunganModel> =
        ArrayList(renunganModels)
    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.renungan_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rModel = renunganModels[position]
        holder.title.text = rModel.title
        holder.content.text = rModel.content
        holder.itemView.setOnClickListener { v: View? -> listener!!.onItemClick(rModel) }
    }

    override fun getItemCount(): Int {
        return renunganModels.size
    }

    override fun getFilter(): Filter {
        return renunganFilter
    }

    private val renunganFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<RenunganModel> = ArrayList()

            if (constraint.isEmpty()) {
                filteredList.addAll(getRenunganModels)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }

                for (item in getRenunganModels) {
                    if (item.title?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true || item.content?.lowercase(
                            Locale.getDefault()
                        )?.contains(filterPattern) == true
                    ) {
                        filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            renunganModels.clear()
            val filteredList = results.values as? List<RenunganModel>
            if (filteredList != null){
                renunganModels.addAll(filteredList)
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.list_item_title)
        var content: TextView = itemView.findViewById(R.id.list_item_content)
    }

    fun interface OnItemClickListener {
        fun onItemClick(renunganModel: RenunganModel?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
}
