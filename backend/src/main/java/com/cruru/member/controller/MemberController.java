package com.cruru.member.controller;

import com.cruru.member.controller.dto.MemberCreateRequest;
import com.cruru.member.service.MemberService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid MemberCreateRequest request) {
        long memberId = memberService.create(request);
        return ResponseEntity.created(URI.create("/v1/members/" + memberId)).build();
    }
}