package com.google.example.gms.nativeads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An adapter for a list of {@link Listing}s and {@link com.google.android.gms.ads.formats.NativeAd}
 * formats.
 */
public class AdsListingAdapter extends ArrayAdapter<Object> {
    // A map of class to view type, used to recognize view types.
    private final Map<Class<?>, Integer> viewTypes;

    public AdsListingAdapter(Context context, int resource, List<Object> items) {
        super(context, resource, items);

        Map<Class<?>, Integer> types = new HashMap<Class<?>, Integer>();
        int typeNum = 0;
        for (Object item : items) {
            if (!types.containsKey(item.getClass())) {
                types.put(item.getClass(), typeNum++);
            }
        }
        viewTypes = Collections.unmodifiableMap(types);
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypes.get(getItem(position).getClass());
    }

    @Override
    public int getViewTypeCount() {
        return viewTypes.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
            throws IllegalArgumentException {
        Object item = getItem(position);

        if (item instanceof Listing) {
            // Listing items already have all the data required, so they just need to be displayed.
            LinearLayout listingLayout = (LinearLayout) convertView;
            if (listingLayout == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listingLayout = (LinearLayout) inflater.inflate(R.layout.item, parent, false);
                listingLayout.setTag(ListingViewHolder.create(listingLayout));
            }

            Listing listing = (Listing) item;
            ((ListingViewHolder) listingLayout.getTag()).populateView(listing);

            return listingLayout;
        } else if (item instanceof AdPlacement) {
            return ((AdPlacement) item).getView(convertView, parent);
        } else {
            // Any unknown items will cause exceptions, though this shouldn't ever happen.
            throw new IllegalArgumentException(
                    String.format("Adapter can't handle getView() for list item of type %s",
                            item.getClass().getName()));
        }


    }
}
