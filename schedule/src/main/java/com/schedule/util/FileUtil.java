package com.schedule.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.schedule.pa.entity.Doctor;

public class FileUtil {

	public static List<Doctor> doctorList() {
		Sheet sheet = null;
        Row row = null;
        Workbook wb =null;
        String filePath = "D:\\data.xlsx";
        wb = readExcel(filePath);
        Map<String,Doctor> map = new HashMap<String,Doctor>();
        List<Doctor> ll = new ArrayList<Doctor>();
        if(wb != null){
            //用来存放表中数据
            //获取第一个sheet
            sheet = wb.getSheet("aa");
            int colnum = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i<colnum; i++) {
                row = sheet.getRow(i);
                if(row !=null){
                        String doctor = (String) getCellFormatValue(row.getCell(3));
                        String assistant = (String) getCellFormatValue(row.getCell(2));
                        if(map.containsKey(doctor)) {
                        	Doctor a = map.get(doctor);
                        	a.getAssistantList().add(assistant);
                        }else{
	                        String dept = (String) getCellFormatValue(row.getCell(0));
	                        String city = (String) getCellFormatValue(row.getCell(1));
	                        
	                        String director = (String) getCellFormatValue(row.getCell(4));
	                        String sex = (String) getCellFormatValue(row.getCell(5));
	                        String age = (String) getCellFormatValue(row.getCell(6));
	                        String range = (String) getCellFormatValue(row.getCell(7));
	                        
	                        Doctor a = new Doctor();
	                        a.setDept(dept);
	                        a.setCity(city);
	                        a.setDoctor(doctor);
	                        a.setDirector(director);
	                        a.setZsex(sex.replace(".0", ""));
	                        a.setAge(Integer.valueOf(age.replace(".0", "")));
	                        a.setRange(Integer.valueOf(range.replace(".0", "")));
	                        a.getAssistantList().add(assistant);
	                        a.setDates(dateBan());
	                        a.setNumOfWeek(numOfWeek());
	                        map.put(doctor,a);
	                        ll.add(a);
                        }
                }else{
                    break;
                }
            }
        }
        return ll;
	}
		
	public static Map<String , String> dateBan() {
		Map<String , String> initData = new LinkedHashMap<String , String>();
		 Calendar cal = Calendar.getInstance();
		 cal.set(Calendar.MONTH , 4);
		 cal.set(Calendar.DATE , 1);
		 for(int i=-1; i<30 ; i++) {
			 if(i==-1) {
				 cal.add(Calendar.DATE, -1);
			 }else{
				 cal.add(Calendar.DATE, 1);
			 }
			 String dateStr = DateUtil.convert2String(cal.getTime(),"yyyyMMdd");
			 initData.put(dateStr, "0");
		 }
		 return initData;
	}
	
	
	public static Map<String , Integer> numOfWeek() {
		Map<String , Integer> numOfWeekMap = new LinkedHashMap<String , Integer>();
		 Calendar cal = Calendar.getInstance();
		 cal.set(Calendar.MONTH , 4);
		 cal.set(Calendar.DATE , 1);
		 for(int i=-1; i<30 ; i++) {
			 if(i==-1) {
				 cal.add(Calendar.DATE, -1);
			 }else{
				 cal.add(Calendar.DATE, 1);
			 }
			 String dateStr = DateUtil.convert2String(cal.getTime(),"yyyyMMdd");
			 numOfWeekMap.put(dateStr,cal.get(Calendar.DAY_OF_WEEK));
			 
		 }
		 return numOfWeekMap;
	}
	
	//读取excel
	public static Workbook readExcel(String filePath){
	    Workbook wb = null;
	    if(filePath==null){
	        return null;
	    }
	    String extString = filePath.substring(filePath.lastIndexOf("."));
	    InputStream is = null;
	    try {
	        is = new FileInputStream(filePath);
	        if(".xls".equals(extString)){
	            return wb = new HSSFWorkbook(is);
	        }else if(".xlsx".equals(extString)){
	            return wb = new XSSFWorkbook(is);
	        }else{
	            return wb = null;
	        }
	        
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return wb;
	}
	public static Object getCellFormatValue(Cell cell){
	    Object cellValue = null;
	    if(cell!=null){
	        //判断cell类型
	        switch(cell.getCellType()){
	        case Cell.CELL_TYPE_NUMERIC:{
	            cellValue = String.valueOf(cell.getNumericCellValue());
	            break;
	        }
	        case Cell.CELL_TYPE_FORMULA:{
	            //判断cell是否为日期格式
	            if(org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)){
	                //转换为日期格式YYYY-mm-dd
	                cellValue = cell.getDateCellValue();
	            }else{
	                //数字
	                cellValue = String.valueOf(cell.getNumericCellValue());
	            }
	            break;
	        }
	        case Cell.CELL_TYPE_STRING:{
	            cellValue = cell.getRichStringCellValue().getString();
	            break;
	        }
	        default:
	            cellValue = "";
	        }
	    }else{
	        cellValue = "";
	    }
	    return cellValue;
	}
	
	public static void method3(String str , String content) {
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(str, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.write(content.getBytes());
			randomFile.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
}
