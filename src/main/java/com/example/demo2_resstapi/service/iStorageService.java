package com.example.demo2_resstapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface iStorageService {
    //sẽ có 4 loại: 1.lưu    2.Load   3.Đọc file   4.Xóa file
    public String storeFile(MultipartFile file);
    public Stream<Path> loadAll(); //load all file inside a folder
    public byte[] readFileContent(String fileName); //readFileContent sẽ đọc 1 image, và trả về kiểu byte, rồi đưa chung vào mảng.
    public void deleteAllFiles();
}
