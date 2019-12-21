
package com.chatopera.cc.app.aop;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
@Profile("dev1")
public class CacheAop {

	public static Set<String> useCache = Sets.newConcurrentHashSet();

	/**
	 * @author 周希来
	 * @since 2019年3月27日
	 */
	public CacheAop() {

		log.info("初始化CacheAop方法追踪器成功！！！");

	}
	/**
	 * 拦截+任意返回值，任意方法参数，当前包和子包的方法
	 */
	@Pointcut("execution(* com.chatopera.cc.app.cache.redis..*(..))")
	public void pointUrl() {

	}
	@Before("pointUrl()")
	public void toPrintLog() {
	}
	@Around("pointUrl()")
	public Object  around(ProceedingJoinPoint pjd) throws Throwable {
		//获取类名
		String className =pjd.getTarget().getClass().getName();
		String simpleName = pjd.getTarget().getClass().getSimpleName();
		//获取方法名称
		String methodName = pjd.getSignature().getName();
		//保存用过的controller

		CacheAop.useCache.add(className);

	    // 定义返回参数
		Object result = null;
		long startTime = System.currentTimeMillis();
		Object[] args = pjd.getArgs();
	    MethodSignature methodSignature = (MethodSignature) pjd.getSignature();
	    //2.最关键的一步:通过这获取到方法的所有参数名称的字符串数组
	    String[] parameterNames = methodSignature.getParameterNames();
		//序列化时过滤掉request和response
		Map<String,String> map = Maps.newHashMap();
		Stream.iterate(0, i -> i + 1).limit(parameterNames.length).forEach(i -> {
			if(parameterNames.length>0) {
				String parameterName = parameterNames[i].toString();
					String arg = args.length > i ? args[i]+"" : "";
					map.put(parameterName, arg.toString());

			}
		});

	    // 执行目标方法
	    result = pjd.proceed();
	    int line = ClassTool.getLine(className, methodName)-1;
	    //log.info("\n");
		if(!ClassTool.resultList.contains(methodName)){
	    //打印调用方法位置
			log.info("methodUrl:"  +"\n"+ className+"."+methodName+"\033[32;4m("+simpleName+".java:"+line+")"+"\033[0m");
			log.info(" method:"+"\033[32;4m"+methodName+"\033[0m");
			log.info(" params:" + JSON.toJSONString(map,SerializerFeature.DisableCircularReferenceDetect));
			//log.info(" data:"  +"\n"+ JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
			// 获取执行完的时间
			log.info(" total times:" + (System.currentTimeMillis() - startTime)+"\n");
			printPre();
		}
	    return result;
	}

	/**
	 * 打印调用方法
	 */
	private void printPre(){
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
		String className = stackTraceElement.getClassName();//调用的类名
		String methodName = stackTraceElement.getMethodName();//调用的方法名
		int line = stackTraceElement.getLineNumber();//调用的行数
		String fileName = stackTraceElement.getFileName();

		log.info("methodPre:"  +"\n"+ className+"."+methodName+"\033[32;4m("+fileName+".java:"+line+")"+"\033[0m");

	}

	 private static class ClassTool {

		public static ClassPool pool = ClassPool.getDefault();

		public static String[] strs = {"getStarttime","setStarttime","checkLoginValid","ping"};

		public static List<String> resultList = List.of(strs);

		public static int getLine(String classPage,String method) {


			List<String> resultList = List.of(strs);
			CtClass cc;
			try {
				cc = pool.getOrNull(classPage);

				if(cc!=null&&!resultList.contains(method)) {
					//判断该类是否有实现的方法
					Set<CtMethod> ctMethods = Sets.newHashSet(cc.getDeclaredMethods());
					Set<String> methodStr = ctMethods.stream().map(CtMethod::getName).collect(Collectors.toSet());
					if (methodStr.contains(method)){
						CtMethod methodX = cc.getDeclaredMethod(method);
						return methodX.getMethodInfo().getLineNumber(0);
					}else{
						//从父类找到该方法
						CtClass superclass = cc.getSuperclass();

						//CtMethod methodX = cc.getMethod(method,null);
						return 1;

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		     return 1;
		}
	}

}
