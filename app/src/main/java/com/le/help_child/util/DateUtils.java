package com.le.help_child.util;

import android.text.TextUtils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ly on 2017/05/20.
 *
 */
public class DateUtils {

	public static final String DEFAULT_TEMPLATE_DAY = "yyyy-MM-dd";
	public static final String DEFAULT_TEMPLATE = "yyyy-MM-dd HH:mm:ss";
	
	private DateUtils(){}

	/**
	 * 获取当前是一年中的第几周（以周一开始）
	 * @return int
	 */
	public static int getAllWeek(Date date) {
		//时间相关函数接口方法声明
		Calendar cal;
		cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
		cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
		cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 获取当前是周几（以周一开始,周日要特殊处理为0）
	 * @return int
	 */
	public static int getWeekDay(Date date) {
		//时间相关函数接口方法声明
		Calendar cal;
		cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
		cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
		cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK)-1;
	}





	/**
	 * * /
	 /**
	 * 使用用户格式提取字符串日期
	 *
	 * @param strDate 日期字符串
	 * @param pattern 日期格式
	 * @return
	 */

	public static Date parse(String strDate, String pattern) {

		if (TextUtils.isEmpty(strDate)) {
			return null;
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			return df.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用用户格式格式化日期
	 *
	 * @param date    日期
	 * @param pattern 日期格式
	 * @return
	 */

	public static String format(Date date, String pattern) {
		String returnValue = "";
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			returnValue = df.format(date);
		}
		return returnValue;
	}

	public static String formatFormLong(Long dateLong) {
		String returnValue = "";
		if (dateLong != null) {
			Date date = new Date(dateLong);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			returnValue = df.format(date);
		}
		return returnValue;
	}
	
}
