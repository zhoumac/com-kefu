package com.chatopera.cc.app.aop.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintLog {
    /**
     * 打印调用方法
     * i 调用链层级
     */
    public static void printPre(int i){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[i];
        String className = stackTraceElement.getClassName();//调用的类名
        String methodName = stackTraceElement.getMethodName();//调用的方法名
        int line = stackTraceElement.getLineNumber();//调用的行数
        String fileName = stackTraceElement.getFileName();

        log.info("methodPre:"  +"\n"+ className+"."+methodName+"\033[32;4m("+fileName+":"+line+")"+"\033[0m");

    }
}
