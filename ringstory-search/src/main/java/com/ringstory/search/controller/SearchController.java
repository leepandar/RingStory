package com.ringstory.search.controller;
import com.ringstory.search.dto.Result;
import com.ringstory.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    @GetMapping
    public Result<?> search(@RequestParam Long familyId,
                             @RequestParam(required=false) String keyword,
                             @RequestParam(required=false) Long personId,
                             @RequestParam(required=false) String location,
                             @RequestParam(required=false) String dateFrom,
                             @RequestParam(required=false) String dateTo) {
        return Result.success(searchService.searchPhotos(familyId, keyword, personId, location, dateFrom, dateTo));
    }
}
