package com.schedule.pa.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.schedule.util.DateUtil;

public class Doctor {
	/*
	L班前一天和后一天排休
	C、D、X、E之后不能排A或A早
	E之后不能排C
	女医生>45岁，男医生>55岁，不上凌晨L班，其他班次均需要上，
	每月每位医生班次均衡(20-22)
	A早需≥4，X≥4，X+E≥4。每位医生每月A早+X+E≥8
	 */
	
	/*
	 16班次*2/5 = 6.4班修假 = 6个修假班
	 22*30 = 660
	 660/20 = 33人
	 660/22 = 30人

	 16班次*2/5 = 6.4班修假  = 7个修假班
	 23*30 = 690
	 690/20 = 35人
	 690/22 = 32人
	*/
	public static final int WORK_DAY_MIN = 20;
	public static final int WORK_DAY_MAX = 22;
	private int age;
	private int range;

	private int countA1;
	private int countX;
	private int countE;
	private boolean gold;
	private String isDirector; //1：主任
	
	private String dept;
	private String city;
	private String offDutyL; //1可以不上L班，其它值要上
	private int numOfWorkDay = 0;
	private String preSchedule;
	private String lastSchedule;
	private String zid;
	private List<String> assistantList = new ArrayList<String>();
	private String doctor;
	private String director;
	private String zsex;
	private String zage;
	private String zdis;
	private String zcity;
	private String zdept;
	private Map<String,String> dates = new LinkedHashMap<String,String>();
	private Map<String,Integer> numOfWeek = new LinkedHashMap<String,Integer>();
	
	public boolean canL(String dateStr) {
		if("1".equals(offDutyL)) {
			return false;
		}
		
		String preStr = DateUtil.addDays(dateStr, -1);
		String nextStr = DateUtil.addDays(dateStr, 1);
		String preVal = dates.get(preStr);
		String nextVal = dates.get(nextStr);
		if(("0".equals(preVal) || "休".equals(preVal)) && ("0".equals(nextVal) || "休".equals(nextVal) || nextVal == null )) {
			if((age<=55 && "1".equals(zsex)) || (age<=45 && "1".equals(zsex))) {
				return true;
			}
		}
		return false;
	}
	
	public void scheduling(String dateStr , String schedule) {
		if("L".equals(schedule)) {
			String preStr = DateUtil.addDays(dateStr, -1);
			String nextStr = DateUtil.addDays(dateStr, 1);
			dates.put(preStr, "休");
			dates.put(dateStr, "L");
			if(dates.get(nextStr)!=null) {
				dates.put(nextStr, "休");
			}
			preSchedule = "L";
			lastSchedule = "休";
		}else{
			dates.put(dateStr, schedule);
			
			preSchedule = lastSchedule;
			lastSchedule = schedule;
		}
		if(!"休".equals(schedule)) {
			numOfWorkDay++;
		}

		if("A早".equals(schedule)) {
			countA1++;
		}
		if("X".equals(schedule)) {
			countX++;
		}
		if("E".equals(schedule)) {
			countE++;
		}
	}
	
	public String seqOfSchedule(String dateStr) {
		StringBuffer s = new StringBuffer();
		Iterator<String> iter = dates.keySet().iterator();
		while(iter.hasNext()) {
			String d = iter.next();
			if(Integer.valueOf(d)>=Integer.valueOf(dateStr)) {
				break;
			}
			s.append(dates.get(d));
		}
		return s.toString();
	}
	
	public String seqOfScheduleAll() {
		StringBuffer s = new StringBuffer();
		Iterator<String> iter = dates.keySet().iterator();
		while(iter.hasNext()) {
			String d = iter.next();
			s.append(dates.get(d));
		}
		return s.toString();
	}
	
//	C、D、X、E之后不能排A或A早
	public boolean cdxe(String dateStr , String schedule) {
		if(!"A".equals(schedule) && !"A早".equals(schedule)) {
			return true;
		}
		String preStr = DateUtil.addDays(dateStr, -1);
		String preSchedule = dates.get(preStr);
		if("C".equals(preSchedule) || "D".equals(preSchedule) || "X".equals(preSchedule)|| "E".equals(preSchedule)) {
			return false;
		}
		return true;
	}

//	距离大于50公司不能上A早
	public boolean rangeValidate(String schedule) {
		if(!"A早".equals(schedule)) {
			return true;
		}
		if(range>50) {
			return false;
		}
		return true;
	}

//	E之后不能排C
	public boolean cdxec(String dateStr , String schedule) {
		if(!"C".equals(schedule)) {
			return true;
		}
		String preStr = DateUtil.addDays(dateStr, -1);
		String preSchedule = dates.get(preStr);
		if("E".equals(preSchedule)) {
			return false;
		}
		return true;
	}

	public boolean breakQueue(){
		Iterator<String> iterator = dates.keySet().iterator();
		while(iterator.hasNext()) {
			String str = iterator.next();
			if("0".equals(dates.get(str))) {
				return true;
			}
			
		}
		return false;
	}
	
	public boolean canArrange() {
		if(numOfWorkDay >= WORK_DAY_MAX) { //当月已上满22天
			return false;
		}
		return true;
	}
	
	public boolean hasBan(String dateStr) {
		String ban = dates.get(dateStr);
		if(!"0".equals(ban)) {
			return false;
		}
		return true;
	}
	
