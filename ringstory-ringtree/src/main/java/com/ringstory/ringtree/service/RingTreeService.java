package com.ringstory.ringtree.service;

import com.ringstory.ringtree.dto.RingTreeNode;

/**
 * 年轮树服务接口
 */
public interface RingTreeService {

    /**
     * 构建家庭年轮树
     */
    RingTreeNode buildTree(Long familyId);
}
