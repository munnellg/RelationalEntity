package ie.munnellg;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RelationalFieldSetterMeta
{
	public String inverseField();

	// public String inverseSetter() default null;
}
