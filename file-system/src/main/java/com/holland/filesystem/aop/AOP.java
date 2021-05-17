package com.holland.filesystem.aop;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Component
@Aspect
public class AOP {

    @Resource
    private HttpServletRequest request;

    @Pointcut(value = "((@within(org.springframework.stereotype.Controller))" + ")")
    private void pointCut() {
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void doAfter(JoinPoint joinPoint, Object result) {
        System.out.println("doAfter");
 //        Response response = (Response) result;

        //请求的参数     /*序列化时过滤掉request和response*/     /*过滤掉文件*/
        final Object[] args = joinPoint.getArgs();
        final String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        final String param = params.apply(parameterNames, args);
        final String userId = request.getHeader("userid");
//        final String res = JSON.toJSONString(response);

//        LogForLogin annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(LogForLogin.class);
//        if (annotation != null) {
//            final Map<String, Object> paramMap = this.paramMap.apply(parameterNames, args);
//            String description = annotation.description();
//            String[] params = annotation.params();
//            Object[] objects = Arrays.stream(params).map(paramMap::get).toArray(Object[]::new);
//            String s = new Formatter().format(description, objects).toString();
//            System.out.println(s);
//        }
//
//        final Log log = new Log()
//                .setIp(IPUtils.getIpAddr(request))
//                .setOperateApi(request.getRequestURI())
//                .setOperateTime(new Date())
//                .setOperateUserId("null".equals(userId) || StringUtils.isEmpty(userId) ? -1 : Integer.parseInt(userId))
//                .setParam(param.length() < 1024 ? param : param.substring(0, 1024))
//                .setResult(response.getCode())
//                .setResponse(res.length() < 1024 ? res : res.substring(0, 1024));
//
//        logService.add(log);
    }

    @AfterThrowing(value = "pointCut()", throwing = "e")
    public void doOnException(JoinPoint joinPoint, Exception e) throws Exception {
        System.out.println("doOnException");
        //请求的参数     /*序列化时过滤掉request和response*/     /*过滤掉文件*/
        final Object[] args = joinPoint.getArgs();
        final String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        final String param = params.apply(parameterNames, args);

        final String userId = request.getHeader("userid");
        final String res = e.getClass().getName() + ":\t" + e.getMessage() + '\n' + Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .reduce((s, s2) -> s + '\n' + s2);

//        final Log log = new Log()
//                .setIp(IPUtils.getIpAddr(request))
//                .setOperateApi(request.getRequestURI())
//                .setOperateTime(new Date())
//                .setOperateUserId("null".equals(userId) || StringUtils.isEmpty(userId) ? -1 : Integer.parseInt(userId))
//                .setParam(param.length() < 1024 ? param : param.substring(0, 1024))
//                .setResult(ResultCodeEnum.ServiceException.getCode())
//                .setResponse(res.length() < 1024 ? res : res.substring(0, 1024));
//
//        logService.add(log);
    }

    private final BiFunction<String[], Object[], Map<String, Object>> paramMap = (names, values) -> {
        final int length = names.length;
        final Map<String, Object> map = new HashMap<>();
        if (length == 0) {
            return map;
        }
        for (int i = 0; i < length; i++) {
            if (values[i] instanceof HttpServletRequest || values[i] instanceof HttpServletResponse
                    || values[i] instanceof MultipartFile || values[i] instanceof File) {
                continue;
            }

            map.put(names[i], values[i]);
        }
        return map;
    };

    private final BiFunction<String[], Object[], String> params = (names, values) -> {
        int length = names.length;
        if (length == 0) {
            return "{}";
        }
        StringBuilder builder = new StringBuilder("{");
        for (int i = 0; i < length; i++) {
            if (values[i] instanceof HttpServletRequest || values[i] instanceof HttpServletResponse
                    || values[i] instanceof MultipartFile || values[i] instanceof File) {
                continue;
            }

            String val;
            try {
                val = JSON.toJSONString(values[i]);
            } catch (Exception e) {
                val = "\"" + values[i] + "\"";
            }
            builder.append("\"").append(names[i]).append("\":").append(val).append(",");
        }
        int len = builder.length();
        return builder.replace(len - 1, len, "}").toString();
    };
}
