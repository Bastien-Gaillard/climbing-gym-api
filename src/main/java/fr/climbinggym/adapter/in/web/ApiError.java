package fr.climbinggym.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(String code, String message, Map<String, String> fields) {

    public record Body(ApiError error) {
    }

    public static Body of(String code, String message) {
        return new Body(new ApiError(code, message, Map.of()));
    }

    public static Body of(String code, String message, Map<String, String> fields) {
        return new Body(new ApiError(code, message, fields));
    }
}