	public boolean shangwutian(String dateStr) {
		String a5 = dates.get(DateUtil.addDays(dateStr, -5));
		String a4 = dates.get(DateUtil.addDays(dateStr, -4));
		String a3 = dates.get(DateUtil.addDays(dateStr, -3));
		String a2 = dates.get(DateUtil.addDays(dateStr, -2));
		String a1 = dates.get(DateUtil.addDays(dateStr, -1));
		if(!"0".equals(a5)&&!"休".equals(a5)&&
				!"0".equals(a4)&&!"休".equals(a4)&&
				!"0".equals(a3)&&!"休".equals(a3)&&
				!"0".equals(a2)&&!"休".equals(a2)&&
				!"0".equals(a1)&&!"休".equals(a1)) {
			return false;
		}
		return true;
	}
	
//	public boolean canA1Ban() {
//		int count = 0;
//		Iterator<String> iterator = dates.keySet().iterator();
//		while(iterator.hasNext()) {
//			String str = iterator.next();
//			if("A早".equals(dates.get(str))) {
//				count++;
//			}
//		}
//		if(count>3) {
//			return false;
//		}
//		return true;
//	}
//	
//	public boolean canXBan() {
//		int count = 0;
//		Iterator<String> iterator = dates.keySet().iterator();
//		while(iterator.hasNext()) {
//			String str = iterator.next();
//			if("X".equals(dates.get(str))) {
//				count++;
//			}
//		}
//		if(count>3) {
//			return false;
//		}
//		return true;
//	}
//	
//	public boolean canA1XEBan() {
//		int count = 0;
//		Iterator<String> iterator = dates.keySet().iterator();
//		while(iterator.hasNext()) {
//			String str = iterator.next();
//			if("A早".equals(dates.get(str))||"X".equals(dates.get(str))||"E".equals(dates.get(str))) {
//				count++;
//			}
//		}
//		if(count>7) {
//			return false;
//		}
//		return true;
//	}
//	
//	public boolean canXEBan() {
//		int count = 0;
//		Iterator<String> iterator = dates.keySet().iterator();
//		while(iterator.hasNext()) {
//			String str = iterator.next();
//			if("X".equals(dates.get(str))||"E".equals(dates.get(str))) {
//				count++;
//			}
//		}
//		if(count>3) {
//			return false;
//		}
//		return true;
//	}
	
	/*
	 C班之后只能上C，D，X，E或休
	 D班之后只能上C，D，X，E或休
	 X班之后只能上C，D，X，E或休
	 E班之后只能上D，X，E或休
	 */
	public boolean fiveContinuity(String dateStr , String c) {
		if(!"C".equals(lastSchedule) && !"D".equals(lastSchedule) && !"X".equals(lastSchedule) && !"E".equals(lastSchedule)) {
			return true;
		}
		
		if("C".equals(lastSchedule) && "D".equals(lastSchedule) && "X".equals(lastSchedule)) {
			if(!"C".equals(c) && !"D".equals(c) && !"X".equals(c) && !"E".equals(c) && !"E".equals(c) && !"休".equals(c)) {
				return false;
			}
		}else if("E".equals(lastSchedule)){
			if(!"D".equals(c) && !"X".equals(c) && !"E".equals(c) && !"E".equals(c) && !"休".equals(c)) {
				return false;
			}
		}
		return true;
	}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getNumOfWorkDay() {
		return numOfWorkDay;
	}
	public void setNumOfWorkDay(int numOfWorkDay) {
		this.numOfWorkDay = numOfWorkDay;
	}
	public String getPreSchedule() {
		return preSchedule;
	}

	public void setPreSchedule(String preSchedule) {
		this.preSchedule = preSchedule;
	}

	public String getLastSchedule() {
		return lastSchedule;
	}

	public void setLastSchedule(String lastSchedule) {
		this.lastSchedule = lastSchedule;
	}

	public String getZid() {
		return zid;
	}
	public void setZid(String zid) {
		this.zid = zid;
	}
	public String getZsex() {
		return zsex;
	}
	public void setZsex(String zsex) {
		this.zsex = zsex;
	}
	public String getZage() {
		return zage;
	}
	public void setZage(String zage) {
		this.zage = zage;
	}
	public String getZdis() {
		return zdis;
	}
	public void setZdis(String zdis) {
		this.zdis = zdis;
	}
	public String getZcity() {
		return zcity;
	}
	public void setZcity(String zcity) {
		this.zcity = zcity;
	}
	public String getZdept() {
		return zdept;
	}
	public void setZdept(String zdept) {
		this.zdept = zdept;
	}
	public Map<String, String> getDates() {
		return dates;
	}
	public void setDates(Map<String, String> dates) {
		this.dates = dates;
	}

	public String getDoctor() {
		return doctor;
	}

	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public String getOffDutyL() {
		return offDutyL;
	}

	public void setOffDutyL(String offDutyL) {
		this.offDutyL = offDutyL;
	}

	public Map<String, Integer> getNumOfWeek() {
		return numOfWeek;
	}

	public void setNumOfWeek(Map<String, Integer> numOfWeek) {
		this.numOfWeek = numOfWeek;
	}

	public int getCountA1() {
		return countA1;
	}

	public void setCountA1(int countA1) {
		this.countA1 = countA1;
	}

	public int getCountX() {
		return countX;
	}

	public void setCountX(int countX) {
		this.countX = countX;
	}

	public int getCountE() {
		return countE;
	}

	public void setCountE(int countE) {
		this.countE = countE;
	}

	public List<String> getAssistantList() {
		return assistantList;
	}

	public void setAssistantList(List<String> assistantList) {
		this.assistantList = assistantList;
	}

	public boolean isGold() {
		return gold;
	}

	public void setGold(boolean gold) {
		this.gold = gold;
	}

	public String getIsDirector() {
		return isDirector;
	}

	public void setIsDirector(String isDirector) {
		this.isDirector = isDirector;
	}
}
