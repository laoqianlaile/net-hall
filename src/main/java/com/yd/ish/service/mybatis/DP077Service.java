package com.yd.ish.service.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.ish.dao.mybatis.DP077Mapper;
import com.yd.ish.model.mybatis.DP077;
@Service
public class DP077Service {
	@Autowired
	DP077Mapper DP077Mapper;
	
	
	public List<DP077> selectByCause(DP077 dp077){
		return DP077Mapper.selectByCause(dp077);
	}


	public int deleteByCause(DP077 dp077) {
		return DP077Mapper.deleteByCause(dp077);
		
	}


	public List<DP077> selectMaxSeqno() {
		return DP077Mapper.selectMaxSeqno();
		
	}


	public int db2batchInsert(List<DP077> list) {
		return DP077Mapper.db2batchInsert(list);
		
	}

	public int oraclebatchInsert(List<DP077> list) {
		return DP077Mapper.oraclebatchInsert(list);
		
	}
	
	public List<DP077> selectBySlh(int instance) {
		return DP077Mapper.selectBySlh(instance);
	}


	public int deleteBySlh(int instance) {
		return DP077Mapper.deleteBySlh(instance);
	}

	public List<DP077> selectRepeatRyxx(DP077 dp077) {
		return DP077Mapper.selectRepeatRyxx(dp077);
	}
	
	public List<DP077> selectRepeatGRZH(DP077 dp077) {
		return DP077Mapper.selectRepeatGRZH(dp077);
	}
	
	public int insert(DP077 dp077) {
		return DP077Mapper.insert(dp077);
	}


	public int updateBySeqnoInstance(DP077 dp077) {
		return DP077Mapper.updateBySeqnoInstance(dp077);
	}


	public List<DP077> selectRepeatDyw(DP077 dp077) {
		return DP077Mapper.selectRepeatDyw(dp077);
	}


	public List<DP077> selectOrderByKhsx(int instance) {
		return DP077Mapper.selectOrderByKhsx(instance);
	}
	
	 public List<DP077> selectBySlhAndField(Map<String,Object> map) {
        return DP077Mapper.selectBySlhAndField(map);
    }

	public List<DP077> selectRepeatLpxx(DP077 dp077) {
		return DP077Mapper.selectRepeatLpxx(dp077);
	}

	public List<DP077> selectRepeatLdxx(DP077 dp077) {
		return DP077Mapper.selectRepeatLdxx(dp077);
	}

    public int deleteByDpbusitype(@Param("dpbusitype")String[] dpbusitype, @Param("instance")int instance) {
		return DP077Mapper.deleteByDpbusitype(dpbusitype,instance);

    }
    public int selectLdLpCounts(DP077 dp077) {
		return DP077Mapper.selectLdLpCounts(dp077);
	}
}
