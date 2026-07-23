package com.ringstory.ringtree.service;

import com.ringstory.ringtree.dto.RingTreeNodeDTO;

import java.util.List;

/**
 * 年轮树服务接口
 */
public interface RingTreeService {

    /**
     * 构建家庭年轮树
     */
    RingTreeNodeDTO buildTree(Long familyId);

    /**
     * 按需加载子节点（懒加载）
     * @param parentNodeId 父节点ID（null表示获取根节点的直接子节点）
     */
    List<RingTreeNodeDTO> getChildNodes(Long familyId, String parentNodeId);
}
