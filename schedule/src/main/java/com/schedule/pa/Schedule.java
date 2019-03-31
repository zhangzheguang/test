package com.schedule.pa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.schedule.pa.entity.Doctor;
import com.schedule.util.DateUtil;
import com.schedule.util.FileUtil;

public class Schedule extends ScheduleBase{

	/*
	L班前一天和后一天排休
	C、D、X、E之后不能排A或A早
	E之后不能排C
	女医生>45岁，男医生>55岁，不上凌晨L班，其他班次均需要上，
	每月每位医生班次均衡(20-22)
	A早需≥4，X≥4，X+E≥4。每位医生每月A早+X+E≥8
	 */
	private final static String[] TITLE = {"科室","职场","助手","医生","主任"};
	private final static String L = "L";

	private final static int TOTAL_CHANGE_NUM = 20000;
	
	private static BlockingDeque<Doctor> queue = new LinkedBlockingDeque<Doctor>();
	 
	 public static void main(String[] args) {
		 Schedule schedule = new Schedule();
		 try {
			schedule.processSchedule();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 public void processSchedule() throws Exception{
		 List<Doctor> doctorList = FileUtil.doctorList();
		 Map<String , Map<String , Integer>> scheduleMap = schedule();
		 
		 int numOfAllSchedule = 0;
		 int days = doctorList.get(0).getDates().size()-1;
		 Iterator<String> ite = scheduleMap.keySet().iterator();
		 while(ite.hasNext()) {
			 String dateStr = ite.next();
			 Map<String,Integer> m = scheduleMap.get(dateStr);
			 
			 Iterator<String> item = m.keySet().iterator();
			 while(item.hasNext()) {
				 String scheduleM = item.next();
				 numOfAllSchedule += m.get(scheduleM);
			 }
		 }
		 
		 if(numOfAllSchedule/days>doctorList.size()) {
			 System.out.println("numOfAllSchedule:"+numOfAllSchedule+",days:"+days+",doctorList.size()"+doctorList.size());
			 throw new Exception("人员不足，不能排班");
		 }
			 
		 for(Doctor d : doctorList) {
			 queue.push(d);
		 }
		 
		 while(queue.size()>0) {
			 Iterator<String> baiciIte = scheduleMap.keySet().iterator();
			 Doctor d = queue.poll();
			 boolean isFirst2 = true;
			 while(baiciIte.hasNext()) {
				 String key = baiciIte.next();
				 if(isFirst2) {
					 isFirst2 = false;
					 continue;
				 }
				 Map<String,Integer> m = (Map<String,Integer>) scheduleMap.get(key);
				 Iterator<String> keyList = m.keySet().iterator();
				 boolean breakFlag = false;
				 while(keyList.hasNext()) {
					 String bc = keyList.next();
					 if(!d.hasBan(key)) {
						 break;
					 }
					 
					 Integer renli = m.get(bc);
					 if(renli>0) {
						 breakFlag = true;
						 m.put(bc , renli-1);
						 d.scheduling(key, bc);
						 break;
					 }
					 
				 }
				 
				 if(breakFlag) {
					 break;
				 }
			 }
			 if(!d.breakQueue()) {
				 break;
			 }else{
				 queue.addLast(d);
			 }
		 }

//		 for(int i=0 ; i<doctorList.size();i++) {
//			 System.out.println(i+":"+doctorList.get(i).getDoctor());
//		 }
		 
		 printPaibanInfo(doctorList);
		 System.out.println("总数："+TOTAL_CHANGE_NUM);
		 System.out.println("优化前："+optimalAlgorithmScore(doctorList));
//		 System.out.println("------------------------------------------------------------");
//		 printPaibanInfo2(scheduleMap);
//		 System.out.println("------------------------------------------------------------");
		 changeShifts(doctorList);
//		 System.out.println("------------------------------------------------------------");
//		 printPaibanInfo(doctorList);
//		 System.out.println("------------------------------------------------------------");
		 printPaibanInfo(doctorList);
		 System.out.println("优化后："+optimalAlgorithmScore(doctorList));
	 }
	 
	 
	 public void changeShifts(List<Doctor> doctorList){
		 int scheduleDayNum = doctorList.get(0).getDates().size()-1;
		 int numPerDay = TOTAL_CHANGE_NUM%scheduleDayNum==0?TOTAL_CHANGE_NUM/scheduleDayNum:TOTAL_CHANGE_NUM/scheduleDayNum+1;
		 Map<String ,String> scheduleMap = doctorList.get(0).getDates();
		 Iterator<String> scheduleIterator = scheduleMap.keySet().iterator();
		 boolean isFirst = true;
		 while(scheduleIterator.hasNext()) {
			 String dateStr = scheduleIterator.next();
			 if(isFirst) {
				 isFirst = false;
				 continue;
			 }
			 
			 List<String> list = changeShifts(numPerDay, doctorList.size());
			 for(int i=0 ; i<list.size() ; i++) {
				 String changeNumArray = list.get(i);
				 String[] changeNums = changeNumArray.split(",");

				 String schedule1 = doctorList.get(Integer.valueOf(changeNums[0])).getDates().get(dateStr);
				 String schedule2 = doctorList.get(Integer.valueOf(changeNums[1])).getDates().get(dateStr);
				
				 if("揭海英".equals(doctorList.get(Integer.valueOf(changeNums[0])).getDoctor()) && "20190529".equals(dateStr) ) {
//					 System.out.println("schedule1:"+schedule1);
//					 System.out.println("schedule2:"+schedule2);
//					 System.out.println("");
				 }

				 if(schedule1.equals(schedule2)) {
					 continue;
				 }
				 
//				 System.out.println("changeNumArray:"+changeNumArray+",dateStr"+dateStr);
				 
				int oriScore1 = penaltyNumber(doctorList,doctorList.get(Integer.valueOf(changeNums[0])) , dateStr , null);
				int oriScore2 = penaltyNumber(doctorList,doctorList.get(Integer.valueOf(changeNums[1])) , dateStr , null);
				 
				int newScore1 = penaltyNumber(doctorList,doctorList.get(Integer.valueOf(changeNums[0])) , dateStr , schedule2);
				int newScore2 = penaltyNumber(doctorList,doctorList.get(Integer.valueOf(changeNums[1])) , dateStr , schedule1);

				 if((newScore1+newScore2)<(oriScore1+oriScore2)) {
//					FileUtil.method3("d:\\content.txt" , dateStr+","+doctorList.get(Integer.valueOf(changeNums[0])).getDoctor()
//							+","+schedule1+","+doctorList.get(Integer.valueOf(changeNums[1])).getDoctor()+","+schedule2+"\r\n");
					 doctorList.get(Integer.valueOf(changeNums[0])).getDates().put(dateStr,schedule2);
					 doctorList.get(Integer.valueOf(changeNums[1])).getDates().put(dateStr,schedule1);
				 }
			 }
		 }
		 
	 }
	 
	 public static List<String> changeShifts(int numPerDay , int doctorNum){
		 List<String> list = new ArrayList<String>();
		 while(numPerDay>0) {
			 while(true) {
				 Random ran = new Random();
				 int change1 = ran.nextInt(doctorNum);
				 int change2 = ran.nextInt(doctorNum);
				 if(change1 != change2) {
					 list.add(change1+","+change2);
					 break;
				 }
			 }
			 numPerDay--;
		 }
		 return list;
	 }
	 
	 private static boolean isFullA1(List<Doctor> doctorList , String doctor) {
		 for(Doctor d : doctorList) {
			 if(d.getDoctor().equals(doctor)) {
				 continue;
			 }
			 if(d.getCountA1()<4) {
				 return false;
			 }
		 }
		 return true;
	 }
	 
	 private static boolean isFullX(List<Doctor> doctorList , String doctor) {
		 for(Doctor d : doctorList) {
			 if(d.getDoctor().equals(doctor)) {
				 continue;
			 }
			 if(d.getCountX()<4) {
				 return false;
			 }
		 }
		 return true;
	 }
	 
	 private static boolean isFullA1XE(List<Doctor> doctorList , String doctor) {
		 for(Doctor d : doctorList) {
			 if(d.getDoctor().equals(doctor)) {
				 continue;
			 }
			 if((d.getCountX()+d.getCountA1()+d.getCountE())<8) {
				 return false;
			 }
		 }
		 return true;
	 }
	 
	 
	private static void printPaibanInfo(List<Doctor> doctorList) {
		
		for(String title : TITLE) {
			System.out.print(title);
			System.out.print("\t");
    	}
    	
    	Doctor d = doctorList.get(0);
    	Iterator ite = d.getDates().keySet().iterator();
    	while(ite.hasNext()) {
    		String dateStr = (String)ite.next();
			System.out.print(dateStr);
			System.out.print("\t");
    	}

		System.out.println("");
		
    	for(Doctor dd : doctorList) {
    		System.out.print("儿科");
    		System.out.print("\t");
    		System.out.print(dd.getCity());
    		System.out.print("\t");
    		System.out.print("");
    		System.out.print("\t");
    		System.out.print(dd.getDoctor());
    		System.out.print("\t");
    		System.out.print("");
    		System.out.print("\t");
    		Map map = dd.getDates();
    		Iterator<String> ii = map.keySet().iterator();
    		while(ii.hasNext()) {
    			String dateStr = ii.next();
    			String bc = (String) map.get(dateStr);
        		System.out.print(bc);
        		System.out.print("\t");
    		}
    		System.out.println("");
    	}
	}
	
	public static void printPaibanInfo2(Map<String , Map<String , Integer>> scheduleMap) {
    	Iterator itee = scheduleMap.keySet().iterator();
    	int i = 0;
    	while(itee.hasNext()) {
    		String key =(String) itee.next();
    		Map<String,Integer> m = scheduleMap.get(key);
    		if(m.get("L")>0 || m.get("A早")>0 || m.get("A")>0 || m.get("D")>0 || m.get("X")>0 ||m.get("E")>0) {
    			i++;
	    		System.out.println(key+":"+scheduleMap.get(key));
    		}
    	}
    	if(i==0) {
    		System.out.println("班次已全部分配");
    	}
	}
	
	public static Map<String , Map<String , Integer>> schedule() {
		 Map<String , Map<String , Integer>> initData = new LinkedHashMap<String , Map<String , Integer>>();
		 Calendar cal = Calendar.getInstance();
		 cal.set(Calendar.MONTH , 4);
		 cal.set(Calendar.DATE , 1);
		 for(int i=-1; i<30 ; i++) {
			 Map<String , Integer> map = new HashMap<String , Integer>();
			 if(i==-1) {
				 cal.add(Calendar.DATE, -1);
				 map.put("L",0);
				 map.put("A早",0);
				 map.put("A",0);
//				 map.put("C",0);
				 map.put("D",0);
				 map.put("X",0);
				 map.put("E",0);
				 map.put("休",0);
			 }else{
				 cal.add(Calendar.DATE, 1);
				 map.put("L",1);
				 map.put("A早",3);
				 map.put("A",3);
//				 map.put("C",0);
				 map.put("D",4);
				 map.put("X",3);
				 map.put("E",1);
				 if(i%2==0) {
					 map.put("休",7);
				 }else{
					 map.put("休",6);
				 }
			 }
			 String dateStr = DateUtil.convert2String(cal.getTime(),"yyyyMMdd");
			 initData.put(dateStr, map);
		 }
		 return initData;
	}
}
