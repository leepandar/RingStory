package com.ringstory.search.controller;

import com.ringstory.common.exception.BusinessException;
import com.ringstory.common.exception.ErrorCode;
import com.ringstory.common.response.R;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ringstory.search.entity.PhotoDocument;
import com.ringstory.search.service.EsIndexService;
import com.ringstory.search.service.SearchHistoryService;
import com.ringstory.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final SearchHistoryService searchHistoryService;
    private final EsIndexService esIndexService;

    /**
     * 搜索照片（支持分页 + 参数校验）
     */
    @GetMapping
    @SentinelResource(value = "photoSearch", blockHandler = "searchBlock")
    public R<List<PhotoDocument>> search(
            @RequestParam Long familyId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long personId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        // 参数校验
        if (familyId == null) {
            throw new BusinessException(ErrorCode.SEARCH_PARAM_INVALID, "familyId不能为空");
        }
        // 记录搜索历史
        if (userId != null && keyword != null && !keyword.isBlank()) {
            searchHistoryService.addHistory(userId, keyword);
        }
        return R.success(searchService.searchPhotos(
                familyId, keyword, personId, location, dateFrom, dateTo, page, size));
    }

    /**
     * 获取搜索历史
     */
    @GetMapping("/history/{userId}")
    public R<List<String>> getHistory(@PathVariable Long userId) {
        return R.success(searchHistoryService.getHistory(userId));
    }

    /**
     * 清空搜索历史
     */
    @DeleteMapping("/history/{userId}")
    public R<Void> clearHistory(@PathVariable Long userId) {
        searchHistoryService.clearHistory(userId);
        return R.success();
    }

    /**
     * 重建 ES 索引（管理接口）
     */
    @PostMapping("/admin/reindex")
    public R<Void> reindex() {
        esIndexService.recreateIndex();
        return R.success();
    }

    // ==================== Sentinel 降级方法 ====================

    public R<List<PhotoDocument>> searchBlock(
            Long familyId, String keyword, Long personId, String location,
            String dateFrom, String dateTo, Long userId, int page, int size, BlockException ex) {
        log.warn("搜索接口被限流, familyId={}", familyId);
        return R.fail(ErrorCode.INTERNAL_ERROR, "搜索服务繁忙，请稍后重试");
    }
}
