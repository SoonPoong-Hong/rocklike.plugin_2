package ${model.package};

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.kt.cms.cuc.common.xml.JaxbLongAdapter;
import com.kt.framework.domain.KTDefaultVo;

/**
 * @Description : ${model.description!}
 *
 * @Author  : 
 * @Date    : ${f.today()}
 * @Version : 1.0
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "searchCondition")  // searchCondition, info, list
public class ${model.name} extends KTDefaultVo {

//    @XmlElement(name = "info")
//    List<table_row> list;
}