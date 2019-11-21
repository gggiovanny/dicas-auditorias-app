package com.dicas.auditorias.ui.auditorias

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicas.auditorias.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_status_list_dialog.*
import kotlinx.android.synthetic.main.fragment_status_list_dialog_item.view.*

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    StatusListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [StatusListDialogFragment.Listener].
 */
class StatusListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = StatusAdapter(arguments?.getInt(ARG_ITEM_COUNT)!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        /*
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }

         */
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onStatusClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.fragment_status_list_dialog_item,
            parent,
            false
        )
    ) {

        internal val text: TextView = itemView.text

        init {
            text.setOnClickListener {
                /*
                mListener?.let {
                    it.onStatusClicked(adapterPosition)
                    dismiss()
                }

                 */
            }
        }
    }

    private inner class StatusAdapter internal constructor(private val mItemCount: Int) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = position.toString()
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): StatusListDialogFragment =
            StatusListDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }
}
