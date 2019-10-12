[TOC]

# 后端整体架构

- 动态module加载组件: 使得项目的扩展性和可维护性提升
- 动态数据服务组件: 易于扩展项目功能, 方便为前段提供更多数据接口
- 异步数据库访问: 简单的异步功能使得项目运行效率提升
- 可编程的排序服务: 为将来的功能扩展提供维护接口
- 简单的json包装类: 为开发提供方便

项目整体初始化执行逻辑如下:
ServletContext初始化 $\to$ 数据库模块初始化 $\to$ 数据服务模块初始化 $\to$ Token处理初始化 $\to$ 排序服务初始化 $\to$ 外部module初始化 $\to$ 检查管理员

## 动态module加载组件
该功能提供了一个动态加载外部jar包的效果
若想实现一个module只需要以下步骤

- 编写 module.json 模块描述文件 具体可参阅 [link](https://github.com/402b/CompetitionManagerMessageModule/blob/master/src/main/resources/module.json)
|键|值|
|---|---|
|name|模块名|
|version|模块版本|
|main|模块主类的类路径|
- 实现模块主类(继承[Module](https://github.com/402b/CompetitionManagerCore/blob/master/src/main/java/com/github/b402/cmc/core/module/Module.java))
- 编写自己的业务逻辑

## 动态数据服务组件
本项目后端的关键数据接口
用于向前端提供所有数据访问支持
所有数据服务皆为异步, 且运行在同一个[servlet](https://github.com/402b/CompetitionManagerCore/blob/master/src/main/java/com/github/b402/cmc/core/servlet/DataServlet.kt)上
前端请求任何/Data/\* GET或POST请求将会转交给对应的数据服务处理
如/Data/login 就会转交给负责登入的数据服务

其中所有数据服务的接口统一为传入一段json
若请求为GET 则为获取一个名为 "parm" 的 Parameter字符串读取为json
若请求为POST 则会将POST的所有数据读取为一个json并从中提取键为"parm"的数据
获取json后 若数据服务需要权限访问, 则会检查传入的json是否携带token信息并解析token以判断访问者是否有权限使用这个数据服务
校验通过过 则会从传入的json中读取本次数据服务所需的子json参数"Data"
并反射数据服务定义的SubmitData构造器 构造数据类 提交给数据服务处理

实现如下:
创建一个类并继承[DataService\<S exentd SubmitData\>](https://github.com/402b/CompetitionManagerCore/blob/master/src/main/java/com/github/b402/cmc/core/service/DataService.kt)
其中 父类构造器所需参数如下
|变量名|含义|类型|
|---|---|---|
|path|本数据服务的地址|String|
|permission|本服务所需的权限|Permission|
|sClass|自定义数据读取类|Class\<S\>|
需要实现的抽象方法: suspend[^1] fun onRequest(data: S): ReturnData

[^1]: 本方法为协程的可挂起方法

## 异步数据库访问
本项目数据库连接池采用[HikariCP](https://github.com/brettwooldridge/HikariCP)
所有数据库访问皆采用回调函数的方式互交
提供以下方法

|方法名|参数[^2]|返回|说明| 
|---|---|---| 
|operateConnection|Consumer\<Connection>|void|以阻塞的形式操作数据库(通常用于服务初始化)| 
|coroutinesConnection|suspend Consumer\<Connection>|void|在协程中以阻塞形式操作数据库| 
|async|suspend Consumer\<Connection>|Deferred\<Boolean>|在同一个协程上下文中异步执行操作数据库,操作成功后将会推迟返回true,反之false| 
|asyncDeferred\<R>|suspend Function\<Connection,R>|Deferred\<R>|在同一个协程上下文中异步执行操作数据库并延迟返回结果| 

[^2]: 注,所有kotlin的函数类型将用java.util.function的类描述

## 可编程的排序服务
排序服务包括数据合法性校验, 数据排序等功能
根据实际使用场景,有时可能需要一些独特的排序方式
通过排序服务接口,可以注册自定义排序方式

## 简单的json包装类
本项目采用gson作为json解析器
并且在其基础上进行了接口包装, 提供了访问接口[ConfigurationSection](https://github.com/402b/CompetitionManagerCore/blob/master/src/main/java/com/github/b402/cmc/core/configuration/ConfigurationSection.kt)
通过该接口 可以以可读性高的方式读写json的内存映射Map\<String,Object>
