package com.skypyb.poet.spring.boot.core.client;

import com.skypyb.poet.spring.boot.core.exception.AnnexOperationException;
import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex;
import com.skypyb.poet.spring.boot.core.model.Navigation;
import com.skypyb.poet.spring.boot.core.util.HttpResourceViewUtils;
import com.skypyb.poet.spring.boot.core.util.StreamUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * 以本地文件系统作为服务基础来进行附件操作的客户端
 */
public class LocalFileServerClient implements PoetAnnexClient, PoetAnnexClientHttpSupport {

    private PoetAccessRouter router;


    //region setter/getter

    public LocalFileServerClient(PoetAccessRouter router) {
        Assert.notNull(router, "router not be null!");
        this.router = router;
    }


    @Override
    public void setRouter(PoetAccessRouter router) {
        Assert.notNull(router, "router not be null!");
        this.router = router;
    }


    @Override
    public Optional<PoetAccessRouter> getRouter() {
        return Optional.ofNullable(router);
    }

    // endregion


    //region Implements PoetAnnexClient methods

    @Override
    public DefaultPoetAnnex save(InputStream in, String name) {
        return save(in, name, null);
    }

    @Override
    public DefaultPoetAnnex save(InputStream in, String name, String module) {
        Navigation routing = router.routing(module, name);
        Path path = generatePath(routing);

        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            //创建、覆盖
            Files.copy(in, path);
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to save file!", e);
            ex.setPath(routing.getPath());
            throw ex;
        } finally {
            StreamUtil.close(in);
        }

        DefaultPoetAnnex annex = DefaultPoetAnnex.of(routing);

        try {
            annex.setLength(Files.size(path));
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to read file length!", e);
            ex.setPath(routing.getPath());
            throw ex;
        }

