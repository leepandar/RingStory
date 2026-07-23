package com.ringstory.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.ringstory.search.entity.PhotoDocument;
import com.ringstory.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeQuery;
import org.springframework.data.elasticsearch.core.query.NativeQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类（基于 Elasticsearch）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<PhotoDocument> searchPhotos(Long familyId, String keyword,
                                             Long personId, String location,
                                             String dateFrom, String dateTo) {
        NativeQueryBuilder queryBuilder = NativeQuery.builder();

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        boolQuery.must(m -> m.term(t -> t.field("familyId").value(familyId)));

        // 关键词搜索（备注内容 + 文件名）
        if (keyword != null && !keyword.isBlank()) {
            boolQuery.must(m -> m.multiMatch(mm -> mm
                    .fields("noteContent", "originalName", "tags")
                    .query(keyword)));
        }

        // 人物筛选
        if (personId != null) {
            boolQuery.must(m -> m.term(t -> t.field("persons").value(String.valueOf(personId))));
        }

        // 地理位置筛选（简化：使用 radius 参数）
        if (location != null && !location.isBlank()) {
            // location 格式: "lat,lon,radius"
            String[] parts = location.split(",");
            if (parts.length >= 3) {
                double lat = Double.parseDouble(parts[0]);
                double lon = Double.parseDouble(parts[1]);
                String distance = parts[2] + "km";
                boolQuery.must(m -> m.geoDistance(gd -> gd
                        .field("location")
                        .location(gl -> gl.latlon(ll -> ll.lat(lat).lon(lon)))
                        .distance(distance)));
            }
        }

        // 日期范围筛选
        if (dateFrom != null && !dateFrom.isBlank()) {
            LocalDateTime from = LocalDate.parse(dateFrom, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            boolQuery.must(m -> m.range(r -> r.field("shootTime").gte(from.toString())));
        }
        if (dateTo != null && !dateTo.isBlank()) {
            LocalDateTime to = LocalDate.parse(dateTo, DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
            boolQuery.must(m -> m.range(r -> r.field("shootTime").lte(to.toString())));
        }

        queryBuilder.withQuery(q -> q.bool(boolQuery.build()));
        queryBuilder.withPageable(org.springframework.data.domain.PageRequest.of(0, 50));

        SearchHits<PhotoDocument> hits = elasticsearchOperations.search(
                queryBuilder.build(), PhotoDocument.class);

        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public void syncPhotoNote(Long photoId, String noteContent) {
        try {
            Optional<PhotoDocument> existing = elasticsearchOperations.get(
                    String.valueOf(photoId), PhotoDocument.class);
            if (existing.isPresent()) {
                PhotoDocument doc = existing.get();
                doc.setNoteContent(noteContent);
                elasticsearchOperations.save(doc);
                log.info("ES 同步照片备注更新: photoId={}", photoId);
            }
        } catch (Exception e) {
            log.error("ES 同步照片备注失败: photoId={}", photoId, e);
        }
    }

    @Override
    public void syncPhotoTags(Long photoId, List<String> tags) {
        try {
            Optional<PhotoDocument> existing = elasticsearchOperations.get(
                    String.valueOf(photoId), PhotoDocument.class);
            if (existing.isPresent()) {
                PhotoDocument doc = existing.get();
                doc.setTags(tags);
                elasticsearchOperations.save(doc);
                log.info("ES 同步照片标签更新: photoId={}, tags={}", photoId, tags);
            }
        } catch (Exception e) {
            log.error("ES 同步照片标签失败: photoId={}", photoId, e);
        }
    }
}
