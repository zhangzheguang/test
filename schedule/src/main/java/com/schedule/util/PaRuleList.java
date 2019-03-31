package com.schedule.util;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.schedule.pa.entity.Doctor;

//i:1,desc:L班前一天排休
//i:2,desc:L班后一天排休
//i:3,desc:C班之后不能排A或A早
//i:4,desc:D班之后不能排A或A早
//i:5,desc:X班之后不能排A或A早
//i:6,desc:E班之后不能排A或A早
//i:7,desc:E之后不能排C
//i:8,desc:女医生>45岁不能排L班
//i:9,desc:男医生>55岁不能排L班
//i:10,desc:每月每位医生班次均衡
//i:11,desc:A早每个月需要大于等于4个班次
//i:12,desc:X每个月需要大于等于4个班次
//i:13,desc:X+E班每个月需要大于等于4个班次
//i:14,desc:每位医生每月A早+X+E大于等于8个班次
//i:15,desc:住址距离>50公里，不能上A早
//i:17,desc:C班之后只能上C，D，X，E或休
//i:18,desc:D班之后只能上C，D，X，E或休
//i:19,desc:X班之后只能上C，D，X，E或休
//i:20,desc:E班之后只能上C，D，X，E或休
//i:21,desc:C班不能连上>5day
//i:22,desc:D班不能连上>5day
//i:23,desc:X班不能连上>5day
//i:24,desc:E班不能连上>5day
//i:25,desc:B班不能连续>7day
//i:26,desc:C班之后只能上C，D，X，E或休
//i:27,desc:付费诊室+普内科+金牌医生不排A早，每天>=2个X班
//i:28,desc:付费诊室+全科+金牌医生不排A早，每天>=2个X班
//i:29,desc:付费诊室+消化内科+金牌医生不排A早，每天>=2个X班
//i:30,desc:付费诊室+普外科+金牌医生不排A早，每天>=2个X班
//i:31,desc:参与L班排班的科室范围包括：大内科、大外科、儿科、妇科、付费科室
//i:32,desc:医生可以申请不排L班
//i:33,desc:科主任上>8,<10个特殊班次
//i:34,desc:每天避免同一班次安排在同一职场
//i:35,desc:每位医生班次需均衡
//i:36,desc:每日人力均衡，人员充裕才能排休，人员不充足不能排休
//i:37,desc:周六、周日排班人力>=排班建议人力
//i:38,desc:外科当天为L班，前日需要有医生排E班
//i:39,desc:内科当天为L班，前日需有医生排E班
//i:40,desc:每天可排班组12，按照白班5组，晚班7组排
//i:41,desc:每天可排班组11，按照白班4组，晚班7组排
//i:42,desc:人员充裕时，多余人员排晚班
//i:43,desc:人力缺少时，减少早班人员
public class PaRuleList {

	private PaRuleList(){}
    
    private static class PaRuleListHandler {
        private static PaRuleList singleton = new PaRuleList();
    }
    
