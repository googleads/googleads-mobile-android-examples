package com.google.example.dfp;

/**
 * An enumeration of DFP samples that can be run in this project. Sample ad units that support each
 * feature are provided, but you can replace these ad units with your own to perform testing.
 */
public enum DfpSample {
  STANDARD_BANNER("/6253334/dfp_example_ad/banner"),
  MULTIPLE_AD_SIZES("/6253334/dfp_example_ad/multisize"),
  APP_EVENTS("/6253334/dfp_example_ad/appevents");

  public String adunitId;

  private DfpSample(String adUnitId) {
    this.adunitId = adUnitId;
  }
}
