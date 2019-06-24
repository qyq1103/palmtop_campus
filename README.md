# palmtop_campus
添加毕业项目到GitHub的存储库
下载或是拉去项目时，请记得拉取PalmtopCampus项目（PalmtopCampus是这个项目的移动端。）
项目说明：原项目未使用maven，现存储库中的项目使用的是maven
数据库字段：
管理人员表
字段	类型	长度	允许为空	主键	自增	注释
id	int	8	否	是	是	管理员id
name	varchar	32	是	否	否	管理员账号
password	varchar	64	否	否	否	管理员密码
type	int	8	否	否	否	管理员权限

学生信息表
字段	类型	长度	允许为空	主键	自增	注释
id	varchar	16	否	是	否	学生学号
password	varchar	64	否	否	否	学生密码

教师信息表
字段	类型	长度	允许为空	主键	自增	注释
id	int	16	否	是	否	教师工号
password	varchar	64	否	否	否	教师密码