    public static PaRuleList getInstance(){
        return PaRuleListHandler.singleton;
    }	
	
//	i:1,desc:L班前一天排休
	public void leftHolidayCurL(Map<String,String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("L".equals(schedule)) {
			String pre = DateUtil.addDays(curDateStr, -1);
			String bc = m.get(pre);
			if(!"休".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("1").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
	
//	i:2,desc:L班后一天排
	public void rightHolidayCurL(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("L".equals(schedule)) {
			String next = DateUtil.addDays(curDateStr, 1);
			String bc = m.get(next);
			if(!"休".equals(bc)) {
				
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("2").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
	
//	i:2,desc:其它排左右不能有L班
	public void leftRightHolidayCurL(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		
		if(!"0".equals(schedule) && !"休".equals(schedule)){
			String pre = DateUtil.addDays(curDateStr, -1);
			String next = DateUtil.addDays(curDateStr, 1);
			String preBc = m.get(pre);
			String nextBc = m.get(next);
			if("L".equals(preBc) || "L".equals(nextBc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("2").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
	
//	i:3,desc:C班之后不能排A或A早
	public void rightAOrA1CurC(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("C".equals(schedule)) {
			String next = DateUtil.addDays(curDateStr, 1);
			String bc = m.get(next);
			if("A".equals(bc) || "A早".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("3").get("weight");
				scoreMap.put("score",score);
			}
		}

		if("A".equals(schedule) || "A早".equals(schedule)) {
			String pre = DateUtil.addDays(curDateStr, -1);
			String bc = m.get(pre);
			if("C".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("3").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
//	i:4,desc:D班之后不能排A或A早
	public void rightAOrA1CurD(Map<String,String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("D".equals(schedule)) {
			String next = DateUtil.addDays(curDateStr, 1);
			String bc = m.get(next);
			if("A".equals(bc) || "A早".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("4").get("weight");
				scoreMap.put("score",score);
			}
		}

		if("A".equals(schedule) || "A早".equals(schedule)) {
			String pre = DateUtil.addDays(curDateStr, -1);
			String bc = m.get(pre);
			if("D".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("4").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
//	i:5,desc:X班之后不能排A或A早
	public void rightAOrA1CurX(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("X".equals(schedule)) {
			String next = DateUtil.addDays(curDateStr, 1);
			String bc = m.get(next);
			if("A".equals(bc) || "A早".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("5").get("weight");
				scoreMap.put("score",score);
			}
		}

		if("A".equals(schedule) || "A早".equals(schedule)) {
			String pre = DateUtil.addDays(curDateStr, -1);
			String bc = m.get(pre);
			if("X".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("5").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
//	i:6,desc:E班之后不能排A或A早
	public void rule6(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("E".equals(schedule)) {
			String next = DateUtil.addDays(curDateStr, 1);
			String bc = m.get(next);
			if("A".equals(bc) || "A早".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("6").get("weight");
				scoreMap.put("score",score);
			}
		}

		if("A".equals(schedule) || "A早".equals(schedule)) {
			String pre = DateUtil.addDays(curDateStr, -1);
			String bc = m.get(pre);
			if("E".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("6").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
//	i:7,desc:E之后不能排C
	public void cAfterE(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("E".equals(schedule)) {
			String next = DateUtil.addDays(curDateStr, 1);
			String bc = m.get(next);
			if("C".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("7").get("weight");
				scoreMap.put("score",score);
			}
		}

		if("C".equals(schedule)) {
			String pre = DateUtil.addDays(curDateStr, -1);
			String bc = m.get(pre);
			if("E".equals(bc)) {
				int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("7").get("weight");
				scoreMap.put("score",score);
			}
		}
	}
//	i:8,desc:女医生>45岁不能排L班
	public void female45(Doctor d, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		Map<String,String> m = d.getDates();
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if(d.getAge()>45 && "2".equals(d.getZsex())&&"L".equals(schedule)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("8").get("weight");
			scoreMap.put("score",score);
		}
	}
//	i:9,desc:男医生>55岁不能排L班
	public void man55(Doctor d, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		Map<String,String> m = d.getDates();
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if(d.getAge()>55 && "1".equals(d.getZsex()) && "L".equals(schedule)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("9").get("weight");
			scoreMap.put("score",score);
		}
	}
	
//	i:10,desc:每月每位医生班次均衡
	public boolean rule10() {
		return true;
	}
	
//	i:11,desc:A早每个月需要大于等于4个班次
	public void a1Than4PerMonth(Map<String,String> m , Map<String,Integer> scoreMap) {
		Iterator<String> ite = m.keySet().iterator();
		int count = 4;
		boolean isFirst = true;
		while(ite.hasNext()) {
			if(isFirst) {
				isFirst = false;
				continue;
			}
			
			String key = ite.next();
			String bc = m.get(key);
			if("A早".equals(bc)) {
				count--;
				if(count<=0) {
					break;
				}
			}
		}
		
		if(count>0) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("11").get("weight")*count;
			scoreMap.put("score",score);
		}
	}
//	i:12,desc:X每个月需要大于等于4个班次
	public void xThan4PerMonth(Map<String,String> m , Map<String,Integer> scoreMap) {
		Iterator<String> ite = m.keySet().iterator();
		int count = 4;
		boolean isFirst = true;
		while(ite.hasNext()) {
			if(isFirst) {
				isFirst = false;
				continue;
			}
			
			String key = ite.next();
			String bc = m.get(key);
			if("X".equals(bc)) {
				count--;
				if(count<=0) {
					break;
				}
			}
		}
		
		if(count>0) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("12").get("weight")*count;
			scoreMap.put("score",score);
		}
	}
//	i:13,desc:X+E班每个月需要大于等于4个班次
	public void xeThan4PerMonth(Map<String,String> m , Map<String,Integer> scoreMap) {
		Iterator<String> ite = m.keySet().iterator();
		int count = 4;
		boolean isFirst = true;
		while(ite.hasNext()) {
			if(isFirst) {
				isFirst = false;
				continue;
			}
			
			String key = ite.next();
			String bc = m.get(key);
			if("X".equals(bc) || "E".equals(bc)) {
				count--;
				if(count<=0) {
					break;
				}
			}
		}
		
		if(count>0) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("13").get("weight")*count;
			scoreMap.put("score",score);
		}
	}
	
//	i:14,desc:每位医生每月A早+X+E大于等于8个班次
	public void a1xeThan8PerMonth(Map<String,String> m , Map<String,Integer> scoreMap) {
		Iterator<String> ite = m.keySet().iterator();
		int count = 8;
		boolean isFirst = true;
		while(ite.hasNext()) {
			if(isFirst) {
				isFirst = false;
				continue;
			}
			
			String key = ite.next();
			String bc = m.get(key);
			if("A早".equals(bc) || "X".equals(bc) || "E".equals(bc)) {
				count--;
				if(count<=0) {
					break;
				}
			}
		}
		
		if(count>0) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("14").get("weight")*count;
			scoreMap.put("score",score);
		}
	}
	
//	i:15,desc:住址距离>50公里，不能上A早
	public void distancd50A1(Doctor d, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		Map<String,String> m = d.getDates();
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if(d.getRange()>50 && "A早".equals(schedule)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("15").get("weight");
			scoreMap.put("score",score);
		}
	}
//	i:16,desc:不上A早需要上D班，4A早=4D30
	public boolean rule16() {
		return true;
	}
//	i:17,desc:C班之后只能上C，D，X，E或休
	public void cAfterCDXE(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		String next = DateUtil.addDays(curDateStr, 1);
		if(!"C".equals(schedule)) {
			return ;
		}
		String bc = m.get(next);
		if(StringUtils.isEmpty(bc)) {
			return ;
		}
		
		if(!"C".equals(bc) && !"D".equals(bc) && !"X".equals(bc) && !"E".equals(bc) && !"休".equals(bc)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("17").get("weight");
			scoreMap.put("score",score);
		}
	}
//	i:18,desc:D班之后只能上C，D，X，E或休
	public void dAfterCDXE(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		String next = DateUtil.addDays(curDateStr, 1);
		if(!"D".equals(schedule)) {
			return ;
		}
		String bc = m.get(next);
		if(StringUtils.isEmpty(bc)) {
			return ;
		}
		
		if(!"C".equals(bc) && !"D".equals(bc) && !"X".equals(bc) && !"E".equals(bc) && !"休".equals(bc)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("18").get("weight");
			scoreMap.put("score",score);
		}
	}
//	i:19,desc:X班之后只能上C，D，X，E或休
	public void xAfterCDXE(Map<String,String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		String next = DateUtil.addDays(curDateStr, 1);
		if(!"X".equals(schedule)) {
			return ;
		}
		String bc = m.get(next);
		if(StringUtils.isEmpty(bc)) {
			return ;
		}
		
		if(!"C".equals(bc) && !"D".equals(bc) && !"X".equals(bc) && !"E".equals(bc) && !"休".equals(bc)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("19").get("weight");
			scoreMap.put("score",score);
		}
	}
//	i:20,desc:E班之后只能上C，D，X，E或休
	public void eAfterCDXE(Map<String,String> m, Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		String next = DateUtil.addDays(curDateStr, 1);
		if(!"X".equals(schedule)) {
			return ;
		}
		String bc = m.get(next);
		if(StringUtils.isEmpty(bc)) {
			return ;
		}
		
		if(!"C".equals(bc) && !"D".equals(bc) && !"X".equals(bc) && !"E".equals(bc) && !"休".equals(bc)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("20").get("weight");
			scoreMap.put("score",score);
		}
	}

//	i:32,desc:医生可以申请不排L班14
	public void noScheduleL(Doctor d , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		Map<String,String> m = d.getDates();
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		if("1".equals(d.getOffDutyL()) && "L".equals(schedule)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("32").get("weight");
			scoreMap.put("score",score);
		}
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 21
	 * C班不能连上五天
	 * @param map
	 * @return
	 */
	public void fiveCBan(Map<String, String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=-4;i<4;i++) {
			String dateStr = DateUtil.addDays(curDateStr, i);
			if(i==0&&_schedule!=null) {
				stringBuilder.append(_schedule);
				continue;
			}
			stringBuilder.append(m.get(dateStr));
		}
		
		if(stringBuilder.toString().contains("CCCCC")) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("21").get("weight");
			scoreMap.put("score",score);
		}
		
	}
////	i:22,desc:D班不能连上>5day
	public void fiveDBan(Map<String, String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=-4;i<4;i++) {
			String dateStr = DateUtil.addDays(curDateStr, i);
			if(i==0&&_schedule!=null) {
				stringBuilder.append(_schedule);
				continue;
			}
			stringBuilder.append(m.get(dateStr));
		}
		
		if(stringBuilder.toString().contains("DDDDD")) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("22").get("weight");
			scoreMap.put("score",score);
		}
		
	}
////	i:23,desc:X班不能连上>5day
	public void fiveXBan(Map<String, String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=-4;i<4;i++) {
			String dateStr = DateUtil.addDays(curDateStr, i);
			if(i==0&&_schedule!=null) {
				stringBuilder.append(_schedule);
				continue;
			}
			stringBuilder.append(m.get(dateStr));
		}
		
		if(stringBuilder.toString().contains("XXXXX")) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("23").get("weight");
			scoreMap.put("score",score);
		}
		
	}
////	i:24,desc:E班不能连上>5day
	public void fiveeEBan(Map<String, String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=-4;i<4;i++) {
			String dateStr = DateUtil.addDays(curDateStr, i);
			if(i==0&&_schedule!=null) {
				stringBuilder.append(_schedule);
				continue;
			}
			stringBuilder.append(m.get(dateStr));
		}
		
		if(stringBuilder.toString().contains("EEEEE")) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("24").get("weight");
			scoreMap.put("score",score);
		}
		
	}
////	i:25,desc:B班不能连续>7day
	public void sevenBBan(Map<String, String> m , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=-4;i<4;i++) {
			String dateStr = DateUtil.addDays(curDateStr, i);
			if(i==0&&_schedule!=null) {
				stringBuilder.append(_schedule);
				continue;
			}
			stringBuilder.append(m.get(dateStr));
		}
		if(stringBuilder.toString().contains("BBBBBBB")) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("25").get("weight");
			scoreMap.put("score",score);
		}
	}
	
//	i:26,desc:C班之后只能上C，D，X，E或休
	public void rule26(Doctor d , Map<String,Integer> scoreMap , String curDateStr , String _schedule) {
		Map<String,String> m = d.getDates();
		String schedule = m.get(curDateStr);
		if(_schedule!=null) {
			schedule = _schedule;
		}
		String next = DateUtil.addDays(curDateStr, 1);
		if(!"X".equals(schedule)) {
			return ;
		}
		String bc = m.get(next);
		if(StringUtils.isEmpty(bc)) {
			return ;
		}
		
		if(!"C".equals(bc) && !"D".equals(bc) && !"X".equals(bc) && !"E".equals(bc) && !"休".equals(bc)) {
			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("26").get("weight");
			scoreMap.put("score",score);
		}
	}
	
	/**
	 * 27
	 * 付费诊室+普内科+金牌医生不排A早，每天>=2个X班
	 * @param list    科室集合
	 * @param dateStr 当前日期
	 * @return
	 */
	public boolean pngoldDocInAZao(List<Doctor> Doctorlist, String dateStr) {

		int goldDocNum = 0;
		int XBanNum = 0;
		for (Doctor doctor : Doctorlist) {
			if ("普内科".equals(doctor.getDept()) && doctor.isGold()) {
				goldDocNum++;
				String banci = doctor.getDates().get(dateStr);
				if ("X".equals(banci)) {
					XBanNum++;
				}
				if ("A早".equals(banci)) {
					return false;
				}
			}
		}

		if (goldDocNum == 0) {
			return true;
		}
		if (goldDocNum == 1) {
			if (XBanNum == 1) {
				return true;
			}
		}

		if (goldDocNum == 2) {
			if (XBanNum == 1) {
				return false;//扣十分
			}
			if (XBanNum == 2) {
				return true;
			}
		}

		if (goldDocNum > 2) {
			if (XBanNum >= 2) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 28
	 * 付费诊室+全科+金牌医生不排A早，每天>=2个X班
	 * @param list    科室集合
	 * @param dateStr 当前日期
	 * @return
	 */

	public boolean xhgoldDocInAZao(List<Doctor> Doctorlist, String dateStr) {

		int goldDocNum = 0;
		int XBanNum = 0;
		for (Doctor doctor : Doctorlist) {
			if ("全科".equals(doctor.getDept()) && doctor.isGold()) {
				goldDocNum++;
				String banci = doctor.getDates().get(dateStr);
				if ("X".equals(banci)) {
					XBanNum++;
				}
				if ("A早".equals(banci)) {
					return false;
				}
			}
		}
		if (goldDocNum == 0) {
			return true;
		}
		if (goldDocNum == 1) {
			if (XBanNum == 1) {
				return true;
			}
		}
		if (goldDocNum == 2) {
			if (XBanNum == 1) {
				return false;//
			}
			if (XBanNum == 2) {
				return true;
			}
		}
		if (goldDocNum > 2) {
			if (XBanNum >= 2) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 29
	 * 付费诊室+消化内科+金牌医生不排A早，每天>=2个X班
	 * @param list    科室集合
	 * @param dateStr 当前日期
	 * @return
	 */

	public boolean pwgoldDocInAZao(List<Doctor> Doctorlist, String dateStr) {

		int goldDocNum = 0;
		int XBanNum = 0;
		for (Doctor doctor : Doctorlist) {
			if ("消化内科".equals(doctor.getDept()) && doctor.isGold()) {
				goldDocNum++;
				String banci = doctor.getDates().get(dateStr);
				if ("X".equals(banci)) {
					XBanNum++;
				}
				if ("A早".equals(banci)) {
					return false;
				}
			}
		}

		if (goldDocNum == 0) {
			return true;
		}
		if (goldDocNum == 1) {
			if (XBanNum == 1) {
				return true;
			}
		}

		if (goldDocNum == 2) {
			if (XBanNum == 1) {
				return false;
			}
			if (XBanNum == 2) {
				return true;
			}
		}

		if (goldDocNum > 2) {
			if (XBanNum >= 2) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 30
	 * 付费诊室+普外科+金牌医生不排A早，每天>=2个X班
	 * @param Doctorlist 医生集合
	 * @param dateStr    当前日期
	 * @return
	 */

	public boolean qkgoldDocInAZao(List<Doctor> Doctorlist, String dateStr) {

		int goldDocNum = 0;
		int XBanNum = 0;
		for (Doctor doctor : Doctorlist) {
			if ("普外科".equals(doctor.getDept()) && doctor.isGold()) {
				goldDocNum++;
				String banci = doctor.getDates().get(dateStr);
				if ("X".equals(banci)) {
					XBanNum++;
				}
				if ("A早".equals(banci)) {
					return false;
				}
			}
		}

		if (goldDocNum == 0) {
			return true;
		}
		if (goldDocNum == 1) {
			if (XBanNum == 1) {
				return true;
			}
		}

		if (goldDocNum == 2) {

			if (XBanNum == 1) {
				return false;//扣十分
			}
			if (XBanNum == 2) {
				return true;
			}
		}

		if (goldDocNum > 2) {
			if (XBanNum >= 2) {
				return true;
			}
		}
		return false;
	}
	
	//
	/**
	 * 31
	   * 参与L班排班的科室范围包括：大内科、大外科、儿科、妇科、付费科室
	 * @param Doctorlist
	 * @param dateStr
	 * @return
	 */

	public boolean lBan(List<Doctor> Doctorlist, String dateStr) {
		
		for (Doctor doctor : Doctorlist) {
			String banci = doctor.getDates().get(dateStr);
			String keshi = doctor.getDept();
			if("L".equals(banci)) {
				if("大内科".equals(keshi) || "大外科".equals(keshi) || "儿科".equals(keshi) || "妇科".equals(keshi) ||"付费科室".equals(keshi) ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 33
	 * 科主任上>8,<10个特殊班次
	 * 
	 * @param doctorList 医生集合
	 * 
	 * @return
	 */
	public boolean director(List<Doctor> doctorList) {
		int banCiNum = 0;
		for (Doctor doc : doctorList) {
			if ("1".equals(doc.getIsDirector())) {
				Map<String, String> dates = doc.getDates();
				Set<String> timeSet = dates.keySet();
				for (String cDateStr : timeSet) {
					String banci = dates.get(cDateStr);
					if (!"B".equals(banci) || !"0".equals(banci) || "休".equals(banci)) {
						banCiNum++;
					}
				}
			}
		}
		if (banCiNum >= 8 && banCiNum <= 10) {
			return true;
		}
		return false;
	}
	
	/**
	 * 34
	 * 同一职场医生不能安排在同一班次
	 * 
	 * @param doctorList
	 * @param dateStr
	 * @return
	 */
	public static boolean isAllCity(List<Doctor> doctorList, String dateStr) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();// 职场和班次的set集合
		for (Doctor doctor : doctorList) {

			String banci = doctor.getDates().get(dateStr);
			String zhichang = doctor.getCity();

			Set<String> set = map.get(zhichang);
			if (set == null) {
				Set<String> hashset = new HashSet<String>();
				hashset.add(banci);
				map.put(zhichang, hashset);
			} else {
				set.add(banci);
				map.put(zhichang, set);
			}

		}
		Set<String> keySet = map.keySet();
		for (String zhichang : keySet) {
			Set<String> set = map.get(zhichang);
			if (set.size() > 1) {
				return false;// 一个职场不在一个班次
			} else {
				return true;
			}
		}
		return true;

	}
	
	//---------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * 36 每日人力均衡，人员充裕才能排休，人员不充足不能排休
	 * 
	 * @param yishengList 人力列表
	 * @param banciMap 班次信息
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @return
	 */
	public boolean isRenliBalanced(List<Doctor> yishengList, Map<String, Map<String, Integer>> banciMap,
			String curDate) {
		if (yishengList == null || yishengList.size() <= 0) {
			return false;
		}
		Map<String, Integer> banci = banciMap.get(curDate);
		if (banci == null || banci.size() <= 0) {
			return true;
		}
		// 当前需要多少人
		int reliNeed = 0;
		for (Integer value : banci.values()) {
			reliNeed += value;
		}
		// 有多少人休息
		Integer restNums = 0;
		// 有多少人上班
		Integer workNums = 0;
		for (Doctor doctor : yishengList) {
			Map<String, String> doctorBanci = doctor.getDates();

			String bc = doctorBanci.get(curDate);
			if ("休".equals(bc)) {
				restNums++;
			} else if ("0".equals(bc)) {
				workNums++;
			}
		}
		// 如果人员不充裕
		if (workNums < reliNeed && restNums > 0) {
			return false;
		}
		return true;
	}

	
	/**
	 * 37周六、周日排班人力>=排班建议人力
	 * 
	 * @param yishengList 人力列表
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @param suggestRenli 建议排班人力
	 * @return
	 */
	public boolean isSatSunDay(List<Doctor> yishengList, String curDate, int suggestRenli) {
		// 找出周六、周日的日期
		Date date = DateUtil.convert2Date(curDate, "yyyyMMdd");
		int dayOfWeek = DateUtil.getWeekDay(date);
		// 如果是周六、周日
		if (dayOfWeek == 0 || dayOfWeek == 6) {
			Integer workNums = 0;
			for (Doctor doctor : yishengList) {
				Map<String, String> doctorBanci = doctor.getDates();
				String bc = doctorBanci.get(curDate);
				if (!"0".equals(bc) && !"休".equals(bc)) {
					workNums++;
				}
			}
			if (workNums < suggestRenli) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 38外科当天为L班，前日需要排E班
	 * 
	 * @param yishengList 人力列表
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @return
	 */
	public boolean isOutEbanCurLban(List<Doctor> yishengList, String curDate) {
		for (Doctor doctor : yishengList) {
			if("外科".equals(doctor.getDept())) {
				Map<String,String> doctorBanci = doctor.getDates();
				String curBc = doctorBanci.get(curDate);
				//当前为L班
				if("L".equals(curBc)) {
					String preDate = DateUtil.addDays(curDate, -1);
					String preBc = doctorBanci.get(preDate);
					if(!"E".equals(preBc)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 39 内科当天为L班，前日需排E班
	 * 
	 * @param yishengList 人力列表
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @return
	 */
	public boolean isInEbanCurLban(List<Doctor> yishengList, String curDate) {
		for (Doctor doctor : yishengList) {
			if("内科".equals(doctor.getDept())) {
				Map<String,String> doctorBanci = doctor.getDates();
				String curBc = doctorBanci.get(curDate);
				//当前为L班
				if("L".equals(curBc)) {
					String preDate = DateUtil.addDays(curDate, -1);
					String preBc = doctorBanci.get(preDate);
					if(!"E".equals(preBc)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 40每天可排班组12，按照白班5组，晚班7组排
	 * @param yishengList 人力列表
	 * @param banciMap 班次信息
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @return
	 */
	public boolean isRenLi12Allot(List<Doctor> yishengList, Map<String, Map<String, Integer>> banciMap,
			String curDate) {
		Map<String, Integer> banci = banciMap.get(curDate);
		if (banci == null || banci.size() <= 0) {
			return true;
		}
		Integer dayBanci = 0;
		for (Map.Entry<String, Integer> entry : banci.entrySet()) {
			String bc = entry.getKey();
			if (!"L".equals(bc) && !"B".equals(bc) && !"休".equals(bc)) {
				dayBanci++;
			}
		}

		int zhaoBc = 0;
		int wanBc = 0;
		for (Doctor doctor : yishengList) {
			Map<String, String> doctorBc = doctor.getDates();
			String bc = doctorBc.get(curDate);
			if ("A早".equals(bc) || "A".equals(bc)) {
				zhaoBc++;
			}
			if ("C".equals(bc) || "D".equals(bc) || "X".equals(bc) || "E".equals(bc)) {
				wanBc++;
			}
		}
		if (dayBanci == 12) {
			if (zhaoBc != 5 || wanBc != 7) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 41每天可排班组11，按照白班4组，晚班7组排
	 * @param yishengList 人力列表
	 * @param banciMap 班次信息
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @return
	 */
	public boolean isRenLi11Allot(List<Doctor> yishengList, Map<String, Map<String, Integer>> banciMap,
			String curDate) {
		Map<String, Integer> banci = banciMap.get(curDate);
		if (banci == null || banci.size() <= 0) {
			return true;
		}
		Integer dayBanci = 0;
		for (Map.Entry<String, Integer> entry : banci.entrySet()) {
			String bc = entry.getKey();
			if (!"L".equals(bc) && !"B".equals(bc) && !"休".equals(bc)) {
				dayBanci++;
			}
		}

		int zhaoBc = 0;
		int wanBc = 0;
		for (Doctor doctor : yishengList) {
			Map<String, String> doctorBc = doctor.getDates();
			String bc = doctorBc.get(curDate);
			if ("A早".equals(bc) || "A".equals(bc)) {
				zhaoBc++;
			}
			if ("C".equals(bc) || "D".equals(bc) || "X".equals(bc) || "E".equals(bc)) {
				wanBc++;
			}
		}
		if (dayBanci == 11) {
			if (zhaoBc != 4 || wanBc != 7) {
				return false;
			}
		}
		return true;
	}

	
	/**
	 * 42人员充裕时，多余人员排晚班
	 * @param yishengList 人力列表
	 * @param banciMap 班次信息
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @return
	 */
	public boolean isRenLiSurplus(List<Doctor> yishengList, Map<String, Map<String, Integer>> banciMap,
			String curDate) {
		Map<String, Integer> banci = banciMap.get(curDate);
		if (banci == null || banci.size() <= 0) {
			return true;
		}
		Integer dayBanci = 0;
		for (Map.Entry<String, Integer> entry : banci.entrySet()) {
			String bc = entry.getKey();
			if (!"L".equals(bc) && !"B".equals(bc) && !"休".equals(bc)) {
				dayBanci++;
			}
		}

		int zhaoBc = 0;
		int wanBc = 0;
		int surplus = 0;
		for (Doctor doctor : yishengList) {
			Map<String, String> doctorBc = doctor.getDates();
			String bc = doctorBc.get(curDate);
			if ("A早".equals(bc) || "A".equals(bc)) {
				zhaoBc++;
			}
			if ("C".equals(bc) || "D".equals(bc) || "X".equals(bc) || "E".equals(bc)) {
				wanBc++;
			}
			if ("0".equals(bc)) {
				surplus++;
			}
		}

		// 有剩余人员没有排班
		if (surplus > 0) {
			return false;
		}

		// 有多余人员是否排到了晚班
		if (dayBanci < (zhaoBc + wanBc)) {
			if (zhaoBc == 5) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 43人力缺少时，减少早班人员
	 * @param yishengList 人力列表
	 * @param banciMap 班次信息
	 * @param curDate 当前日期  "yyyyMMdd"
	 * @return
	 */
	public boolean isRenLiDeficiency(List<Doctor> yishengList, Map<String, Map<String, Integer>> banciMap,
			String curDate) {
		Map<String, Integer> banci = banciMap.get(curDate);
		if (banci == null || banci.size() <= 0) {
			return true;
		}
		Integer dayBanci = 0;
		for (Map.Entry<String, Integer> entry : banci.entrySet()) {
			String bc = entry.getKey();
			if (!"L".equals(bc) && !"B".equals(bc) && !"休".equals(bc)) {
				dayBanci++;
			}
		}

		int zhaoBc = 0;
		int wanBc = 0;
		int surplus = 0;
		for (Doctor doctor : yishengList) {
			Map<String, String> doctorBc = doctor.getDates();
			String bc = doctorBc.get(curDate);
			if ("A早".equals(bc) || "A".equals(bc)) {
				zhaoBc++;
			}
			if ("C".equals(bc) || "D".equals(bc) || "X".equals(bc) || "E".equals(bc)) {
				wanBc++;
			}
			if ("0".equals(bc)) {
				surplus++;
			}
		}

		// 有剩余人员没有排班
		if (surplus > 0) {
			return false;
		}

		// 有人员不足是否排到了晚班
		if (dayBanci > (zhaoBc + wanBc)) {
			if (wanBc == 7) {
				return false;
			}
		}
		return true;
	}
}