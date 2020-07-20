package com.yd.ish.dao.mybatis;

import java.util.List;
import java.util.Map;

import com.yd.ish.model.mybatis.DP077;
import org.apache.ibatis.annotations.Param;

public interface DP077Mapper {
    int deleteByPrimaryKey(Integer seqno);

    int insert(DP077 record);

    int insertSelective(DP077 record);

    DP077 selectByPrimaryKey(Integer seqno);

    int updateByPrimaryKeySelective(DP077 record);

    int updateByPrimaryKey(DP077 record);

	List<DP077> selectByCause(DP077 dp077);

	int deleteByCause(DP077 record);

	List<DP077> selectMaxSeqno();

	int oraclebatchInsert(List<DP077> list);

	List<DP077> selectBySlh(int instance);

	int deleteBySlh(int instance);

	int db2batchInsert(List<DP077> list);

	List<DP077> selectRepeatRyxx(DP077 dp077);
	
	List<DP077> selectRepeatGRZH(DP077 dp077);
	
	int updateBySeqnoInstance(DP077 dp077);

	List<DP077> selectRepeatDyw(DP077 dp077);

	List<DP077> selectOrderByKhsx(int instance);

	List<DP077> selectBySlhAndField(Map<String,Object> map);

	List<DP077> selectRepeatLpxx(DP077 dp077);

	List<DP077> selectRepeatLdxx(DP077 dp077);

	int deleteByDpbusitype(@Param("dpbusitype") String[] dpbusitype, @Param("instance") int instance);

    int selectLdLpCounts(DP077 dp077);
}