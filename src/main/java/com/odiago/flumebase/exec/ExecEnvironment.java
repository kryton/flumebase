/**
 * Licensed to Odiago, Inc. under one or more contributor license
 * agreements.  See the NOTICE.txt file distributed with this work for
 * additional information regarding copyright ownership.  Odiago, Inc.
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.odiago.flumebase.exec;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import com.odiago.flumebase.plan.FlowSpecification;

import com.odiago.flumebase.server.SessionId;
import com.odiago.flumebase.server.UserSession;

/**
 * Specification of an environment in which FlowElements
 * can be executed. Supports a local (in-process) implementation, a remote implementation, and
 * potentially others.
 */
public abstract class ExecEnvironment {

  public ExecEnvironment() {
  }

  /**
   * Connect the client to the execution environment.
   * @return the SessionId of this connection.
   */
  public abstract SessionId connect() throws InterruptedException, IOException;

  /**
   * @return true if we are connected to the execution environment.
   */
  public abstract boolean isConnected();

  /** @return the name of this execution environment. e.g. "local",
   * or perhaps the server you're connected to, etc.
   */
  public String getEnvName() {
    return this.getClass().getName();
  }

  /**
   * Submit a query statement to the planner.
   * @return a response containing any text for the user, as well as any
   * flow ids spawned, etc.
   */
  public abstract QuerySubmitResponse submitQuery(String query, Map<String, String> options)
      throws InterruptedException, IOException;

  /**
   * Deploys a specified flow in the environment. Operates at a lower level
   * than submitQuery(), which allows the environment itself to plan the flow
   * based on the query.
   * @return the FlowId of this flow.
   */
  public abstract FlowId addFlow(FlowSpecification spec) throws InterruptedException, IOException;

  /**
   * Terminates a running flow.
   */
  public abstract void cancelFlow(FlowId id) throws InterruptedException, IOException;

  /**
   * Return information about all active flows in the execution environment, keyed
   * by FlowId.
   */
  public abstract Map<FlowId, FlowInfo> listFlows() throws InterruptedException, IOException;

  /**
   * Waits for the specified flow to complete.
   */
  public abstract void joinFlow(FlowId id) throws InterruptedException, IOException;

  /**
   * Waits up to 'timeout' milliseconds for the specified flow to complete. If timeout
   * is zero, blocks indefinitely.
   *
   * @return true if the flow completed, false if the timeout was reached instead.
   */
  public abstract boolean joinFlow(FlowId id, long timeout)
      throws InterruptedException, IOException;

  /** Subscribe the specified user session to watch the output of the specified flow. */
  public abstract void watchFlow(SessionId sessionId, FlowId flowId)
      throws InterruptedException, IOException;

  /** Unsubscribe the specified user session from the output of the specified flow. */
  public abstract void unwatchFlow(SessionId sessionId, FlowId flowId)
      throws InterruptedException, IOException;

  /** @return a list of all FlowIds being watched by this client. */
  public abstract List<FlowId> listWatchedFlows(SessionId sessionId)
      throws InterruptedException, IOException;

  /**
   * Sets the stream name associated with the output of a flow. If name is null,
   * removes any stream association from the flow.
   */
  public abstract void setFlowName(FlowId flowId, String name)
      throws InterruptedException, IOException;

  /**
   * Disconnects this client from the environment.
   */
  public abstract void disconnect(SessionId sessionId)
      throws InterruptedException, IOException;

  /**
   * Shuts down the execution environment. Closes all flows and prevents new
   * flows from starting.
   */
  public abstract void shutdown() throws InterruptedException, IOException;

  /**
   * Lookup a user session based on the session id.
   * @return the UserSession for the specified SessionId, or null if no such
   * session exists.
   */
  protected UserSession getSession(SessionId id) {
    return null; // Default implementation: there is no such session.
  }
}

