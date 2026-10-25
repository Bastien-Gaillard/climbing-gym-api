package fr.climbinggym.adapter.in.web.equipment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record QuantityRequest(@Min(1) @Max(500) int quantity) {
}
