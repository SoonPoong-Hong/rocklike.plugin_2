package rocklike.plugin.bookmark;


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;

/**
 * 이건 필요가 없다.
 * @author hong
 *
 */
public class HongMarkerUpdater implements IMarkerUpdater {

	@Override
	public String getMarkerType() {
		return HongBookmarkHelper.MARKER;
	}

	@Override
	public String[] getAttribute() {
		return null;
	}

	@Override
	public boolean updateMarker(IMarker marker, IDocument document, Position position) {
		try {
			int start = position.getOffset();
			int end = position.getOffset() + position.getLength();

			marker.setAttribute(IMarker.CHAR_START, end);
			marker.setAttribute(IMarker.CHAR_END, end);
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}


}
