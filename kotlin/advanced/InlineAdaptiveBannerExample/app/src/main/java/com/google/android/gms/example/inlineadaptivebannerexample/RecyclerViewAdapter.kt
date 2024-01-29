package com.google.android.gms.example.inlineadaptivebannerexample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.gms.ads.AdView
import com.google.android.gms.example.inlineadaptivebannerexample.RecyclerViewAdapter.AdViewHolder
import com.google.android.gms.example.inlineadaptivebannerexample.RecyclerViewAdapter.MenuItemViewHolder

/**
 * The [RecyclerViewAdapter] class.
 *
 * The adapter provides access to the items in the [MenuItemViewHolder] or the [AdViewHolder].
 */
internal class RecyclerViewAdapter
/** For this example app, the recyclerViewItems list contains only [MenuItem] and [AdView] types. */
(
  // An Activity's Context.
  private val context: Context,
  // The list of banner ads and menu items.
  private val recyclerViewItems: List<Any>,
) : RecyclerView.Adapter<ViewHolder>() {
  /** The [MenuItemViewHolder] class. Provides a reference to each view in the menu item view. */
  inner class MenuItemViewHolder internal constructor(view: View) : ViewHolder(view) {
    val menuItemName: TextView
    val menuItemDescription: TextView
    val menuItemPrice: TextView
    val menuItemCategory: TextView
    val menuItemImage: ImageView

    init {
      menuItemImage = view.findViewById(R.id.menu_item_image)
      menuItemName = view.findViewById(R.id.menu_item_name)
      menuItemPrice = view.findViewById(R.id.menu_item_price)
      menuItemCategory = view.findViewById(R.id.menu_item_category)
      menuItemDescription = view.findViewById(R.id.menu_item_description)
    }
  }

  /** The [AdViewHolder] class. */
  inner class AdViewHolder internal constructor(view: View?) : ViewHolder(view!!)

  override fun getItemCount(): Int {
    return recyclerViewItems.size
  }

  /** Determines the view type for the given position. */
  override fun getItemViewType(position: Int): Int {
    return if (position % MainActivity.ITEMS_PER_AD == 0) BANNER_AD_VIEW_TYPE
    else MENU_ITEM_VIEW_TYPE
  }

  /**
   * Creates a new view for a menu item view or a banner ad view based on the viewType. This method
   * is invoked by the layout manager.
   */
  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return when (viewType) {
      MENU_ITEM_VIEW_TYPE -> {
        val menuItemLayoutView =
          LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.menu_item_container, viewGroup, false)
        MenuItemViewHolder(menuItemLayoutView)
      }
      else -> {
        val bannerLayoutView =
          LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.banner_ad_container, viewGroup, false)
        AdViewHolder(bannerLayoutView)
      }
    }
  }

  /**
   * Replaces the content in the views that make up the menu item view and the banner ad view. This
   * method is invoked by the layout manager.
   */
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    when (getItemViewType(position)) {
      MENU_ITEM_VIEW_TYPE -> {
        val menuItemHolder = holder as MenuItemViewHolder
        val menuItem = recyclerViewItems[position] as MenuItem

        // Get the menu item image resource ID.
        val imageName = menuItem.imageName
        val imageResID = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        // Add the menu item details to the menu item view.
        menuItemHolder.menuItemImage.setImageResource(imageResID)
        menuItemHolder.menuItemName.text = menuItem.name
        menuItemHolder.menuItemPrice.text = menuItem.price
        menuItemHolder.menuItemCategory.text = menuItem.category
        menuItemHolder.menuItemDescription.text = menuItem.description
      }
      else -> {
        val bannerHolder = holder as AdViewHolder
        val adView = recyclerViewItems[position] as AdView
        val adCardView = bannerHolder.itemView as ViewGroup
        // The AdViewHolder recycled by the RecyclerView may be a different
        // instance than the one used previously for this position. Clear the
        // AdViewHolder of any subviews in case it has a different
        // AdView associated with it, and make sure the AdView for this position doesn't
        // already have a parent of a different recycled AdViewHolder.
        if (adCardView.childCount > 0) {
          adCardView.removeAllViews()
        }
        if (adView.parent != null) {
          (adView.parent as ViewGroup).removeView(adView)
        }

        // Add the banner ad to the ad view.
        adCardView.addView(adView)
      }
    }
  }

  companion object {
    // A menu item view type.
    private const val MENU_ITEM_VIEW_TYPE = 0

    // The banner ad view type.
    private const val BANNER_AD_VIEW_TYPE = 1
  }
}
