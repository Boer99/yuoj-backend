package com.yupi.yuoj.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求（管理员）
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 创建用户 id
     */
//    private Long userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 答案
     */
    private String answer;

    /**
     * 判题用例（json数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置（json对象）
     */
    private JudgeConfig judgeConfig;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}