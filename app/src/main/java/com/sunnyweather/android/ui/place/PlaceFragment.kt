package com.sunnyweather.android.ui.place

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.weather.WeatherActivity

class PlaceFragment : Fragment(), LifecycleObserver {
    val viewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    // 当activity的onCreate方法调用的时候，fragment会监听到，并执行onCreate方法
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
     fun onCreate() {
        if (viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        // fragment布局
        val layoutManager = LinearLayoutManager(activity)
        val recyclerView: RecyclerView? = activity?.findViewById(R.id.recyclerView)
        val searchPlaceEdit: EditText? = activity?.findViewById(R.id.searchPlaceEdit)
        val bgImageView: ImageView? = activity?.findViewById(R.id.bgImageView)

        // recyclerView设置
        recyclerView?.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView?.adapter = adapter

        // 如果输入的文字发生变化，进行搜索
        searchPlaceEdit?.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView?.visibility = View.GONE
                bgImageView?.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        // 设置监听器，当placeLiveData发生变化，表示获取到了新的地点，从result中获取数据后，进行更新
        viewModel.placeLiveData.observe(this, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                // 展示recyclerView
                recyclerView?.visibility = View.VISIBLE
                // 隐藏背景图片
                bgImageView?.visibility = View.GONE

                // 清除原有数据并更新
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)

                // 通知adapter进行数据更新
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        // 完成任务，删除监听
        activity?.lifecycle?.removeObserver(this)
    }

    /**
     * fragment的生命周期
     * 1. onAttach
     * 2. onCreate
     * 3. onCreateView
     * 4. onActivityCreated
     * 5. onStart
     * 6. onResume // fragment可见
     * 7. onPause
     * 8. onStop
     * 9. onDestroyView
     * 10. onDestroy
     * 11. onDetach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 开始阶段，添加为activity监听器
        activity?.lifecycle?.addObserver(this)
    }
}
