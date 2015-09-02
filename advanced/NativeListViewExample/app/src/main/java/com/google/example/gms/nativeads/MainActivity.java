package com.google.example.gms.nativeads;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a {@link ListView} with native content ads and native app install ads embedded in it.
 */
public class MainActivity extends ActionBarActivity {
    public static final String LOG_TAG = "NativeAdsExample";

    /**
     * A DFP ad unit configured to serve app install and content ads
     */
    private static final String DFP_NATIVE_AD_UNIT_ID = "/6499/example/native";

    /**
     * A DFP ad unit configured to serve a simple custom template ad
     */
    private static final String DFP_SIMPLE_CUSTOM_TEMPLATE_AD_UNIT_ID =
            "/6499/example/custom_template/simple";

    /**
     * A DFP ad unit configured to both of the above in rotation
     */
    private static final String DFP_MULTIFORMAT_AD_UNIT_ID =
            "/6499/example/native-multiformat";

    /**
     * Index where a DFP content ad should be placed.
     */
    private static final int DFP_CONTENT_AD_INDEX = 6;

    /**
     * Index where a DFP app install ad should be placed.
     */
    private static final int DFP_APP_INSTALL_AD_INDEX = 12;

    /**
     * Index where a DFP custom template ad should be placed
     */
    private static final int DFP_CUSTOM_TEMPLATE_AD_INDEX = 18;

    /**
     * Index where a DFP custom template ad should be placed
     */
    private static final int DFP_MULTIFORMAT_AD_INDEX = 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new MainListingFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The main fragment for real estate listings.
     */
    public static class MainListingFragment extends ListFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            AdsListingAdapter mainAdapter =
                    new AdsListingAdapter(this.getActivity(), R.layout.item, getRowItems());
            this.setListAdapter(mainAdapter);
        }

        @Override
        public void onListItemClick(ListView listView, View view, int position, long id) {
            super.onListItemClick(listView, view, position, id);
            Object data = listView.getItemAtPosition(position);
            if (data instanceof Listing) {
                Listing listing = (Listing) data;
                ListingDetailsFragment fragment = ListingDetailsFragment.newInstance(listing);
                if (fragment != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, fragment)
                            .detach(this)
                            .addToBackStack(null)
                            .commit();
                }
            }
        }

        /**
         * Returns a list of sample listings with native ads embedded. In practice, the listings
         * would typically be fetched from a web service instead of hard-coded.
         */
        private List<Object> getRowItems() {
            List<Object> rowItems = new ArrayList<Object>();
            rowItems.add(new Listing(
                    "North 12th Street (log in for details)",
                    "House for Sale",
                    "1,700 sq. ft, duplex, 4 BR, 2.5 baths, backyard, parking in front",
                    "$1500/mo",
                    R.drawable.house1));
            rowItems.add(new Listing(
                    "166 North 7th Street (bet. Bedford & Driggs)",
                    "Apartment for Sale",
                    "1,320 sq. ft, 3 BR, 2 baths, gym, garage in building, doorman",
                    "$1,220,000",
                    R.drawable.house2));
            for (int i = 2; i < 32; i++) {
                rowItems.add(makeListing(i));
            }

            // Put in ads into the list.
            rowItems.add(DFP_CONTENT_AD_INDEX,
                    new ContentAdPlacement(new ContentAdFetcher(DFP_NATIVE_AD_UNIT_ID)));
            rowItems.add(DFP_APP_INSTALL_AD_INDEX,
                    new AppInstallAdPlacement(new AppInstallAdFetcher(DFP_NATIVE_AD_UNIT_ID)));
            rowItems.add(DFP_CUSTOM_TEMPLATE_AD_INDEX,
                    new SimpleCustomTemplateAdPlacement(new SimpleCustomTemplateAdFetcher(
                            DFP_SIMPLE_CUSTOM_TEMPLATE_AD_UNIT_ID,
                            SimpleCustomTemplateAdViewHolder.DFP_TEMPLATE_ID)));
            rowItems.add(DFP_MULTIFORMAT_AD_INDEX,
                    new MultiFormatAdPlacement(new MultiFormatAdFetcher(
                            DFP_MULTIFORMAT_AD_UNIT_ID,
                            SimpleCustomTemplateAdViewHolder.DFP_TEMPLATE_ID,
                            ExtendedCustomTemplateAdViewHolder.DFP_TEMPLATE_ID)));

            return rowItems;
        }

        /**
         * Generates a sample listing.
         */
        private Listing makeListing(int number) {
            int mod = number % 3;
            int resource;

            if (mod == 0) {
                resource = R.drawable.house1;
            } else if (mod == 1) {
                resource = R.drawable.house2;
            } else {
                resource = R.drawable.house3;
            }

            return new Listing(
                    String.format("Generic listing #%d", number),
                    "House for rent",
                    "1,000 sq. ft, 2/2 with 2-car garage!",
                    "$2,800/mo",
                    resource);
        }
    }

    /**
     * The details view for a {@link Listing}.
     */
    public static class ListingDetailsFragment extends Fragment {
        private static final String ARG_HEADLINE = "headline";
        private static final String ARG_BODY = "body";
        private static final String ARG_IMAGE_RESOURCE = "imageResource";

        /**
         * Create a new instance of {@link ListingDetailsFragment}, initialized with listing
         * content.
         */
        public static ListingDetailsFragment newInstance(Listing listing) {
            ListingDetailsFragment fragment = new ListingDetailsFragment();

            Bundle args = new Bundle();
            args.putString(ARG_HEADLINE, listing.getHeadline());
            args.putString(ARG_BODY, listing.getBody());
            args.putInt(ARG_IMAGE_RESOURCE, listing.getImageResource());
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            String headline = args.getString(ARG_HEADLINE);
            String body = args.getString(ARG_BODY);
            int iconResource = args.getInt(ARG_IMAGE_RESOURCE);

            View listingLayout = inflater.inflate(R.layout.fragment_listing, container, false);
            TextView headlineView = (TextView) listingLayout.findViewById(R.id.listing_headline);
            headlineView.setText(headline);
            TextView bodyView = (TextView) listingLayout.findViewById(R.id.listing_body);
            bodyView.setText(body);
            ImageView iconView = (ImageView) listingLayout.findViewById(R.id.listing_image);
            iconView.setImageResource(iconResource);

            return listingLayout;
        }
    }
}
