package com.threathunter.labrador.core.builder;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.*;
import com.threathunter.labrador.common.util.DimensionUtil;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.util.EnumUtil.*;
import com.threathunter.labrador.core.builder.basic.MethodDefineRegistry;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by wanbaowang on 17/8/22.
 */
@Slf4j
public class VariableBuilder {

    private String text;

    private MethodDefineRegistry methodDefineRegistry;

    private List<String> profileRelevants;

    public VariableBuilder(String text) {
        profileRelevants = new ArrayList<>();
        profileRelevants.add("profile");
        profileRelevants.add("slot");
        this.methodDefineRegistry = new MethodDefineRegistry();
        this.text = text;
    }

    private boolean isValidSourceField(String source, String field) {
        return Env.getEventFieldContainer().containsEventSourceField(source, field);
    }


    public Map<Module, Map<String, Element>> build() throws BuilderException {
        Map<Module, Map<String, Element>> moduleElements = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String currentVariableName = "";
        try {
            Map<String, Object> root = mapper.reader(Map.class).readValue(this.text);
            Preconditions.checkArgument((root.containsKey("values")), "VariableBuilder error, content miss [values] attribute.");
            List<Map<String, Object>> items = (List<Map<String, Object>>) root.get("values");
            if (items.size() == 0) {
                throw new BuilderException("VariableBuilder error, values size is 0");
            }
            for (int i = 0; i < items.size(); i++) {
                Map<String, Object> item = items.get(i);
                if (!item.containsKey("module")) {
                    throw new BuilderException("variable size " + (i + 1) + " miss [module] element");
                }
                String _module = String.valueOf(item.get("module"));
                if(!profileRelevants.contains(_module)) {
                    continue;
                }
                String itemContent = mapper.writeValueAsString(item);
                Element element = new Element(itemContent);
                if (!item.containsKey("name")) {
                    throw new BuilderException("variable size " + (i + 1) + " miss [name] element");
                }
                String name = String.valueOf(item.get("name"));

                currentVariableName = name;
                element.setName(name);


                if (!EnumUtil.containsEnum(Module.class, _module)) {
                    throw new BuilderException("variable name " + name + " illegal module type " + _module);
                }

                Module module = Module.valueOf(_module);

                element.setModule(module);

                if (!item.containsKey("value_type")) {
                    throw new BuilderException("variable name " + name + " miss [value_type] element");
                }
                element.setValueType(EnumUtil.parseDataType(String.valueOf(item.get("value_type"))));

                if (!item.containsKey("value_subtype")) {
                    throw new BuilderException("variable name " + name + " miss [value_subtype] element");
                }
                element.setValueSubType(EnumUtil.parseDataType(String.valueOf(item.get("value_subtype"))));

                if (!item.containsKey("value_category")) {
                    throw new BuilderException("variable name " + name + " miss [value_category] element");
                }

                element.setValueCategory(EnumUtil.parseDimension(String.valueOf(item.get("value_category"))));

                List<Object> _sources = (List<Object>) item.get("source");
                if (_sources.size() == 0) {
                    throw new BuilderException("variable name " + name + " [source] element size is 0 ");
                }
                Map<String, String> firstSource = (Map<String, String>) _sources.get(0);

                if (!firstSource.containsKey("name")) {
                    throw new BuilderException("variable name " + name + " miss [source.name] element");
                }

                if(!item.containsKey("remark")) {
                    throw new BuilderException("variable name " + name + " miss [remark] ");
                }

                element.setRemark(String.valueOf(item.get("remark")));

                List<String> sources = new ArrayList<>();
                for (int j = 0; j < _sources.size(); j++) {
                    Map<String, String> curSource = (Map<String, String>) _sources.get(j);
                    if (!curSource.containsKey("name")) {
                        throw new LabradorException("variable name " + name + " [source] element miss [name] child element");
                    }
                    sources.add(curSource.get("name"));
                }
                String firstSourceName = firstSource.get("name");
                element.setSources(sources);
                //source来源于基础事件，即profile变量为基础变量
                if (Env.getEventFieldContainer().containsEventSource(firstSourceName)) {
                    element.setBasic(true);
                }

                if (module != Module.profile) {
                    moduleElements.computeIfAbsent(module, k -> new HashMap<>()).put(name, element);
                    continue;
                }

                String _dimension = String.valueOf(item.get("dimension"));
                if (_dimension.startsWith("others")) {
                    Dimension dimension = Dimension.others;
                    if (!_dimension.contains(":")) {
                        throw new BuilderException("variable name " + name + " dimension is others, but miss : symbol");
                    }
                    String[] dimensionItems = _dimension.split(":");
                    if (dimensionItems.length == 1) {
                        throw new BuilderException("variable name " + name + " dimension is others,but miss others field");
                    }
                    String dimensionField = dimensionItems[1];
                    element.setDimension(dimension);
                    if (!isValidSourceField(firstSourceName, dimensionField)) {
                        throw new BuilderException("variable name " + name + " dimension is others, but dimensionField is invalid " + dimensionField);
                    }
                    element.setDimensionField(dimensionField);
                } else {
                    if (element.isBasic()) {
                        if (!isValidSourceField(firstSourceName, _dimension)) {
                            throw new BuilderException("variable name " + name + " dimensionField is invalid " + _dimension);
                        }
                    }
                    Dimension dimension = Dimension.valueOf(_dimension);
                    element.setDimension(dimension);
                    element.setDimensionField(_dimension);
                }

                if (!item.containsKey("status")) {
                    throw new BuilderException("variable name " + name + " miss [status] element");
                }
                String _status = String.valueOf(item.get("status"));
                Status status = Status.valueOf(_status);
                element.setStatus(status);

                if (!item.containsKey("source")) {
                    throw new BuilderException("variable name " + name + " miss [source] element");
                }

                Filter variableFilter = new Filter();
                if (item.containsKey("filter")) {
                    Map<String, Object> filterElement = (Map<String, Object>) item.get("filter");
                    if (filterElement.containsKey("condition")) {
                        List conditionElements = (List) filterElement.get("condition");
                        for (Object conditionElement : conditionElements) {
                            Map conditionElementMap = (Map) conditionElement;
                            if (conditionElementMap.containsKey("condition")) {
                                List subConditionElements = (List) conditionElementMap.get("condition");
                                ConditionGroup conditionGroup = new ConditionGroup();
                                List<Condition> conditions = new ArrayList<>();
                                for (Object subConditionElement : subConditionElements) {
                                    Map subConditionMap = (Map) subConditionElement;
                                    conditions.add(convertCondition(subConditionMap));
                                }
                                OperateType operateType = OperateType.valueOf(String.valueOf(conditionElementMap.get("type")));
                                conditionGroup.setConditions(conditions);
                                conditionGroup.setOperateType(operateType);
                                variableFilter.addGroups(conditionGroup);
                            } else {
                                ConditionGroup conditionGroup = new ConditionGroup();
                                Condition condition = convertCondition(conditionElementMap);
                                conditionGroup.addCondition(condition);
                            /*OperateType operateType = OperateType.valueOf(String.valueOf(conditionElementMap.get("FilterType")));
                            conditionGroup.setOperateType(operateType);*/
                                variableFilter.addGroups(conditionGroup);
                            }
                        }
                        OperateType filterOperateType = OperateType.valueOf(String.valueOf(filterElement.get("type")));
                        variableFilter.setOperateType(filterOperateType);
                        element.setFilter(variableFilter);
                    } else {
                        ConditionGroup conditionGroup = new ConditionGroup();
                        //此处增加source判断是为了辩别是否包含filter内容
                        if(filterElement.containsKey("source")) {
                            Condition condition = convertCondition(filterElement);
                            conditionGroup.addCondition(condition);
                            variableFilter.addGroups(conditionGroup);

                            OperateType filterOperateType = OperateType.and;
                            variableFilter.setOperateType(filterOperateType);
                            element.setFilter(variableFilter);
                        }
                    }
                }

                if (item.containsKey("period")) {

                    Map<String, Object> periodItem = (Map<String, Object>) item.get("period");

                    if(periodItem.containsKey("end")) {
                        try {
                            Integer end = Integer.valueOf(periodItem.get("end").toString());
                            if(end > 0) {
                                throw new BuilderException("variable name " + name + "[period.end] illegal, [period.end] must <= 0");
                            }
                            element.setPeriodEnd(end);
                        } catch (Exception e) {
                            throw new BuilderException("variable name " + name + "[period.end] illegal, [period.end] must be Integer");
                        }
                    }

                    if(periodItem.containsKey("start")) {
                        try {
                            int start = Integer.valueOf(periodItem.get("start").toString());
                            if(start > 0) {
                                throw new BuilderException("variable name " + name + "[period.start] illegal, [period.start] must <= 0");
                            }
                            if(start>element.getPeriodEnd()) {
                                throw new BuilderException("variable name " + name + "[period.start] illegal, [period.start] must <= [period.end]");
                            }
                            element.setPeriodStart(start);
                        } catch (Exception e) {
                            throw new BuilderException("variable name " + name + "[period.start] illegal, [period.start] must be Integer");
                        }
                    }

                    if (!periodItem.containsKey("type")) {
                        throw new BuilderException("variable name " + name + " doesn't has [period.type] element");
                    }
                    PeriodType periodType = PeriodType.valueOf(String.valueOf(periodItem.get("type")));
                    //如果是ever，则视为period为空
                    if(periodType != PeriodType.ever) {
                        element.setPeriodType(periodType);
                    }
                }

                if (!item.containsKey("function")) {
                    throw new BuilderException("variable name " + name + " miss [function] element");
                }

                Map<String, String> functionItem = (Map<String, String>) item.get("function");
                if (!functionItem.containsKey("method")) {
                    throw new BuilderException("variable name " + name + " miss [function.method] element");
                }
                String method = functionItem.get("method");
                Function functionMethod = Function.valueOf(method);
                element.setFunctionMethod(functionMethod);

                if (!functionItem.containsKey("object")) {
                    throw new BuilderException("variable name " + name + " miss [function.object] element");
                }

                String _function_object = functionItem.get("object");

                if (StringUtils.isBlank(_function_object)) {
                    element.setFunctionField("");
                } else {
                    if (element.isBasic()) {
                        if (!isValidSourceField(firstSourceName, _function_object)) {
                            throw new BuilderException("variable name " + name + " [function.object] " + _function_object + " is invalid");
                        }
                    }
                    element.setFunctionField(_function_object);
                }

                if (!functionItem.containsKey("object_type")) {
                    throw new BuilderException("variable name " + name + " miss [function.object_type] element");
                }

                if (functionItem.containsKey("param")) {
                    try {
                        if(StringUtils.isBlank(functionItem.get("param"))) {
                            element.setFunctionSize(0);
                        } else {
                            int functionSize = Integer.valueOf(String.valueOf(functionItem.get("param")));
                            element.setFunctionSize(functionSize);
                        }
                    } catch (Exception e) {
                        throw new BuilderException("variable name " + name + " [function.param] error, should be integer type ");
                    }
                }

                if (element.isBasic()) {
                    //TODO 检查类型暂时屏弊，如:ORDER_SUBMIT暂时不存在
/*                    String functionObjectType = Env.getEventFieldContainer().getEventSourceFieldType(firstSourceName, _function_object);
                    if (!functionObjectType.equals(functionItem.get("object_type"))) {
                        throw new BuilderException("object detect with source " + firstSourceName + " object " + _function_object + " does not match config type " + functionObjectType);
                    }*/
                }


                element.setFunctionObjectDataType(EnumUtil.parseDataType(functionItem.get("object_type")));
                if (!item.containsKey("groupbykeys")) {
                    throw new BuilderException("variable name " + name + " miss property [groupbykeys]");
                }

                List _groupKeys = (List) item.get("groupbykeys");
                List<String> groupKeys = (List<String>) (List) _groupKeys;

                if(element.getDimension() != Dimension.global) {
                    if (!DimensionUtil.isSameDimentsion(groupKeys.get(0), element.getDimensionField())) {
                        throw new BuilderException("variable name " + name + "first group key " + groupKeys.get(0) + " is not equal with dimension");
                    }
                }

                if (groupKeys.size() == 2) {
                    if (!groupKeys.get(1).equals(element.getFunctionField())) {
                        throw new BuilderException("variable name " + name + " group keys size is 2, but function object is not same with " + groupKeys.get(1));
                    }
                }

                if (groupKeys.size() > 2) {
                    throw new BuilderException("too many group keys, size is  " + groupKeys.size());
                }
                element.setGroupKeys(groupKeys);
                moduleElements.computeIfAbsent(module, k -> new HashMap<>()).put(name, element);
            }
        } catch (IOException e) {
            log.warn("varaible " + currentVariableName, e);
            throw new BuilderException("varaible " + currentVariableName + " " + e.getMessage(), e);
        } catch (Exception e) {
            log.warn("varaible " + currentVariableName, e);
            throw new BuilderException("varaible " + currentVariableName + " " + e.getMessage(), e);
        }
        return moduleElements;
    }

