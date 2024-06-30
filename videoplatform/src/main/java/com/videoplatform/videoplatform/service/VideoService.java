package com.videoplatform.videoplatform.service;

import com.videoplatform.videoplatform.entity.Video;
import com.videoplatform.videoplatform.exception.ResourceNotFoundException;
import com.videoplatform.videoplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;

@Service
public class VideoService {
    @Autowired
    private S3Client s3Client;

    @Autowired
    private VideoRepository videoRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadVideo(MultipartFile file, String title, String description) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        String videoUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoUrl(videoUrl);
        videoRepository.save(video);

        return videoUrl;
    }

    public Video getVideo(Integer id) {
        return videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

}
