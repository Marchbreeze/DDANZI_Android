package co.orange.main.main.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import co.orange.core.R
import co.orange.core.amplitude.AmplitudeManager
import co.orange.core.base.BaseFragment
import co.orange.core.extension.dpToPx
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.stringOf
import co.orange.core.extension.toast
import co.orange.core.navigation.NavigationManager
import co.orange.core.state.UiState
import co.orange.core.util.GridInfiniteScrollListener
import co.orange.main.alarm.AlarmActivity
import co.orange.main.databinding.FragmentHomeBinding
import co.orange.main.detail.DetailActivity
import co.orange.main.detail.DetailActivity.Companion.EXTRA_IS_LIKED
import co.orange.main.main.home.HomeAdapter.Companion.VIEW_TYPE_BANNER
import co.orange.main.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import co.orange.main.R as featureR

@AndroidEntryPoint
class HomeFragment() : BaseFragment<FragmentHomeBinding>(featureR.layout.fragment_home) {
    @Inject
    lateinit var navigationManager: NavigationManager
    private lateinit var detailActivityLauncher: ActivityResultLauncher<Intent>

    private var _adapter: HomeAdapter? = null
    val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private val viewModel by activityViewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    setLikeState(
                        result.data?.getBooleanExtra(EXTRA_IS_LIKED, false),
                        viewModel.clickedPosition
                    )
                }
            }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initSearchBtnListener()
        initAlarmBtnListener()
        initSellBtnListener()
        setDeviceToken()
        setGridRecyclerView()
        setRecyclerViewDeco()
        setListWithInfinityScroll()
        observeGetHomeDataState()
        observeLikeState()
    }

    override fun onResume() {
        super.onResume()

        AmplitudeManager.trackEvent("view_home")
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
        AmplitudeManager.trackEvent("click_home_banner")
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WEB_BANNER)))
    }

    private fun initProductClickListener(
        productId: String,
        position: Int,
    ) {
        viewModel.clickedPosition = position
        detailActivityLauncher.launch(DetailActivity.createIntent(requireContext(), productId))
    }

    private fun initLikeClickListener(
        productId: String,
        isInterested: Boolean,
        position: Int,
    ) {
        if (!viewModel.getUserLogined()) {
            navigationManager.toLoginView(requireContext(), "like")
            return
        }
        viewModel.setLikeStateWithServer(productId, isInterested, position)
    }

    private fun initSearchBtnListener() {
        binding.btnSearch.setOnSingleClickListener {
            AmplitudeManager.trackEvent("click_home_search")
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
    }

    private fun initAlarmBtnListener() {
        binding.btnAlarm.setOnSingleClickListener {
            if (viewModel.getUserLogined()) {
                startActivity(Intent(requireContext(), AlarmActivity::class.java))
            } else {
                navigationManager.toLoginView(requireContext(), "sell")
            }
        }
    }

    private fun initSellBtnListener() {
        binding.btnSell.setOnSingleClickListener {
            AmplitudeManager.trackEvent("click_home_sell")
            if (viewModel.getUserLogined()) {
                navigationManager.toSellOnboardingView(requireContext())
            } else {
                navigationManager.toLoginView(requireContext(), "sell")
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

    private fun setListWithInfinityScroll() {
        binding.rvHome.addOnScrollListener(
            GridInfiniteScrollListener { viewModel.getHomeDataFromServer() },
        )
    }

    private fun setLikeState(isLiked: Boolean?, position: Int) {
        if (isLiked == null) return
        if (isLiked) {
            adapter.plusItemLike(position)
        } else {
            adapter.minusItemLike(position)
        }
    }

    private fun observeGetHomeDataState() {
        viewModel.getHomeDataState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        adapter.addBannerItem(state.data.homeImgUrl)
                        adapter.addItemList(state.data.productList)
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun observeLikeState() {
        viewModel.likeState.flowWithLifecycle(lifecycle).distinctUntilChanged().onEach { isLiked ->
            setLikeState(isLiked, viewModel.likedPosition)
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
