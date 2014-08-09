package ${model.package};

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.kt.cms.cuc.common.util.ExceptionUtil;
import com.kt.framework.controller.impl.KTControllerImpl;

/**
 * @Description : ${model.description!}
 *
 * @Author  : 
 * @Date    : ${f.today()}
 * @Version : 1.0
 *
 */
@Controller
public class ${model.name} extends KTControllerImpl {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 에러처리용 메소드
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleExceptions(Exception e){
		LoggerFactory.getLogger(this.getClass()).error(e.toString(), e);
		return ExceptionUtil.getModelAndView(e);
    }
    
    
}
