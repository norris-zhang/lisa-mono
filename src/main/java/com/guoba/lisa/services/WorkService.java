package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Picture;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.datamodel.Work;
import com.guoba.lisa.repositories.LisaClassRepository;
import com.guoba.lisa.repositories.PictureRepository;
import com.guoba.lisa.repositories.StudentRepository;
import com.guoba.lisa.repositories.WorkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class WorkService {
    private final PictureRepository pictureRepository;
    private final WorkRepository workRepository;
    private final StudentRepository studentRepository;
    private final LisaClassRepository classRepository;
    private final PictureService pictureService;

    public WorkService(PictureRepository pictureRepository, WorkRepository workRepository, StudentRepository studentRepository,
                       LisaClassRepository classRepository, PictureService pictureService) {
        this.pictureRepository = pictureRepository;
        this.workRepository = workRepository;
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
        this.pictureService = pictureService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createWork(Work work, Picture picture, byte[] pictureBytes, Long institutionId) throws IllegalAccessException, IOException {
        Optional<Student> studentOpt = studentRepository.findById(work.getStudent().getId());
        if (!studentOpt.isPresent() || !studentOpt.get().getInstitution().getId().equals(institutionId)) {
            throw new IllegalAccessException("Student not found.");
        }
        Optional<LisaClass> clazzOpt = classRepository.findById(work.getClazz().getId());
        if (!clazzOpt.isPresent() || !clazzOpt.get().getInstitution().getId().equals(institutionId)) {
            throw new IllegalAccessException("Class not found.");
        }

        pictureRepository.save(picture);

        String absolutePath = pictureService.picturePathAbsolute(picture.getId());
        Files.createDirectories(Paths.get(absolutePath));
        Files.write(Paths.get(absolutePath, picture.getId() + "." + picture.getExtension()), pictureBytes);

        picture.setPath(pictureService.picturePathRelative(picture.getId()));
        pictureRepository.save(picture);

        work.setPicture(picture);
        workRepository.save(work);

    }
}
