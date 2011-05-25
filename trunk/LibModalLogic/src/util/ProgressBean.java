/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */
package util;

import java.beans.*;
import java.io.Serializable;

/**
 * Class to monitor progress.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class ProgressBean implements Serializable,PropertyChangeListener {

  public static final String PROGRESS = "progress";
  private int progress = -1;
  private PropertyChangeSupport propertySupport;

  /**
   * Constructor of class ProgressBean.
   */
  public ProgressBean() {
    propertySupport = new PropertyChangeSupport(this);
  }

  /**
   * Return the current progress.
   *
   * @return the progress
   */
  public int getProgress() {
    return progress;
  }

  /**
   * Set the progress.
   *
   * @param progress the progress
   */
  public void setProgress(int progress) {
    int oldValue = this.progress;
    this.progress = progress;
    propertySupport.firePropertyChange(PROGRESS, oldValue, progress);
  }

  /**
   * Set the progress normalized.
   *
   * @param value the value
   * @param max the maximum
   */
  public void setProgress(int value, int max) {
    int p = (int)(100 * (((float)value) / max));
    setProgress(p);
  }

  /**
   * Add a listener to the progress.
   *
   * @param listener the listener
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(listener);
  }


  /**
   * Remove a listener.
   *
   * @param listener the listener
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(listener);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    setProgress((Integer)evt.getNewValue());
  }
}
