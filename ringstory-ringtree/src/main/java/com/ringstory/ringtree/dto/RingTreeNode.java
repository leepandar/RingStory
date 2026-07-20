package com.ringstory.ringtree.dto;
import lombok.AllArgsConstructor; import lombok.Data;
import java.util.List;
@Data @AllArgsConstructor
public class RingTreeNode {
    private String label;
    private String type; // year/season/month/day
    private int photoCount;
    private List<RingTreeNode> children;
}
