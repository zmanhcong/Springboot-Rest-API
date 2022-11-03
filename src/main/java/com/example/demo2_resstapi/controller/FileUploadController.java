package com.example.demo2_resstapi.controller;

import com.example.demo2_resstapi.models.ResponseObject;
import com.example.demo2_resstapi.service.iStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.security.cert.Extension;
import java.util.List;
import java.util.stream.Collectors;

//Upload image to server
@Controller
@RequestMapping(path = "/api/v1/FileUpload")
public class FileUploadController {
    //This controller receive file/image from client
    @Autowired
    private iStorageService storageService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam("file")MultipartFile file){
        try {
            // Save files to a foller -> use a service
            String generatedFileName = storageService.storeFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "upload file successfully", generatedFileName)
            );
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("ok",exception.getMessage(), "")
            );
        }
    }
    //get image's url: phục vụ cho việc truy cập vào image thông qua url.
    //input: đưa vào tên image muốn lấy ra. exg: http://localhost:8080/api/v1/FileUpload/files/c1db3adab6d64ce4880d0842cf98478d.png
    //output: lấy ra được image muốn lấy.
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName){
        try {
            byte[] bytes = storageService.readFileContent(fileName);  //đưa image muốn lấy thành bytes, rồi return .
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        }catch (Exception exception){
            return ResponseEntity.noContent().build();
        }
    }

    //Load tất cả ảnh có trong thư mục "loads"
    @GetMapping("")
    public ResponseEntity<ResponseObject> getUploadFiles(){
        try {
            List<String> urls = storageService.loadAll()
                    .map(path -> {
                        //convert file name to url(send request "readDetailFile") co the tham khao tai day: https://www.codementor.io/@nilmadhab/let-s-develop-file-upload-service-from-scratch-using-java-and-spring-boot-1e4tvdcja4
                        String urlPath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "readDetailFile", path.getFileName().toString()).build().toUri().toString();
                        return urlPath;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ResponseObject("ok", "List fils successfully", urls));
            //urls: ket quả trả ra là 1 list path của image. click vào thì sẽ xem được anh. exg: http://localhost:8080/api/v1/FileUpload/files/0bfdff963af442b6b4a815d1f25b8bc2.png
        }catch (Exception exception){
            return ResponseEntity.ok(new ResponseObject("ok", "List files failed, have no image in server", new String[]{}));
        }
    }

}
