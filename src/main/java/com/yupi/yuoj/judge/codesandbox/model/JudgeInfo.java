package com.yupi.yuoj.judge.codesandbox.model;

import lombok.Data;

/**
 * 判题信息（json对象）
 */
@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间
     */
    private Long time;
}
