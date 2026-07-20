package com.ringstory.search.controller;

import com.ringstory.common.response.R;
import com.ringstory.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 搜索照片
     */
    @GetMapping
    public R<List<Map<String, Object>>> search(
            @RequestParam Long familyId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long personId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return R.success(searchService.searchPhotos(
                familyId, keyword, personId, location, dateFrom, dateTo));
    }
}
