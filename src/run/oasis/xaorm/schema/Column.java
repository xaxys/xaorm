package run.oasis.xaorm.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

@Target({ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
    String type() default "";
    boolean nullable() default true;
    boolean primaryKey() default false;
    boolean autoIncrement() default false;
    boolean unique() default false;
    String raw() default "";
}
