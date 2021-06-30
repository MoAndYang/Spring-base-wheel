package wheel.springframework.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源抽象接口。
 * 获取资源（配置文件）的接口
 */
public interface Resource {

    InputStream getInputStream() throws IOException;
}
