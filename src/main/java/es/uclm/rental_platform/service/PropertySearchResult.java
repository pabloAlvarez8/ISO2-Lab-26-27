package es.uclm.rental_platform.service;

import es.uclm.rental_platform.domain.Property;

import java.util.Collections;
import java.util.List;

public class PropertySearchResult {

    private final List<Property> properties;
    private final String errorMessage;

    public PropertySearchResult(List<Property> properties, String errorMessage) {
        this.properties = properties != null ? properties : Collections.emptyList();
        this.errorMessage = errorMessage;
    }

    public static PropertySearchResult success(List<Property> properties) {
        return new PropertySearchResult(properties, null);
    }

    public static PropertySearchResult error(String errorMessage) {
        return new PropertySearchResult(Collections.emptyList(), errorMessage);
    }

    public List<Property> getProperties() {
        return properties;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }
}
