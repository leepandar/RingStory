package com.ringstory.ringtree.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 年轮树节点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingTreeNodeDTO {

    /** 节点标签 */
    private String label;

    /** 节点类型（root/year/season/month/day） */
    private String type;

    /** 照片数量 */
    private int photoCount;

    /** 子节点列表 */
    private List<RingTreeNodeDTO> children;
}
