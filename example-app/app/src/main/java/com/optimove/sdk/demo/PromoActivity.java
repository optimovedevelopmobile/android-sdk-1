package com.optimove.sdk.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.optimove.sdk.optimove_sdk.main.tools.opti_logger.OptiLogger;
import com.optimove.sdk.optimove_sdk.optipush.deep_link.DeepLinkHandler;
import com.optimove.sdk.optimove_sdk.optipush.deep_link.LinkDataError;
import com.optimove.sdk.optimove_sdk.optipush.deep_link.LinkDataExtractedListener;

import java.util.Map;

/**
 * Accessible only from a {@code deep link}.
 * Check the manifest to see how the {@link android.content.IntentFilter} was defined.
 */
public class PromoActivity extends AppCompatActivity implements LinkDataExtractedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);
        //Get the deep link components asynchronously
        new DeepLinkHandler(getIntent()).extractLinkData(this);
    }

    /**
     * Called when the deep link was found
     *
     * @param map: The deep link parameters (can be empty)
     */
    @Override
    public void onDataExtracted(Map<String, String> map) {

        TextView outputTv = findViewById(R.id.outputTextView);
        StringBuilder builder = new StringBuilder(map.size());
        for (String key : map.keySet())
            builder.append(map.get(key)).append("\n");
        outputTv.setText(builder.toString());
    }

    /**
     * Called when the deep link was not found
     *
     * @param linkDataError: The error that occurred while extracting the deep link
     */
    @Override
    public void onErrorOccurred(LinkDataError linkDataError) {

        OptiLogger.d("DYNAMIC_LINK_ERROR", linkDataError.name());
    }
}
