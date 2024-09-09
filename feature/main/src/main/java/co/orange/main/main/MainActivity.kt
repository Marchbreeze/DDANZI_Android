package co.orange.main.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import co.orange.core.R
import co.orange.core.base.BaseActivity
import co.orange.core.extension.colorOf
import co.orange.core.extension.initOnBackPressedListener
import co.orange.main.alarm.AlarmActivity
import co.orange.main.databinding.ActivityMainBinding
import co.orange.main.main.home.HomeFragment
import co.orange.main.main.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import co.orange.main.R as featureR

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(featureR.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initOnBackPressedListener(binding.root)
        initBnvItemIconTintList()
        initBnvItemSelectedListener()
        setNavigationBarBlack()
        getNotificationIntent()
    }

    private fun initBnvItemIconTintList() {
        with(binding.bnvMain) {
            itemIconTintList = null
            selectedItemId = R.id.menu_home
        }
    }

    private fun initBnvItemSelectedListener() {
        supportFragmentManager.findFragmentById(featureR.id.fcv_main) ?: navigateTo<HomeFragment>()

        binding.bnvMain.setOnItemSelectedListener { menu ->
            if (binding.bnvMain.selectedItemId == menu.itemId) {
                return@setOnItemSelectedListener false
            }
            when (menu.itemId) {
                R.id.menu_home -> navigateTo<HomeFragment>()

                R.id.menu_profile -> navigateTo<ProfileFragment>()

                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(featureR.id.fcv_main, T::class.java.canonicalName)
        }
    }

    private fun setNavigationBarBlack() {
        this.window.navigationBarColor = colorOf(R.color.black)
    }

    private fun getNotificationIntent() {
        if (!intent.getStringExtra(EXTRA_TYPE).isNullOrEmpty()) {
            startActivity(Intent(this, AlarmActivity::class.java))
        }
    }

    companion object {
        private const val EXTRA_TYPE = "EXTRA_DEFAULT"

        @JvmStatic
        fun getIntent(
            context: Context,
            type: String? = null,
        ) = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_TYPE, type)
        }
    }
}