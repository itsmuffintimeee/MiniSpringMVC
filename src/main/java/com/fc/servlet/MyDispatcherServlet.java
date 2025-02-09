package com.fc.servlet;

import com.fc.annotation.*;
import com.fc.controller.UserController;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * 自定义Dispatcher
 *
 * @author juice
 */
public class MyDispatcherServlet extends HttpServlet {

    /**
     * 扫描指定包下(com.fc)的所有的类：全限定类名
     */
    private final List<String> qualifiedNames = new ArrayList<String>();
    /**
     * IoC容器：存放被实例化后的类
     */
    private final Map<String, Object> ioc = new HashMap<String, Object>();
    /**
     * 存放映射地址：请求的URL映射集
     * K：访问的url
     * V：对应的method
     */
    private final Map<String, Object> urlHandlers = new HashMap<String, Object>();

    /**
     * 初始化方法
     *
     * @throws ServletException
     */
    @Override
    public void init()
            throws ServletException {
        // 扫描指定包下的所有的包中的类，并且拿到类的全限定类名(包名+类名)初始化给变量qualifiedName
        doScanPackage("com.fc");
        // 实例化qualifiedName中所有被@MyController和@MyService注解修饰的类
        doInstance();
        // 处理类中的依赖关系：向接口注入实现类
        doAutowired();
        // 遍历IoC获取路径映射
        doUrlMapping();
    }

