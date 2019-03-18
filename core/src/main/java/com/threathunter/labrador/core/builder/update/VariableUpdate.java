package com.threathunter.labrador.core.builder.update;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Element;
import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.builder.VariableBuilder;
import com.threathunter.labrador.core.builder.idgenerator.VariableIdGenerator;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.exception.UpdateException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 变量更新
 */
public class VariableUpdate extends BaseUpdate {

    private VariableIdGenerator variableIdGenerator;

    public VariableUpdate(VariableIdGenerator variableIdGenerator) {
        this.variableIdGenerator = variableIdGenerator;
    }

    @Override
    public void update() throws UpdateException, IOException, BuilderException, LabradorException {
        String content = parseContent();
        if (StringUtils.isBlank(content)) {
            throw new LabradorException("variable content is empty");
        }
        VariableBuilder builder = new VariableBuilder(content);
        Map<EnumUtil.Module, Map<String, Element>> moduleElements = builder.build();
        Map<String, Element> slotElements = moduleElements.containsKey(EnumUtil.Module.slot) ? moduleElements.get(EnumUtil.Module.slot) : new HashMap<>();
        Map<String, Element> profileElements = moduleElements.containsKey(EnumUtil.Module.profile) ? moduleElements.get(EnumUtil.Module.profile) : new HashMap();

        for (Map.Entry<String, Element> entry : profileElements.entrySet()) {
            String variablename = entry.getKey();
            Element element = entry.getValue();
            String source = element.getSources().get(0);
            if (profileElements.containsKey(source)) {
                Variable variable = builder.convertDerived(element);
                variable.setProfileModuleType(EnumUtil.ProfileModuleType.derived);
                Env.getVariables().putVariableIfAbsentWithDiemnsionScenes(variable);
                //来源于slot的基础变量
            } else if (element.isBasic() && !slotElements.containsKey(source)) {
                Variable variable = builder.convert(element);
                variable.setProfileModuleType(EnumUtil.ProfileModuleType.basic);
                if (!Env.getVariables().contains(variable.getName())) {
                    populateVariable(variable);
                }
                //衍生变量
            } else if (slotElements.containsKey(source)) { //外部变量
                Variable variable = builder.convertSlot(element);
                variable.setProfileModuleType(EnumUtil.ProfileModuleType.basic_batch);
                if (!Env.getVariables().contains(variable.getName())) {
                    populateVariable(variable);
                }
                Env.getVariables().addSlot2ProfileMapping(source, variablename);
            } else {
                throw new LabradorException("update variable error, variable " + variablename + " ProfileModuleType illegal");
            }
        }

        //设置slot变量的moduleType
        for (Map.Entry<String, Element> entry : slotElements.entrySet()) {
            String variableName = entry.getKey();
            Element element = entry.getValue();
            Variable variable = new Variable(element);
            variable.setProfileModuleType(EnumUtil.ProfileModuleType.external_slot);
            variable.setName(variableName);
            Env.getVariables().putVariableifAbsent(variable);
        }
    }

    private void populateVariable(Variable variable) throws LabradorException, IOException {
        //从存储中能找到变量名
        if (variableIdGenerator.containsId(variable.getName())) {
            int code = variableIdGenerator.loadId(variable.getName());
            if (code < 0) {
                throw new LabradorException("update variable error, found variable " + variable + " from warehouse, bug code is illegal");
            }
            variable.setCode(code);
        } else {
            int code = variableIdGenerator.generateId(variable.getName());
            variable.setCode(code);
        }
        Env.getVariables().putVariableIfAbsentWithDiemnsionScenes(variable);
    }

    @Override
    public String getUrl() {
        return ConfigUtil.getString("nebula.labrador.update.variable.url");
    }

    @Override
    public String getFilePath() {
        return ConfigUtil.getString("nebula.labrador.update.variable.file");
    }

    public VariableIdGenerator getVariableIdGenerator() {
        return variableIdGenerator;
    }

    public void setVariableIdGenerator(VariableIdGenerator variableIdGenerator) {
        this.variableIdGenerator = variableIdGenerator;
    }
}
