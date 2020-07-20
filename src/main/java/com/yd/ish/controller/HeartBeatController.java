package com.yd.ish.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 心跳地址（满足K8S和综服对网厅的心跳地址监测）
 *
 */
@Controller
public class HeartBeatController {
	@RequestMapping(value="/heartbeat",produces="text/plain;charset=utf-8")
	@ResponseBody
	public String heartbeatCheck() {
		return "{\"recode\":\"000000\",\"msg\":\"成功\"}";
	}
}
