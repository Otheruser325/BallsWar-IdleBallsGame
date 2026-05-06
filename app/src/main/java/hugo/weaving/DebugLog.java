package hugo.weaving;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * No-op replacement for Hugo's DebugLog annotation so release builds no longer
 * depend on the obsolete Gradle bytecode-weaving plugin.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface DebugLog {
}
