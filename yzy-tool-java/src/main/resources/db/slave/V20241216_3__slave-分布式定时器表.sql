


/*==============================================================*/
/* Table: t_scheduler_lock                             */
/*==============================================================*/

CREATE TABLE `t_scheduler_lock` (
    `SEQUENCE_NO` int(18) NOT NULL AUTO_INCREMENT COMMENT '顺序号',
    `PK_T_SCHEDULER_LOCK` varchar(36) NOT NULL COMMENT 'ID',
    `KEY_NAME` varchar(500) NOT NULL COMMENT '锁的名称（唯一标识任务）',
     `KEY_VALUE` varchar(500) NOT NULL COMMENT '锁的名称',
    `LOCK_UNTIL` datetime NOT NULL COMMENT '锁的有效期（过期时间）',
    `LOCKED_BY` varchar(500) DEFAULT NULL COMMENT '获取锁的节点信息',
    `TRACE_ID` varchar(100) DEFAULT NULL COMMENT 'TRACE_ID',
     `LOCK_STATE` int(1) DEFAULT NULL COMMENT '锁状态，0=未锁，1=已锁',
     `LOCK_VERSION` int NOT NULL DEFAULT 0 COMMENT '版本号，支持乐观锁',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '释放变更时间',
    `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`SEQUENCE_NO`),
    UNIQUE KEY `U_T_T_SCHEDULER_LOCK_01` (`PK_T_SCHEDULER_LOCK`),
    UNIQUE KEY `U_T_T_SCHEDULER_LOCK_02` (`KEY_NAME`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='分布式锁定时器调度表';
