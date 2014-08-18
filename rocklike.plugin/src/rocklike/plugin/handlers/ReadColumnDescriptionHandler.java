package rocklike.plugin.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import rocklike.plugin.jdt.quickassist.ColumnDescVO;
import rocklike.plugin.jdt.quickassist.ColumnDescriptionStore;
import rocklike.plugin.util.HongMessagePopupUtil;

public class ReadColumnDescriptionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			FileDialog fd = new FileDialog(HandlerUtil.getActiveShell(event), SWT.OPEN);
			fd.setText("컬럼의 description이 있는 XML파일을 선택하세요.");
			String[] filterExt = {"*.xml", "*.*"};
			fd.setFilterExtensions(filterExt);
			String filePath = fd.open();
			if(filePath!=null){
				ColumnDescVO vo = ColumnDescriptionStore.unmarshalToVo(new File(filePath));
				ColumnDescriptionStore.saveToLocal(vo);
				HongMessagePopupUtil.showInfomsg("읽어들였습니다.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			HongMessagePopupUtil.showErrMsg(e);
		}
		return null;
	}



}
