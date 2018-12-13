package ank.com.secretchatlab.utils;

import com.github.moduth.blockcanary.BlockCanaryContext;
import ank.com.secretchatlab.BuildConfig;


/**
 * Configuration class. Used in Block Canary - ui-block detection library for Android
 */

public class AppBlockCanaryContext extends BlockCanaryContext {

    public String provideQualifier() {
        return BuildConfig.VERSION_NAME;
    }

    public int provideBlockThreshold() {
        return 1000;
    }
}
