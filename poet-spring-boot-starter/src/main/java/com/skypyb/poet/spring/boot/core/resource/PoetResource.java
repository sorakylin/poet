package com.skypyb.poet.spring.boot.core.resource;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件的下载、预览等操作的资源层，当然 也包括上传
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
