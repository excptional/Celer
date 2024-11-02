package com.te.pewala.main_files.fragments

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
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.PendingRequestAdapter
import com.te.pewala.main_files.models.RedeemItems
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.db.LocalStorage

class Wallet : Fragment() {

    private var redeemItemsArray = arrayListOf<RedeemItems>()
    private lateinit var redeemAdapter: PendingRequestAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var backBtn: ImageView
    private lateinit var addMoney: CardView
    private lateinit var withdrawMoney: CardView
    private lateinit var walletBalance: TextView
    private lateinit var whiteView: View
    private lateinit var loaderWallet: LottieAnimationView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var pendingPaymentsBtn: CardView
    private lateinit var redeemRequestsBtn: CardView
    private lateinit var mainLayout: LinearLayout
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var grpPendingRequest: LinearLayout
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        backBtn = view.findViewById(R.id.back_btn_wallet)
        addMoney = view.findViewById(R.id.add_money_btn_wallet)
        withdrawMoney = view.findViewById(R.id.redeem_money_btn_wallet)
        walletBalance = view.findViewById(R.id.balance_wallet)
        whiteView = view.findViewById(R.id.whiteView_wallet)
        loaderWallet = view.findViewById(R.id.loader_wallet)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_wallet)
        mainLayout = view.findViewById(R.id.main_layout_wallet)
        recyclerview = view.findViewById(R.id.pending_request_recyclerview_wallet)
        grpPendingRequest = view.findViewById(R.id.grp_pending_request_wallet)
        pendingPaymentsBtn = view.findViewById(R.id.pending_payments_wallet)
        redeemRequestsBtn = view.findViewById(R.id.redeem_request_wallet)

        loadData()

        redeemAdapter = PendingRequestAdapter(requireContext(), redeemItemsArray)
        recyclerview.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = redeemAdapter

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        refreshLayout.setOnRefreshListener {
            loadData()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        addMoney.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_add, null, navBuilder.build())
        }

        withdrawMoney.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_redeem, null, navBuilder.build())
        }

        pendingPaymentsBtn.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.nav_pending_payments, null, navBuilder.build())
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        walletBalance.text = "₹${userdata!!["balance"]}"
        mainLayout.visibility = View.VISIBLE
        whiteView.visibility = View.GONE
        loaderWallet.visibility = View.GONE
        refreshLayout.isRefreshing = false
        name = userdata["name"]!!
        phone = userdata["phone"]!!
        dbViewModel.fetchRedeemRequest(userdata["uid"]!!)
        dbViewModel.redeemRequestDetails.observe(viewLifecycleOwner) { list2 ->
            if (list2.isNotEmpty()) fetchData(list2)
            else {
                recyclerview.visibility = View.GONE
                refreshLayout.isRefreshing = false
                grpPendingRequest.visibility = View.GONE
            }
        }
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        redeemItemsArray = arrayListOf()
        for (i in list) {
            if (i.exists()) {
                val redeemData = RedeemItems(
                    name,
                    phone,
                    i.getString("status"),
                    i.getString("request_send_time"),
                    i.getString("amount")
                )
                redeemItemsArray.add(redeemData)
            }
        }
        redeemAdapter.updateRedeemItems(redeemItemsArray)
        recyclerview.visibility = View.VISIBLE
        grpPendingRequest.visibility = View.VISIBLE
        refreshLayout.isRefreshing = false
    }

}