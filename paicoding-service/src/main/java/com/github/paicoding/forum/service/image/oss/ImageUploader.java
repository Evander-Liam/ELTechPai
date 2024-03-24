package com.github.paicoding.forum.service.image.oss;

import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YiHui
 * @date 2023/1/12
 */
public interface ImageUploader {
    String DEFAULT_FILE_TYPE = "txt";
    Set<MediaType> STATIC_IMG_TYPE = new HashSet<>(Arrays.asList(MediaType.ImagePng, MediaType.ImageJpg, MediaType.ImageWebp, MediaType.ImageGif));

    /**
     * 文件上传
     *
     * @param input
     * @param fileType
     * @return
     */
    String upload(InputStream input, String fileType);

    /**
     * 判断外网图片是否依然需要处理
     *
     * @param fileUrl
     * @return true 表示忽略，不需要转存
     */
    boolean uploadIgnore(String fileUrl);

    /**
     * 根据魔数获取图片文件类型
     *
     * @param input
     * @param fileType
     * @return
     */
    default String getFileType(InputStream input, String fileType) throws IOException {
        if (StringUtils.isNotBlank(fileType)) {
            return fileType;
        }

        if (!(input instanceof ByteInputStream)) {
            byte[] bytes = StreamUtils.copyToByteArray(input);
            input = new ByteArrayInputStream(bytes);
        }

        // 根据魔数判断文件类型
        MediaType type = MediaType.typeOfMagicNum(FileReadUtil.getMagicNum((ByteArrayInputStream) input));
        if (STATIC_IMG_TYPE.contains(type)) {
            return type.getExt();
        }

        return null;
    }
}
