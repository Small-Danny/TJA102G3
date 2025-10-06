package com.tibafit.repository.sport;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tibafit.model.sport.SportDataStatus;
import com.tibafit.model.sport.SportVO;

public interface SportRepository extends JpaRepository<SportVO, Integer> {
	static final int DATA_STATUS_DELETE = SportDataStatus.DELETE.getCodeNum();
	
	
	public Boolean existsBySportNameAndSportDataStatusNot(String sportName, Integer notStatus);
	
	
	@Query(value = "SELECT * FROM sport WHERE sport_data_status IN :statuses ORDER BY sport_id DESC", nativeQuery = true)
	List<SportVO> findBySportDataStatuses(@Param("statuses") List<Integer> statuses);
	
	
	@Query(
			value = "SELECT * FROM sport WHERE sport_data_status IN :statuses ORDER BY sport_id DESC", 
			countQuery="SELECT * FROM sport WHERE sport_data_status IN :statuses",
			nativeQuery = true
			)
	Page<SportVO> findBySportDataStatusesPage(@Param("statuses") List<Integer> statuses, Pageable pageable);
	
    
    @Query(value = 
    	       "SELECT * " +
    	       "FROM sport s " +
    	       "WHERE ( " +
    	       "        :nameDesc IS NULL " +
    	       "        OR ( " +
    	       "            s.sport_name LIKE CONCAT('%', :nameDesc, '%') " +
    	       "            OR s.sport_description LIKE CONCAT('%', :nameDesc, '%') " +
    	       "        ) " +
    	       "      ) " +
    	       "  AND (:level IS NULL OR s.sport_level = :level) " +
    	       "  AND (:createStart IS NULL OR s.create_datetime >= :createStart) " +
    	       "  AND (:createEnd IS NULL OR s.create_datetime <= :createEnd) " +
    	       "  AND (:updateStart IS NULL OR s.update_datetime >= :updateStart) " +
    	       "  AND (:updateEnd IS NULL OR s.update_datetime <= :updateEnd) " +
    	       "  AND (s.sport_data_status IN :statuses)" +
    	       "ORDER BY s.sport_id DESC",
	       nativeQuery = true)
	public List<SportVO> findByComplexCondition(
	        @Param("nameDesc") String sportNameDescFuzzy,
	        @Param("level") String sportLevel,
	        @Param("createStart") String createStartDate,
	        @Param("createEnd") String createEndDate,
	        @Param("updateStart") String updateStartDate,
	        @Param("updateEnd") String updateEndDate,
	        @Param("statuses") List<Integer> statuses);
    
	
    @Modifying
    @Query(value = "UPDATE sport SET sport_data_status = :sport_data_status WHERE sport_id IN :ids", nativeQuery = true)
    Integer updateSportDataStatusByIds(@Param("sport_data_status") Integer status, @Param("ids") List<Integer> ids);
}

