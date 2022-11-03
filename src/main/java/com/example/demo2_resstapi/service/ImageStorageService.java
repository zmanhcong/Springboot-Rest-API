package com.example.demo2_resstapi.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

@Service  //khai bao de java hieu day la 1 service.
public class ImageStorageService implements iStorageService{
    // mình sẽ sử dụng upload path là : path của project này + "uploads". ==> storageFolder = uploads.
    private final Path storageFolder = Paths.get("uploads");
    //Contructer . cai này sẽ được gọi khi có inject.
    public  ImageStorageService(){
        try {
            Files.createDirectories(storageFolder);
        }catch (IOException exception){
            throw  new RuntimeException("Cannot initialize storage", exception);
        }
    }

    private boolean isImageFile(MultipartFile file) {
        //Let install FileNameUtils để interface này cung cấp cho ta nhiều function liên quan đến file;
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()); //Phần này dùng để lấy ra tên đuôi file. ví dụ: png. jpg..vv..vv và "FilenameUtils" là utils mình mới đưa vào pom (commons.io)    }
        return Arrays.asList(new String[]{"png", "jpg", "jpeg", "bmg"})
                .contains(fileExtension.trim().toLowerCase());
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            System.out.println("haha");
            if (file.isEmpty()){
                throw new RuntimeException("Failed to store empty file.");
            }
            //check file is images ?
            if(!isImageFile(file)){
                throw new RuntimeException("You can only upload image file");
            }
            //file must be <= 5Mb
            float fileSizeInMegabytes = file.getSize()/1_000_000.0f;
            if (fileSizeInMegabytes > 5.0f) {
                throw new RuntimeException("File must be <= 5Mb ");
            }
            //File must be rename/ course file cannot duplicate by name.
            //Ta làm như sau: 1.lấy extension đuôi file. 2.tạo 1 chuỗi ngẫu nhiên. 3.ghép chuỗi ngẫu nhiên + đuôi file ( ví dụ:ax4543.png)
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String generatedFileName = UUID.randomUUID().toString().replace("-",""); //trong chuỗi ngẫu nhiên mà có "-" thì bỏ nó đi.
            generatedFileName = generatedFileName+"."+fileExtension;

            //Đây là lấy ra filePath để chuẩn bị cho việc lưu file.
            Path destinationFilePath = this.storageFolder.resolve(
                    Paths.get(generatedFileName)).normalize().toAbsolutePath();
            if (!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath())){
                throw new RuntimeException(
                        "Cannot store file outside current directory."
                );
            }

            //Copy file file này vào destination địa điểm lưu file.
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return generatedFileName;
        }catch (IOException exception){
            throw new RuntimeException("Failed to store file", exception);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        // load all the files tron thư mục bằng walk, độ sâu maxDepth là 1. vì trong thư mục uploads image chỉ có 1 tầng thôi.
        try {
            return Files.walk(this.storageFolder, 1)
                    // ignore the root path
                    .filter(path -> !path.equals(this.storageFolder))
                    .map(this.storageFolder::relativize);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }

    }

    @Override
    public byte[] readFileContent(String fileName) {
        try {
            //Từ controller chỉ định muốn đọc image(exg:et342.jpg), rồi mình sẽ lấy file đó. return thành byte.
            Path file = storageFolder.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()){
                byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                return bytes;
            }
            else {
                throw new RuntimeException(
                        "Could not read file: " + fileName);
            }
        }
        catch (IOException exception){
            throw new RuntimeException("Could not read file: " + fileName, exception);
        }
    }

    @Override
    public void deleteAllFiles() {

    }
}
