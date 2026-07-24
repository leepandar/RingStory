use actix_web::{web, App, HttpServer, HttpResponse, middleware};
use serde::{Deserialize, Serialize};
use std::sync::Arc;

#[derive(Debug, Serialize, Deserialize)]
struct DetectRequest {
    image_url: String,
    family_id: u64,
}

#[derive(Debug, Serialize, Deserialize)]
struct DetectResponse {
    faces: Vec<FaceBox>,
}

#[derive(Debug, Serialize, Deserialize)]
struct FaceBox {
    x: f32,
    y: f32,
    w: f32,
    h: f32,
    confidence: f32,
    embedding: Vec<f32>,
}

async fn detect_face(req: web::Json<DetectRequest>) -> HttpResponse {
    // TODO: 下载图片 -> YOLOv8-face 检测 -> ArcFace 特征提取
    // 此处为模拟返回
    HttpResponse::Ok().json(DetectResponse {
        faces: vec![FaceBox {
            x: 100.0, y: 50.0, w: 60.0, h: 80.0,
            confidence: 0.95,
            embedding: vec![0.1; 512],
        }],
    })
}

async fn health() -> HttpResponse {
    HttpResponse::Ok().json(serde_json::json!({"status": "ok"}))
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    env_logger::init();
    log::info!("Starting RingStory Face Service on 0.0.0.0:8090");

    HttpServer::new(|| {
        App::new()
            .wrap(middleware::Logger::default())
            .route("/health", web::get().to(health))
            .route("/api/face/detect", web::post().to(detect_face))
    })
    .bind("0.0.0.0:8090")?
    .run()
    .await
}
