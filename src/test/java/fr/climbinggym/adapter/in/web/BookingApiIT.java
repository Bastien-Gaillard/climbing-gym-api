package fr.climbinggym.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import fr.climbinggym.application.port.in.BookingUseCase;
import fr.climbinggym.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingApiIT extends AbstractIntegrationTest {

    @Autowired
    private BookingUseCase bookingUseCase;

    @Test
    void books_then_cancels_a_slot() throws Exception {
        var memberId = registerAdult(false);
        subscribe(memberId, "TEN_ENTRIES");
        var slotId = planSlot("BOULDER", 20);

        var json = postJson("/api/slots/" + slotId + "/bookings", "{\"memberId\":\"" + memberId + "\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andReturn().getResponse().getContentAsString();
        String bookingId = JsonPath.read(json, "$.data.id");

        mockMvc.perform(get("/api/slots/{id}", slotId))
                .andExpect(jsonPath("$.data.bookedCount").value(1))
                .andExpect(jsonPath("$.data.remainingPlaces").value(19));
        mockMvc.perform(get("/api/members/{id}/subscriptions", memberId))
                .andExpect(jsonPath("$.data[0].remainingEntries").value(9));

        mockMvc.perform(post("/api/bookings/{id}/cancel", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
        mockMvc.perform(get("/api/members/{id}/subscriptions", memberId))
                .andExpect(jsonPath("$.data[0].remainingEntries").value(10));
    }

    @Test
    void refuses_rope_slot_without_belay_certification() throws Exception {
        var memberId = registerAdult(false);
        subscribe(memberId, "MONTHLY");
        var slotId = planSlot("ROPE", 10);

        postJson("/api/slots/" + slotId + "/bookings", "{\"memberId\":\"" + memberId + "\"}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("BELAY_CERTIFICATION_REQUIRED"));
    }

    @Test
    void never_overbooks_a_slot_under_concurrent_requests() throws Exception {
        var capacity = 3;
        var slotId = UUID.fromString(planSlot("BOULDER", capacity));
        var memberIds = new ArrayList<UUID>();
        for (int i = 0; i < 8; i++) {
            var memberId = registerAdult(false);
            subscribe(memberId, "MONTHLY");
            memberIds.add(UUID.fromString(memberId));
        }

        var start = new CountDownLatch(1);
        var results = new ArrayList<Future<Boolean>>();
        try (var executor = Executors.newFixedThreadPool(memberIds.size())) {
            for (var memberId : memberIds) {
                Callable<Boolean> attempt = () -> {
                    start.await();
                    try {
                        bookingUseCase.book(slotId, memberId);
                        return true;
                    } catch (RuntimeException e) {
                        return false;
                    }
                };
                results.add(executor.submit(attempt));
            }
            start.countDown();

            var succeeded = 0;
            for (var result : results) if (result.get()) succeeded++;
            assertThat(succeeded).isEqualTo(capacity);
        }

        mockMvc.perform(get("/api/slots/{id}", slotId))
                .andExpect(jsonPath("$.data.bookedCount").value(capacity))
                .andExpect(jsonPath("$.data.remainingPlaces").value(0));
    }
}
