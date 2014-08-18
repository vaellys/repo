package cn.zthz.tool.db;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
public @interface Field {
	String name() default "" ;
	Class<?> type() default Object.class;
}
