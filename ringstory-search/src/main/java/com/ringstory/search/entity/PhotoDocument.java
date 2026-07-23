package com.ringstory.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ES 照片文档实体
 * 对应索引 ringstory_photos
 */
@Data
@Document(indexName = "ringstory_photos")
@Setting(settingPath = "/es-settings/photo-settings.json")
public class PhotoDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private Long photoId;

    @Field(type = FieldType.Keyword)
    private Long familyId;

    @Field(type = FieldType.Date)
    private LocalDateTime shootTime;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String noteContent;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.GeoPoint)
    private GeoPoint location;

    @Field(type = FieldType.Keyword)
    private List<String> persons;

    @Field(type = FieldType.Boolean)
    private Boolean isFavorite;

    @Field(type = FieldType.Keyword)
    private String originalName;

    @Field(type = FieldType.Keyword)
    private String ossKey;

    @Field(type = FieldType.Keyword)
    private Long uploaderId;

    @Field(type = FieldType.Date)
    private LocalDateTime uploadTime;

    /**
     * 地理位置点
     */
    @Data
    public static class GeoPoint {
        private Double lat;
        private Double lon;

        public GeoPoint() {}

        public GeoPoint(Double lat, Double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}
