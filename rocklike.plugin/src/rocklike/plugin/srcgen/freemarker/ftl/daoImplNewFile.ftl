package ${model.package};

import java.util.List;
import org.springframework.stereotype.Repository;
import com.kt.framework.integration.persistence.impl.KTDaoImpl;
import ${interface.package}.${interface.name};

/**
 * @Description : ${model.description!}
 *
 * @Author  : 
 * @Date    : ${f.today()}
 * @Version : 1.0
 *
 */
@Repository("${interface.name?uncap_first}")
public class ${model.name} extends KTDaoImpl implements ${interface.name}{

    @Override
    public Class getClazz() {
        return this.getClass();
    }
    
    
}    