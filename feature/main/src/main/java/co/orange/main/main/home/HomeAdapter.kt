package co.orange.main.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.orange.core.R
import co.orange.core.util.ItemDiffCallback
import co.orange.domain.entity.response.ProductModel
import co.orange.main.databinding.ItemHomeBannerBinding
import co.orange.main.databinding.ItemProductBinding

class HomeAdapter(
    private val bannerClick: (String) -> (Unit),
    private val productClick: (String, Int) -> (Unit),
    private val likeClick: (String, Boolean, Int) -> (Unit),
) : ListAdapter<ProductModel, RecyclerView.ViewHolder>(diffUtil) {
    private var itemList = mutableListOf<ProductModel>()
    private var bannerItem: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater by lazy { LayoutInflater.from(parent.context) }

        return when (viewType) {
            VIEW_TYPE_BANNER ->
                HomeBannerViewHolder(
                    ItemHomeBannerBinding.inflate(inflater, parent, false),
                    bannerClick,
                )

            VIEW_TYPE_PRODUCT ->
                HomeProductViewHolder(
                    ItemProductBinding.inflate(inflater, parent, false),
                    productClick,
                    likeClick,
                )

            else -> throw ClassCastException(
                parent.context.getString(
                    R.string.view_type_error_msg,
                    viewType,
                ),
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is HomeBannerViewHolder -> {
                bannerItem?.let { holder.onBind(it) }
            }

            is HomeProductViewHolder -> {
                val itemPosition = position - HEADER_COUNT
                holder.onBind(itemList[itemPosition], itemPosition)
            }
        }
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.bottomMargin = if (position == currentList.size) 24 else 0
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount() = itemList.size + HEADER_COUNT

    override fun getItemViewType(position: Int) =
        when (position) {
            0 -> VIEW_TYPE_BANNER
            else -> VIEW_TYPE_PRODUCT
        }

    fun addBannerItem(bannerUrl: String) {
        this.bannerItem = bannerUrl
        notifyDataSetChanged()
    }

    fun addItemList(newItems: List<ProductModel>) {
        this.itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    fun plusItemLike(position: Int) {
        itemList[position].apply {
            isInterested = true
            interestCount += 1
        }
        notifyItemChanged(position + HEADER_COUNT)
    }

    fun minusItemLike(position: Int) {
        itemList[position].apply {
            isInterested = false
            interestCount -= 1
        }
        notifyItemChanged(position + HEADER_COUNT)
    }

    companion object {
        private val diffUtil =
            ItemDiffCallback<ProductModel>(
                onItemsTheSame = { old, new -> old.productId == new.productId },
                onContentsTheSame = { old, new -> old == new },
            )

        const val HEADER_COUNT = 1

        const val VIEW_TYPE_BANNER = 0
        const val VIEW_TYPE_PRODUCT = 1
    }
}
