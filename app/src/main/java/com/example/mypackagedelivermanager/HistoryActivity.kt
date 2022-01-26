package com.example.mypackagedelivermanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypackagedelivermanager.Entities.Parcel
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mypackagedelivermanager.Firebase_Manager_Parcel.NotifyDataChange
import java.lang.Exception
import android.view.LayoutInflater
import androidx.fragment.app.Fragment


class HistoryActivity : Fragment() {
    private var parcelRecycleView: RecyclerView? = null
    private var parcelsList: MutableList<Parcel>? = null
    private var fireBaseM: Firebase_Manager_Parcel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_history, container, false)


        parcelRecycleView = view.findViewById(R.id.RV_parcel)
        parcelRecycleView!!.setHasFixedSize(true)
        parcelRecycleView!!.layoutManager = LinearLayoutManager(activity)
        fireBaseM = Firebase_Manager_Parcel()

        fireBaseM!!.notifyToParcelList(
            object : NotifyDataChange<MutableList<Parcel>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun OnDataChanged(obj: MutableList<Parcel>) {
                    if (parcelRecycleView!!.adapter == null) {
                        parcelsList = obj
                        parcelRecycleView!!.adapter = ParcelsRecycleViewAdapter()
                    } else
                        parcelRecycleView!!.adapter!!.notifyDataSetChanged()
                }

                override fun onFailure(exception: Exception?) {
                    Toast.makeText(
                        activity,
                        "error to get parcels list\n" + exception.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroy() {
        fireBaseM!!.stopNotifyToStudentList()
        super.onDestroy()
    }

    inner class ParcelsRecycleViewAdapter :
        RecyclerView.Adapter<ParcelsRecycleViewAdapter.ParcelViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelViewHolder {
            val v: View = LayoutInflater.from(activity).inflate(
                R.layout.parcel_cv, parent, false
            )
            return ParcelViewHolder(v)
        }

        override fun onBindViewHolder(holder: ParcelViewHolder, position: Int) {
            val parcel: Parcel = parcelsList!![position]
            holder.seder_tv!!.text = parcel.sender
            holder.pktId_tv!!.text = parcel.pktId
        }

        override fun getItemCount(): Int {
            return parcelsList!!.size
        }

        inner class ParcelViewHolder(itemView: View) : ViewHolder(itemView) {
            var seder_tv: TextView? = null
            var pktId_tv: TextView? = null

            init {
                seder_tv = itemView.findViewById(R.id.TV_sender)
                pktId_tv = itemView.findViewById(R.id.TV_pktId)
            }
        }
    }
}