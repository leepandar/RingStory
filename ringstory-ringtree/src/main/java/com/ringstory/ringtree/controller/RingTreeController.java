package com.ringstory.ringtree.controller;

import com.ringstory.common.response.R;
import com.ringstory.ringtree.dto.RingTreeNode;
import com.ringstory.ringtree.service.RingTreeInvalidService;
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
    private final RingTreeInvalidService ringTreeInvalidService;

    /**
     * 获取家庭年轮树
     */
    @GetMapping("/{familyId}")
    public R<RingTreeNode> getRingTree(@PathVariable Long familyId) {
        return R.success(ringTreeService.buildTree(familyId));
    }

    /**
     * 使年轮树缓存失效
     */
    @PostMapping("/{familyId}/invalidate")
    public R<Void> invalidateCache(@PathVariable Long familyId) {
        ringTreeInvalidService.invalidateCache(familyId);
        return R.success();
    }

    /**
     * 强制重建年轮树缓存
     */
    @PostMapping("/{familyId}/rebuild")
    public R<Void> rebuildCache(@PathVariable Long familyId) {
        ringTreeInvalidService.rebuildCache(familyId);
        return R.success();
    }
}
