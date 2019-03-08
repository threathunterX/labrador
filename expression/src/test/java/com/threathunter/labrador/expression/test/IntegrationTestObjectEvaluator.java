package com.threathunter.labrador.expression.test;

import java.util.ArrayList;

import com.threathunter.labrador.expression.Evaluator;
import com.threathunter.labrador.expression.ExpressionContext;
import com.threathunter.labrador.expression.ExpressionEvaluator;
import com.threathunter.labrador.expression.IllegalExpressionException;

import junit.framework.TestCase;


public class IntegrationTestObjectEvaluator extends TestCase {
	
	public static class Tx{
		String name = null;
		Tx(String x){
			name = x;
		}
		
		@com.threathunter.labrador.expression.annotation.Operator(sign="*")
		public Object xx(Object ob){
			return "xx";
		}
	}
	
	/**
	 */
	public void testObject(){
		System.out.println("testPriority");
		ArrayList<String> expressions = new ArrayList<String>();
		expressions.add("-(10 + (23 - 3)\r\n * (4 / 5)) % 6");//2
		
		Evaluator<Object> eval = new Evaluator<Object>(){

			@Override
			public Object evalutor(com.threathunter.labrador.expression.op.Operator op, Object first, Object second)
					throws IllegalExpressionException {
				
				System.out.println("evalutor op:" + op.getToken() + ", first:" + first + ", sec:" + second);
				return "**";
			}

			@Override
			public boolean canOperator(com.threathunter.labrador.expression.op.Operator op, Object first, Object second)
					throws IllegalExpressionException {
				return true;
			}
			
		};
		

		
		ExpressionContext ctx = new ExpressionContext(){
			public Object bindObject(String name){
				System.out.println("evalutor name:" + name);
				return new Tx(name); //"**";
			}
		};
		
		expressions.add("-(10 + (23 - 3)\r\n * (4 / 5)) % 6");//2

		expressions.add("TEST * AA");//2
		ctx.setStrict(false);

		for(String expression : expressions){
			System.out.println("expression : " + expression);
			//System.out.println(ExpressionEvaluator.compile(expression));
			Object result = ExpressionEvaluator.evaluate(expression, ctx, null);
			System.out.println("result = " + result);
			System.out.println();
		}
		System.out.println("----------------------------------------------------");		
		System.out.println("----------------testPriority over------------------");
		System.out.println("----------------------------------------------------");
	}
	

}
