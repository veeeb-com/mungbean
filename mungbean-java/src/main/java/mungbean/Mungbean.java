/*
   Copyright 2009 Janne Hietamäki

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package mungbean;

import mungbean.cluster.ClusterDbOperationExecutor;

public class Mungbean {
    private final DBOperationExecutor executor;

    public Mungbean(Settings settings, String host, int port) {
        this(settings, new Server(host, port));
    }

    public Mungbean(Settings settings, Server server) {
        executor = new SingleNodeDbOperationExecutor(settings, server);
    }

    public Mungbean(Settings settings, Server... servers) {
        executor = new ClusterDbOperationExecutor(settings, servers);
    }

    public Database openDatabase(String name) {
        return new Database(executor, name);
    }

    public void close() {
        executor.close();
    }
}
