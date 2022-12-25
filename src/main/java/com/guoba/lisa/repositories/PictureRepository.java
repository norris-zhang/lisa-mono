package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
}
