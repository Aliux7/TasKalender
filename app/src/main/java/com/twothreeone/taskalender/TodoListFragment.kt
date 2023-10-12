package com.twothreeone.taskalender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView.OnHSMenuClickListener


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TodoListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var horizontalScrollView: HorizontalScrollMenuView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun initMenu() {
        if (::horizontalScrollView.isInitialized) {
            horizontalScrollView.addItem("Default", 0)
            horizontalScrollView.addItem("Home", 0)
            horizontalScrollView.addItem("Work", 0)
            horizontalScrollView.addItem("Add", 0)

            horizontalScrollView.setOnHSMenuClickListener(OnHSMenuClickListener { menuItem, position ->
                textView.text = menuItem.text
            })
        } else {
            Log.e("YourTag", "horizontalScrollView is not initialized, cannot add items")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_todo_list, container, false)
        horizontalScrollView = rootView.findViewById(R.id.taskMenu)
        textView = rootView.findViewById((R.id.textView))
        initMenu()

        // You can now access horizontalScrollView
        return rootView
    }
}