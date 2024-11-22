package co.orange.setting.history.item

import android.graphics.Paint
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import co.orange.core.extension.breakLines
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.setOverThousand
import co.orange.core.extension.setPriceForm
import co.orange.domain.entity.response.HistorySellModel
import co.orange.setting.databinding.ItemProductBinding
import coil.load

class HistorySellViewHolder(
    val binding: ItemProductBinding,
    val itemClick: (String) -> (Unit),
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: HistorySellModel.ItemProductModel) {
        with(binding) {
            root.setOnSingleClickListener {
                itemClick(item.itemId)
            }

            tvProductTitle.text = item.productName.breakLines()
            ivProductImage.load(item.imgUrl)

            tvProductOldPrice.apply {
                text = item.originPrice.setPriceForm()
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            tvProductNewPrice.text = item.salePrice.setPriceForm()
            tvProductLikeCount.text = item.interestCount.setOverThousand()
            btnItemLike.isVisible = false
        }
    }
}
