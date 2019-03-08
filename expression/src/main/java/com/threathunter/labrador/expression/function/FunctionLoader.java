/**
 *
 */
package com.threathunter.labrador.expression.function;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 表达式中函数加载器，从配置文件中加载可用的外部方法
 *
 * @author 林良益，卓诗垚
 * @version 2.0
 *          Feb 3, 2009
 */
@SuppressWarnings("unchecked")
public class FunctionLoader {
    private static final String FILE_NAME = "/expression.cfg.xml";

    private static FunctionLoader single = new FunctionLoader();

    private Map<String, Function> definedFunctions = new HashMap<>();

    public static Function getFunctionByName(String name) {
        return single.definedFunctions.get(name);
    }

    //所有方法Map
    private HashMap<String, FunctionInvoker> functionMap = new HashMap<String, FunctionInvoker>();

    /**
     * 私有，禁止外部新建
     */
    private FunctionLoader() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 初始化,解析XML配置
     *
     * @throws ParserConfigurationException
     * @throws Exception
     */
    private void init() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(FunctionLoader.class.getResourceAsStream(FILE_NAME));
        NodeList rootNodes = doc.getElementsByTagName("function-configuration");
        if (rootNodes.getLength() < 1) {
            return;
        }
        rootNodes = rootNodes.item(0).getChildNodes();
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node beanNode = rootNodes.item(i);
            if (!beanNode.getNodeName().equals("bean")) {
                continue;
            }
            //读class类
            String className = beanNode.getAttributes().getNamedItem("class").getNodeValue();
            Class _class = Class.forName(className);

