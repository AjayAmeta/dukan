package com.shopmanager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.shopmanager.databinding.ActivityMainBinding
import com.shopmanager.utils.PreferenceHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        appBarConfig = AppBarConfiguration(setOf(R.id.nav_create_bill, R.id.nav_progress, R.id.nav_products, R.id.nav_bills), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfig)
        binding.navView.setupWithNavController(navController)

        // Set shop info in header
        val header = binding.navView.getHeaderView(0)
        header.findViewById<android.widget.TextView>(R.id.tvHeaderShopName)?.text = PreferenceHelper.getShopName(this)
        header.findViewById<android.widget.TextView>(R.id.tvHeaderShopType)?.text = PreferenceHelper.getShopType(this)

        // Switch shop
        binding.navView.menu.findItem(R.id.nav_switch_shop)?.setOnMenuItemClickListener {
            PreferenceHelper.clearShop(this)
            startActivity(android.content.Intent(this, ShopSelectionActivity::class.java))
            finish()
            true
        }
    }

    override fun onSupportNavigateUp() = 
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
}
