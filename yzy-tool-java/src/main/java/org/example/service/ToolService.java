package org.example.service;

import org.example.vo.KeyWordVo;
import org.example.vo.ScanVo;

import java.util.List;

/**
* @author 杨镇宇
* @date 2024/6/13 15:38
* @version 1.0
*/

public interface ToolService {
     List<KeyWordVo> scan(ScanVo scanVo);
}