            NodeList subNodes = beanNode.getChildNodes();
            List<Parameter> constructorArgs = null;
            HashSet<Function> functions = new HashSet<Function>();
            for (int j = 0; j < subNodes.getLength(); j++) {
                Node subNode = subNodes.item(j);
                if (subNode.getNodeName().equals("constructor-args") && constructorArgs == null) {
                    //读取类的构造参数
                    constructorArgs = parseConstructorArgs(subNode);
                } else if (subNode.getNodeName().equals("function")) {
                    //读取方法描述
                    if (!functions.add(parseFunctions(subNode))) {
                        throw new SAXException("方法名不能重复");
                    }
                }
            }
            if (functions.size() <= 0) {
                continue;
            }
            Object ins = null;
            if (constructorArgs == null || constructorArgs.size() <= 0) {
                //使用默认构造函数
                ins = _class.newInstance();
            } else {
                //使用给定的构造函数
                Class[] cs = getParameterTypes(constructorArgs);
                Object[] ps = getParameterValues(constructorArgs);
                Constructor c = _class.getConstructor(cs);
                ins = c.newInstance(ps);
            }
            //封装FunctionInvoker放入Map中
            for (Function f : functions) {
                Method m = _class.getMethod(f.methodName, getParameterTypes(f.types));
                functionMap.put(f.name, new FunctionInvoker(m, ins));
                definedFunctions.put(f.name, f);
            }

        }
    }

    /**
     * 解析构参数法的描述
     *
     * @param argRootNode
     * @return
     */
    private List<Parameter> parseConstructorArgs(Node argRootNode) {
        NodeList argsNode = argRootNode.getChildNodes();
        List<Parameter> args = new ArrayList<Parameter>();
        for (int i = 0; i < argsNode.getLength(); i++) {
            Node argNode = argsNode.item(i);
            if (argNode.getNodeName().equals("constructor-arg")) {
                //参数类型
                String type = argNode.getAttributes().getNamedItem("type").getNodeValue();
                //参数值
                String value = argNode.getTextContent();
                args.add(new Parameter(type, value));
            }
        }
        return args;
    }

    /**
     * 解析方法的描述
     *
     * @param funRootNode
     * @return
     */
    private Function parseFunctions(Node funRootNode) {
        String name = funRootNode.getAttributes().getNamedItem("name").getNodeValue();
        String methodName = funRootNode.getAttributes().getNamedItem("method").getNodeValue();
        Node verifyNode = funRootNode.getAttributes().getNamedItem("verify");
        Function f = new Function(name, methodName);
        if (null != verifyNode) {
            if (!verifyNode.getNodeValue().trim().equals("true")) {
                throw new IllegalStateException("if verify exists, value must be true");
            }
            f.setVerifyMethod(verifyNode.getNodeValue());
        }
        NodeList argsNode = funRootNode.getChildNodes();
        for (int i = 0; i < argsNode.getLength(); i++) {
            Node argNode = argsNode.item(i);
            if (argNode.getNodeName().equals("parameter-type")) {
                //参数类型
                f.addType(argNode.getTextContent());
            } else {
                continue;
            }
            boolean regex = false;
            boolean type = false;
            Node variableRegexNode = argNode.getAttributes().getNamedItem("regex");
            if (null != variableRegexNode) {
                f.getVariableNameRegex().add(variableRegexNode.getNodeValue());
                regex = true;
            }

            Node variableTypeNode = argNode.getAttributes().getNamedItem("type");
            if (null != variableTypeNode) {
                if (!(variableTypeNode.getNodeValue().equals("constant") || variableTypeNode.getNodeValue().equals("variable"))) {
                    throw new IllegalStateException("if [parameter-type.type] exists, value must be [variable/constant]");
                }
                type = true;
                f.getVariableType().add(variableTypeNode.getNodeValue());
            }

            if(regex != type) {
                throw new IllegalStateException(String.format("method name %s config exception, attribute regex and type must both exist or not exist ", name));
            }
        }
        return f;
    }

    /**
     * 取得参数列表的类型
     *
     * @param parameters
     * @return
     */
    private Class[] getParameterTypes(List<Parameter> parameters) {
        if (parameters == null) {
            return null;
        }
        Class[] types = new Class[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            types[i] = parameters.get(i).type;
        }
        return types;
    }

    /**
     * 取得参数列表的值
     *
     * @param parameters
     * @return
     */
    private Object[] getParameterValues(List<Parameter> parameters) {
        if (parameters == null) {
            return null;
        }
        Object[] values = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            values[i] = parameters.get(i).value;
        }
        return values;
    }

    /**
     * 参数定义
     *
     * @author zsy
     * @version Feb 18, 2009
     */
    private class Parameter {
        Class type;//参数类型
        Object value;//参数值

        public Parameter(String _type, String _value) {
            try {
                type = getTypeClass(_type);
                Constructor c = type.getConstructor(new Class[]{String.class});
                value = c.newInstance(_value);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public Parameter(String _type) {
            try {
                type = getTypeClass(_type);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private Class getTypeClass(String _type) throws ClassNotFoundException {
            if ("boolean".equals(_type)) {
                return boolean.class;
            } else if ("byte".equals(_type)) {
                return byte.class;
            } else if ("char".equals(_type)) {
                return char.class;
            } else if ("double".equals(_type)) {
                return double.class;
            } else if ("float".equals(_type)) {
                return float.class;
            } else if ("int".equals(_type)) {
                return int.class;
            } else if ("long".equals(_type)) {
                return long.class;
            } else if ("short".equals(_type)) {
                return short.class;
            }
            return Class.forName(_type);

        }
    }

    /**
     * 方法定义
     *
     * @author zsy
     * @version Feb 18, 2009
     */
    public class Function {
        private String name;//表达式使用的方法名称
        private String methodName;//类是的实际方法名称
        private String verifyMethod = "";//验证方法
        private List<Parameter> types;//方法的参数类型
        private List<String> variableClassType = new ArrayList<>();//变量参数类型字符串
        private List<String> variableNameRegex = new ArrayList<>();//变量名称正则
        private List<String> variableType = new ArrayList<>();//变量类型，仅为variable/constant


        public Function(String _name, String _methodName) {
            if (_name == null || _methodName == null) {
                throw new IllegalArgumentException();
            }
            name = _name;
            methodName = _methodName;
            types = new ArrayList<Parameter>();
        }

        public void addType(String type) {
            types.add(new Parameter(type));
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Function other = (Function) obj;
            return name.equals(other.name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getVerifyMethod() {
            return verifyMethod;
        }

        public void setVerifyMethod(String verifyMethod) {
            this.verifyMethod = verifyMethod;
        }

        public List<Parameter> getTypes() {
            return types;
        }

        public void setTypes(List<Parameter> types) {
            this.types = types;
        }

        public List<String> getVariableClassType() {
            return variableClassType;
        }

        public void setVariableClassType(List<String> variableClassType) {
            this.variableClassType = variableClassType;
        }

        public List<String> getVariableNameRegex() {
            return variableNameRegex;
        }

        public void setVariableNameRegex(List<String> variableNameRegex) {
            this.variableNameRegex = variableNameRegex;
        }

        public List<String> getVariableType() {
            return variableType;
        }

        public void setVariableType(List<String> variableType) {
            this.variableType = variableType;
        }
    }

    /**
     * 方法调用
     *
     * @author zsy
     * @version Feb 18, 2009
     */
    private class FunctionInvoker {
        Method method;//java方法
        Object instance;//调用的实例

        public FunctionInvoker(Method m, Object i) {
            method = m;
            instance = i;
        }

        /**
         * 执行方法
         *
         * @param args 参数值列表
         * @return
         * @throws NoSuchMethodException
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         */
        public Object invoke(Object[] args) throws NoSuchMethodException,
                IllegalAccessException, InvocationTargetException {
            return method.invoke(instance, args);
        }
    }


    /**
     * 表达式可用函数除了从配置文件“functionConfig.xml”加载外，
     * 还可以通过此方法运行时添加
     *
     * @param functionName 方法别名，表达式使用的名称
     * @param instance     调用的实例名
     * @param method       调用的方法
     */
    public static void addFunction(String functionName, Object instance, Method method) {
        if (functionName == null || instance == null || method == null) {
            return;
        }
        single.functionMap.put(functionName, single.new FunctionInvoker(method, instance));
    }

    /**
     * 取得方法
     *
     * @param functionName
     * @return
     * @throws NoSuchMethodException
     */
    public static Method loadFunction(String functionName) throws NoSuchMethodException {
        FunctionInvoker f = single.functionMap.get(functionName);
        if (f == null) {
            throw new NoSuchMethodException();
        }
        return f.method;
    }

    /**
     * 执行方法
     *
     * @param functionName
     * @param parameters
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokeFunction(String functionName, Object[] parameters)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        FunctionInvoker f = single.functionMap.get(functionName);
        if (f == null) {
            throw new NoSuchMethodException();
        }
        return f.invoke(parameters);
    }

}
