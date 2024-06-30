package com.videoplatform.videoplatform.controller;

import com.videoplatform.videoplatform.entity.Video;
import com.videoplatform.videoplatform.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("title") String title,
                                              @RequestParam("description") String description) {
        try {
            String videoUrl = videoService.uploadVideo(file, title, description);
            return ResponseEntity.ok(videoUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading video");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable Integer id) {
        return ResponseEntity.ok(videoService.getVideo(id));
    }

    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/{id}/previous")
    public ResponseEntity<Video> getPreviousVideo(@PathVariable Long id) {
        List<Video> videos = videoService.getAllVideos();
        for (int i = 1; i < videos.size(); i++) {
            if (videos.get(i).getId().equals(id)) {
                return ResponseEntity.ok(videos.get(i - 1));
            }
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/next")
    public ResponseEntity<Video> getNextVideo(@PathVariable Long id) {
        List<Video> videos = videoService.getAllVideos();
        for (int i = 0; i < videos.size() - 1; i++) {
            if (videos.get(i).getId().equals(id)) {
                return ResponseEntity.ok(videos.get(i + 1));
            }
        }
        return ResponseEntity.noContent().build();
    }

}
