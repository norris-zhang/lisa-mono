package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.dtos.PictureVo;
import com.guoba.lisa.services.PictureService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class PictureController {
    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {this.pictureService = pictureService;}

    @GetMapping(path = "/picture/{picId}")
    public ResponseEntity<InputStreamResource> picture(@PathVariable Long picId, Authentication auth) {
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        try {
            PictureVo pictureVo = pictureService.pictureData(picId, authUser.getInstitutionId());

            String[] split = pictureVo.getContentType().split("/");
            return ResponseEntity.ok()
                .contentType(new MediaType(split[0], split[1]))
                .body(new InputStreamResource(new ByteArrayInputStream(pictureVo.getData())));
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
