package com.study.controller;

import com.study.pojo.Resume;
import com.study.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 处理业务逻辑的
 */
@Controller
@RequestMapping("/resume")
public class ResumeController {
    @Autowired
    private ResumeService resumeService;

    // 获取列表
    @RequestMapping("/query")
    public String query(ModelMap modelMap){
        List<Resume> resumes = resumeService.queryAll();
        modelMap.addAttribute("list", resumes);
        return "list";
    }

    // 删除
    @RequestMapping(value = "/{id}",method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@PathVariable("id")Long id){
        resumeService.delete(id);
        return "success";
    }

    // 更新内容
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

    // 插入
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
