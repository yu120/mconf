package cn.ms.mconf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Configure Entity Annotations.
 * 
 * @author lry
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataEntity {

	/**
	 * The Environment.
	 * 
	 * @return
	 */
	String env() default "";

	/**
	 * The Group.
	 * 
	 * @return
	 */
	String group() default "";

	/**
	 * The Version.
	 * 
	 * @return
	 */
	String version() default "";

}
