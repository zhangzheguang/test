package com.schedule.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RuleWeight {

	public static Map<String , Map<String , Object>> map = new LinkedHashMap<String , Map<String , Object>>();
	
	static {
		
		String[] ruleArray = {"L班前一天排休","L班后一天排休","C班之后不能排A或A早","D班之后不能排A或A早","X班之后不能排A或A早","E班之后不能排A或A早","E之后不能排C","女医生>45岁不能排L班","男医生>55岁不能排L班","每月每位医生班次均衡","A早每个月需要大于等于4个班次","X每个月需要大于等于4个班次","X+E班每个月需要大于等于4个班次","每位医生每月A早+X+E大于等于8个班次","住址距离>50公里，不能上A早","不上A早需要上D班，4A早=4D","C班之后只能上C，D，X，E或休","D班之后只能上C，D，X，E或休","X班之后只能上C，D，X，E或休","E班之后只能上C，D，X，E或休","C班不能连上>5day","D班不能连上>5day","X班不能连上>5day","E班不能连上>5day","B班不能连续>7day","C班之后只能上C，D，X，E或休","付费诊室+普内科+金牌医生不排A早，每天>=2个X班","付费诊室+全科+金牌医生不排A早，每天>=2个X班","付费诊室+消化内科+金牌医生不排A早，每天>=2个X班","付费诊室+普外科+金牌医生不排A早，每天>=2个X班","参与L班排班的科室范围包括：大内科、大外科、儿科、妇科、付费科室","医生可以申请不排L班","科主任上>8,<10个特殊班次","每天避免同一班次安排在同一职场","每位医生班次需均衡","每日人力均衡，人员充裕才能排休，人员不充足不能排休","周六、周日排班人力>=排班建议人力","外科当天为L班，前日需要有医生排E班","内科当天为L班，前日需有医生排E班","每天可排班组12，按照白班5组，晚班7组排","每天可排班组11，按照白班4组，晚班7组排","人员充裕时，多余人员排晚班","人力缺少时，减少早班人员"};
		int ii = 500;
		for(int i=0 ; i<ruleArray.length; i++) {
			int ww = ii - i*10;
			Map<String , Object> m = new HashMap<String , Object>();

//			System.out.println("i:"+(i+1)+",desc:"+ruleArray[i]);
			m.put("desc", ruleArray[i]);
			if(i==0) {
				m.put("weight", 2000);
			}else if(i==1) {
				m.put("weight", 1000);
			}else{
				m.put("weight", ww);
			}
			map.put(String.valueOf(i+1), m);
		}
	}
	
	public static void main(String[] args) {
	}
	
//	i:1,desc:L班前一天排休
//	i:2,desc:L班后一天排休
//	i:3,desc:C班之后不能排A或A早
//	i:4,desc:D班之后不能排A或A早
//	i:5,desc:X班之后不能排A或A早
//	i:6,desc:E班之后不能排A或A早
//	i:7,desc:E之后不能排C
//	i:8,desc:女医生>45岁不能排L班
//	i:9,desc:男医生>55岁不能排L班
//	i:10,desc:每月每位医生班次均衡
//	i:11,desc:A早每个月需要大于等于4个班次
//	i:12,desc:X每个月需要大于等于4个班次
//	i:13,desc:X+E班每个月需要大于等于4个班次
//	i:14,desc:每位医生每月A早+X+E大于等于8个班次
//	i:15,desc:住址距离>50公里，不能上A早
//	i:16,desc:不上A早需要上D班，4A早=4D
//	i:17,desc:C班之后只能上C，D，X，E或休
//	i:18,desc:D班之后只能上C，D，X，E或休
//	i:19,desc:X班之后只能上C，D，X，E或休
//	i:20,desc:E班之后只能上C，D，X，E或休
//	i:21,desc:C班不能连上>5day
//	i:22,desc:D班不能连上>5day
//	i:23,desc:X班不能连上>5day
//	i:24,desc:E班不能连上>5day
//	i:25,desc:B班不能连续>7day
//	i:26,desc:C班之后只能上C，D，X，E或休
//	i:27,desc:付费诊室+普内科+金牌医生不排A早，每天>=2个X班
//	i:28,desc:付费诊室+全科+金牌医生不排A早，每天>=2个X班
//	i:29,desc:付费诊室+消化内科+金牌医生不排A早，每天>=2个X班
//	i:30,desc:付费诊室+普外科+金牌医生不排A早，每天>=2个X班
//	i:31,desc:参与L班排班的科室范围包括：大内科、大外科、儿科、妇科、付费科室
//	i:32,desc:医生可以申请不排L班
//	i:33,desc:科主任上>8,<10个特殊班次
//	i:34,desc:每天避免同一班次安排在同一职场
//	i:35,desc:每位医生班次需均衡
//	i:36,desc:每日人力均衡，人员充裕才能排休，人员不充足不能排休
//	i:37,desc:周六、周日排班人力>=排班建议人力
//	i:38,desc:外科当天为L班，前日需要有医生排E班
//	i:39,desc:内科当天为L班，前日需有医生排E班
//	i:40,desc:每天可排班组12，按照白班5组，晚班7组排
//	i:41,desc:每天可排班组11，按照白班4组，晚班7组排
//	i:42,desc:人员充裕时，多余人员排晚班
//	i:43,desc:人力缺少时，减少早班人员

}