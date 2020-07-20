package com.yd.ish.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yd.biz.engine.ITransLsxx;
import com.yd.svrplatform.util.DataPool;
@Service
public class TransLsxxImp implements ITransLsxx {

	@Override
	public String getTableName() {
		 
		return "gg_lsxx";
	}

	@Override
	public Map<String, Object> getExpPram(DataPool pool) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("ywlx", pool.getString("yewulx"));
		map.put("zjzhlx", pool.getString("zjzhlx"));
		return map;
	}

}
