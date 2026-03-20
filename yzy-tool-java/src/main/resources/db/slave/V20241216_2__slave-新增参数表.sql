


/*==============================================================*/
/* Table: t_param_config                                  */
/*==============================================================*/


CREATE TABLE `t_param_config` (
    `SEQUENCE_NO` int(18) NOT NULL AUTO_INCREMENT COMMENT '顺序号',
    `PK_STD_PRODUCT_CONFIG` varchar(36) NOT NULL COMMENT 'ID',
    `MAIN_CODE` varchar(500) NOT NULL COMMENT '主code',
    `SON_CODE` varchar(36) NOT NULL COMMENT '子code',
    `CODE_NAME` varchar(500) NOT NULL COMMENT '子code名称',
    `REMARKS` varchar(500) DEFAULT NULL COMMENT '备注',
    `FK_USER_UPDATE` varchar(36) DEFAULT NULL COMMENT '变更人编码',
    `USER_NAME_UPDATE` varchar(64) DEFAULT NULL COMMENT '变更人姓名',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
    `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
    `FK_USER_CREATE` varchar(36) NOT NULL COMMENT '创建人编号',
    `USER_NAME_CREATE` varchar(64) NOT NULL COMMENT '创建人姓名',
    `DELETE_FLAG` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志, 未删除:0, 已删除:1',
    `DELETE_TIME` datetime DEFAULT NULL COMMENT '删除时间',
    `FK_USER_DELETE` varchar(36) DEFAULT NULL COMMENT '删除人编号',
    `USER_NAME_DELETE` varchar(64) DEFAULT NULL COMMENT '删除人姓名',
    PRIMARY KEY (`SEQUENCE_NO`),
    UNIQUE KEY `U_T_PARAM_CONFIG_01` (`PK_STD_PRODUCT_CONFIG`),
    KEY `I_T_PARAM_CONFIG_01` (`MAIN_CODE`,`SON_CODE`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';
