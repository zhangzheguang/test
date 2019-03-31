package com.schedule.pa;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.schedule.pa.entity.Doctor;
import com.schedule.util.PaRuleList;


public class ScheduleBase {
	
	protected int optimalAlgorithmScore(List<Doctor> doctorList) {
		int score = 0 ;
		for(int i=0 ; i<doctorList.size() ; i++) {
			Doctor d = doctorList.get(i);
			Map<String,String> map = d.getDates();
			Iterator<String> ite = map.keySet().iterator();
			boolean isFirst = true;
			while(ite.hasNext()) {
				String curDateStr = ite.next();
				if(isFirst) {
					isFirst = false;
					continue;
				}
				score = score + penaltyNumber(doctorList , d , curDateStr , null);
			}
		}
		return score;
	}
	
	
	protected int penaltyNumber(List<Doctor> doctorList , Doctor d , String curDateStr , String schedule) {
		Map<String,Integer> scoreMap = new HashMap<String,Integer>();
		scoreMap.put("score", 0);
		
		PaRuleList paRuleList = PaRuleList.getInstance();
//		i:1,desc:L班前一天排休
		paRuleList.leftHolidayCurL(d.getDates() , scoreMap , curDateStr , schedule);
//		i:2,desc:L班后一天排
		paRuleList.rightHolidayCurL(d.getDates() , scoreMap , curDateStr , schedule);
//		i:2,desc:其它排左右不能有L班
		paRuleList.leftRightHolidayCurL(d.getDates() , scoreMap , curDateStr , schedule);
//		i:3,desc:C班之后不能排A或A早
		paRuleList.rightAOrA1CurC(d.getDates() , scoreMap , curDateStr , schedule);
//		i:4,desc:D班之后不能排A或A早
		paRuleList.rightAOrA1CurD(d.getDates() , scoreMap , curDateStr , schedule);
//		i:5,desc:X班之后不能排A或A早
		paRuleList.rightAOrA1CurX(d.getDates() , scoreMap , curDateStr , schedule);
//		i:6,desc:E班之后不能排A或A早
		paRuleList.rightAOrA1CurX(d.getDates() , scoreMap , curDateStr , schedule);
//		i:7,desc:E之后不能排C
		paRuleList.cAfterE(d.getDates() , scoreMap , curDateStr , schedule);
//		i:8,desc:女医生>45岁不能排L班
		paRuleList.female45(d, scoreMap , curDateStr , schedule);
//		i:9,desc:男医生>55岁不能排L班
		paRuleList.man55(d, scoreMap , curDateStr , schedule);
////		i:10,desc:每月每位医生班次均衡
////		i:11,desc:A早每个月需要大于等于4个班次
//		paRuleList.a1Than4PerMonth(d.getDates() , scoreMap);
////		i:12,desc:X每个月需要大于等于4个班次
//		paRuleList.xThan4PerMonth(d.getDates() , scoreMap);
////		i:13,desc:X+E班每个月需要大于等于4个班次
//		paRuleList.xeThan4PerMonth(d.getDates(),  scoreMap);
////		i:14,desc:每位医生每月A早+X+E大于等于8个班次
//		paRuleList.a1xeThan8PerMonth(d.getDates() , scoreMap);
//		i:15,desc:住址距离>50公里，不能上A早
		paRuleList.distancd50A1(d, scoreMap , curDateStr , schedule);
//		i:16,desc:不上A早需要上D班，4A早=4D
//		i:17,desc:C班之后只能上C，D，X，E或休
		paRuleList.cAfterCDXE(d.getDates() , scoreMap , curDateStr , schedule);
//		i:18,desc:D班之后只能上C，D，X，E或休
		paRuleList.dAfterCDXE(d.getDates() , scoreMap , curDateStr , schedule);
//		i:19,desc:X班之后只能上C，D，X，E或休
		paRuleList.xAfterCDXE(d.getDates() , scoreMap , curDateStr , schedule);
//		i:20,desc:E班之后只能上C，D，X，E或休
		paRuleList.eAfterCDXE(d.getDates() , scoreMap , curDateStr , schedule);
		//-----------------------------------------------------------------------------------------------------------------
//////	i:21,desc:C班不能连上>5day
//		paRuleList.fiveCBan(d.getDates() , scoreMap , curDateStr , schedule);
//////	i:22,desc:D班不能连上>5day
//		paRuleList.fiveDBan(d.getDates() , scoreMap , curDateStr , schedule);
//////	i:23,desc:X班不能连上>5day
//		paRuleList.fiveXBan(d.getDates() , scoreMap , curDateStr , schedule);
//////	i:24,desc:E班不能连上>5day
//		paRuleList.fiveeEBan(d.getDates() , scoreMap , curDateStr , schedule);
//////	i:25,desc:B班不能连续>7day
//		paRuleList.sevenBBan(d.getDates() , scoreMap , curDateStr , schedule);
////		i:26,desc:C班之后只能上C，D，X，E或休
//		paRuleList.rule26(d , scoreMap , curDateStr , schedule);
////		i:32,desc:医生可以申请不排L班
//		paRuleList.noScheduleL(d , scoreMap , curDateStr , schedule);
		
//		Map<String , Map<String , Integer>> scheduleMap = Schedule.schedule();
//		/**
//		 * 每日人力均衡，人员充裕才能排休，人员不充足不能排休
//		 */
//		if(!isRenliBalanced(doctorList, scheduleMap, curDateStr)) {
//			//换班不会对此逻辑产生影响
//			int score = scoreMap.get("score")+(Integer)RuleWeight.map.get("36").get("weight");
//			scoreMap.put("score",score);
//		}
//		/**
//		 * 周六、周日排班人力>=排班建议人力
//		 */
////		isSatSunDay(doctorList, curDateStr, int suggestRenli);              ----------------------------------------------------
//		/**
//		 * 每天可排班组12，按照白班5组，晚班7组排
//		 */
//		if(!isRenLi12Allot(doctorList, scheduleMap,curDateStr)){
//			//换班不会对此逻辑产生影响
//		}
//		/**
//		 * 每天可排班组11，按照白班4组，晚班7组排
//		 */
//		if(!isRenLi11Allot(doctorList, scheduleMap,curDateStr)){
//			//换班不会对此逻辑产生影响
//		}
//		/**
//		 * 人员充裕时，多余人员排晚班
//		 */
//		if(!isRenLiSurplus(doctorList, scheduleMap, curDateStr)){
//			//换班不会对此逻辑产生影响
//		}
//		/**
//		 * 人力缺少时，减少早班人员
//		 */
//		if(!isRenLiDeficiency(doctorList, scheduleMap,curDateStr)){
//			//换班不会对此逻辑产生影响
//		}
								
		return scoreMap.get("score");
	
	}
	
	public static void main(String[] args) {
	}
	

}
