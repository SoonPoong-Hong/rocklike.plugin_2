package rocklike.plugin.test;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IType;

import rocklike.plugin.srcgen.dao.GenDaoMethodDialog;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMybatisHelper;

public class TestHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
//	        IType type = HongJdtHelper.resolveSelectedType();
//	        HongJdtHelper.getInterfaces(type);
//			GenDaoMethodDialog dialog = new GenDaoMethodDialog();
//			if(dialog.open()==Window.OK){
//				GenDaoMethodResult methodInfo = dialog.getGenDaoMethodResult();
//				System.out.println(methodInfo);
//			}

//			IFile f = HongEclipseFileHelper.getFileFromWorkspaceRoot("/test-javaProject/src/main/resource/sqlmap/sql/dst/dm/Dm3.xml");
//			IDOMModel domModel = HongMybatisHelper.appendToLastInMybatisXml(f, "select", "__id", "BigInteger", "String", "\n\n");
//			domModel.save();

//			ICompilationUnit icu = HongJdtHelper.getSelectedICompilationUnit();
//			HongJavaSourceHelper.addImportIfNotExists(icu, "com.kt.cms.cuc.acq.ca.domain.PhysicalImage");

//			new DaoMethodGenerator().testDaoMethodGenerate();
//			IType t = HongJdtHelper.getSelectedICompilationUnit().getTypes()[0];
//			String pkg = HongMybatisHelper.assumeMybatisQueryXmlPackageId(t);
//			System.out.println(pkg);

			IType t = HongJdtHelper.getSelectedICompilationUnit().getTypes()[0];
			new GenDaoMethodDialog(t).open();


        } catch (Exception e) {
	        e.printStackTrace();
        }
		return null;
	}

}
