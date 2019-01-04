package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.jesta.R
import com.jesta.data.Mission
import com.jesta.data.Relation
import kotlinx.android.synthetic.main.jesta_status.view.*

import java.util.ArrayList

class StatusAdapter(
) : RecyclerView.Adapter<StatusAdapter.RecyclerHolder>() {

    private val list = ArrayList<Pair<Relation,Mission>>()
    private val expansionsCollection = ExpansionLayoutCollection()

    init {
        expansionsCollection.openOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        val status = list[position]

        holder.bind(list[position])

        expansionsCollection.add(holder.expansionLayout)

        holder.title.text = status.second.title
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setItems(items: List<Pair<Relation,Mission>>) {
        this.list.addAll(items)
        notifyDataSetChanged()
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var expansionLayout: ExpansionLayout
            internal set

        var title: TextView
            internal set

        init {
            expansionLayout = itemView.expansionLayout
            title = itemView.jesta_status_title
        }

        fun bind(`object`: Any) {
            expansionLayout.collapse(false)
        }

        companion object {

            private val LAYOUT = R.layout.jesta_status

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(LAYOUT, viewGroup, false))
            }
        }
    }
}