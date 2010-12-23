// (c) Copyright 2010 Odiago, Inc.

package com.odiago.rtengine.exec;

import java.io.IOException;

/**
 * A flow element is a worker in a flow; it has an input side
 * and an output side, and some function. 
 * Flow elements perform filtering, aggregation, joins, etc.
 * Flow elements represent specific physical entities.
 * Multiple flow elements may perform the same function. For example,
 * the same filter ("forward all messages matching regex 'r'") may
 * be applied to multiple input streams in parallel. Each such input
 * stream requires a separate FlowElement to be deployed.
 */
public abstract class FlowElement {
  
  /** Event attribute name defining the origin stream of an event. */
  public static final String STREAM_NAME_ATTR = "rtsql:stream";

  /**
   * Called before the FlowElement processes any events. This may trigger
   * the FlowElement to begin emitting generated events, etc.
   */
  public abstract void open() throws IOException, InterruptedException;

  /**
   * Called to notify the FlowElement to stop processing events. After
   * the close() call returns, the FE may not emit further events
   * downstream. After this call returns, isClosed() should return true.
   */
  public abstract void close() throws IOException, InterruptedException;

  /**
   * Returns true if close() has already been called on this FlowElement.
   */
  public abstract boolean isClosed();

  /**
   * Process another input event in the current window.
   * May emit output events if there is a 1-to-1 (non-aggregate) correlation.
   */
  public abstract void takeEvent(EventWrapper e) throws IOException, InterruptedException;

  /**
   * Finish any aggregation associated with this window, and emit any output
   * events that are associated with the entire window.
   */
  public void completeWindow() throws IOException, InterruptedException {
    // Default operation: ignore windowing.
  }

  /**
   * @return the FlowElementContext that the element is bound to.
   */
  public abstract FlowElementContext getContext();
  
}