    private Condition convertCondition(Map item) throws BuilderException {
        Condition condition = new Condition();
        if (!item.containsKey("source")) {
            throw new BuilderException("filter condition miss element [source]");
        }
        condition.setSource(String.valueOf(item.get("source")));

        if (!item.containsKey("object")) {
            throw new BuilderException("filter condtion miss element [object]");
        }
        condition.setObject(String.valueOf(item.get("object")));

        if (!item.containsKey("object_type")) {
            throw new BuilderException("filter condition miss element [object_type]");
        }
        condition.setObjectType(EnumUtil.parseDataType(String.valueOf(item.get("object_type"))));

        if (!item.containsKey("operation")) {
            throw new BuilderException("filter condition miss element [operation]");
        }
        condition.setOperation(EnumUtil.parseOperation(String.valueOf(item.get("operation"))));
        condition.setValue(String.valueOf(item.get("value")));
        condition.setType(FilterType.valueOf(String.valueOf(item.get("type"))));
        return condition;
    }

    /**
     * 转换非标准的Element
     */
    public Variable convertDerived(Element element) throws BuilderException {
        Variable variable = new Variable(element);
        variable.setName(element.getName());
        if(null != element.getPeriodType()) {
            PeriodType periodType = element.getPeriodType();
            if(periodType != PeriodType.last_n_days && periodType!= PeriodType.last_n_hours) {
                throw new BuilderException("builder derived variable " + element.getName() + " failed, period Type is not last_n_days and not last_n_hours");
            }
        }
        return variable;
    }

    public Variable convert(Element element) throws BuilderException {
        Variable variable = new Variable(element);
        variable.setProfileModuleType(ProfileModuleType.basic);
        variable.setName(element.getName());
        if (element.getSources() != null) {
            variable.setDerived(true);
        } else {
            variable.setDerived(false);
        }
        variable = methodDefineRegistry.define(variable.getElement().getFunctionMethod(), variable);
        return variable;
    }

    public Variable convertSlot(Element element) {
        Variable variable = new Variable(element);
        variable.setProfileModuleType(ProfileModuleType.basic);
        variable.setName(element.getName());
        if (element.getSources() != null) {
            variable.setDerived(true);
        } else {
            variable.setDerived(false);
        }
        return variable;
    }

}
