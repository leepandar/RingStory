package com.ringstory.search.service;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> searchPhotos(Long familyId, String keyword, Long personId,
                                                   String location, String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder(
            "SELECT p.id, p.oss_key, p.shoot_time, p.original_name, pn.content as note_content ");
        sql.append("FROM t_photo_2026 p LEFT JOIN t_photo_note pn ON p.id = pn.photo_id WHERE p.family_id=? AND p.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        params.add(familyId);

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (pn.content LIKE ? OR p.original_name LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (dateFrom != null) { sql.append(" AND p.shoot_time >= ?"); params.add(dateFrom); }
        if (dateTo != null) { sql.append(" AND p.shoot_time <= ?"); params.add(dateTo); }
        sql.append(" ORDER BY p.shoot_time DESC LIMIT 50");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }
}
