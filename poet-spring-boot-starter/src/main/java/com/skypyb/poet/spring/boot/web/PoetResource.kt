package com.skypyb.poet.spring.boot.web

import com.skypyb.poet.spring.boot.autoconfigure.PoetProperties
import com.skypyb.poet.spring.boot.PoetAnnexContext
import com.skypyb.poet.core.exception.AnnexAccessException
import com.skypyb.poet.core.model.PoetAnnex
import com.skypyb.poet.spring.boot.store.PoetAnnexRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 附件的下载、预览、上传等相关操作的资源层
 * /bs开头的路径为附件相关的业务操作 (增删改查)
 */
//@RequestMapping(value = "#{poetProperties.webUrlPrefix}")
@RequestMapping(value = ["\${poet.webUrlPrefix:/poet}"])
@RestController
class PoetResource {

    @Autowired
    private lateinit var poetAnnexContext: PoetAnnexContext
    @Autowired
    private lateinit var poetProperties: PoetProperties

    @Autowired
    private var poetAnnexRepository: PoetAnnexRepository? = null

    private fun res(): HttpServletResponse {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        return attributes.response!!
    }

    private fun req(): HttpServletRequest {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        return attributes.request
    }


    @GetMapping("/view/{name}")
    fun fileView(@PathVariable name: String) {
        validateEnable()
        poetAnnexContext.view(name, res())
    }

    @GetMapping("/view-media/{name}")
    fun mediaFileView(@PathVariable name: String?) {
        validateEnable()
        poetAnnexContext.viewMedia(name, req(), res())
    }

    @GetMapping("/download/{name}")
    fun download(@PathVariable name: String?) {
        validateEnable()
        poetAnnexContext.down(name, res())
    }

    @RequestMapping("/up")
    @Throws(IOException::class)
    fun upload(@RequestParam("file") file: MultipartFile,
               @RequestParam(value = "module", required = false) module: String?): PoetAnnex {
        validateEnable()
        return poetAnnexContext.save(file.inputStream, file.originalFilename, module)
    }

    //region -- Business
    @GetMapping("/bs/{name}")
    fun findAnnex(@PathVariable name: String?): PoetAnnex? {
        validateEnable()
        val annex = poetAnnexRepository!!.findByName(name)
        return annex
    }

    @DeleteMapping("/bs/{name}")
    fun deleteAnnex(@PathVariable name: String?): Unit {
        validateEnable()
        poetAnnexContext.delete(name)
    }

    //endregion


    private fun validateEnable() {
        if (poetProperties.enableWebResource && poetProperties.enableDBStore) {
            return
        }
        throw AnnexAccessException("prohibit")
    }
}