    /**
     *
     */
    private void doUrlMapping() {
        // 遍历IoC容器
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取value，已经被实例化的类
            Object value = entry.getValue();
            // 获取Class对象
            Class<?> clazz = value.getClass();
            // 判断clazz是否有被@MyController注解所修饰
            if (clazz.isAnnotationPresent(MyController.class)) {
                // 获取@MyController注解对象
                MyRequestMapping myRequestMapping = clazz.getAnnotation(MyRequestMapping.class);
                // 获取@MyController注解中的value值
                String prefixUrl = parseString(myRequestMapping.value());
                // 获取类中的所有方法
                Method[] methods = clazz.getMethods();
                // 遍历类中的所有方法
                for (Method method : methods) {
                    // 判断方法上是否带有@MyRequestMapping注解
                    if (method.isAnnotationPresent(MyRequestMapping.class)) {
                        // 得到方法上的@MyRequestMapping注解
                        MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                        // 取出@MyRequestMapping注解中的value值
                        String suffixUrl = parseString(requestMapping.value());
                        // 将url路径绑定对应的方法
                        urlHandlers.put(prefixUrl + suffixUrl, method);
                    }
                }
            }
        }
    }

    /**
     * 处理类中的依赖关系，接口注入实现类
     */
    private void doAutowired() {
        // 容器中都是被实例化的类，遍历容器为接口注入实现类
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取集合(IoC)中的类
            Object obj = entry.getValue();
            // 获取Class对象
            Class<?> clazz = obj.getClass();
            // 判断是否为@MyController注解修饰的类
            if (clazz.isAnnotationPresent(MyController.class)) {

                // 得到类中的所有属性，下一步就该判断是否带有@MyAutowired注解
                Field[] fields = clazz.getDeclaredFields();
                // 遍历类中的所有属性
                for (Field field : fields) {
                    if (field.isAnnotationPresent(MyAutowired.class)) {
                        MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                        String key = myAutowired.value();
                        // 获取已经被实例化的接口实现类对象
                        Object instance = ioc.get(key);
                        // 开启访问 private 权限
                        field.setAccessible(true);
                        try {
                            // Autowired依赖注入
                            // 将当前对象（Controller注解对象）的指定字段（Autowired注解字段）设置值（ServiceImpl对象）
                            field.set(obj, instance);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }// 循环结束
            }// 不是@MyController注解修饰的类
        }
    }

    /**
     * 实例化qualifiedName中所有被@MyController和@MyService注解修饰的类
     */
    private void doInstance() {
        for (String qualifiedName : qualifiedNames) {
            try {
                // 装载一个类并且对其进行实例化
                Class<?> clazz = Class.forName(qualifiedName);
                // isAnnotationPresent(Class clazz)：判断指定的注解是否在指定的类上
                // 判断@MyController注解是否在指定的类上
                if (clazz.isAnnotationPresent(MyController.class)) {
                    // 控制层的类实例化
                    Object obj = clazz.newInstance();
                    // 得到控制层类中的@MyRequestMapping注解
                    MyRequestMapping myRequestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    // 得到@MyRequestMapping注解的value的值
                    String str = myRequestMapping.value();
                    // 在value值前添加"/"
                    String value = parseString(str);
                    // 添加至IoC容器中
                    ioc.put(value, obj);
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    // service层的类实例化
                    Object obj = clazz.newInstance();
                    // 得到@MyService注解的value值，也就是service层接口实现类的名称
                    MyService ms = clazz.getAnnotation(MyService.class);
                    // 添加至Ioc容器中
                    ioc.put(ms.value(), obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }// 循环结束
    }

    /**
     * 在字符串前添加"/"
     *
     * @param value 原始字符串
     * @return 添加"/"后的字符串
     */
    private String parseString(String value) {
        return null != value ? "/" + value : null;
    }

    /**
     * 扫描指定包下(文件夹中的所有的文件)的所有的类
     *
     * @param packagePath 指定的包：com.fc
     */
    private void doScanPackage(String packagePath) {
        // 将包中的.替换为/，也就是com.fc替换为com/fc
        String newPackagePath = packagePath.replace(".", "/");
        // 拿到包的从盘符开始的路径(绝对路径)
        URL absPath = this.getClass().getClassLoader().getResource(newPackagePath);
        // 判断非空后拿到绝对路径
        String fileStr = Objects.requireNonNull(absPath).getFile();
        File file = new File(fileStr);
        // 返回目录下的所有文件名+后缀和文件夹名称，返回的是String数组
        String[] list = file.list();
        // 如果数组为空则代表指定的文件夹为空，不进行循环
        for (String path : null != list ? list : new String[0]) {
            // 指定文件夹中的文件或文件夹的绝对路径
            File filePath = new File(fileStr + "/" + path);
            // 判断是否为文件夹
            if (filePath.isDirectory()) {
                // 代表为文件夹，递归调用扫描
                doScanPackage(packagePath + "." + path);
            } else {
                // 将文件的后缀.class替换为""空字符串，得到类名
                String className = filePath.getName().replace(".class", "");
                // 拼接为全限定类名(包名+类名)并添加到qualifiedName集合中
                qualifiedNames.add(packagePath + "." + className);
            }
        }// 循环结束
    }

    /**
     * 处理get请求
     *
     * @param req  请求对象
     * @param resp 响应对象
     * @throws ServletException Servlet异常
     * @throws IOException      读写异常
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * 处理post请求
     *
     * @param req  请求对象
     * @param resp 响应对象
     * @throws ServletException Servlet异常
     * @throws IOException      读写异常
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 设置编码集
            req.setCharacterEncoding("utf-8");
            resp.setContentType("text/html; charset=utf-8");
            // 获取请求的servlet路径
            String path = req.getServletPath();
            // 根据请求路径从url映射集中获取对应的方法
            Method method = (Method) urlHandlers.get(path);
            // 根据"/"将请求的url进行分割，分割为数组
            String[] split = path.split("/");
            // 例如：/user/getJson将被分割为：[0] = ""，[1] = "user"，[2] = "getJson"
            // 截取数组中下标为1元素
            String key = split[1];
            // 从IoC中取出该对象
            UserController userController = (UserController) ioc.get(parseString(key));
            Object[] args = null;
            // 容器启动时会初始化，所以需要先判空，防止发生空指针异常
            if (null != method) {
                // 获取方法中的参数列表
                args = getArgs(req, resp, method);
                // 通过代理调用对应的方法
                method.invoke(userController, args);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取请求的方法中的参数
     *
     * @param req    请求对象
     * @param resp   响应对象
     * @param method 方法对象
     * @return 方法的参数列表
     */
    private Object[] getArgs(HttpServletRequest req, HttpServletResponse resp, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 参数列表数组，用于存放方法中的相关参数
        Object[] args = new Object[parameterTypes.length];
        // 参数列表数组的下标
        int argsIndex = 0;
        // 带有注解的相关参数数组的下标
        int index = 0;
        // 遍历方法参数列表
        for (Class<?> parameter : parameterTypes) {
            // 判断参数是否为request对象或相关类对象
            if (ServletRequest.class.isAssignableFrom(parameter)) {
                // 参数列表中的第argsIndex++个参数为request对象
                args[argsIndex++] = req;
            }
            // 判断参数是否为response对象或相关类对象
            if (ServletResponse.class.isAssignableFrom(parameter)) {
                // 参数列表中的第argsIndex++个参数为response对象
                args[argsIndex++] = resp;
            }
            // 获取带有注解的参数，返回的是注解类型的数组
            Annotation[] parameterAnnotations = method.getParameterAnnotations()[index];
            // 判断是否有注解，比如 HttpServlet 参数就没有注解，直接跳过
            if (parameterAnnotations.length > 0) {
                // 遍历这个带有注解的参数列表的数组
                for (Annotation parameterAnnotation : parameterAnnotations) {

                    if (MyRequestParam.class.isAssignableFrom(parameterAnnotation.getClass())) {
                        // 拿到参数中的@MyRequestParam注解
                        MyRequestParam requestParam = (MyRequestParam) parameterAnnotation;
                        // 获取注解@MyRequestParam中的值，并从请求对象中获取对应的值
                        String param = req.getParameter(requestParam.value());
                        // 如果参数的类型为integer类型的话，需要将从request中取出的对象转换为Integer类型
                        if (Integer.class.isAssignableFrom(parameter)) {
                            Integer intParam;
                            try {
                                // 转型：String-->int
                                intParam = Integer.parseInt(param);
                                // 放入参数列表对应的位置
                                args[argsIndex++] = intParam;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // 放入参数列表对应的位置
                            args[argsIndex++] = param;
                        }
                    }

                }// 循环结束
            }
            // 千万不能忘...我踩了这个坑
            index++;
        }// 循环结束
        return args;
    }


}
