package fr.climbinggym.adapter.in.web.member;

import fr.climbinggym.adapter.in.web.ApiResponse;
import fr.climbinggym.application.port.in.MemberUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberUseCase members;

    public MemberController(MemberUseCase members) {
        this.members = members;
    }

    @GetMapping
    public ApiResponse<List<MemberResponse>> search(@RequestParam(required = false) String search) {
        return ApiResponse.of(members.search(search).stream().map(MemberResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> findById(@PathVariable UUID id) {
        return ApiResponse.of(MemberResponse.from(members.findById(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemberResponse> register(@Valid @RequestBody RegisterMemberRequest request) {
        return ApiResponse.of(MemberResponse.from(members.register(request.toCommand())));
    }

    @PatchMapping("/{id}")
    public ApiResponse<MemberResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateMemberRequest request) {
        return ApiResponse.of(MemberResponse.from(members.update(id, request.toCommand())));
    }
}
