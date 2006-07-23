/**
 * 
 */
package org.codehaus.plj.main;

import org.codehaus.plj.core.Glue;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author kocka
 *
 */
public class Main {

	private final static String[] CTX_DESCRIPTORS = {""};

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String[] ctxDescriptors;
		if(args.length == 0) {
			ctxDescriptors = CTX_DESCRIPTORS;
		} else {
			ctxDescriptors = args;
		}
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(ctxDescriptors);
		Glue glue = (Glue)applicationContext.getBean("glue");
		glue.start();
	}

}
