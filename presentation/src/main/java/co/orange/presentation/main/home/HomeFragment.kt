package co.orange.presentation.main.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import co.orange.core.R
import co.orange.core.base.BaseFragment
import co.orange.core.extension.dpToPx
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.stringOf
import co.orange.core.extension.toast
import co.orange.core.state.UiState
import co.orange.presentation.auth.login.LoginActivity
import co.orange.presentation.detail.DetailActivity
import co.orange.presentation.main.home.HomeAdapter.Companion.VIEW_TYPE_BANNER
import co.orange.presentation.search.SearchActivity
import co.orange.sell.onboarding.SellOnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.databinding.FragmentHomeBinding
import kr.genti.presentation.R as featureR

@AndroidEntryPoint
class HomeFragment() : BaseFragment<FragmentHomeBinding>(featureR.layout.fragment_home) {
    private var _adapter: HomeAdapter? = null
    val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private val viewModel by activityViewModels<HomeViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initSearchBtnListener()
        initSellBtnListener()
        setDeviceToken()
        setGridRecyclerView()
        setRecyclerViewDeco()
        observeGetHomeDataState()
        observeItemLikePlusState()
        observeItemLikeMinusState()
    }

    override fun onResume() {
        super.onResume()

        viewModel.getHomeDataFromServer()
    }

    private fun initAdapter() {
        _adapter =
            HomeAdapter(
                bannerClick = ::initBannerClickListener,
                productClick = ::initProductClickListener,
                likeClick = ::initLikeClickListener,
            )
        binding.rvHome.adapter = adapter
    }

    private fun initBannerClickListener(x: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(WEB_BANNER)).apply {
            startActivity(this)
        }
    }

    private fun initProductClickListener(productId: String) {
        DetailActivity.createIntent(
            requireContext(),
            productId,
        ).apply { startActivity(this) }
    }

    private fun initLikeClickListener(
        productId: String,
        isInterested: Boolean,
        position: Int,
    ) {
        if (!viewModel.getUserLogined()) {
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            return
        }
        viewModel.setLikeStateWithServer(productId, isInterested, position)
    }

    private fun initSearchBtnListener() {
        binding.btnSearch.setOnSingleClickListener {
            Intent(requireContext(), SearchActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun initSellBtnListener() {
        binding.btnSell.setOnSingleClickListener {
            if (viewModel.getUserLogined()) {
                Intent(requireActivity(), co.orange.sell.onboarding.SellOnboardingActivity::class.java).apply {
                    startActivity(this)
                }
            } else {
                Intent(requireActivity(), LoginActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun setDeviceToken() {
        viewModel.setDeviceToken(
            Settings.Secure.getString(
                requireContext().contentResolver,
                Settings.Secure.ANDROID_ID,
            ),
        )
    }

    private fun setGridRecyclerView() {
        binding.rvHome.layoutManager =
            GridLayoutManager(context, 2).apply {
                spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (adapter.getItemViewType(position)) {
                                VIEW_TYPE_BANNER -> 2
                                else -> 1
                            }
                        }
                    }
            }
    }

    private fun setRecyclerViewDeco() {
        binding.rvHome.addItemDecoration(
            GridItemDecoration(
                spanCount = 2,
                spacing = 30.dpToPx(requireContext()),
                bottomPadding = 50.dpToPx(requireContext()),
            ),
        )
    }

    private fun observeGetHomeDataState() {
        viewModel.getHomeDataState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        adapter.addBannerItem(state.data.homeImgUrl)
                        adapter.setItemList(state.data.productList)
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun observeItemLikePlusState() {
        viewModel.itemLikePlusState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        adapter.plusItemLike(state.data)
//                        with(binding) {
//                            lottieLike.isVisible = true
//                            lottieLike.playAnimation()
//                            delay(500)
//                            lottieLike.isVisible = false
//                        }
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
                viewModel.resetLikeState()
            }.launchIn(lifecycleScope)
    }

    private fun observeItemLikeMinusState() {
        viewModel.itemLikeMinusState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> adapter.minusItemLike(state.data)
                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
                viewModel.resetLikeState()
            }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    companion object {
        const val WEB_BANNER =
            "https://brawny-guan-098.notion.site/d1259ed5fdfd489eb6cc23a4312c13a0?pvs=4"
    }
}
