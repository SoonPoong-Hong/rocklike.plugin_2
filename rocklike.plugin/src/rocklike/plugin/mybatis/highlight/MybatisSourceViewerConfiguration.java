package rocklike.plugin.mybatis.highlight;


import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import rocklike.plugin.mybatis.highlight.scanner.HongColorProvider;
import rocklike.plugin.mybatis.highlight.scanner.HongSingleTokenScanner;

public class MybatisSourceViewerConfiguration extends TextSourceViewerConfiguration{

	HongSqlScanner sqlScanner;
	HongSingleTokenScanner singleTokenScanner_comment;
	HongSingleTokenScanner singleTokenScanner2_tag;
	HongColorProvider colorProvider;
	SourceViewer sourceViewer;

	public MybatisSourceViewerConfiguration(HongColorProvider colorProvider, SourceViewer sourceViewer){
		super();
		this.colorProvider = colorProvider;
		this.sourceViewer = sourceViewer;
		sqlScanner = new HongSqlScanner(colorProvider);
		singleTokenScanner_comment = new HongSingleTokenScanner(new TextAttribute(colorProvider.getColor(RgbConstants.COMMENT)));
		singleTokenScanner2_tag = new HongSingleTokenScanner(new TextAttribute(colorProvider.getColor(RgbConstants.TAG)));
		
	}
	
	public void setup(){
		Document doc = new Document();
		IDocumentPartitioner partitioner = new FastPartitioner(new MybatisPartitionScanner(), MybatisPartitionScanner.getPartitionTypes());
		partitioner.connect(doc);
		doc.setDocumentPartitioner(partitioner);
		
		sourceViewer.setDocument(doc);
		sourceViewer.configure(this);
	}
	
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return MybatisPartitionScanner.getPartitionTypes();
	}
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		
		DefaultDamagerRepairer dr = null;
		
		dr = new DefaultDamagerRepairer(singleTokenScanner_comment);
		reconciler.setDamager(dr, MybatisPartitionScanner.XML_COMMENT);
		reconciler.setRepairer(dr, MybatisPartitionScanner.XML_COMMENT);
		
		dr = new DefaultDamagerRepairer(singleTokenScanner2_tag);
		reconciler.setDamager(dr, MybatisPartitionScanner.XML_TAG);
		reconciler.setRepairer(dr, MybatisPartitionScanner.XML_TAG);
		
		dr = new DefaultDamagerRepairer(sqlScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(sqlScanner);
		reconciler.setDamager(dr, MybatisPartitionScanner.XML_CDATA);
		reconciler.setRepairer(dr, MybatisPartitionScanner.XML_CDATA);
		
		return reconciler;
	}

	
	
	//---------------------------------------------------------------
	

	@Override
	protected boolean isShowInVerticalRuler(Annotation annotation) {
		// TODO Auto-generated method stub
		return super.isShowInVerticalRuler(annotation);
	}

	@Override
	protected boolean isShowInOverviewRuler(Annotation annotation) {
		// TODO Auto-generated method stub
		return super.isShowInOverviewRuler(annotation);
	}

	@Override
	protected boolean isShownInText(Annotation annotation) {
		// TODO Auto-generated method stub
		return super.isShownInText(annotation);
	}

	@Override
	public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		return super.getUndoManager(sourceViewer);
	}
	
	
}
