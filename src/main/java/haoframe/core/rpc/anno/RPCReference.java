package haoframe.core.rpc.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  应用服务资源
 * @author Administrator
 *
 */
@Target({ElementType.FIELD})   //用于字段，方法，参数
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中
public @interface RPCReference {

}
