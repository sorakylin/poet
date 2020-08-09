package com.skypyb.poet.spring.boot.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件的下载、预览、上传相关操作的资源层
 */
//@RequestMapping("/poet")
@RestController
public class PoetResource {



    @GetMapping("/fv/{name}")
    public void fileView(@PathVariable String name) {

    }

    @GetMapping("/download/{name}")
    public void download(@PathVariable String name) {

    }

    @PostMapping("/up")
    public void upload(@RequestParam("file") MultipartFile file) {

    }


}
