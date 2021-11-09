package com.sunnyweather.android.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place

/**
 * 1.自定义Adapter，继承RecyclerView.Adapter<ClassName.ViewHolder>
 * 2.实现内部类ViewHolder(view:View)，继承RecyclerView.ViewHolder，绑定RecyclerView中的组件
 * 3.实现RecyclerView.Adapter接口中的方法：
 * 3-1：onCreateViewHolder——加载单个RecyclerView布局
 * 3-2：onBindViewHolder绑定单个RecyclerView数据
 * 3-3：getItemCount，返回数据数量
 *
 */

class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>):
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount(): Int {
        return placeList.size
    }


}