        return annex;
    }

    @Override
    public DefaultPoetAnnex save(byte[] data, String name) {
        return save(data, name, null);
    }

    @Override
    public DefaultPoetAnnex save(byte[] data, String name, String module) {
        Navigation routing = router.routing(module, name);
        Path path = generatePath(routing);

        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            //创建、覆盖
            Files.write(path, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DefaultPoetAnnex annex = DefaultPoetAnnex.of(routing);

        try {
            annex.setLength(Files.size(path));
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to save file!", e);
            ex.setPath(routing.getPath());
            throw ex;
        }

        return annex;
    }

    /**
     * 保存时调用的方法, 根据指定路由地址生成本机路径
     *
     * @param routing 路由导航, 指示了一个地址
     * @return java.nio.file.Path 标识的本机路径
     */
    private Path generatePath(Navigation routing) {
        String fullPath = routing.getFullPath();
        return Paths.get(fullPath);
    }

    @Override
    public boolean exist(String key) {
        final Path path = Paths.get(router.formatKey(key));
        return Files.exists(path);
    }


    @Override
    public void delete(String key) {
        final Path path = Paths.get(router.formatKey(key));
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to delete file!", e);
            ex.setPath(key);
            throw ex;
        }
    }


    @Override
    public byte[] getBytes(String key) {
        final Path path = Paths.get(router.formatKey(key));
        //exist ?

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to read file bytes!", e);
            ex.setPath(key);
            throw ex;
        }
    }

    //endregion


    //region Implements PoetAnnexClientHttpSupport methods

    @Override
    public void view(String key, HttpServletResponse response) {
        final Path path = Paths.get(router.formatKey(key));

        final String[] split = key.split(router.getDelimiter());
        final String name = split[split.length - 1];

        try {
            response.reset();

            if (!Files.exists(path)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            long length = Files.size(path);
            long lastModified = Files.getLastModifiedTime(path).toMillis();
            long expires = System.currentTimeMillis() + 604800000L;

            response.addHeader("Content-Type", HttpResourceViewUtils.getContentTypeForSuffix(name));
            response.addHeader("Cache-Control", "max-age=315360000");
            response.addHeader("Accept-Ranges", "bytes");
            response.addHeader("ETag", HttpResourceViewUtils.getETag(lastModified, length));
            response.addHeader("Last-Modified", new Date(lastModified).toString());
            response.addHeader("Expires", (new Date(expires)).toString());
            response.addHeader("Content-Length", String.valueOf(length));

            try (ServletOutputStream out = response.getOutputStream()) {
                Files.copy(path, out);
            }
        } catch (Exception e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to view file!", e);
            ex.setPath(key);
            throw ex;
        }
    }

    @Override
    public void viewMedia(String key, HttpServletResponse response) {
        final Path path = Paths.get(router.formatKey(key));

        final String[] split = key.split(router.getDelimiter());
        final String name = split[split.length - 1];

        try {
            response.reset();

            if (!Files.exists(path)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            long length = Files.size(path);
            long lastModified = Files.getLastModifiedTime(path).toMillis();
            long expires = System.currentTimeMillis() + 604800000L;

            response.addHeader("Content-Type", HttpResourceViewUtils.getContentTypeForSuffix(name));
            response.addHeader("Connection", "keep-alive");
            response.addHeader("Cache-Control", "max-age=315360000");
            response.addHeader("Content-Disposition", "inline;filename=\"" + name + "\"");
            response.addHeader("Accept-Ranges", "bytes");
            response.addHeader("ETag", HttpResourceViewUtils.getETag(lastModified, length));
            response.addHeader("Last-Modified", new Date(lastModified).toString());
            response.addHeader("Expires", (new Date(expires)).toString());
            response.addHeader("Content-Range", "bytes 0-" + (length - 1L) + "/" + length);
            response.addHeader("Content-Length", String.valueOf(length));

            try (ServletOutputStream out = response.getOutputStream()) {
                Files.copy(path, out);
            }

        } catch (Exception e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to view media file!", e);
            ex.setPath(key);
            throw ex;
        }

    }

    @Override
    public void viewMedia(String key, HttpServletRequest request, HttpServletResponse response) {
        final Path path = Paths.get(router.formatKey(key));

        final String[] split = key.split(router.getDelimiter());
        final String name = split[split.length - 1];

        try {
            response.reset();

            if (!Files.exists(path)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            //多次断点请求的标识
            String range = request.getHeader("Range");
            if (!StringUtils.hasText(range)) range = request.getHeader("range");

            final RandomAccessFile randomFile = new RandomAccessFile(path.toFile(), "r");//只读模式

            long contentLength = randomFile.length();
            long lastModified = Files.getLastModifiedTime(path).toMillis();

            int start = 0, end = 0;
            if (Objects.nonNull(range) && range.startsWith("bytes=")) {
                String[] values = range.split("=")[1].split("-");
                start = Integer.parseInt(values[0]);
                if (values.length > 1) {
                    end = Integer.parseInt(values[1]);
                }
            }

            int requestSize = 0;
            if (end != 0 && end > start) {
                requestSize = end - start + 1;
            } else {
                requestSize = Integer.MAX_VALUE;
            }

            response.setContentType(HttpResourceViewUtils.getContentTypeForSuffix(name));
            response.setHeader("Accept-Ranges", "bytes");
            response.addHeader("ETag", HttpResourceViewUtils.getETag(lastModified, contentLength));
            response.addHeader("Last-Modified", new Date(lastModified).toString());

            //第一次请求返回content length来让客户端请求多次实际数据
            if (range == null) {
                response.setHeader("Content-length", String.valueOf(contentLength));
            } else {
                //以后的多次以断点续传的方式来返回
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206
                long requestStart = 0, requestEnd = 0;
                String[] ranges = range.split("=");
                if (ranges.length > 1) {
                    String[] rangeDatas = ranges[1].split("-");
                    requestStart = Integer.parseInt(rangeDatas[0]);
                    if (rangeDatas.length > 1) {
                        requestEnd = Integer.parseInt(rangeDatas[1]);
                    }
                }

                long length = 0;
                if (requestEnd > 0) {
                    length = requestEnd - requestStart + 1;
                    response.setHeader("Content-length", String.valueOf(length));
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
                } else {
                    length = contentLength - requestStart;
                    response.setHeader("Content-length", String.valueOf(length));
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength);
                }
            }

            ServletOutputStream out = response.getOutputStream();
            byte[] buffer = new byte[4096];

            int needSize = requestSize;
            randomFile.seek(start);
            while (needSize > 0) {
                int len = randomFile.read(buffer);
                if (needSize < buffer.length) {
                    out.write(buffer, 0, needSize);
                } else {
                    out.write(buffer, 0, len);
                    if (len < buffer.length) {
                        break;
                    }
                }
                needSize -= buffer.length;
            }

            StreamUtil.close(randomFile, out);//close...

        } catch (Exception e) {
            AnnexOperationException ex = new AnnexOperationException("Failed to view media file!", e);
            ex.setPath(key);
            throw ex;
        }
    }

    @Override
    public void down(String key, HttpServletResponse response) {

        final String delimiter = router.getDelimiter();
        final String[] split = key.split(delimiter);
        final String name = split[split.length - 1];
        down(key, name, response);
    }

    @Override
    public void down(String key, String realName, HttpServletResponse response) {
        final Path path = Paths.get(router.formatKey(key));
        response.reset();// 清空输出流
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + realName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");

        try (ServletOutputStream out = response.getOutputStream()) {
            Files.copy(path, out);
        } catch (IOException e) {
            AnnexOperationException ex = new AnnexOperationException("File download failed!", e);
            ex.setPath(key);
            throw ex;
        }
    }

    //endregion


}
