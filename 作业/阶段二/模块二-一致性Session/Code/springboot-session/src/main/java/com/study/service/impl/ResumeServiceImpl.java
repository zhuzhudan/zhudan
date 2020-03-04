package com.study.service.impl;

import com.study.pojo.Resume;
import com.study.respository.ResumeRespository;
import com.study.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    private ResumeRespository resumeRespository;

    @Override
    public List<Resume> queryAll() {
        return resumeRespository.findAll();
    }

    @Override
    public Resume save(Resume resume) {
        return resumeRespository.save(resume);
    }

    @Override
    public void delete(Long id) {
        resumeRespository.deleteById(id);
    }
}
