# swagger-dubbo
dubbo2.7.3集成swagger2
注意：本项目转自https://github.com/Sayi/swagger-dubbo，在2.0.1版本基础上，修复了一些bug。
1.修复: 支持bubbo2.7.3
2.修复: 支持返回对象泛型参数


:balloon: :balloon: :balloon:  🌱 🌱 🌱 
swagger-dubbo起一个解析Swagger和收集文档的作用


## Maven
```xml
<dependency>
  <groupId>com.deepoove</groupId>
  <artifactId>swagger-dubbo</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## springboot集成

一. 使用注解 `@EnableDubboSwagger`开启dubbo的swagger文档。
```java
package com.xfdsx.service.sorting.infrastructure.config;

import com.deepoove.swagger.dubbo.annotations.EnableDubboSwagger;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@DubboComponentScan(basePackages = { "com" })
@EnableDubboSwagger
public class SwaggerDubboConfig {

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("service");
        applicationConfig.setOwner("owner");
        return applicationConfig;
    }
```

二. 启动web容器，浏览器访问 `http://ip:port/context/swagger-dubbo/api-docs`查看文档。
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

@JKTerrific 在swagger-ui基础上开发了[**swagger-dubbo-ui**](https://github.com/JKTerrific/swagger-dubbo-ui), 解决了页面上的一些展示问题：
* 参数为model时，输入框变更为输入域，并且支持JSON可视化
* Model字段为date、byte时，支持展示具体类型，而不是string

![](swagger-dubbo-example/swagger_ui.png)
