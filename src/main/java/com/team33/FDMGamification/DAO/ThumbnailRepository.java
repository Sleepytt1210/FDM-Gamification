package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public interface ThumbnailRepository extends JpaRepository<Thumbnail, Integer> {
}
