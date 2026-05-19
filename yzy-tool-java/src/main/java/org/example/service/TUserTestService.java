package org.example.service;

import org.example.entity.TUserTest;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.vo.TUserTestVo;

/**
 * <p>
 * 测试表 服务类
 * </p>
 *
 * @author yangzhenyu
 * @since 2026-05-14 11:52:16
 */
public interface TUserTestService extends IService<TUserTest> {
    TUserTest getById(String code);
    int update(TUserTestVo user);
    int save(TUserTestVo user);
    int delete(String code);

}
