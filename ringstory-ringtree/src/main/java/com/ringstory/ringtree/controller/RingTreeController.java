package com.ringstory.ringtree.controller;
import com.ringstory.ringtree.dto.Result;
import com.ringstory.ringtree.dto.RingTreeNode;
import com.ringstory.ringtree.service.RingTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ringtree")
@RequiredArgsConstructor
public class RingTreeController {
    private final RingTreeService ringTreeService;
    @GetMapping("/{familyId}")
    public Result<RingTreeNode> getRingTree(@PathVariable Long familyId) {
        return Result.success(ringTreeService.buildTree(familyId));
    }
}
