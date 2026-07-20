package com.ringstory.ringtree.controller;

import com.ringstory.common.response.R;
import com.ringstory.ringtree.dto.RingTreeNode;
import com.ringstory.ringtree.service.RingTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 年轮树控制器
 */
@RestController
@RequestMapping("/api/ringtree")
@RequiredArgsConstructor
public class RingTreeController {

    private final RingTreeService ringTreeService;

    /**
     * 获取家庭年轮树
     */
    @GetMapping("/{familyId}")
    public R<RingTreeNode> getRingTree(@PathVariable Long familyId) {
        return R.success(ringTreeService.buildTree(familyId));
    }
}
