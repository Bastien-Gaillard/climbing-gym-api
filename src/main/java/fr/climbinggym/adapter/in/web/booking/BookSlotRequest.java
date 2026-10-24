package fr.climbinggym.adapter.in.web.booking;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookSlotRequest(@NotNull UUID memberId) {
}
