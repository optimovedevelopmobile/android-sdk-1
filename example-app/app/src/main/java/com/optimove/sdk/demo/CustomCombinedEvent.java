package com.optimove.sdk.demo;

import android.support.annotation.Nullable;

import com.optimove.sdk.optimove_sdk.optitrack.OptimoveEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@code Custom Event} can contain Optional and Mandatory parameters.
 * Everything is defined the in the SDK's configurations.
 * In this case the event has 1 mandatory string param and 1 optional number.
 */
public class CustomCombinedEvent implements OptimoveEvent {

    @Nullable
    private String stringParam;
    @Nullable
    private Double numParam;

    public CustomCombinedEvent(@Nullable String stringParam, @Nullable Double numParam) {

        this.stringParam = stringParam;
        this.numParam = numParam;
    }

    /**
     * Declares the {@code custom event}'s {@code name}
     *
     * @return The {@code name} as defined in the configurations
     */
    @Override
    public String getName() {
        return "custom_combined_event";
    }

    /**
     * Declares the {@code custom event}'s {@code parameters}
     *
     * @return The {@code parameters} as defined in the configurations
     */
    @Override
    public Map<String, Object> getParameters() {

        Map<String, Object> params = new HashMap<>();
        if (stringParam != null)
            params.put("string_param", stringParam);
        if (numParam != null)
            params.put("number_param", numParam);
        return params;
    }
}
