package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ThumbnailRepository extends JpaRepository<Thumbnail, Integer> {
}
