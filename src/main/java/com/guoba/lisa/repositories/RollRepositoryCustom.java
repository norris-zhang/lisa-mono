package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Roll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RollRepositoryCustom {
    Page<Roll> findByClazzKeywordAndStudentKeyword(String classKeyword, String stuKeyword, PageRequest page);
}
