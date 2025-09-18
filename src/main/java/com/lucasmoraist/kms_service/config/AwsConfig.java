package com.lucasmoraist.kms_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;

@Configuration
public class AwsConfig {

    @Value("${secrets.aws.access-key}")
    private String accessKey;

    @Value("${secrets.aws.secret-key}")
    private String secretKey;

    @Value("${secrets.aws.region}")
    private String region;

    @Bean
    public KmsClient kmsClient() {
        return KmsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

}
