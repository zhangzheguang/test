package com.schedule;

import java.util.Calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

//自动配置
@EnableAutoConfiguration
@ComponentScan("com.schedule.pa")
public class App extends SpringBootServletInitializer{
//	 @Override
//	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//	        return application.sources(App.class);
//	    }

	 public static void main(String[] args) {
		//启动springboot项目 启动tommcat 加载springmvc 注解类
		SpringApplication.run(App.class, args);
		
		
		Calendar cal = Calendar.getInstance();
		System.out.println(-1%7);
		
		
		
		
		
	}
}
