


/*==============================================================*/
/* Table: t_user_test                                  */
/*==============================================================*/


CREATE TABLE `t_user_test` (
    `PK_ID` int(18) NOT NULL AUTO_INCREMENT COMMENT '顺序号',
    `CODE` varchar(36) NOT NULL COMMENT 'CODE',
    `NAME` varchar(200) NOT NULL COMMENT '姓名',
    PRIMARY KEY (`PK_ID`),
    UNIQUE KEY `U_tuser_test_01` (`CODE`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='测试表';
