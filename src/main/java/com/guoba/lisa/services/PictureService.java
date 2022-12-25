package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.Picture;
import com.guoba.lisa.dtos.PictureVo;
import com.guoba.lisa.repositories.PictureRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PictureService {
    @Value("${student.works.root}")
    private String worksRoot;
    @Value("${student.works.folders.max}")
    private Integer maxFolders;

    private final PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public String picturePathRelative(Long pictureId) {
        return "" + pictureId % maxFolders;
    }

    public String picturePathAbsolute(Long pictureId) {
        return worksRoot + File.separator + picturePathRelative(pictureId);
    }

    public String pictureFileAbsolute(Picture picture) {
        return worksRoot + File.separator + picture.getPath() + File.separator + picture.getId()
            + "." + picture.getExtension();
    }


    public PictureVo pictureData(Long picId, Long institutionId) throws IllegalAccessException, IOException {
        PictureVo vo = new PictureVo();
        Optional<Picture> pictureOpt = pictureRepository.findById(picId);
        if (!pictureOpt.isPresent() || !pictureOpt.get().getInstitution().getId().equals(institutionId)) {
            throw new IllegalAccessException("Picture not found.");
        }
        Picture picture = pictureOpt.get();
        vo.setContentType(picture.getMimetype());
        vo.setData(Files.readAllBytes(Paths.get(pictureFileAbsolute(picture))));

        return vo;
    }
}
