/**
 * 
 */
package com.threathunter.labrador.expression.op.define;

import com.threathunter.labrador.expression.datameta.BaseDataMeta;
import com.threathunter.labrador.expression.op.IOperatorExecution;
import com.threathunter.labrador.expression.IllegalExpressionException;
import com.threathunter.labrador.expression.datameta.Constant;
import com.threathunter.labrador.expression.op.Operator;

/**
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2009-02-06
 */
public class Op_COLON implements IOperatorExecution {

	public static final Operator THIS_OPERATOR = Operator.COLON;
	/* (non-Javadoc)
	 * @see org.wltea.expression.op.IOperatorExecution#execute(org.wltea.expression.datameta.Constant[])
	 */
	public Constant execute(Constant[] args) {
		throw new UnsupportedOperationException("操作符\"" + THIS_OPERATOR.getToken() + "不支持该方法");
	}

	/* (non-Javadoc)
	 * @see org.wltea.expression.op.IOperatorExecution#verify(int, org.wltea.expression.datameta.BaseDataMeta[])
	 */
	public Constant verify(int opPositin, BaseDataMeta[] args)
			throws IllegalExpressionException {
		throw new UnsupportedOperationException("操作符\"" + THIS_OPERATOR.getToken() + "不支持该方法");
	}


}
