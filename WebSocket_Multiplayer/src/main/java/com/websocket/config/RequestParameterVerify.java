package com.websocket.config;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.Set;

/**
 * 请求参数验证处理
 * 注意：使用该类进行参数验证，请求参数对象属性必须有相应的注解(包路径：javax.validation.constraints.*),可参考User类
 */
public class RequestParameterVerify {

    /**
     * @author zhanglei
     * 请求参数校验
     * param t
     * @param <T>
     */
    public static <T> void validate(@Valid T t) {
        if(null == t){
            throw new RuntimeException("请求参数为空！");
        }
        Set<ConstraintViolation<@Valid T>> validateSet = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(t, new Class[0]);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "|" + m2)
                    .orElse("请求参数不全！");
            throw new RuntimeException(messages);
        }
    }
}
