package com.study.respository;

import com.study.pojo.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * crud操作
 */
public interface ResumeRespository extends JpaRepository<Resume, Long> {
}
