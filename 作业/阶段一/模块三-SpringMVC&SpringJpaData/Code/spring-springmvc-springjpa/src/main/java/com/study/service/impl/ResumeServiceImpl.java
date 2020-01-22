package com.study.service.impl;

import com.study.dao.ResumeDao;
import com.study.pojo.Resume;
import com.study.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeDao resumeDao;

    @Override
    public List<Resume> queryAll() {
        return resumeDao.findAll();
    }

    @Override
    public void delete(Long id) {
        resumeDao.deleteById(id);
    }

    @Override
    public Resume save(Resume resume) {
        return resumeDao.save(resume);
    }
}
