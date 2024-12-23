package com.te.celer.main_files.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.celer.R
import com.te.celer.main_files.models.SellerReceivedOrdersItems
import java.text.SimpleDateFormat
import java.util.TimeZone

class SellerReceivedOrdersAdapter(
    private val ordersItems: ArrayList<SellerReceivedOrdersItems>
) :
    RecyclerView.Adapter<SellerReceivedOrdersAdapter.SellerReceivedOrdersViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerReceivedOrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_received_order, parent, false)
        return SellerReceivedOrdersViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: SellerReceivedOrdersViewHolder, position: Int) {
        val currentItem = ordersItems[position]
        val date = java.util.Date(currentItem.time!!.toLong())
        val deliveryDate = java.util.Date(currentItem.deliveryDate!!.toLong())
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val dateFormat = SimpleDateFormat("MMM dd, yyyy")
        dateFormat.timeZone = timeZone

        holder.time.text = "Ordered on " + dateFormat.format(date)
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.buyerName.text = "Ordered by " + currentItem.buyerName
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)
        holder.productPrice.text = currentItem.price + " INR"
        holder.address.text = currentItem.address

        if (currentItem.status == "Pending") {
            holder.btnLayout.visibility = View.VISIBLE
            holder.deliveryDate.visibility = View.GONE
        } else if(currentItem.status == "Delivered") {
            holder.btnLayout.visibility = View.GONE
            holder.deliveryDate.visibility = View.VISIBLE
            holder.deliveryDate.text = "Delivered on " + dateFormat.format(deliveryDate)
        } else {
            holder.btnLayout.visibility = View.GONE
            holder.deliveryDate.visibility = View.VISIBLE
            holder.deliveryDate.text = "Will be delivered on " + dateFormat.format(deliveryDate)
        }

        val bundle = Bundle()
        bundle.putString("productName", currentItem.productName)
        bundle.putString("brandName", currentItem.brandName)
        bundle.putString("buyerName", currentItem.buyerName)
        bundle.putString("address", currentItem.address)
        bundle.putString("quantity", currentItem.quantity)
        bundle.putString("orderTime", dateFormat.format(date))
        bundle.putString("deliveryDate", dateFormat.format(deliveryDate))
        bundle.putString("productPrice", currentItem.price)
        bundle.putString("orderId", currentItem.orderId)
        bundle.putString("productImg", currentItem.productImageUrl)
        bundle.putString("buyerUid", currentItem.buyerUid)
        bundle.putString("sellerUid", currentItem.sellerUid)
        bundle.putString("status", currentItem.status)
        bundle.putString("user", "Seller")
        bundle.putString("confirmationCode", currentItem.confirmationCode)

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        holder.item.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.nav_order_details, bundle, navBuilder.build())
        }

    }

    override fun getItemCount(): Int {
        return ordersItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSellerReceivedOrders(updateSellerReceivedOrdersItems: ArrayList<SellerReceivedOrdersItems>) {
        ordersItems.clear()
        ordersItems.addAll(updateSellerReceivedOrdersItems)
        notifyDataSetChanged()
    }

    class SellerReceivedOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_order_seller)
        val brandName: TextView = itemView.findViewById(R.id.brandName_order_seller)
        val buyerName: TextView = itemView.findViewById(R.id.buyer_name_order_seller)
        val address: TextView = itemView.findViewById(R.id.address_order_seller)
        val time: TextView = itemView.findViewById(R.id.time_order_seller)
        val productImage: ImageView = itemView.findViewById(R.id.productImg_order_seller)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice_order_seller)
        val item: LinearLayout = itemView.findViewById(R.id.itemLayout_order_seller)
        val deliveryDate: TextView = itemView.findViewById(R.id.delivery_date_order_seller)
        val btnLayout: LinearLayout = itemView.findViewById(R.id.btn_layout_order_seller)
    }
}