package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.nio.file.Files.newInputStream;

public class S3Store implements BlobStore {
    private final AmazonS3Client s3Client;
    private final String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.contentType);
        s3Client.putObject(photoStorageBucket,blob.name, blob.inputStream, metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            S3Object object = s3Client.getObject(photoStorageBucket, name);
            ObjectMetadata objectMetadata = object.getObjectMetadata();
            return Optional.of(new Blob(object.getKey(), object.getObjectContent(), objectMetadata.getContentType()));
        } catch (Exception e) {
            try {
                URL defaultCover = this.getClass().getClassLoader().getResource("default-cover.jpg");
                Path path = Paths.get(defaultCover.toURI());
                return Optional.of(new Blob(name, newInputStream(path), new Tika().detect(path)));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteAll() {
        ObjectListing objectListing = s3Client.listObjects(photoStorageBucket);
        while (true) {
            Iterator<S3ObjectSummary> iterator = objectListing.getObjectSummaries().iterator();
            while (iterator.hasNext()) {
                s3Client.deleteObject(photoStorageBucket, iterator.next().getKey());
            }
            if (objectListing.isTruncated()) {
                objectListing = s3Client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }

    }
}
