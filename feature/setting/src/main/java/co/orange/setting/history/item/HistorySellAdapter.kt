package co.orange.setting.history.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import co.orange.core.util.ItemDiffCallback
import co.orange.domain.entity.response.HistorySellModel.ItemProductModel
import co.orange.setting.databinding.ItemHistoryProductBinding

class HistorySellAdapter(
    private val itemClick: (String) -> (Unit),
) : ListAdapter<ItemProductModel, HistorySellViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistorySellViewHolder {
        val inflater by lazy { LayoutInflater.from(parent.context) }
        val binding: ItemHistoryProductBinding =
            ItemHistoryProductBinding.inflate(inflater, parent, false)
        return HistorySellViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(
        holder: HistorySellViewHolder,
        position: Int,
    ) {
        val item = getItem(position) ?: return
        holder.onBind(item)
    }

    companion object {
        private val diffUtil =
            ItemDiffCallback<ItemProductModel>(
                onItemsTheSame = { old, new -> old.itemId == new.itemId },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}
