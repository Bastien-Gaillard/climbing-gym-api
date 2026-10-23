package fr.climbinggym.adapter.in.web.subscription;

import fr.climbinggym.adapter.in.web.ApiResponse;
import fr.climbinggym.application.port.in.SubscribeCommand;
import fr.climbinggym.application.port.in.SubscriptionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class SubscriptionController {

    private final SubscriptionUseCase subscriptions;

    public SubscriptionController(SubscriptionUseCase subscriptions) {
        this.subscriptions = subscriptions;
    }

    @GetMapping("/api/members/{memberId}/subscriptions")
    public ApiResponse<List<SubscriptionResponse>> findByMember(@PathVariable UUID memberId) {
        return ApiResponse.of(subscriptions.findByMember(memberId).stream().map(SubscriptionResponse::from).toList());
    }

    @PostMapping("/api/members/{memberId}/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubscriptionResponse> subscribe(@PathVariable UUID memberId, @Valid @RequestBody SubscribeRequest request) {
        var command = new SubscribeCommand(memberId, request.type(), request.startDate());
        return ApiResponse.of(SubscriptionResponse.from(subscriptions.subscribe(command)));
    }

    @PostMapping("/api/subscriptions/{id}/cancel")
    public ApiResponse<SubscriptionResponse> cancel(@PathVariable UUID id) {
        return ApiResponse.of(SubscriptionResponse.from(subscriptions.cancel(id)));
    }
}
