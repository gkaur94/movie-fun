package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.newInputStream;

public class FileStore implements BlobStore {
    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = getCoverFile(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(IOUtils.toByteArray(blob.inputStream));
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            Path coverFilePath = getExistingCoverPath(name);
            return Optional.of(new Blob(name, newInputStream(coverFilePath), new Tika().detect(coverFilePath)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {

    }

    private File getCoverFile(String name) {
        String coverFileName = format("covers/%s", name);
        return new File(coverFileName);
    }

    private Path getExistingCoverPath(String name) throws URISyntaxException {
        File coverFile = getCoverFile(name);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }
}
