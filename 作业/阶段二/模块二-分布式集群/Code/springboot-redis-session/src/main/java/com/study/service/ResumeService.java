package com.study.service;

import com.study.pojo.Resume;

import java.util.List;

public interface ResumeService {
    // 查询所有
    List<Resume> queryAll();

    // 保存
    Resume save(Resume resume);

    // 删除
    void delete(Long id);
}
