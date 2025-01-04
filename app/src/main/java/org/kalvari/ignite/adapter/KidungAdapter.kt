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
import org.kalvari.ignite.model.KidungModel
import java.util.Locale

class KidungAdapter(var context: Context, var kidungModels: ArrayList<KidungModel>) :
    RecyclerView.Adapter<KidungAdapter.ViewHolder>(), Filterable {

    private var getKidungModels: ArrayList<KidungModel> = ArrayList(kidungModels)
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.kidung_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kidungModel = kidungModels[position]
        holder.title.text = kidungModel.title
        val hasBait = context.getString(R.string.bait_format, kidungModel.bait)
        holder.bait.text = hasBait
        holder.nada.text = kidungModel.nada

        kidungModel.koor?.let {
            holder.koor.text = context.getString(R.string.koor_format, it)
            holder.koor.visibility = View.VISIBLE
        } ?: run {
            holder.koor.visibility = View.GONE
        }

        holder.nolagu.text = kidungModel.no
        holder.itemView.setOnClickListener { onClickListener?.onKidungClick(kidungModel) }
    }

    override fun getItemCount(): Int = kidungModels.size

    override fun getFilter(): Filter = kidungFilter

    private val kidungFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = if (constraint.isEmpty()) {
                getKidungModels
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                getKidungModels.filter {
                    it.title?.lowercase(Locale.getDefault())?.contains(filterPattern) == true ||
                            it.no?.lowercase(Locale.getDefault())?.contains(filterPattern) == true
                }
            }
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            kidungModels.clear()
            val filteredList = results.values as? List<KidungModel>
            if (filteredList != null) kidungModels.addAll(filteredList)
            notifyDataSetChanged()
        }
    }

    fun updateData(newData: List<KidungModel>) {
        kidungModels.clear()
        kidungModels.addAll(newData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.list_item_title)
        var bait: TextView = itemView.findViewById(R.id.list_item_bait)
        var koor: TextView = itemView.findViewById(R.id.list_item_koor)
        var nada: TextView = itemView.findViewById(R.id.list_item_nada)
        var nolagu: TextView = itemView.findViewById(R.id.list_item_number)
    }

    fun interface OnClickListener {
        fun onKidungClick(kidungModel: KidungModel?)
    }

    fun setOnKidungClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }
}
