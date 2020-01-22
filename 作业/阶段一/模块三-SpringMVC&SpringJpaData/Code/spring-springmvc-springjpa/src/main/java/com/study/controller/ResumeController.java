package com.study.controller;

import com.study.pojo.Resume;
import com.study.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @RequestMapping("/query")
    public String query(ModelMap modelMap){
        List<Resume> resumes = resumeService.queryAll();
        modelMap.addAttribute("list", resumes);
        return "list";
    }

    @RequestMapping(value = "/{id}",method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@PathVariable("id")Long id){
        resumeService.delete(id);
        return "success";
    }

    @RequestMapping(value = "/{id}/{name}/{address}/{phone}",method = {RequestMethod.PUT})
    @ResponseBody
    public String update(@PathVariable("id")Long id, @PathVariable("name")String name,
                         @PathVariable("address")String address, @PathVariable("phone")String phone){
        Resume resume = new Resume();
        resume.setId(id);
        resume.setName(name);
        resume.setAddress(address);
        resume.setPhone(phone);

        Resume update = resumeService.save(resume);
        if(update != null) {
            return "success";
        } else {
            return "failed";
        }

    }

    @RequestMapping(value = "/{name}/{address}/{phone}",method = {RequestMethod.GET})
    @ResponseBody
    public String insert(@PathVariable("name")String name, @PathVariable("address")String address,
                         @PathVariable("phone")String phone){
        Resume resume = new Resume();
        resume.setName(name);
        resume.setAddress(address);
        resume.setPhone(phone);

        Resume save = resumeService.save(resume);
        if(save != null) {
            return "success";
        } else {
            return "failed";
        }
    }

}
