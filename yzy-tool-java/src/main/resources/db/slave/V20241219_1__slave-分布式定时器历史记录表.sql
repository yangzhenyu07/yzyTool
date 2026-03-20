


/*==============================================================*/
/* Table: t_scheduler_lock_history                             */
/*==============================================================*/

CREATE TABLE `t_scheduler_lock_history` (
    `SEQUENCE_NO` int(18) NOT NULL AUTO_INCREMENT COMMENT '顺序号',
    `PK_T_SCHEDULER_LOCK_HISTORY` varchar(36) NOT NULL COMMENT 'ID',
    `FK_T_SCHEDULER_LOCK` varchar(36) NOT NULL COMMENT 'ID',
    `KEY_NAME` varchar(500) NOT NULL COMMENT '锁的名称（唯一标识任务）',
    `KEY_VALUE` varchar(500) NOT NULL COMMENT '锁的名称',
    `RESULT_STATE`  varchar(36)  DEFAULT NULL COMMENT 'SUCCESS:成功，ERROR:失败',
    `RESULT_BY` varchar(500) DEFAULT NULL COMMENT '获取锁的节点信息',
    `TRACE_ID` varchar(100) DEFAULT NULL COMMENT 'TRACE_ID',
    `ERROR_MESSAGE` varchar(2000) DEFAULT NULL COMMENT '错误信息',
    `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`SEQUENCE_NO`),
    UNIQUE KEY `U_T_T_SCHEDULER_LOCK_HISTORY_01` (`PK_T_SCHEDULER_LOCK_HISTORY`),
    KEY `U_T_T_SCHEDULER_LOCK_HISTORY_02` (`FK_T_SCHEDULER_LOCK`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='分布式锁定时器调度历史表';
