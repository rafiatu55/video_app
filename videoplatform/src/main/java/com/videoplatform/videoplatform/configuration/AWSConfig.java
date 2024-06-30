package com.videoplatform.videoplatform.configuration;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

@Configuration
public class AWSConfig {
    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey
    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client(){

        AwsBasicCredntials awsCreds = AwsBasicCredntials.create(accessKeyId, secretKey);
        return S3Client.builder()
                .region(Region.of(value))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
