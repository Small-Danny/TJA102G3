package com.tibafit.repository.customsport;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.customsport.CustomSportVO;

public interface CustomSportRepository extends JpaRepository<CustomSportVO, Integer> {

    @Query(value = "SELECT * FROM custom_sport WHERE sport_data_status IN :statuses ORDER BY custom_sport_id DESC", nativeQuery = true)
    List<CustomSportVO> findBySportDataStatuses(@Param("statuses") List<Integer> statuses);

    @Query(
        value = "SELECT * FROM custom_sport WHERE sport_data_status IN :statuses ORDER BY custom_sport_id DESC",
        countQuery = "SELECT COUNT(*) FROM custom_sport WHERE sport_data_status IN :statuses",
        nativeQuery = true
    )
    Page<CustomSportVO> findBySportDataStatusesPage(@Param("statuses") List<Integer> statuses, Pageable pageable);

    
    @Modifying
    @Query(value = "UPDATE custom_sport SET sport_data_status = :status WHERE custom_sport_id IN :ids", nativeQuery = true)
    Integer updateSportDataStatusByIds(@Param("status") Integer status, @Param("ids") List<Integer> ids);
}
