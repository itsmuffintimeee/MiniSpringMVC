# MiniSpringMVC

MiniSpringMVC 是一个基于 Java Servlet 的简化版 Spring MVC 处理器。

## 主要功能

- IoC 容器：扫描 com.fc 包下的类，实例化 @MyController 和 @MyService 修饰的类，并进行依赖注入。

- 请求映射：解析 @MyRequestMapping 注解，将 URL 绑定到相应的 Controller 方法。

- 请求处理：通过 HttpServletRequest 解析请求，获取方法参数并调用对应的处理方法。

- 参数解析：支持 HttpServletRequest、HttpServletResponse 以及 @MyRequestParam 注解的参数解析。
