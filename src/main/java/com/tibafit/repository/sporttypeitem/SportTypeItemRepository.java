package com.tibafit.repository.sporttypeitem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tibafit.model.sporttypeitem.SportTypeItemVO;

@Repository
public interface SportTypeItemRepository extends JpaRepository<SportTypeItemVO, Integer> {
    
	public Boolean existsBySportTypeIdAndSportId(Integer sportTypeId, Integer sportId);
    
    
    @EntityGraph(attributePaths = { 
    	"sportTypeVO",
       	"sportVO"
    })
    public Optional<SportTypeItemVO> findBySportTypeItemId(Integer sportTypeItemId);
    
    
    @EntityGraph(attributePaths = { 
    	"sportTypeVO",
       	"sportVO"
    })
    public List<SportTypeItemVO> findBySportTypeItemIdIn(List<Integer> sportTypeItemIds);

    
    @EntityGraph(attributePaths = { 
    	"sportTypeVO",
       	"sportVO"
    })
    public List<SportTypeItemVO> findBySportTypeId(Integer sportTypeId);
    
    
    @EntityGraph(attributePaths = { 
    	"sportTypeVO",
       	"sportVO"
    })
    public List<SportTypeItemVO> findBySportTypeIdIn(List<Integer> sportTypeIds);

    
    @EntityGraph(attributePaths = { 
    	"sportTypeVO",
       	"sportVO"
    })
    public Optional<SportTypeItemVO> findBySportTypeIdAndSportId(Integer sportTypeId, Integer sportId);


	public void deleteBySportTypeItemIdIn(List<Integer> sportTypeRecordIds);
}
