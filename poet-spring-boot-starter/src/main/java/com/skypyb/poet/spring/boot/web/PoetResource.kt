package com.skypyb.poet.spring.boot.web;

import com.skypyb.poet.spring.boot.autoconfigure.PoetProperties;
import com.skypyb.poet.spring.boot.core.PoetAnnexContext;
import com.skypyb.poet.spring.boot.core.exception.AnnexAccessException;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 附件的下载、预览、上传等相关操作的资源层
 * /bs开头的路径为附件相关的业务操作 (增删改查)
 */
//@RequestMapping(value = "#{poetProperties.webUrlPrefix}")
@RequestMapping(value = "${poet.webUrlPrefix:/poet}")
@RestController
public class PoetResource {

    @Autowired
    private PoetAnnexContext poetAnnexContext;
    @Autowired
    private PoetProperties poetProperties;

    private HttpServletResponse res() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        return attributes.getResponse();
    }

    private HttpServletRequest req() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    @GetMapping("/view/{name}")
    public void fileView(@PathVariable String name, HttpServletResponse response) {
        validateEnable();
        poetAnnexContext.view(name, res());
    }

    @GetMapping("/view-media/{name}")
    public void mediaFileView(@PathVariable String name) {
        validateEnable();
        poetAnnexContext.viewMedia(name, req(), res());
    }

    @GetMapping("/download/{name}")
    public void download(@PathVariable String name) {
        validateEnable();
        poetAnnexContext.down(name, res());
    }

    @RequestMapping("/up")
    public PoetAnnex upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "module", required = false) String module) throws IOException {
        validateEnable();
        final PoetAnnex save = poetAnnexContext.save(file.getInputStream(), file.getOriginalFilename(), module);
        return save;
    }

    //region -- Business

    @GetMapping("/bs/{name}")
    public PoetAnnex findAnnex(@PathVariable String name) {
        validateEnable();
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/bs/{name}")
    public PoetAnnex deleteAnnex(@PathVariable String name) {
        validateEnable();
        throw new UnsupportedOperationException();
    }

    //endregion


    private void validateEnable() {
        if (poetProperties.getEnableWebResource()) {
            return;
        }

        throw new AnnexAccessException("prohibit");
    }
}
