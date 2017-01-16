package towntalk.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by sin31 on 2016-08-03.
 */

@Component
public class AppConfig {
    @Value("${security.token.subject}")
    private String tokenSubject;

    @Value("${security.token.key}")
    private String secretKey;

    @Value("${security.token.expiration}")
    private String tokenExpiration;

    @Value("${security.aes256.private.key}")
    private String privateKey;

    @Value("${security.aes256.public.key}")
    private String publicKey;

    @Value("${aws.access.key.id}")
    private String awsAccessKey;

    @Value("${aws.secret.access.key}")
    private String awsSecretAccessKey;

    @Value("${aws.s3.bucket.weplace}")
    private String awsS3bucketWeplace;

    public String getAwsS3bucketWeplace() {
        return awsS3bucketWeplace;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public String getAwsSecretAccessKey() {
        return awsSecretAccessKey;
    }

    public String getTokenSubject() {
        return tokenSubject;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getTokenExpiration() {
        return tokenExpiration;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
