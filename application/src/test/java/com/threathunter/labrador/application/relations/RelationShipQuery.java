package com.threathunter.labrador.application.relations;


import com.threathunter.labrador.application.AbstractTest;
import com.threathunter.labrador.core.exception.LabradorException;

import java.util.Map;

public class RelationShipQuery extends AbstractTest {

    public static void main(String[] args) throws LabradorException {
        RelationShipService relationShipService = new RelationShipService();
        Map<String,Object> result = relationShipService.loadRelations("456", "uid");
        System.out.println(result);
    }
}
