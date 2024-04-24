package com.yupi.yuoj.model.dto.questionsubmit;

import com.yupi.yuoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 题目提交请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 判题状态（0-待判题、1-判题中、2-成功、3-失败）
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}