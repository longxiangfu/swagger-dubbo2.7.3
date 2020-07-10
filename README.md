# swagger-dubbo
dubbo2.7.3集成swagger2
注意：本项目转自https://github.com/Sayi/swagger-dubbo，在此基础上，修复了一些bug。


=======
## 公告：:balloon: :balloon: :balloon:  🌱 🌱 🌱 
* swagger-dubbo起一个解析Swagger和收集文档的作用


## Maven
```xml
<dependency>
  <groupId>com.deepoove</groupId>
  <artifactId>swagger-dubbo</artifactId>
  <version>2.0.1</version>
</dependency>
```

## 两步集成

一. 使用注解 `@EnableDubboSwagger`开启dubbo的swagger文档。
```java
package com.deepoove.swagger.dubbo.example;

import org.springframework.context.annotation.Configuration;
import com.deepoove.swagger.dubbo.annotations.EnableDubboSwagger;

@Configuration
@EnableDubboSwagger
public class SwaggerDubboConfig {

}
```

二. 在spring的*-servlet.xml配置中，开启属性占位符的配置，开启Configuration注解，声明SwaggerDubboConfig。

```xml
<context:annotation-config />
<bean class="com.deepoove.swagger.dubbo.example.SwaggerDubboConfig" />
<context:property-placeholder />
```

集成已经完毕，启动web容器，浏览器访问 `http://ip:port/context/swagger-dubbo/api-docs`查看文档。
```json
{
  "swagger": "2.0",
  "info": {
    "version": "1.0",
    "title": "dubbo-example-app",
    "contact": {
      "name": "Sayi"
    }
  },
  "basePath": "/dubbo-provider",
  "paths": {
    "/h/com.deepoove.swagger.dubbo.example.api.service.UserService/get": {
      "get": {
        "tags": [
          "UserService"
        ],
        "summary": "获取用户",
        "description": "User get(java.lang.String)通过id取用户信息",
        "operationId": "get",
        "parameters": [
          {
            "name": "id",
            "in": "query",
            "description": "用户id",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "",
            "schema": {
              "$ref": "#/definitions/User"
            }
          }
        }
      }
    }
  },
  "definitions": {
    "User": {
      "type": "object",
      "properties": {
        "id": {
          "type": "string"
        },
        "name": {
          "type": "string",
          "description": "用户姓名"
        },
        "site": {
          "type": "string"
        }
      }
    }
  }
}
```


## swagger-ui查看文档

可以在任何能托管页面的容器内集成swagger-ui，配置swagger-dubbo提供的`http://ip:port/context/swagger-dubbo/api-docs`，可能需要跨域支持，详情参见官方文档 [swagger-ui](https://github.com/swagger-api/swagger-ui)

@JKTerrific 在swagger-ui基础上开发了[**swagger-dubbo-ui**](https://github.com/JKTerrific/swagger-dubbo-ui), 解决了页面上的一些展示问题：
* 参数为model时，输入框变更为输入域，并且支持JSON可视化
* Model字段为date、byte时，支持展示具体类型，而不是string

![](swagger-dubbo-example/swagger_ui.png)

## 配置
swagger-dubbo默认无需任何配置，但是也提供了一些可选项。

新增文件swagger-dubbo.properties，加载配置文件。

```xml
<context:property-placeholder location="classpath*:swagger-dubbo.properties" />
```

配置项说明：
```properties
#http请求地址，默认为http://ip:port/h/com.XXX.XxService/method
swagger.dubbo.http=h

#dubbo 服务版本号
swagger.dubbo.application.version = 1.0
#dubbo服务groupId
swagger.dubbo.application.groupId = com.deepoove
#dubbo服务artifactId
swagger.dubbo.application.artifactId = dubbo.api

#rpc zk调用 or 本地调用
swagger.dubbo.cluster = rpc

#是否启用swagger-dubbo，默认为true
swagger.dubbo.enable = true
```

## 跨域支持
```xml
  <!-- 跨域支持，Spring4.3.10+，低版本请设置拦截器开启跨域 -->
  <mvc:cors>
    <mvc:mapping path="/swagger-dubbo/**" allowed-origins="*" />
  </mvc:cors>
```

## SpringBoot 集成 Swagger-dubbo
SpringBoot对配置做了简化，集成swagger-dubbo只需要做第一步就可以了：使用注解 `@EnableDubboSwagger`开启dubbo的swagger文档。参见[spring-boot示例](swagger-dubbo-example/dubbo-provider-springboot)

## swagger-dubbo集成注意事项
* 对于服务接口方法重载，为了在http请求中唯一确认一个方法，需要使用注解`@ApiOperation(nickname = "byArea")`,通过nickname标记唯一路径(如果不填写，将只显示一个方法)。此时，rest的请求地址为：`http://ip:port/h/com.XXX.XxService/method/byArea`
[Stackoverflow:重载的方法能够映射到同一URL地址吗](http://stackoverflow.com/questions/17196766/can-resteasy-choose-method-based-on-query-params)

* Object对象作为http请求参数为json string格式。
[Stackoverflow:POST的方法能够接收多个参数吗？](http://stackoverflow.com/questions/5553218/jax-rs-post-multiple-objects)

* swagger注解既可以写在接口上，也可以写在实现类上。 
* 原生类型作为http请求参数为必填。

 
>>>>>>> 初始化项目
