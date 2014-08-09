package ${model.package};

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ${interface.package}.${interface.name};

import com.kt.cms.cuc.common.util.ExceptionBuilder;

/**
 * @Description : ${model.description!}
 *
 * @Author  : 
 * @Date    : ${f.today()}
 * @Version : 1.0
 *
 */
@Service("${interface.name?uncap_first}")
public class ${model.name} implements ${interface.name} {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    

}    