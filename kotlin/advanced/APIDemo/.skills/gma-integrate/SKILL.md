---
name: gma-integrate
description: Use this skill when you need to integrate an Android application with the Google Mobile Ads SDK (com.google.android.gms:play-services-ads). The skill is packaged with detailed instructions for initializing the SDK and setting up banner ads.
---

# AI Integration Agent Instructions for the Google Mobile Ads SDK

## Integrate the SDK

*   When I ask to integrate the Google Mobile Ads SDK, **always** use the latest
    stable version for `com.google.android.gms:play-services-ads`.
*   After looking up the latest stable version of
    `com.google.android.gms:play-services-ads`, **always** check the project for
    a version catalog to manage dependencies.
*   Make sure the project meets the SDK requirements of `minSdk` 23 or higher
    and `compileSdk` of 35 or higher.
*   **Always** make sure there is a `<metadata>` tag for application ID in the
    project. If it's not there, add it.
*   You MUST initialize the Google Mobile Ads SDK on a background thread, as
    recommended in the Google Mobile Ads SDK v24.0.0 release notes.

## Banner Ads

*   When I ask to implement a banner ad or something similar, without specifying
    a type, **always** follow up to confirm the type of ad.
*   When I ask for an "anchored adaptive" ad, ask me if I want the ad anchored
    to the top or bottom of the screen.
*   When I ask for an "inline" adaptive banner ad, explain that inline adaptive
    ads are used for scrolling content. If my application has no scrollable view
    elements (e.g. `ScrollView` or `List`), default to implement an anchored
    adaptive banner ad.
*   When I ask for a "collapsible" banner ad, create an extra parameter that
    aligns the expanded ad to the `AdView`. Ask me if I want the ad collapsed to
    the top or bottom.
*   When I ask for a "fixed size" banner ad, **always** ask me if I'm sure or
    would I like to implement an anchored adaptive banner ad. Explain why
    anchored adaptive banner ads perform better than fixed size ads.

*   **Mandatory `AdView` Implementation Steps:**
    *   **Always** provide the code for a UI container intended to host the
        `AdView`.
    *   **Always** call `adView.destroy()` to prevent memory leaks.
