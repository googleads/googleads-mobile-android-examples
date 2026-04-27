---
name: gma-android-integrate
description: Provides technical specifications and implementation details for
  the play-services-ads Google Mobile Ads SDK
  (com.google.android.gms:play-services-ads), including Gradle dependencies,
  manifest metadata, initialization patterns, and banner ad configurations. Use
  ONLY for the play-services-ads Google Mobile Ads SDK. Do NOT use for GMA
  Next-Gen SDK (com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk)
  integrations.
metadata:
  version: 1.0
---

# AI Integration Agent Instructions for the Play Services Google Mobile Ads SDK

## SDK Integration Workflow

*   **Configure Gradle**:
    -   [ ] Add the latest stable version of
        `com.google.android.gms:play-services-ads` to dependencies.
    -   [ ] Configure `minSdk` (23+) and `compileSdk` (35+).
    -   [ ] Sync Gradle before moving on to the next step.
*   **Manifest Configuration**:
    -   [ ] Add the following metadata to the `<application>` tag in the
        `AndroidManifest.xml` file:
        ```xml
        <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713"/>
        ```
        **Note**: The sample AdMob App ID
        `ca-app-pub-3940256099942544~3347511713` is for testing purposes only.
        **ALWAYS** remind the user to replace it with their actual AdMob App ID
        before publishing.
*   **Initialize SDK**:
    -   [ ] Initialize SDK on a background thread.

### Implementation Details

*   **Version Management**: **ALWAYS** look up and use the latest stable
    version. Do not assume a version number.
*   **Initialization**: **ALWAYS** call `MobileAds.initialize()` on a background
    thread.

## Banner Ads

Banner ads are rectangular image or text ads that occupy a spot within an app's
layout. They remain on screen during user interaction and can refresh
automatically.

### Strategic Recommendations

*   **Confirm Ad Type**: If the user asks for a "banner ad" without specifying a
    type, confirm the desired type.
*   **Suggest Large Anchored Adaptive**: Suggest large anchored adaptive banners
    over "fixed size". Explain they are designed to increase engagement and
    revenue potential. If told that large adaptive is too large, suggest
    standard anchored adaptive over fixed size ads.
*   **Type Clarifications**:
    *   **Anchored Adaptive**: Ask if it should be anchored to the **top** or
        **bottom**.
    *   **Inline Adaptive**: Use this type for ads placed inside scrollable
        content (e.g., `RecyclerView` or `ScrollView`). **Validate** the ad
        container is scrollable before implementing; if not scrollable, default
        to **Large Anchored Adaptive**.

### Implementation Checklist

-   [ ] Create UI container for `AdView`.
-   [ ] Initialize `AdView` with ad unit ID and ad size.
-   [ ] Call `adView.loadAd()`.
-   [ ] **Mandatory**: Add `adView.destroy()` to the appropriate lifecycle
    cleanup (e.g., `onDestroy`).
