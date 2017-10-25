package com.eumji.zblog.interceptor;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.eumji.zblog.mapper.LogMapper;
import com.eumji.zblog.util.IPAddressUtil;
import com.eumji.zblog.vo.LogInfo;
@Component
public class URLInterceptor implements HandlerInterceptor {

	@Autowired
	private LogMapper logMapper;
	private static final Logger logger = LoggerFactory.getLogger(URLInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
			throws Exception {
		String ip = IPAddressUtil.getClientIpAddress(httpServletRequest);
		String url = httpServletRequest.getRequestURL().toString();
		logger.info("wfwf>>>preHandle:" + ip);
		logger.info("wfwf>>>preHandle:" + url);
		LogInfo log = new LogInfo();
		log.setUserId("guest");
		log.setIp(ip);
		log.setMethod(httpServletRequest.getMethod());
		log.setUrl(url);
		log.setArgs("guest");
		log.setClassMethod("guest");
		log.setException("guest");
		log.setOperateTime(new Date());
		logger.info(log.toString());
		logMapper.save(log);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {
		String ip = IPAddressUtil.getClientIpAddress(httpServletRequest);
		String url = httpServletRequest.getRequestURL().toString();
		logger.info("wfwf>>>postHandle:" + ip);
		logger.info("wfwf>>>postHandle:" + url);

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
		String ip = IPAddressUtil.getClientIpAddress(httpServletRequest);
		String url = httpServletRequest.getRequestURL().toString();
		logger.info("wfwf>>>afterCompletion:" + ip);
		logger.info("wfwf>>>afterCompletion:" + url);
	}
}
