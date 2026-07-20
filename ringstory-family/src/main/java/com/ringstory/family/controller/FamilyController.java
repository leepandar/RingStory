package com.ringstory.family.controller;
import com.ringstory.family.dto.Result;
import com.ringstory.family.entity.FamilyEntity;
import com.ringstory.family.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping
    public Result<FamilyEntity> create(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        return Result.success(familyService.createFamily(body.get("name"), userId));
    }

    @GetMapping("/{id}")
    public Result<FamilyEntity> get(@PathVariable Long id) {
        return Result.success(familyService.getById(id));
    }

    @GetMapping("/{id}/members")
    public Result<?> getMembers(@PathVariable Long id) {
        return Result.success(familyService.getMembers(id));
    }

    @PostMapping("/{id}/invitation")
    public Result<?> createInvitation(@PathVariable Long id, @RequestParam Long userId) {
        return Result.success(familyService.createInvitation(id, userId));
    }
}
