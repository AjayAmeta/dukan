package com.shopmanager.utils
import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_NAME = "ShopManagerPrefs"
    private const val KEY_SHOP_ID = "selected_shop_id"
    private const val KEY_SHOP_NAME = "selected_shop_name"
    private const val KEY_SHOP_TYPE = "selected_shop_type"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSelectedShop(context: Context, id: Int, name: String, type: String) {
        prefs(context).edit().putInt(KEY_SHOP_ID, id).putString(KEY_SHOP_NAME, name).putString(KEY_SHOP_TYPE, type).apply()
    }
    fun getShopId(context: Context) = prefs(context).getInt(KEY_SHOP_ID, -1)
    fun getShopName(context: Context) = prefs(context).getString(KEY_SHOP_NAME, "") ?: ""
    fun getShopType(context: Context) = prefs(context).getString(KEY_SHOP_TYPE, "") ?: ""
    fun clearShop(context: Context) = prefs(context).edit().clear().apply()
    fun isShopSelected(context: Context) = prefs(context).getInt(KEY_SHOP_ID, -1) != -1
}
