package com.tydic.framework.config.log.service;

import com.github.pagehelper.PageSerializable;
import com.tydic.core.rational.Return;
import com.tydic.core.util.collection.Maps;
import com.tydic.core.util.json.JSON;
import com.tydic.framework.config.log.aop.anno.Loggable;
import com.tydic.framework.config.log.aop.anno.LoggableIgnore;
import com.tydic.framework.config.log.aop.enums.LoggableTypeEnum;
import com.tydic.framework.config.log.bean.LoggerDto;
import com.tydic.framework.config.spring.factory.BeanSelfAware;
import com.tydic.framework.spring.security.oauth2.OauthUserDetails;
import com.tydic.framework.util.IpUtil;
import com.tydic.framework.util.MacIpUtil;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-06-10 19:13
 */

public abstract class AbstractLoggerService implements LoggerService, BeanSelfAware<LoggerService> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${eda.app.id:-1}")
    private String serverAppId;

    private static final String TRANS_ID_PREFIX = "trans_";

    private ThreadLocal<String> transactionIdThreadLocal = new ThreadLocal<>();

    private LoggerService proxy;

    @Override
    public void setSelf(LoggerService proxy) {
        this.proxy = proxy;
    }

    @Override
    public void processLog(ProceedingJoinPoint point, Object target, Object result, String errorMsg) {
        try {
            Object[] params = point.getArgs();
            Class targetClazz = point.getTarget().getClass();
            String clazzName = targetClazz.getName();

            String logTypeName = null;
            String logModule = null;
            String logFeatures = null;

            Signature signature = point.getSignature();

            if (signature instanceof MethodSignature) {
                String methodName = signature.getName();
                Method method = targetClazz.getMethod(methodName, ((MethodSignature) signature).getParameterTypes());

                boolean useLogServiceFlag = false;
                if (target instanceof RestController) {
                    boolean hasApiOperation = method.isAnnotationPresent(ApiOperation.class);
                    if (hasApiOperation) {
                        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
                        logTypeName = LoggableTypeEnum.VISIT.name();
                        logModule = apiOperation.tags()[0];
                        logFeatures = apiOperation.value();

                        useLogServiceFlag = true;

                    }
                } else if (target instanceof Loggable) {
                    Loggable loggable = (Loggable) target;
                    logTypeName = loggable.type().name();
                    logModule = loggable.module();
                    logFeatures = loggable.features();

                    useLogServiceFlag = true;
                }
                if (useLogServiceFlag) {
                    ignoreParams(method, params);
                    log(logTypeName, logModule, logFeatures, clazzName, methodName, params, ignoreMethodOrReturn(method, result), errorMsg);
                }
            }

        } catch (Exception e) {
            logger.error("记录log时捕捉到异常：" + e.getMessage());
        }
    }

    /**
     * 对带有@GecLoggableIgnore注解的参数,进行剔除
     *
     * @param method
     * @param params
     */
    private void ignoreParams(Method method, Object[] params) {
        if (method == null || params == null || params.length == 0) {
            return;
        }
        Annotation[][] annos = method.getParameterAnnotations();
        if (!ObjectUtils.isEmpty(params) && !ObjectUtils.isEmpty(annos)) {
            for (int i = 0; i < params.length; i++) {
                Optional<Annotation> optional = Arrays.stream(annos[i]).filter(annotation -> LoggableIgnore.class.isAssignableFrom(annotation.annotationType())).findAny();

                if (optional.isPresent()) {
                    params[i] = ((LoggableIgnore) optional.get()).value();
                }
            }
        }
    }

    /**
     * 对于方法返回值，记录日志时，进行处理..
     *
     * @param method
     * @param result
     * @return
     */
    private Object ignoreMethodOrReturn(Method method, Object result) {
        if (method == null || result == null || result instanceof Void) {
            return result;
        }
        LoggableIgnore anno = method.getAnnotation(LoggableIgnore.class);
        if (anno != null) {
            if (anno.methodIgnore()) {
                return null;
            } else {
                return anno.value();
            }
        }
        return result;
    }

    @Override
    public void log(String logType, String logModule, String logFeatures, String clazzName, String methodName, Object[] params, Object result, String errorStackMsg) {
        LoggerDto bean = new LoggerDto();
        bean.setLogLevel(errorStackMsg == null ? "INFO" : "ERROR");

        bean.setOptTime(LocalDateTime.now().toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OauthUserDetails curUser = (OauthUserDetails) authentication.getPrincipal();
            bean.setOptUserCode(curUser.getLoginAcct());
//			bean.setLatnId(curUser.getLatnId());
        } else {
            bean.setOptUserCode("");
            bean.setLatnId("");
        }

        bean.setIp(IpUtil.getRemoteHost());

        bean.setLoggableType(logType);
        bean.setModule(logModule);
        bean.setFeatures(logFeatures);

        String operateObject = clazzName + "." + methodName;
        bean.setOperateObject(operateObject);


        if (params.length == 0) {
            bean.setParams("");
        } else {
            List filteredParams = Arrays.stream(params).filter(param ->
                    !(param instanceof HttpServletRequest || param instanceof HttpServletResponse || param instanceof InputStreamSource)
            ).collect(Collectors.toList());
            if (!filteredParams.isEmpty()) {
                bean.setParams(JSON.stringify(filteredParams));
                ;
            }
        }

        if (result instanceof Void || result instanceof StreamingResponseBody) {
            bean.setResult("");
        } else {
            //解决返回列表打印日志过长的问题
            if (result instanceof Return) {
                Return ret = (Return) result;
                if (ret.getData() instanceof Collection) {
                    result = Maps.of("code", ret.getCode())
                            .of("message", ret.getMessage())
                            .of("data", ret.getData().getClass().getName() + ": [" + ((Collection) ret.getData()).size() + "] ");
                } else if (ret.getData() instanceof PageSerializable) {
                    result = Maps.of("code", ret.getCode())
                            .of("message", ret.getMessage())
                            .of("data", ret.getData().getClass().getName() + ": [" + ((PageSerializable) ret.getData()).getList().size() + "] ");
                }
            } else if (result instanceof Collection) {
                result = Maps.of("data", result.getClass().getName() + ": [" + ((Collection) result).size() + "] ");
            }

            bean.setResult(JSON.stringify(result));
        }

        bean.setServiceCode(serverAppId);

        //设置ip
        bean.setServiceIp(MacIpUtil.getIpv4());

        String transId;
        if ((transId = transactionIdThreadLocal.get()) == null) {
            transId = TRANS_ID_PREFIX + UUID.randomUUID();
            transactionIdThreadLocal.set(transId);
        } else {
            /**
             * ThreadLocal在使用完之后，需要主动清空;
             *
             * transID 会在controller,service调用两次; transactionIdThreadLocal.get() !=null, 表示是第二次调用，
             * 此时主动调用清空ThreadLocal
             */
            transactionIdThreadLocal.remove();
        }
        bean.setTransactionId(transId);

        bean.setErrorStackMsg(errorStackMsg);

        proxy.log(bean);
    }
}
