package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.TransactionHistoryAdapter
import com.te.celer.main_files.models.TransactionHistoryItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.LocalStorage

class History : Fragment() {

    private val localStorage = LocalStorage()
    private lateinit var transactionHistoryAdapter: TransactionHistoryAdapter
    private var transactionHistoryItems = arrayListOf<TransactionHistoryItems>()
    private lateinit var historyShimmer: ShimmerFrameLayout
    private lateinit var recyclerview: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var nothingFoundText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var uid: String
    private lateinit var phone: String
    private lateinit var backBtn: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        historyShimmer = view.findViewById(R.id.history_shimmer)
        recyclerview = view.findViewById(R.id.recyclerView_history)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_history)
        mainLayout = view.findViewById(R.id.mainLayout_history)
        nothingFoundText = view.findViewById(R.id.nothingFound_history)
        backBtn = view.findViewById(R.id.back_btn_history)

        historyShimmer.startShimmer()
        historyShimmer.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        transactionHistoryAdapter =
            TransactionHistoryAdapter(requireContext(), transactionHistoryItems)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = transactionHistoryAdapter

        loadData()

        refreshLayout.setOnRefreshListener {
            historyShimmer.startShimmer()
            historyShimmer.visibility = View.VISIBLE
            mainLayout.visibility = View.GONE
            getData()
        }

        backBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_home)
//            requireActivity().onBackPressed()
        }

        return view
    }

    private fun getData() {
        dbViewModel.fetchTransactionDetails(uid)
        dbViewModel.transactionDetails.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                nothingFoundText.visibility = View.GONE
                fetchData(list)
            } else {
                historyShimmer.visibility = View.GONE
                mainLayout.visibility = View.GONE
                nothingFoundText.visibility = View.VISIBLE
                refreshLayout.isRefreshing = false
            }
        }
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        transactionHistoryItems = arrayListOf()
        for (i in list) {
            if (i.exists()) {
                val transactionData = TransactionHistoryItems(
                    i.getString("amount"),
                    i.getString("operation"),
                    i.getString("tid"),
                    i.getString("time"),
                    i.getString("operator_name"),
                    i.getString("operator_phone")
                )
                transactionHistoryItems.add(transactionData)

            }
        }
        transactionHistoryAdapter.updateTransactionHistory(transactionHistoryItems)
        historyShimmer.clearAnimation()
        historyShimmer.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
        refreshLayout.isRefreshing = false
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!
        getData()

    }
}