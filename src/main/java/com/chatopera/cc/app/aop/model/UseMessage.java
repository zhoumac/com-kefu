package com.chatopera.cc.app.aop.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@ApiModel("运行时使用过的系统变量")
public class UseMessage implements Serializable {
    @ApiModelProperty("使用过的数据库表")
    private Set<String> useTables;

    @ApiModelProperty("使用过的Controllers")
    private Set<String>  useControllers;

    @ApiModelProperty("使用过的缓存")
    private Set<String>  useCache;
}
