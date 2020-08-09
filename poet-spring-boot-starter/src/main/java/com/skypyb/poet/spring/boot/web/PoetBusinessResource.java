package com.skypyb.poet.spring.boot.web;

import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一些业务操作相关的资源层, 例如信息获取、删除文件
 */
//@RequestMapping("/poet-bs")
@RestController
public class PoetBusinessResource {

    @GetMapping("/{name}")
    public PoetAnnex findAnnex(@PathVariable String id) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{name}")
    public PoetAnnex deleteAnnex(@PathVariable String id) {
        throw new UnsupportedOperationException();
    }

}
