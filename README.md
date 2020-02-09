# Netty模块基础
本项目提供了一个基于Netty与SpringBoot+的便捷网络服务器模板，包含多种网络协议。

## 协议类型
Http、WebSocket、Socket、UDP.

### 模块说明

`-netty `

     -config --配置参数与配置注入`
    
     -controller --Http`
    
     -handler --对应协议的处理器`
   
     -server --对应协议的服务启动器`
    
 ### 配置说明
 配置文件按照SpringBoot的资源约束包规范，于resources下。
 
 ### 应用入口
 
 `NettyApplication.class`
