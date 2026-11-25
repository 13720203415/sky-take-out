package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有异常
    @ExceptionHandler(Exception.class)
    public Result<?> exception(Exception e) {
        // 打印异常信息，方便后端排查
        e.printStackTrace();
        log.error("系统异常", e);
        // 返回统一失败JSON
        return Result.error(e.getMessage());
    }

    //参数校验异常
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e){
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败：{}", msg, e);
        return Result.error(msg);
    }

    // JSON参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> validException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验失败：{}", msg);
        return Result.error(msg);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<?> validException(SQLIntegrityConstraintViolationException e) {
        String msg = e.getMessage();
        if(msg.contains("Duplicate key")){
            String [] split = msg.split(" ");
            String userName = split[2];
            return Result.error(userName + "已存在");
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
