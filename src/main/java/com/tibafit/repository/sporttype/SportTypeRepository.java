package com.tibafit.repository.sporttype;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tibafit.model.sporttype.SportTypeVO;

@Repository
public interface SportTypeRepository extends JpaRepository<SportTypeVO, Integer> {

	public Boolean existsBySportTypeNameAndSportTypeDataStatusNot(String sportTypeName, Integer notStatus);

    @EntityGraph(attributePaths = { 
    	"sportTypeItemVOs",
       	"sportTypeItemVOs.sportVO"
    })
    public Optional<SportTypeVO> findBySportTypeId(Integer sportTypeId);
    
    @EntityGraph(attributePaths = { 
    	"sportTypeItemVOs",
       	"sportTypeItemVOs.sportVO"
    })
    public List<SportTypeVO> findBySportTypeIdIn(List<Integer> sportTypeIds);
    
    
    @EntityGraph(attributePaths = { 
        "sportTypeItemVOs",
        "sportTypeItemVOs.sportVO"
    })
    public Optional<SportTypeVO> findBySportTypeIdAndSportTypeDataStatusIn(
    	Integer sportTypeId, 
    	List<Integer> SportTypeDataStatuses
    );
    
    @EntityGraph(attributePaths = { 
    	"sportTypeItemVOs",
       	"sportTypeItemVOs.sportVO"
    })
    public List<SportTypeVO> findBySportTypeIdInAndSportTypeDataStatusIn(
    	List<Integer> sportTypeIds, 
    	List<Integer> SportTypeDataStatuses
    );
    
    
    @EntityGraph(attributePaths = { 
    	"sportTypeItemVOs",
       	"sportTypeItemVOs.sportVO"
    })
    public List<SportTypeVO> findBySportTypeDataStatusIn(List<Integer> SportTypeDataStatuses, Sort sort);
    
    
	@Query(value = 
		       "SELECT * " +
		       "FROM sport_type s " +
		       "WHERE (:name IS NULL OR s.sport_type_name LIKE CONCAT('%', :name, '%')) " +
		       "  AND (:createStart IS NULL OR s.create_datetime >= :createStart) " +
		       "  AND (:createEnd IS NULL OR s.create_datetime <= :createEnd) " +
		       "  AND (:updateStart IS NULL OR s.update_datetime >= :updateStart) " +
		       "  AND (:updateEnd IS NULL OR s.update_datetime <= :updateEnd) " +
		       "  AND (s.sport_type_data_status IN :statuses)" +
		       "ORDER BY s.sport_type_id ASC",
		       nativeQuery = true)
		public List<SportTypeVO> findByComplexCondition(
		        @Param("name") String sportTypeNameFuzzy,
		        @Param("createStart") String createStartDate, 
		        @Param("createEnd") String createEndDate, 
		        @Param("updateStart") String updateStartDate, 
		        @Param("updateEnd") String updateEndDate, 
		        @Param("statuses") List<Integer> statuses);
    
    
	@Modifying
	@Query(value = "UPDATE sport_type SET sport_type_data_status = :dataStatus WHERE sport_type_id IN :sportTypeIds", nativeQuery = true)
	public Integer updateSportTypeDataStatusBySportTypeIds(@Param("dataStatus") Integer dataStatus, @Param("sportTypeIds") List<Integer> sportTypeIds);

}
