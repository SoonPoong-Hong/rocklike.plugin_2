/*******************************************************************************
 * Copyright (c) 2009 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/

package de.loskutov.anyedit.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey
 */
public final class TextReplaceResultSet {

    private final List<LineReplaceResult> delegate;
    private int startLine = -1;
    private int stopLine = -1;
    private Exception exception;

    public TextReplaceResultSet(){
        delegate = new ArrayList<LineReplaceResult>();
    }

    /**
     *
     * @param o could be null value, if the line is not changed
     */
    public boolean add(LineReplaceResult o) {
        return delegate.add(o);
    }

    /**
     * @param index line index in the document
     * @return null if index is out of range
     */
    public LineReplaceResult get(int index) {
        if(index >= delegate.size()) {
            return null;
        }
        return delegate.get(index);
    }

    public boolean areResultsChanged(){
        for (int i = 0; i < size(); i++) {
            if(get(i) != null){
                return true;
            }
        }
        return false;
    }

    public int size(){
        return delegate.size();
    }

    /**
     * @return Returns the startLine.
     */
    public int getStartLine() {
        return this.startLine;
    }

    /**
     * @param startLine The startLine to set.
     */
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    /**
     * @return Returns the stopLine - last changed line in result
     */
    public int getStopLine() {
        return this.stopLine;
    }

    /**
     * @param stopLine The stopLine to set.
     */
    public void setStopLine(int stopLine) {
        this.stopLine = stopLine;
    }

    public int getNumberOfLines(){
        if(getStartLine()>= 0 && getStopLine() >= 0){
            return getStopLine() - getStartLine() + 1;
        }
        return 0;
    }

    public void clear(){
        delegate.clear();
    }

    /**
     * @return exception which was thrown during writing the document, if any
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param exception which was thrown during writing the document, if any
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
