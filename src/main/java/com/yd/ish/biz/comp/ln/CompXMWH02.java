package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 名称：CompXMWH02
 * <p>功能：校验楼栋、楼盘信息是否有信息变更<br>
 * @brief 校验楼栋、楼盘信息是否有信息变更
 * @author 柏慧敏
 * @version 0.1 柏慧敏 2019-06-17 创建
 * @note
 */
@Component("CompXMWH02")
public class CompXMWH02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXMWH02.class);
	/**
	 * 数据分割符
	 */
	private static final String file_separators = ReadProperty.getString("file_separators");
	/**
	 * 编码格式
	 */
	private static final String READFILE_ENCODING_BSP = ReadProperty.getString("readfile_encoding_bsp");

	@Autowired
	DP077Service dp077Service;

	@Autowired
	GetDownFileMap getDownFileMap;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		String filename=getString("filename");//下传文件名称
    	/*入口参数赋值结束*/

		//校验标识 1-有变化，0-没有变化
		String jyjg = "0";//校验结果
		//获取下传文件中的楼栋信息
		List<HashMap<String, Object>> ldlist = new ArrayList();
		//获取下传文件中的楼盘信息
		List<HashMap<String, Object>> lplist = new ArrayList();

		// 查询动态列表临时数据 提交前楼栋信息
		DP077 dp077 = new DP077();
		dp077.setDpbusitype("03");
		dp077.setInstance(instance);
		List<DP077> dp077ldlist = dp077Service.selectByCause(dp077);
		dp077.setDpbusitype("04");
		List<DP077> dp077lplist = dp077Service.selectByCause(dp077);
		if (!"".equals(filename)) {
			String[] filenames = filename.split(",");
			for(int i = 0; i<filenames.length;i++){
				File file = FileSwap.getFile(filenames[i]);
				if (file == null) {
					logger.error("下传文件不存在：" + filenames[i]);
					throw new TransOtherException("系统错误，请查看日志！");
				} else {
					//解析下传文件
					BufferedReader br = null;
					try {
						br = new BufferedReader(new InputStreamReader(new FileInputStream(file), READFILE_ENCODING_BSP));
						//批量查表一次行数
						int count = 0;
						//累计记录文件总行数
						int total = 0;
						//是否为文件表头信息
						Boolean first = true;
						//记录行文件
						String tmp = null;

						//实际返回的数据
						HashMap<String, Object> tmpdata = null;
						//文件中第一行列名
						String[] fileColNames = null;

						//如果文件信息不为null，开始解析文件
						while ((tmp = br.readLine()) != null) {
							if (first) {
								fileColNames = tmp.toLowerCase().split(file_separators);
								first = false;
								continue;
							} else {
								tmpdata = getDownFileMap.query(fileColNames, tmp);
								if(tmpdata.get("ywlx") != null && "03".equals(tmpdata.get("ywlx").toString())){
									ldlist.add(tmpdata);
								} else if(tmpdata.get("ywlx") != null && "04".equals(tmpdata.get("ywlx").toString())){
									lplist.add(tmpdata);
								}
							}
						}
					} catch (RuntimeException | IOException e) {
						YDLogger.openOut();
						e.printStackTrace();
						throw new TransOtherException("系统错误，请查看日志！");
					} finally {
						//打开临时关闭日志输出
						YDLogger.openOut();
						try {
							if (br != null) {
								br.close();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
							logger.error("关闭文件出错：" + e1.getMessage(), e1);
							throw new TransOtherException("系统错误，请查看日志！");
						}
					}
				}
			}
		} else {
			logger.error("读取楼栋楼盘信息文件失败!");
		}
		int count;
		//start 校验楼栋信息
		if (ldlist.size() == dp077ldlist.size()) {
			if (ldlist.size() == 0 && dp077ldlist.size() == 0) {
				jyjg = "0";
			}else {
				//1.把临时表中的数据转成map的形式
				for (int i = 0; i < dp077ldlist.size(); i++) {
					count=0;
					HashMap<String, Object> newmap = new HashMap<String, Object>();
					newmap.put("ywlx", dp077ldlist.get(i).getDpbusitype());
					newmap.put("ldbh", dp077ldlist.get(i).getAccnum1());
					newmap.put("ysxkzh", dp077ldlist.get(i).getAccnum2() );
					newmap.put("skyh", dp077ldlist.get(i).getAgentinstcode());
					newmap.put("yhzhmc", dp077ldlist.get(i).getAccname1());
					newmap.put("yhzh", dp077ldlist.get(i).getAccname2());
					newmap.put("zcs", dp077ldlist.get(i).getPeoplenum().toString());
					for (int j = 0; j < ldlist.size(); j++) {
						HashMap<String, Object> ldMap = ldlist.get(j);
						// 楼栋编号若相等，比较map中具体内容。
						if (ldlist.get(j).get("ldbh").equals(newmap.get("ldbh"))) {
							// 若map中内容不相等，说明列表中的信息进行了修改，校验通过
							if (!ldMap.equals(newmap)) {
								jyjg = "1";
								break;
							}
						}
						// 若楼栋编号无相同的，count加1
						else{
							count++;
						}
					}
					// 若实际数据不在原有文件中，校验通过
					if(count==ldlist.size()){
						jyjg = "1";
						break;
					}
					if("1".equals(jyjg)){
						break;
					}
				}
			}
		}
		//start 校验楼盘信息
		if (lplist.size() == dp077lplist.size() && "0".equals(jyjg)) {
			if (lplist.size() == 0 && dp077lplist.size() == 0) {
				jyjg = "0";
			}else {
				//1.把临时表中的数据转成map的形式
				for (int i = 0; i < dp077lplist.size(); i++) {
					count=0;
					HashMap<String, Object> newmap = new HashMap<String, Object>();
					newmap.put("ywlx", dp077lplist.get(i).getDpbusitype());
					newmap.put("ldbh", dp077lplist.get(i).getAccnum1());
					newmap.put("dyh", dp077lplist.get(i).getUnitaccnum1() );
					newmap.put("szcs", dp077lplist.get(i).getUnitaccnum2());
					newmap.put("mph", dp077lplist.get(i).getUnitaccname1());
					newmap.put("jzmj", dp077lplist.get(i).getUnitprop().toString());
					newmap.put("tnmj", dp077lplist.get(i).getIndiprop().toString());
					newmap.put("jzxsdj", dp077lplist.get(i).getAmt1().toString());
					newmap.put("fwzj", dp077lplist.get(i).getAmt2().toString());
					for (int j = 0; j < lplist.size(); j++) {
						HashMap<String, Object> lpMap = lplist.get(j);
						// 楼栋编号、单元号、所在层数、门牌号若相等，比较map中具体内容。
						if (lplist.get(j).get("ldbh").equals(newmap.get("ldbh")) && lplist.get(j).get("dyh").equals(newmap.get("dyh"))
								&& lplist.get(j).get("szcs").equals(newmap.get("szcs")) && lplist.get(j).get("mph").equals(newmap.get("mph"))) {
							// 若map中内容不相等，说明列表中的信息进行了修改，校验通过
							if (!lpMap.equals(newmap)) {
								jyjg = "1";
								break;
							}
						}
						// 若楼栋编号、单元号、所在层数、门牌号无相同的，count加1
						else{
							count++;
						}
					}
					// 若实际数据不在原有文件中，校验通过
					if(count==lplist.size()){
						jyjg = "1";
						break;
					}
					if("1".equals(jyjg)){
						break;
					}
				}
			}
		}
		if("0".equals(jyjg)){
			throw new TransSingleException("数据没有变化，不能提交!");
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
