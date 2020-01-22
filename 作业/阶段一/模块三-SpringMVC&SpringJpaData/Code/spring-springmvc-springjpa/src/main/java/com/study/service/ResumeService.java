package com.study.service;

import com.study.pojo.Resume;

import java.util.List;

public interface ResumeService {
    List<Resume> queryAll();

    void delete(Long id);

    Resume save(Resume resume);
}
