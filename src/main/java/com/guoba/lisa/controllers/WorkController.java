package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.Institution;
import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Picture;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.datamodel.Work;
import com.guoba.lisa.services.StudentService;
import com.guoba.lisa.services.WorkService;
import com.guoba.lisa.web.models.AddWork;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.ZonedDateTime;

@Controller
public class WorkController {
    private final StudentService studentService;
    private final WorkService workService;

    public WorkController(StudentService studentService, WorkService workService) {
        this.studentService = studentService;
        this.workService = workService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/works/add")
    public String addWork(Long stuId, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        try {
            Student student = studentService.getStudentById(stuId, authUser.getInstitutionId());
            model.addAttribute("student", student);
            return "works/add";
        } catch (IllegalAccessException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/classes");
            return "404";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMsg", "Student not found.");
            model.addAttribute("gobackurl", "/classes");
            return "404";
        }
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/works/add")
    public String createWork(@RequestParam MultipartFile workPic, @ModelAttribute AddWork addWork, Authentication auth, Model model,
                             RedirectAttributes redirectAttributes) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();

        Work work = new Work();
        work.setTitle(addWork.getWorkTitle());
        work.setDescription(addWork.getWorkDesc());
        work.setDate(addWork.getWorkDate());
        work.setUploadDate(ZonedDateTime.now());
        Student student = new Student();
        student.setId(addWork.getStuId());
        work.setStudent(student);
        LisaClass clazz = new LisaClass();
        clazz.setId(addWork.getWorkClass());
        work.setClazz(clazz);

        Picture picture = new Picture();
        Institution institution = new Institution();
        institution.setId(authUser.getInstitutionId());
        picture.setInstitution(institution);
        String fileName = workPic.getOriginalFilename();
        picture.setFileName(fileName);
        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
        picture.setExtension(ext);
        picture.setMimetype(workPic.getContentType());
        picture.setPath("");

        try {
            workService.createWork(work, picture, workPic.getBytes() ,authUser.getInstitutionId());
            redirectAttributes.addFlashAttribute("addWork", addWork);
            return "redirect:/student/" + addWork.getStuId();
        } catch (IOException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/classes");
            return "500";
        } catch (IllegalAccessException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/classes");
            return "404";
        }

    }
}
