package com.google.example.gms.fullscreennativeexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.example.gms.fullscreennativeexample.databinding.FragmentMainBinding;

/** A fragment class that provides the UI to load/show a native ad. */
public class MainFragment extends Fragment {

  public FragmentMainBinding binding;

  public MainFragment() {
    super(R.layout.fragment_main);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentMainBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Configure "Load ad" button.
    binding.loadAdButton.setOnClickListener(
        unusedView -> {
          binding.loadAdButton.setEnabled(false);
          binding.showAdButton.setEnabled(false);

          MainActivity mainActivity = ((MainActivity) getActivity());
          if (mainActivity != null) {
            mainActivity.loadAd();
          }
        });

    // Configure "Show ad" button.
    binding.showAdButton.setOnClickListener(
        unusedView -> {
          binding.loadAdButton.setEnabled(true);
          binding.showAdButton.setEnabled(false);

          // Display the full-screen native ad.
          final FragmentTransaction fragmentTransaction =
              getParentFragmentManager().beginTransaction();
          NativeAdFragment nativeAdFragment = new NativeAdFragment();
          fragmentTransaction.add(R.id.fragment_container_view, nativeAdFragment);
          fragmentTransaction.show(nativeAdFragment).addToBackStack(null);
          fragmentTransaction.commit();
        }
    );
  }
}
