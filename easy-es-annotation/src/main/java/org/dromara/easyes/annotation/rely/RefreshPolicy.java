package org.dromara.easyes.annotation.rely;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据刷新策略枚举
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@AllArgsConstructor
public enum RefreshPolicy {
    /**
     * 使用全局设置: easy-es.global-config.db-config.refresh-policy
     */
    GLOBAL(""),
    /**
     * 不立即刷新 (es默认的数据刷新策略)
     */
    NONE("false"),
    /**
     * 立即刷新,性能损耗高
     */
    IMMEDIATE("true"),
    /**
     * 请求提交数据后，等待数据完成刷新(1s)，再结束请求 性能损耗适中
     */
    WAIT_UNTIL("wait_for");

    /**
     * 刷新策略值
     */
    @Getter
    private String value;
}
