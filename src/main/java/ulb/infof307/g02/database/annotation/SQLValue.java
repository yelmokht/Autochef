package ulb.infof307.g02.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark some attributes as "need to be inserted/retrieved in/from the database" attributes.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLValue {

	/**
	 * Give the opportunity to give another value to the attribute than the attribute name in the Java code.
	 * Used to put the database column name associated to the attribute.
	 *
	 * @return the value of the annotation
	 */
	String value() default "";

}
