package com.example.demo2_resstapi.controller;

import com.example.demo2_resstapi.models.Product;
import com.example.demo2_resstapi.models.ResponseObject;
import com.example.demo2_resstapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/Products")
public class ProductController {

    @Autowired
    private ProductRepository repository ;

    @GetMapping("")
    List<Product> getAllProducts(){
       return repository.findAll();
    }

    @GetMapping("/{id}")
    //Lets return an jobject with data,messess, status
    ResponseEntity<ResponseObject> findById(@PathVariable Long id){
        Optional<Product> foundProduct = repository.findById(id);
        if (foundProduct.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Query product successfully", foundProduct)
            );
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("False", "cannot find product with id= " + id, "")
            );
        }
    }

    //Insert new Product with POST method
    //Postman: RAW, JSON
    @PostMapping("insert")
    ResponseEntity<ResponseObject> insertProduct(@RequestBody Product newProduct) {
        // validate: 2 product must not same name!!

        List<Product> foundProducts = repository.findByProductName(newProduct.getProductName().trim());
        if (foundProducts.size()>0){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("faild", "Product bi trung roi, hay dat lai ten khac di", "")
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Insert product successfully", repository.save(newProduct))
        );
    }

    //Update, upsert
    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newProduct, @PathVariable Long id){
        Product updateProduct = repository.findById(id)
                .map(product -> {
                    product.setProductName(newProduct.getProductName());
                    product.setYear(newProduct.getYear());
                    product.setPrice(newProduct.getPrice());
                    product.setUrl(newProduct.getUrl());
                    return repository.save(product);
                }).orElseGet(()-> {
                    newProduct.setId(id);
                    return repository.save(newProduct);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Update product successfully", updateProduct)   //updateProduct o day chinh la data
        );
        //ket qua se tra ve nhu sau
        //        "status": "ok",
        //                "message": "Update product successfully",
        //                "data": {
        //                    "id": 1,
        //                    "productName": "laptop1",
        //                    "year": 2020,
        //                    "price": 2200.0,
        //                    "url": ""
        //        }
        //    }
    }

    //Delte a product => DELETE mothod
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id){
        boolean exists = repository.existsById(id);
        if(exists) {
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("Ok", "Delete successfully", "")
            );
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot fint product id for delete", "")
            );
        }
//        {   KET QUA SE TRA VE NHU SAU
//            "status": "Ok",
//                "message": "Delete successfully",
//                "data": ""
//        }
    }


}
